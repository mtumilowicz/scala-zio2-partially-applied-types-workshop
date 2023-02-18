package app

import app.custom.{HelloService, ZIOCustom}
import zio._

import java.io.IOException

object Main extends ZIOAppDefault {

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    r4

  val r = ZIO.serviceWithZIO[HelloService](_.say("Welcome to your first ZIO app!"))
    .provideLayer(HelloService.inMemory)
  val r2 = ZIOCustom.serviceWithZIO[HelloService, Any, IOException, Unit](_.say("Welcome to your first ZIO app!"))
    .provideLayer(HelloService.inMemory)
  val r3 = ZIOCustom.serviceWithZIO((_: HelloService).say("Welcome to your first ZIO app!"))
    .provideLayer(HelloService.inMemory)
  val r4 = ZIOCustom.serviceWithZIO2[HelloService](_.say("Welcome to your first ZIO app!"))
    .provideLayer(HelloService.inMemory)

}