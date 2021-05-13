# enclosure

Simple macro to generate an `case class Enclosure(fullModuleName: String)` data-type telling you in which module it was instantiated in. Think, `log4s` or `log4cats` logger names based on the class/object/trait they were created in, but generalized and useable outside of that context.

Honestly, the library should be pretty stable and will most likely change only to support new Scala versions. But what do I know?

## getting started

This library is published for Scala `3.0.0`, `2.13`, `2.12`, both on the JVM and JS platforms.

```scala
libraryDependencies ++= "com.lorandszakacs" %% "enclosure" % "0.1.2"
```

## usage

This library provides one single type: `com.lorandszakacs.enclosure.Enclosure` which simply contains the fully qualified name of the enclosing module. Where module is either one of:

- class
- object
- package (object)

Simply add an implicit parameter to your methods/classes, and a macro will generate a value for `Enclosure` for you.

Similar in usage to [`munit.Location`](https://github.com/scalameta/munit/blob/main/munit/shared/src/main/scala/munit/Location.scala), or [`org.tpolecat.SourcePos`](https://github.com/tpolecat/SourcePos).

```scala
package myapp
import com.lorandszakacs.enclosure.Enclosure

object Printer {
  def locatedPrintln(s: String)(implicit enc: Enclosure): Unit =  {
    println(s"[${enc.fullModuleName}] $s")
  }
}

// -------
package myapp.module

object Main extends App {
  myapp.Printer.locatedPrintln("in main!")
  // prints out:
  // [myapp.module.Main] in main!
  nestedMethod()
  // idem

  def nestedMethod(): Unit = {
    myapp.Printer.locatedPrintln("in main!")
  }
}
```

## motivation

The library was created to generalize, and make available to end-users, the `log4s`, `log4cats` logger name from enclosing class behavior. But it can obviously find itself other usages now as well.
