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

import com.opera.core.systems.OperaProduct;

public class TestOperaDesktopDriverSupplier extends AbstractTestDriverSupplier {

  public TestOperaDesktopDriverSupplier() {
    super(OperaProduct.DESKTOP);
  }

  public TestOperaDesktopDriver get() {
    // Uncomment this if you wish to connect Opera manually:
    //settings.autostart(false);

    return new TestOperaDesktopDriver(getSettings());
  }

  public boolean supplies(Class<? extends TestDriver> klass) {
    return TestOperaDesktopDriver.class.equals(klass);
  }

}