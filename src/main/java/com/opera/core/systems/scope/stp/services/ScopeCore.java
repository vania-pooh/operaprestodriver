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

import com.opera.core.systems.OperaDriver.PrivateData;
import com.opera.core.systems.scope.AbstractService;
import com.opera.core.systems.scope.ScopeServices;
import com.opera.core.systems.scope.exceptions.ScopeException;
import com.opera.core.systems.scope.protos.CoreProtos;
import com.opera.core.systems.scope.protos.CoreProtos.BrowserInformation;
import com.opera.core.systems.scope.protos.CoreProtos.ClearFlags;
import com.opera.core.systems.scope.protos.CoreProtos.ClearPrivateDataArg;
import com.opera.core.systems.scope.protos.UmsProtos.Response;
import com.opera.core.systems.scope.services.Core;
import com.opera.core.systems.scope.stp.services.messages.CoreMessage;

import java.util.List;

public class ScopeCore extends AbstractService implements Core {

  private boolean supportsMeta = false;
  private BrowserInformation browserInformation;

  public ScopeCore(ScopeServices services) {
    super(services, SERVICE_NAME);

    if (isVersionInRange("1.2")) {
      supportsMeta = true;
    }

    services.setCore(this);
  }

  private void assertHasMetaInformation() {
    if (!supportsMeta) {
      throw new UnsupportedOperationException("not available in this product");
    }
  }

  public void init() {
    if (supportsMeta) {
      browserInformation = getBrowserInformation();
    }
  }

  public String getCoreVersion() {
    assertHasMetaInformation();
    return browserInformation.getCoreVersion();
  }

  public String getOperatingSystem() {
    assertHasMetaInformation();
    return browserInformation.getOperatingSystem();
  }

  public String getProduct() {
    assertHasMetaInformation();
    return browserInformation.getProduct();
  }

  public String getBinaryPath() {
    assertHasMetaInformation();
    return browserInformation.getBinaryPath();
  }

  public String getUserAgent() {
    assertHasMetaInformation();
    return browserInformation.getUserAgent();
  }

  public Integer getProcessID() {
    assertHasMetaInformation();
    return browserInformation.getProcessID();
  }

  public void clearPrivateData(List<ClearFlags> flags) {
    ClearPrivateDataArg.Builder arg = ClearPrivateDataArg.newBuilder();
    arg.addAllClearList(flags);
    executeMessage(CoreMessage.CLEAR_PRIVATE_DATA, arg);
  }

  public void clearPrivateData(PrivateData... flags) {
    clearPrivateData(privateDataFlagsToScope(flags));
  }

  private BrowserInformation getBrowserInformation() {
    Response response = executeMessage(CoreMessage.GET_BROWSER_INFORMATION);
    BrowserInformation.Builder builder = BrowserInformation.newBuilder();
    buildPayload(response, builder);
    return builder.build();
  }

  private List<CoreProtos.ClearFlags> privateDataFlagsToScope(PrivateData... flags) {
    ImmutableList.Builder<ClearFlags> clearFlags = ImmutableList.builder();

    for (PrivateData flag : flags) {
      switch (flag) {
        case ALL:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_ALL);
          break;
        case VISITED_LINKS:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_VISITED_LINKS);
          break;
        case DISK_CACHE:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_DISK_CACHE);
          break;
        case IMAGE_CACHE:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_IMAGE_CACHE);
          break;
        case MEMORY_CACHE:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_MEMORY_CACHE);
          break;
        case SENSITIVE_DATA:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_SENSITIVE_DATA);
          break;
        case SESSION_COOKIES:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_SESSION_COOKIES);
          break;
        case ALL_COOKIES:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_ALL_COOKIES);
          break;
        case GLOBAL_HISTORY:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_GLOBAL_HISTORY);
          break;
        case CONSOLE:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_CONSOLE);
          break;
        case THUMBNAILS:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_THUMBNAILS);
          break;
        case WEBDATABASES:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_WEBDATABASES);
          break;
        case WEBSTORAGE:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_WEBSTORAGE);
          break;
        case APPCACHE:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_APPCACHE);
          break;
        case GEOLOCATION_PERMISSIONS:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_GEOLOCATION_PERMISSIONS);
          break;
        case SITE_PREFS:
          clearFlags.add(CoreProtos.ClearFlags.CLEAR_SITE_PREFS);
          break;
        default:
          throw new ScopeException("Unhandled private data flag: " + flag);
      }
    }

    return clearFlags.build();
  }

}