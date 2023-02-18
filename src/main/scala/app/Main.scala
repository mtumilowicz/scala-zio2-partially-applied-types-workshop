package app

import io.github.gaelrenoux.tranzactio.doobie.Connection
import io.github.gaelrenoux.tranzactio.{DatabaseOps, DbException}
import zio.Console.printLine
import zio._

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

  def a[C : Tag, R, E, A](zio: ZIO[R, E, A]): ZIO[R with DatabaseOps.ServiceOps[C], Either[DbException, E], A] = for {
    db <- ZIO.service[DatabaseOps.ServiceOps[C]]
    result <- db.transaction[R, E, A](zio)
  } yield result

  def a2[C] = new InDbTransactionPartiallyApplied[C]

  val l5 = a2[Connection](ZIO.succeed(5))

  final class InDbTransactionPartiallyApplied[C] {
    def apply[R, E, A](zio: ZIO[R, E, A])(implicit tag: Tag[C]): ZIO[R with DatabaseOps.ServiceOps[C], Either[DbException, E], A] = for {
      db <- ZIO.service[DatabaseOps.ServiceOps[C]]
      result <- db.transaction[R, E, A](zio)
    } yield result
  }


}