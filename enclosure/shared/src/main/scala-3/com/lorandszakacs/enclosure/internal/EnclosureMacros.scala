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
      val moduleSym: Symbol = findEnclosingModule(Symbol.spliceOwner)
      fullNameFromSymbol(moduleSym)
    }

    @tailrec private def findEnclosingModule(sym: Symbol): Symbol = {
      sym match {
        case s if s.isNoSymbol                     =>
          report.throwError(
              s"Enclosure requires an enclosing class, object, or package. But couldn't find one. We are at: ${sym.toString()}"
            )
        case s if s.isClassDef || s.isPackageDef => s
        case other                        =>
          //TODO: gather all methods along the way so that we can fill in that information as well
          findEnclosingModule(other.owner)
      }
    }

    /**
     * In the vast majority of cases we can get a clean, '$' free names by
     * looking at the companion module.
     *
     * The exception cases are outlined below in the match statement at the
     * call site to this method.
     */
    private def fullNameFromCompanionModule(sym: Symbol): Option[String] = {
      if(sym.flags.is(Flags.Package)) {
        Option(sym.fullName)
      } else {
        val companionModule = sym.companionModule
        if (companionModule.isNoSymbol) {
          Option.empty
        } else {
          val companionModuleFN = companionModule.fullName
          if (companionModuleFN.contains("$")) Option.empty else Option(companionModuleFN)
        }
      }
    }

    /**
     * When the object name is taken from (sym: Symbol).companionModule.fullName,
     * it does not contain the dollar character, it simply ends in .package
     *
     * N.B. (sym: Symbol).isPackageDef -- does not return true in this case!
     */
    private def isPackageObject(fullNameFromCompanionModule: String): Boolean = {
      fullNameFromCompanionModule.endsWith(".package")
    }

    /**
     * Scala 3 top level definitions in packages always end like this.
     *
     * Scoured through the Quotes api using the debug method bellow,
     * and couldn't find any better way to determine this (except maybe)
     * the confluence of flags... which can show up in other places as well.
     *
     * N.B. (sym: Symbol).isPackageDef -- does not return true in this case!
     */
    private def isTopLevelDefinition(sym: Symbol): Boolean = {
      sym.fullName.endsWith("$package$")
    }

    /**
     * Top level traits without a companion object should have a clean,
     * dollar free name. If they have a companion object, 
     * then [[fullNameFromCompanionModule]] will return the proper name.
     */
    private def isTopLevelTrait(sym: Symbol): Boolean = {
      val flags = sym.flags
      flags.is(Flags.Trait) && !flags.is(Flags.Local)
    }

    /**
     * Absolutely last ditch effort is to recurse through each part of the
     * symbol and remove the dollar sign. I do not like this very much,
     * honestly, since it seems extremely hacky. But there is no other way
     * of dealing with nested classes/traits/objects that are nested
     * only within _other objects_.
     *
     * Technically the sym.noSymbol check should never happen,
     * but you never know :shrug:, better to not accidentally recurse
     * infinitely becase sym.owner will happily return no symbol ad infinitum.
     */
    private def cleanObjectDollars(sym: Symbol): String = {
      if(sym.isNoSymbol) {
        reportBug(sym, what = "noSymbol", returnedValue = "")
        ""
      } else
      if(sym.flags.is(Flags.Package))
        sym.fullName
      else 
        s"${cleanObjectDollars(sym.owner)}.${sym.name.stripSuffix("$")}"
    }

    @tailrec private def fullNameFromSymbol(sym: Symbol): String = {
      fullNameFromCompanionModule(sym) match {
        case Some(name) => if (isPackageObject(name)) fullNameFromSymbol(sym.owner) else name
        case _ if isTopLevelDefinition(sym) => fullNameFromSymbol(sym.owner)
        case _ if isTopLevelTrait(sym) => sym.fullName
        case _  => 
          /* we are in the case of nested objects/classes/traits in other objects,
           * and we have to recurse to remove all the $ signs along the way
           */
          cleanObjectDollars(sym)
      }
    }
    
    private def reportBug(sym: Symbol, what: String, returnedValue: String): Unit = {
      report.warning(
        s"""|Enclosure generation encountered an unknown situation.
            |
            |This is bug, please report it with the following information:
            |
            |      https://github.com/lorandszakacs/enclosure/issues
            |
            |situation       = $what
            |symbol          = ${sym.toString}
            |symbolName      = ${sym.name}
            |symbolFullName  = ${sym.fullName}
            |flags           = ${sym.flags.show}
            |
            |We are still returning the following value instead of erroring out:
            |$returnedValue
            |
            |""".stripMargin
      )
    }

    @scala.annotation.nowarn
    private def debug(sym: Symbol, where: String): Unit = {
      report.warning(
        s"""|+++++++++++++++++++++++++++++++++++
            |           - This is a debug warning, should never be displayed in prod -
            |           - please file an issue if you ever see this while using the library
            |
            |      where = $where
            |
            |
            |      sym.toString     = ${sym.toString}
            |
            |      sym.flags    = ${sym.flags.show}
            |     
            |      sym.name     = ${sym.name}
            |      sym.fullName = ${sym.fullName}
            |      
            |      sym.owner          = ${sym.owner}
            |      sym.owner.fullName = ${sym.owner.fullName}
            |
            |      sym.moduleClass          = ${sym.moduleClass}
            |      sym.moduleClass.fullName = ${sym.moduleClass.fullName}
            |
            |      sym.isClassConstructor = ${sym.isClassConstructor}
            |      sym.isAnonymousClass   = ${sym.isAnonymousClass}
            |      sym.isClassDef         = ${sym.isClassDef}
            |      sym.isPackageDef       = ${sym.isPackageDef} 
            |
            |      sym.companionClass     = ${sym.companionClass}
            |      sym.companionModule    = ${sym.companionModule}
            |      sym.companionModule.fn = ${sym.companionModule.fullName}
            |      sym.companionModule.ip = ${sym.companionModule.isPackageDef}
            |
            |------------------------------------
            |
            |
            |
            """.stripMargin
      )
    } // end debug

  }

}
