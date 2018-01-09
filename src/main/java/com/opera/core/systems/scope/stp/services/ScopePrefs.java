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

import com.opera.core.systems.scope.AbstractService;
import com.opera.core.systems.scope.ScopeServices;
import com.opera.core.systems.scope.exceptions.ScopeException;
import com.opera.core.systems.scope.protos.PrefsProtos.GetPrefArg;
import com.opera.core.systems.scope.protos.PrefsProtos.GetPrefArg.Mode;
import com.opera.core.systems.scope.protos.PrefsProtos.ListPrefsArg;
import com.opera.core.systems.scope.protos.PrefsProtos.Pref;
import com.opera.core.systems.scope.protos.PrefsProtos.PrefList;
import com.opera.core.systems.scope.protos.PrefsProtos.PrefValue;
import com.opera.core.systems.scope.protos.PrefsProtos.SetPrefArg;
import com.opera.core.systems.scope.protos.UmsProtos.Response;
import com.opera.core.systems.scope.services.Prefs;
import com.opera.core.systems.scope.stp.services.messages.PrefsMessage;

import java.util.List;

public class ScopePrefs extends AbstractService implements Prefs {

  public ScopePrefs(ScopeServices services) {
    super(services, SERVICE_NAME);
    services.setPrefs(this);
  }

  public void init() {
  }

  public String getPref(String section, String key, Mode mode) {
    GetPrefArg.Builder getPrefBuilder = GetPrefArg.newBuilder();
    getPrefBuilder.setSection(section);
    getPrefBuilder.setKey(key);
    getPrefBuilder.setMode(mode);

    Response response = executeMessage(PrefsMessage.GET_PREF, getPrefBuilder);

    PrefValue.Builder prefValueBuilder = PrefValue.newBuilder();
    buildPayload(response, prefValueBuilder);
    PrefValue prefsString = prefValueBuilder.build();

    return prefsString.getValue();
  }

  public List<Pref> listPrefs(Boolean sort, String section) {
    ListPrefsArg.Builder listPrefBuilder = ListPrefsArg.newBuilder();
    if (sort != null) {
      listPrefBuilder.setSort(sort);
    }
    if (section != null && !"".equals(section)) {
      listPrefBuilder.setSection(section);
    }

    Response response = executeMessage(PrefsMessage.LIST_PREFS, listPrefBuilder);

    PrefList.Builder prefListBuilder = PrefList.newBuilder();
    buildPayload(response, prefListBuilder);
    PrefList prefList = prefListBuilder.build();

    return prefList.getPrefListList();
  }

  public void setPrefs(String section, String key, String value) {
    SetPrefArg.Builder setPrefBuilder = SetPrefArg.newBuilder();
    setPrefBuilder.setSection(section);
    setPrefBuilder.setKey(key);
    setPrefBuilder.setValue(value);

    Response response = executeMessage(PrefsMessage.SET_PREF, setPrefBuilder);

    if (response == null) {
      throw new ScopeException("Internal error while setting a preference");
    }
  }

}