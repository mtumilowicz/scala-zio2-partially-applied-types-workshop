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
    r4

  val r = ZIO.serviceWithZIO[HelloService](_.say("Welcome to your first ZIO app!"))
    .provideLayer(HelloService.inMemory)
  val r2 = serviceWithZIO[HelloService, Any, IOException, Unit](_.say("Welcome to your first ZIO app!"))
    .provideLayer(HelloService.inMemory)
  val r3 = serviceWithZIO((_: HelloService).say("Welcome to your first ZIO app!"))
    .provideLayer(HelloService.inMemory)
  val r4 = serviceWithZIO2[HelloService](_.say("Welcome to your first ZIO app!"))
    .provideLayer(HelloService.inMemory)
  def serviceWithZIO[Service : Tag, R, E, A](f: Service => ZIO[R, E, A]): ZIO[R with Service, E, A] =
    ZIO.service[Service].flatMap(service => f(service))

  def serviceWithZIO2[Service] =
    new ServiceWithZIOPartiallyApplied[Service]
  final class ServiceWithZIOPartiallyApplied[Service]() {
    def apply[R, E, A](f: Service => ZIO[R, E, A])(implicit tag: Tag[Service]): ZIO[R with Service, E, A] =
      ZIO.service[Service].flatMap(service => f(service))
  }
}