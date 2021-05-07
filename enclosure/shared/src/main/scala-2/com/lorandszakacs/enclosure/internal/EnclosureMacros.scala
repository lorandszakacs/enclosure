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
import scala.reflect.macros.blackbox

trait EnclosureMacros {
  implicit def generateEnclosure: Enclosure = macro EnclosureMacros.enclosure_impl
}

object EnclosureMacros {

  def enclosure_impl(c: blackbox.Context): c.Expr[Enclosure] = {
    import c.universe._
    val helpers    = new EnclosingModuleHelpers(c)
    val moduleName = helpers.getFullyQualifiedNameOfEnclosingModule
    c.Expr[Enclosure](q"_root_.com.lorandszakacs.enclosure.Enclosure(${moduleName})")
  }

  private final class EnclosingModuleHelpers(val ctx: blackbox.Context) extends AnyVal {
    import ctx.universe._

    /** Where "module" is a:
      *   - class
      *   - object
      *   - package object
      */
    def getFullyQualifiedNameOfEnclosingModule: String = {
      val moduleSym: Symbol = findEnclosingModule(enclosingOwner)
      fullNameFromSymbol(moduleSym)
    }

    @tailrec private def findEnclosingModule(sym: Symbol): Symbol = {
      sym match {
        case NoSymbol                     =>
          ctx.abort(
            ctx.enclosingPosition,
            s"Enclosure requires an enclosing class, object, or package. But couldn't find one. We are at: ${sym.toString()}"
          )
        case c if c.isModule || c.isClass => c
        case other                        =>
          //TODO: gather all methods along the way so that we can fill in that information as well
          findEnclosingModule(other.owner)
      }
    }

    private def isPackageObject(sym: Symbol) = (
      (sym.isModule || sym.isModuleClass)
        && sym.owner.isPackage
        && sym.name.decodedName.toString == termNames.PACKAGE.decodedName.toString
    )

    /*
     * unfortunately there aren't many alternatives to using the internal API, but that's fine.
     * It will probably survive until EOL for Scala 2. In Scala 3 we don't have this issue.
     */
    private def enclosingOwner: Symbol = ctx.internal.enclosingOwner

    private def fullNameFromSymbol(sym: Symbol): String = {

      /* the full name of package objects includes ".package" at the end.
       * That's why we go one level up
       */
      if (isPackageObject(sym)) {
        if (sym.owner.eq(null)) {
          ctx.abort(
            ctx.enclosingPosition,
            s"We have a package object $sym, yet its owner is somehow null. This is most certainly something extremely peculiar. File an issue and reproduction please"
          )
        }
        else sym.owner.fullName
      }
      else {
        val classSymbol: ClassSymbol = (if (sym.isModule) sym.asModule.moduleClass else sym).asClass
        classSymbol.fullName
      }
    }
  }
}
