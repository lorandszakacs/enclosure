/*
 * Copyright 2021 Loránd Szakács
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lorandszakacs.enclosure

object TopLevelObjectEnclosure {
  val enclosure: Enclosure = Enclosure.generateEnclosure
}

class TopLevelClassEnclosure {
  val enclosure: Enclosure = Enclosure.generateEnclosure
}

object NestedClassInObjectEnclosure {

  val enclosure = new NestedClass().enclosure

  private class NestedClass {
    val enclosure: Enclosure = Enclosure.generateEnclosure
  }
}

class NestedClassInClassEnclosure {

  val enclosure = new NestedClass().enclosure

  private class NestedClass {
    val enclosure: Enclosure = Enclosure.generateEnclosure
  }
}

object NestedAnonymousClassEnclosure {

  val enclosure = (new NestedTrait {}).enclosure

  private sealed trait NestedTrait {
    val enclosure: Enclosure = Enclosure.generateEnclosure
  }
}

object NestedMethodEnclosure {
  val enclosure0 = nestedMethod0()
  val enclosure1 = nestedMethod1("someString")

  private def nestedMethod0(): Enclosure = {
    Enclosure.generateEnclosure
  }

  private def nestedMethod1(p1: String): Enclosure = {
    Enclosure.generateEnclosure
  }
}
