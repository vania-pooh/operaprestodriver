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

package com.opera.core.systems.internal;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

/**
 * Interface for use with SocketMonitor for being notified about read/write events.
 */
public interface SocketListener {

  /**
   * @return true if more reading is expected.
   */
  boolean canRead(SelectableChannel ch) throws IOException;

  /**
   * @return true if more writing is needed.
   */
  boolean canWrite(SelectableChannel ch) throws IOException;

}