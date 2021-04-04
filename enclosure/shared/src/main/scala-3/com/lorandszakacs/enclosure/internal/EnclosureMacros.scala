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

import scala.annotation.tailrec
import scala.quoted.*

trait EnclosureMacros {
  implicit inline def generateEnclosure: Enclosure = ${EnclosureMacros.enclosure_impl}
}

object EnclosureMacros {

  def enclosure_impl(using ctx: Quotes): Expr[Enclosure] = {
    val helpers  = new EnclosingModuleHelpers(using ctx)
    val moduleName = Expr(helpers.getFullyQualifiedNameOfEnclosingModule)
    '{Enclosure($moduleName)}
  }

  private final class EnclosingModuleHelpers(using ctx: Quotes) {
    import ctx.reflect.*
    /** Where "module" is a:
      * - class
      * - object
      * - package object
      */
    def getFullyQualifiedNameOfEnclosingModule: String = {
      val moduleSym: Symbol = findEnclosingModule(enclosingOwner)
      fullNameFromSymbol(moduleSym)
    }

    @tailrec private def findEnclosingModule(sym: Symbol): Symbol = {
      sym match {
        case s if s.isNoSymbol                     =>
          report.throwError("Couldn't find an enclosing class or module for the logger")
        case s if s.isClassDef => s
        case other                        =>
          //TODO: gather all methods along the way so that we can fill in that information as well
          findEnclosingModule(other.owner)
      }
    }

    private def enclosingOwner: Symbol = Symbol.spliceOwner

    @tailrec private def fullNameFromSymbol(sym: Symbol): String = {
      val flags = sym.flags
      if (flags.is(Flags.Package)) {
        sym.fullName
      }
      else if (sym.isClassDef) {
        if (flags.is(Flags.Module)) {
          if (sym.name == "package$") {
            fullNameFromSymbol(s.owner)
          }
          else {
            val chomped = sym.name.stripSuffix("$")
            fullNameFromSymbol(sym.owner) + "." + chomped
          }
        }
        else {
          fullNameFromSymbol(sym.owner) + "." + sym.name
        }
      }
      else {
        fullNameFromSymbol(sym.owner)
      }
    }
  }
}
