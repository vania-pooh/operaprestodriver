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

package com.opera.core.systems.scope;

/**
 * A Scope command that is assigned by an id (int) and a service name (string), as specified by the
 * Scope protocol.
 */
public interface Message {

  /**
   * Get command id to execute on host, service definitions are available on <a
   * href="http://dragonfly.opera.com/app/scope-interface/index.html">Scope Interface</a>
   *
   * @return The id associated with the command
   */
  int getID();

  /**
   * Associates a command id with a service name
   *
   * @return Name of the service the command belongs to
   */
  String getServiceName();

}