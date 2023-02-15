import zio._
import zio.Console.printLine

import java.io.IOException

object Main extends ZIOAppDefault {

  class HelloService {
    def say(msg: String): IO[IOException, Unit] = printLine(msg)
  }

  object HelloService {
    val inMemory = ZLayer.succeed(new HelloService)
  }

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    ZIO.serviceWithZIO[HelloService](_.say("Welcome to your first ZIO app!"))
      .provideLayer(HelloService.inMemory)
}