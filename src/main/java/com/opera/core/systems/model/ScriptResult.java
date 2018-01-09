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

package com.opera.core.systems.model;

/**
 * Holds the result of a script execution, namely for an object.  The Scope protocol assigns an
 * object ID for each object retrieved through the protocol, this ID can be used later to release
 * the object.
 */
public class ScriptResult {

  private final int objectId;
  private final String className;

  public ScriptResult(int objectId, String className) {
    this.objectId = objectId;
    this.className = className;
  }

  public int getObjectId() {
    return objectId;
  }

  public String getClassName() {
    return className;
  }

}