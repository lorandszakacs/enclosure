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

import munit.FunSuite
import com.lorandszakacs.enclosure.Enclosure

class EnclosureTestSuite extends FunSuite {

//---------------------------------------------------------------------------

  protected lazy val currentPackage: String = "com.lorandszakacs.enclosure.testing"

  protected def testEnclosure(enc: Enclosure)(expParam: String)(implicit loc: munit.Location): Unit = {
    val expected = s"$currentPackage.$expParam"
    testEnclosureFullyQualified(enc)(expected)
  }

  protected def testEnclosureFullyQualified(enc: Enclosure)(expected: String)(implicit loc: munit.Location): Unit = {
    assertEquals(
      obtained = enc.fullModuleName,
      expected = expected,
      clue = s"""|+++++++++++++++++++++++++++++++++++
                     |
                     |      expected = $expected     
                     |      received = ${enc.fullModuleName}
                     |
                     |------------------------------------
                     |""".stripMargin
    )
  }
}
