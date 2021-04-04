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

class EnclosureTest extends EnclosureTestSuite {

  test("TopLevelObjectEnclosure") {
    testEnclosure(TopLevelObjectEnclosure.enclosure)("TopLevelObjectEnclosure")
  }

  test("TopLevelClassEnclosure") {
    testEnclosure(new TopLevelClassEnclosure().enclosure)("TopLevelClassEnclosure")
  }

  test("TopLevelTraitEnclosure") {
    testEnclosure(new TopLevelTraitEnclosure {}.enclosure)("TopLevelTraitEnclosure")
  }

  test("TopLevelTraitEnclosureWithCompanion") {
    testEnclosure(new TopLevelTraitEnclosureWithCompanion {}.enclosure)("TopLevelTraitEnclosureWithCompanion")
  }

  test("TopLevelSubClassEnclosure") {
    testEnclosure(new TopLevelSubClassEnclosure().enclosure)("TopLevelClassEnclosure")
    testEnclosure(new TopLevelSubClassEnclosure().subEnclosure)("TopLevelSubClassEnclosure")
  }

  test("NestedClassInObjectEnclosure") {
    testEnclosure(NestedClassInObjectEnclosure.enclosure)("NestedClassInObjectEnclosure.NestedClass")
  }

  test("NestedClassInClassEnclosure") {
    testEnclosure(new NestedClassInClassEnclosure().enclosure)("NestedClassInClassEnclosure.NestedClass")
  }

  test("NestedAnonymousTraitInClassEnclosure") {
    testEnclosure(NestedAnonymousTraitInClassEnclosure.enclosure)("NestedAnonymousTraitInClassEnclosure.NestedTrait")
  }

  test("NestedClassInClassInObjectEnclosure") {
    testEnclosure(NestedClassInClassInObjectEnclosure.enclosure)(
      "NestedClassInClassInObjectEnclosure.NestedClass.NestedClassInClass"
    )
  }

  test("NestedObjectInObjectEnclosure") {
    testEnclosure(NestedObjectInObjectEnclosure.ObjectInObject.enclosure)(
      "NestedObjectInObjectEnclosure.ObjectInObject"
    )
  }

  test("CaseClassEnclosure") {
    testEnclosure(CaseClassEnclosure().enclosure)("CaseClassEnclosure")
  }

  test("ParameterizedClassEnclosure") {
    testEnclosure(new ParameterizedClassEnclosure[Nothing]().enclosure)("ParameterizedClassEnclosure")
  }

  test("HigherKindParameterizedClassEnclosure") {
    testEnclosure(new HigherKindParameterizedClassEnclosure[List]().enclosure)("HigherKindParameterizedClassEnclosure")
  }

  test("EnclosureAsClassParam") {
    testEnclosureFullyQualified(new EnclosureAsClassParam().enclosure)(
      s"$currentPackage.${this.getClass().getSimpleName()}"
    )
  }

  test("packageLevelEnclosure") {
    testEnclosureFullyQualified(packageLevelEnclosure)(currentPackage)
  }

  test("NestedMethodEnclosure") {
    testEnclosure(NestedMethodEnclosure.enclosure0)("NestedMethodEnclosure")
    testEnclosure(NestedMethodEnclosure.enclosure1)("NestedMethodEnclosure")
  }

}
