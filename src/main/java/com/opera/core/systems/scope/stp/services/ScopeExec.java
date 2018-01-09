/*
Copyright 2008-2012 Opera Software ASA

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.opera.core.systems.scope.stp.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.opera.core.systems.common.collect.CaseInsensitiveStringSet;
import com.opera.core.systems.internal.OperaColors;
import com.opera.core.systems.internal.VersionUtil;
import com.opera.core.systems.model.Canvas;
import com.opera.core.systems.model.ColorResult;
import com.opera.core.systems.model.OperaColor;
import com.opera.core.systems.model.ScreenCaptureReply;
import com.opera.core.systems.scope.AbstractService;
import com.opera.core.systems.scope.ScopeServices;
import com.opera.core.systems.scope.exceptions.ScopeException;
import com.opera.core.systems.scope.exceptions.WindowNotFoundException;
import com.opera.core.systems.scope.internal.OperaIntervals;
import com.opera.core.systems.scope.internal.OperaMouseKeys;
import com.opera.core.systems.scope.protos.ExecProtos.ActionInfoList;
import com.opera.core.systems.scope.protos.ExecProtos.ActionInfoList.ActionInfo;
import com.opera.core.systems.scope.protos.ExecProtos.ActionList;
import com.opera.core.systems.scope.protos.ExecProtos.ActionList.Action;
import com.opera.core.systems.scope.protos.ExecProtos.Area;
import com.opera.core.systems.scope.protos.ExecProtos.MouseAction;
import com.opera.core.systems.scope.protos.ExecProtos.ScreenWatcher;
import com.opera.core.systems.scope.protos.ExecProtos.ScreenWatcher.ColorSpec;
import com.opera.core.systems.scope.protos.ExecProtos.ScreenWatcherResult;
import com.opera.core.systems.scope.protos.ExecProtos.ScreenWatcherResult.ColorMatch;
import com.opera.core.systems.scope.protos.UmsProtos.Response;
import com.opera.core.systems.scope.services.Exec;
import com.opera.core.systems.scope.stp.services.messages.ExecMessage;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The exec service handles user interactions such as key presses, mouse clicks, screenshot grabbing
 * and executing actions on Opera.
 */
public class ScopeExec extends AbstractService implements Exec {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private final List<String> excludedActions = Lists.newArrayList("Select all", "Delete");

  private Set<String> actions = ImmutableSet.of();

  public ScopeExec(ScopeServices services) {
    super(services, SERVICE_NAME); //, "3.0");

    // Another ugly hack for patch version
    if (VersionUtil.compare(getServiceVersion(), "2.0.1") == -1) {
      excludedActions.add("Close page");
    }

    services.setExec(this);
  }

  public void init() {
    actions = initActionList();
  }

  private Set<String> initActionList() {
    Response response = executeMessage(ExecMessage.GET_ACTION_LIST, null);
    ActionInfoList.Builder builder = ActionInfoList.newBuilder();
    buildPayload(response, builder);
    ActionInfoList infoList = builder.build();

    Set<String> actions = new CaseInsensitiveStringSet();
    for (ActionInfo info : infoList.getActionInfoListList()) {
      actions.add(info.getName());
    }
    return actions;
  }

  public void mouseAction(int x, int y, OperaMouseKeys... keys) {
    mouseAction(x, y, 1, keys);
  }

  public void mouseAction(int x, int y, int count, OperaMouseKeys... keys) {
    if (x < 0 || y < 0) {
      throw new IllegalArgumentException(
          String.format("Element is located outside viewport (%s,%s)", x, y));
    }

    int keyValue = 0;

    // Join keys together using assignment operator for OR.
    for (OperaMouseKeys operaMouseKeys : keys) {
      keyValue |= operaMouseKeys.getValue();
    }

    MouseAction.Builder actionBuilder = MouseAction.newBuilder();
    actionBuilder.setWindowID(services.getWindowManager().getActiveWindowId());
    actionBuilder.setX(x);
    actionBuilder.setY(y);
    actionBuilder.setButtonAction(keyValue);

    // exec 2.2 introduced the ability specify the number of iterations for a mouse click in case
    // we wish to perform a multi-click event (1 and above clicks).  For backwards-compatibility
    // we provide an iteration on number of clicks here.
    if (VersionUtil.compare(getServiceVersion(), "2.2") >= 0) {
      actionBuilder.setRepeatCount(count);
      executeMessage(ExecMessage.SEND_MOUSE_ACTION, actionBuilder);
    } else {
      for (int i = 0; i < count; i++) {
        executeMessage(ExecMessage.SEND_MOUSE_ACTION, actionBuilder.clone());
      }

      // If multi-clicking, wait some time after executing the mouse action so that Opera doesn't
      // consider two consecutive double click's a quadruple click.
      if (count > 1) {
        try {
          Thread.sleep(OperaIntervals.MULTIPLE_CLICK_SLEEP.getMs());
        } catch (InterruptedException e) {
          // nothing
        }
      }
    }
  }

  public Set<String> getActionList() {
    return actions;
  }

  public void action(String using, String... params) {
    action(using, services.getWindowManager().getActiveWindowId(), params);
  }

