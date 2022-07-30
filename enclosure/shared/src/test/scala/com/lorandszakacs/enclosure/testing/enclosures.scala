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

package com.lorandszakacs.enclosure.testing

import com.lorandszakacs.enclosure.Enclosure

object Summoner {
  def summon(implicit enc: Enclosure): Enclosure = enc
}

object TopLevelObjectEnclosure {
  val enclosure: Enclosure = Summoner.summon
}

class TopLevelClassEnclosure {
  val enclosure: Enclosure = Summoner.summon
}

trait TopLevelTraitEnclosure {
  val enclosure: Enclosure = Summoner.summon
}

trait TopLevelTraitEnclosureWithCompanion {
  val enclosure: Enclosure = Summoner.summon
}

object TopLevelTraitEnclosureWithCompanion

class TopLevelSubClassEnclosure extends TopLevelClassEnclosure {
  val subEnclosure: Enclosure = Summoner.summon
}

object NestedClassInObjectEnclosure {

  val enclosure = new NestedClass().enclosure

  private class NestedClass {
    val enclosure: Enclosure = Summoner.summon
  }
}

class NestedClassInClassEnclosure {

  val enclosure = new NestedClass().enclosure

  private class NestedClass {
    val enclosure: Enclosure = Summoner.summon
  }
}

object NestedAnonymousTraitInClassEnclosure {

  val enclosure: Enclosure = (new NestedTrait {}).enclosure

  private sealed trait NestedTrait {
    val enclosure: Enclosure = Summoner.summon
  }
}

object NestedClassInClassInObjectEnclosure {
  val enclosure: Enclosure = (new NestedClass).enclosure

  private class NestedClass {
    val enclosure: Enclosure = new NestedClassInClass().enclosure

    class NestedClassInClass {
      val enclosure: Enclosure = Summoner.summon
    }

  }
}

object NestedObjectInObjectEnclosure {

  object ObjectInObject {
    val enclosure: Enclosure = Summoner.summon
  }
}

case class CaseClassEnclosure() {
  val enclosure: Enclosure = Summoner.summon
}

class ParameterizedClassEnclosure[T] {
  val enclosure: Enclosure = Summoner.summon
}

class HigherKindParameterizedClassEnclosure[F[_]] {
  val enclosure: Enclosure = Summoner.summon
}

class SuperType {
  def enclosure: Enclosure = Summoner.summon
}

class SubType extends SuperType {}

trait SuperTrait {
  def enclosure: Enclosure = Summoner.summon
}

class SuperTraitImpl extends SuperTrait

class EnclosureAsClassParam(implicit val enclosure: Enclosure)

object NestedMethodEnclosure {
  val enclosure0 = nestedMethod0()
  val enclosure1 = nestedMethod1("someString")

  private def nestedMethod0(): Enclosure = {
    Summoner.summon
  }

  private def nestedMethod1(p1: String): Enclosure = {
    // we do this to get rid of unused param warning. Once scala 2.12 support is dropped, we can add the nowarn annotation instead
    val opt = for {
      _ <- Option(p1.toString())
      enc <- Option(Summoner.summon)
    } yield enc
    opt.get

  }
}
