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

package com.lorandszakacs.enclosure.internal

import com.lorandszakacs.enclosure.Enclosure

import scala.quoted.*

trait EnclosureMacros {
  implicit inline def generateEnclosure: Enclosure = ${EnclosureMacros.enclosure_impl}
}

object EnclosureMacros {
    def enclosure_impl(using ctx: Quotes): Expr[Enclosure] = {
    val rootPosition = ctx.reflect.Position.ofMacroExpansion
    val file = Expr(rootPosition.sourceFile.jpath.toString)
     //WIP: definitely wrong
    '{Enclosure($file)}
  }
}
