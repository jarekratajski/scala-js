package org.scalajs.sbtplugin

import org.scalajs.linker.ModuleInitializer
import org.scalajs.linker.ModuleInitializer.{MainMethodWithArgs, VoidMainMethod}
import sjsonnew.LList.:*:
import sjsonnew._


object JSONSerializer {
  import BasicJsonProtocol._
  implicit val vmmIso = LList.iso(
    { vmm: VoidMainMethod => ("VoidMainMethod", vmm.moduleClassName) :*:
        ("encodedMainMethodName", vmm.encodedMainMethodName) :*: LNil },
    { case (_, moduleClassName) :*: (_, encodedMainMethodName) :*: LNil =>
        VoidMainMethod(moduleClassName, encodedMainMethodName) })

  implicit val vmmIso = LList.iso(
    { vmm: MainMethodWithArgs => ("MainMethodWithArgs", vmm.moduleClassName) :*:
      ("encodedMainMethodName", vmm.encodedMainMethodName) :*:
      ("args", vmm.args) :*: LNil },
    { case (_, moduleClassName) :*: (_, encodedMainMethodName) :*:  (_, args) :*: LNil =>
      MainMethodWithArgs(moduleClassName, encodedMainMethodName, args) })

  implicit val  moduleInitializerFormat =  flatUnionFormat2(ModuleInitializer, VoidMainMethod, MainMethodWithArgs)

}
