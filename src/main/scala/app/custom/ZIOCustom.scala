package app.custom

import io.github.gaelrenoux.tranzactio.doobie.Connection
import io.github.gaelrenoux.tranzactio.{DatabaseOps, DbException}
import zio.Console.printLine
import zio._

object ZIOCustom {

  def serviceWithZIO[Service: Tag, R, E, A](f: Service => ZIO[R, E, A]): ZIO[R with Service, E, A] =
    ZIO.service[Service].flatMap(service => f(service))

  def serviceWithZIO2[Service] =
    new ServiceWithZIOPartiallyApplied[Service]

  final class ServiceWithZIOPartiallyApplied[Service]() {
    def apply[R, E, A](f: Service => ZIO[R, E, A])(implicit tag: Tag[Service]): ZIO[R with Service, E, A] =
      ZIO.service[Service].flatMap(service => f(service))
  }

}
