/*
 * Scala.js (https://www.scala-js.org/)
 *
 * Copyright EPFL.
 *
 * Licensed under Apache License 2.0
 * (https://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package org.scalajs.sbtplugin

import org.scalajs.linker.ModuleInitializer
import org.scalajs.linker.ModuleInitializer.VoidMainMethod
import org.scalajs.linker.ModuleInitializer.MainMethodWithArgs
import sbinary.{Format, Input, Output}
import sbt.Cache

object BinarySerializers {

  import sbinary.StandardPrimitives

  private val voidMainMethodFormat = Cache.tuple2Format(Cache.StringFormat, Cache.StringFormat)
  private val mainMethodWithArgsFormat = Cache.tuple3Format(Cache.StringFormat, Cache.StringFormat, Cache.listFormat(Cache.StringFormat))

  implicit object ModuleInitializerFormat extends Format[ModuleInitializer] {
    override def reads(in: Input): ModuleInitializer = {
      val typeName = Cache.StringFormat.reads(in)
      typeName match {
        case "VoidMainMethod" => VoidMainMethod.tupled(voidMainMethodFormat.reads(in))
        case "MainMethodWithArgs" => MainMethodWithArgs.tupled(mainMethodWithArgsFormat.reads(in))
      }
    }

    override def writes(out: Output, value: ModuleInitializer): Unit = {
      value match {
        case a: VoidMainMethod =>
          Cache.StringFormat.writes(out, "VoidMainMethod")
          voidMainMethodFormat.writes(out, VoidMainMethod.unapply(a).get)
        case b: MainMethodWithArgs =>
          Cache.StringFormat.writes(out, "MainMethodWithArgs")
          mainMethodWithArgsFormat.writes(out, MainMethodWithArgs.unapply(b).get)
      }
    }
  }

  implicit object ModulesInitializerFormat extends Format[Seq[ModuleInitializer]] {
    override def reads(in: Input): Seq[ModuleInitializer] = Cache.listFormat(ModuleInitializerFormat).reads(in)

    override def writes(out: Output, value: Seq[ModuleInitializer]): Unit = Cache.listFormat(ModuleInitializerFormat)
      .writes(out, value.toList)
  }
}