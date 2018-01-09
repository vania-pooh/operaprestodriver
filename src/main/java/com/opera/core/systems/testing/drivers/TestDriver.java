/*
Copyright 2012 Opera Software ASA

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

package com.opera.core.systems.testing.drivers;

import com.opera.core.systems.CapturesScreen;
import com.opera.core.systems.OperaDriver;
import com.opera.core.systems.OperaProxy;
import com.opera.core.systems.OperaSettings;
import com.opera.core.systems.OperaUtils;
import com.opera.core.systems.RunsSelftest;
import com.opera.core.systems.preferences.OperaScopePreferences;
import com.opera.core.systems.runner.OperaRunner;
import com.opera.core.systems.scope.ScopeServices;
import com.opera.core.systems.scope.exceptions.ResponseNotReceivedException;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.HasInputDevices;

import java.util.List;

public interface TestDriver extends WebDriver, HasInputDevices, JavascriptExecutor, TakesScreenshot,
                                    RunsSelftest, CapturesScreen {

  int get(String url, long timeout);

  boolean isRunning();

  OperaRunner getRunner();

  OperaSettings getSettings();

  ScopeServices getServices();

  OperaUtils utils();

  OperaProxy proxy();

  OperaScopePreferences preferences();

  OperaDriver.OperaOptions manage();

  @Deprecated
  void operaAction(String using, String... params);

  List<String> listFrames();

  void waitForLoadToComplete() throws ResponseNotReceivedException;

}
