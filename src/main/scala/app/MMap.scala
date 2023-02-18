package app

import cats.Show
import cats.implicits.showInterpolator
import io.circe.syntax.EncoderOps
import io.circe.{Codec, Decoder, Encoder, Json}

import scala.collection.mutable

sealed trait Abc[T <: Entity, K <: EntityId] {
  def codec: Codec[T]
  def show: Show[K]

}

object Abc {
  implicit val userAbc = new Abc[User, UserId] {
    override def codec: Codec[User] = User.codec

    override def show: Show[UserId] = UserId.show
  }

  implicit val accountAbc = new Abc[Account, AccountId] {
    override def codec: Codec[Account] = Account.codec

    override def show: Show[AccountId] = AccountId.show
  }
}


class MMap() {
  val map = mutable.Map[EntityId, Json]()

  def put[T <: Entity : Encoder](entity: T): Unit = {
    map.put(entity.id, entity.asJson)
  }

  def get[T <: Entity : Decoder, K <: EntityId : Show](id: K): Either[String, T] =
    map.get(id) match {
      case Some(json) => parse(json)
      case None => Left(show"No entity for id: $id")
    }

  def get2[T <: Entity, K <: EntityId : Show](id: K)(implicit ev: Abc[T, K]): Either[String, T] =
    get(id)(ev.codec, ev.show)

  private def parse[T <: Entity : Decoder, K <: EntityId](json: Json): Either[String, T] =
    json.as[T] match {
      case Left(error) => Left(show"Cannot parse json: $error")
      case Right(value) => Right(value)
    }
}