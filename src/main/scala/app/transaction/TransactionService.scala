package app.transaction

import app.custom.{HelloService, ZIOCustom}
import io.github.gaelrenoux.tranzactio.doobie.Connection
import io.github.gaelrenoux.tranzactio.{DatabaseOps, DbException}
import zio._

object DbTransactionService {

  def inTransaction[C: Tag, R, E, A](zio: ZIO[R, E, A]): ZIO[R with DatabaseOps.ServiceOps[C], Either[DbException, E], A] = for {
    db <- ZIO.service[DatabaseOps.ServiceOps[C]]
    result <- db.transaction[R, E, A](zio)
  } yield result

  def inTransaction2[C] = new InDbTransactionPartiallyApplied[C]

  val l5 = inTransaction2[Connection](ZIO.succeed(5))

  final class InDbTransactionPartiallyApplied[C] {
    def apply[R, E, A](zio: ZIO[R, E, A])(implicit tag: Tag[C]): ZIO[R with DatabaseOps.ServiceOps[C], Either[DbException, E], A] = for {
      db <- ZIO.service[DatabaseOps.ServiceOps[C]]
      result <- db.transaction[R, E, A](zio)
    } yield result
  }

}
