package app.custom

import zio.Console.printLine
import zio._

import java.io.IOException

  class HelloService {
    def say(msg: String): IO[IOException, Unit] = printLine(msg)
  }

  object HelloService {
    val inMemory = ZLayer.succeed(new HelloService)
  }