/*
Copyright 2011-2012 Opera Software ASA

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

package com.opera.core.systems;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import org.openqa.selenium.WebDriverException;

import java.util.Map;

/**
 * A collection of all Opera products.  Use the {@link #is(OperaProduct)} to compare and check
 * products.
 */
public enum OperaProduct {

  CORE("core-gogi"),
  DESKTOP("desktop"),
  SDK("devices"),
  MOBILE("mobile"),
  MINI("mini"),

  /**
   * Never returned, but can be used to request any product.
   */
  ALL("") {
    @Override
    public boolean is(OperaProduct compareWith) {
      return true;
    }
  };

  private static final Map<String, OperaProduct> lookup =
      Maps.uniqueIndex(ImmutableList.copyOf(values()), new Function<OperaProduct, String>() {
        public String apply(OperaProduct product) {
          return product.getDescriptionString();
        }
      });

  private final String product;

  OperaProduct(String product) {
    this.product = product;
  }

  public String getDescriptionString() {
    return product;
  }

  public String toString() {
    switch (OperaProduct.get(product)) {
      case CORE:
        return "gogi";
      case SDK:
        return "SDK";
      case DESKTOP:
      case MOBILE:
      case MINI:
        return "Opera " + Character.toUpperCase(product.charAt(0)) + product.substring(1);
      default:
        return "(not specified)";
    }
  }

  /**
   * Compares current product with given product.  Some products, such as the general product CORE,
   * might map to several different products, such as CORE_DESKTOP, CORE_SMARTPHONE and CORE_TV.
   *
   * @param compareWith the product to compare the current one with
   * @return true if the products are the same, false otherwise
   */
  public boolean is(OperaProduct compareWith) {
    return this.equals(compareWith);
  }

  public static OperaProduct get(String product) {
    if (product == null) {
      return ALL;
    }

    if (!lookup.containsKey(product)) {
      throw new WebDriverException("Unknown product: " + product);
    }

    return lookup.get(product);
  }

}