  public void action(String using, int windowID, String... params) {
    if (!actions.contains(using)) {
      throw new UnsupportedOperationException("The requested action is not supported: " + using);
    }

    ActionList.Builder builder = ActionList.newBuilder();
    Action.Builder actionBuilder = Action.newBuilder();
    actionBuilder.setName(using);
    if (params != null && params.length > 0) {
      actionBuilder.setValue(params[0]);
    }

    if (!excludedActions.contains(using)) {
      try {
        actionBuilder.setWindowID(windowID);
      } catch (WindowNotFoundException e) {
        logger.warning("Exception from setWindowID: " + e);
        e.printStackTrace();
      }
    }

    // type.setSpace("preserve");
    builder.addActionList(actionBuilder);
    if (executeMessage(ExecMessage.EXEC, builder) == null) {
      throw new ScopeException("Unexpected error while calling action: " + using);
    }
  }

  public void action(String using, int data, String dataString, String dataStringParam) {
    if (!actions.contains(using)) {
      throw new UnsupportedOperationException("The requested action is not supported: " + using);
    }

    ActionList.Builder builder = ActionList.newBuilder();
    Action.Builder actionBuilder = Action.newBuilder();
    actionBuilder.setName(using);
    actionBuilder.setValue(dataString);
    actionBuilder.setData(data);
    actionBuilder.setStringParam(dataStringParam);

    // type.setSpace("preserve");
    builder.addActionList(actionBuilder);
    if (executeMessage(ExecMessage.EXEC, builder) == null) {
      throw new ScopeException("Unexpected error while calling action: " + using);
    }
  }

  public void key(String key) {
    key(key, false);
    key(key, true);
  }

  public void key(String key, boolean up) {
    if (key == null) {
      return;
    }

    if (up) {
      action("_keyup", key);
    } else {
      action("_keydown", key);
    }
  }

  public ScreenCaptureReply containsColor(Canvas canvas, long timeout, OperaColors... colors) {
    ScreenWatcher.Builder builder = ScreenWatcher.newBuilder();
    Area.Builder areaBuilder = Area.newBuilder();

    areaBuilder.setX(canvas.getX());
    areaBuilder.setY(canvas.getY());
    areaBuilder.setH(canvas.getHeight());
    areaBuilder.setW(canvas.getWidth());

    builder.setArea(areaBuilder);

    int i = 0;
    for (OperaColors color : colors) {
      ColorSpec.Builder colorSpec = convertColor(color.getColour());
      colorSpec.setId(++i);
      builder.addColorSpecList(colorSpec);
    }

    ScreenWatcherResult result = executeScreenWatcher(builder, timeout);

    ImmutableList.Builder<ColorResult> matches = ImmutableList.builder();
    for (ColorMatch match : result.getColorMatchListList()) {
      matches.add(new ColorResult(match.getId(), match.getCount()));
    }

    return new ScreenCaptureReply(result.getMd5(), matches.build());
  }

  /**
   * Executes a screenwatcher with the given timeout and returns the result.
   */
  private ScreenWatcherResult executeScreenWatcher(ScreenWatcher.Builder builder, long timeout) {
    if (timeout <= 0) {
      timeout = 1;
    }

    // TODO(andreastt): Unsafe long to int cast
    builder.setTimeOut((int) timeout);

    builder.setWindowID(services.getWindowManager().getActiveWindowId());

    Response response = executeMessage(ExecMessage.SETUP_SCREEN_WATCHER,
                                       builder,
                                       OperaIntervals.RESPONSE_TIMEOUT.getMs() + timeout);

    ScreenWatcherResult.Builder watcherBuilder = ScreenWatcherResult.newBuilder();
    buildPayload(response, watcherBuilder);

    return watcherBuilder.build();
  }

  private ColorSpec.Builder convertColor(OperaColor color) {
    ColorSpec.Builder builder = ColorSpec.newBuilder();
    builder.setBlueHigh(color.getHighBlue());
    builder.setGreenHigh(color.getHighGreen());
    builder.setRedHigh(color.getHighRed());
    builder.setBlueLow(color.getLowBlue());
    builder.setGreenLow(color.getLowGreen());
    builder.setRedLow(color.getLowRed());
    return builder;
  }

  public ScreenCaptureReply screenWatcher(Canvas canvas, long timeout, boolean includeImage,
                                          List<String> hashes) {
    ScreenWatcher.Builder builder = ScreenWatcher.newBuilder();
    Area.Builder areaBuilder = Area.newBuilder();

    // TODO: viewport relative
    areaBuilder.setX(canvas.getX());
    areaBuilder.setY(canvas.getY());
    areaBuilder.setH(canvas.getHeight());
    areaBuilder.setW(canvas.getWidth());

    builder.setArea(areaBuilder);
    builder.addAllMd5List(hashes);
    if (!includeImage) {
      builder.setIncludeImage(false);
    }

    ScreenWatcherResult result = executeScreenWatcher(builder, (int) timeout);

    return new ScreenCaptureReply(result.getMd5(), result.getPng().toByteArray());
  }

}