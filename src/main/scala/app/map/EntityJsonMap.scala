package app.map

import cats.Show
import cats.implicits.showInterpolator
import io.circe.syntax.EncoderOps
import io.circe.{Codec, Decoder, Encoder, Json}

import scala.collection.mutable

sealed trait EntityIdBinding[T <: Entity, K <: EntityId] {
  def entityCodec: Codec[T]
  def entityIdShow: Show[K]

}

object EntityIdBinding {
  implicit val userIdBinding = new EntityIdBinding[User, UserId] {
    override def entityCodec: Codec[User] = User.codec

    override def entityIdShow: Show[UserId] = UserId.show
  }

  implicit val accountIdBinding = new EntityIdBinding[Account, AccountId] {
    override def entityCodec: Codec[Account] = Account.codec

    override def entityIdShow: Show[AccountId] = AccountId.show
  }
}


class EntityJsonMap() {
  val map = mutable.Map[EntityId, Json]()

  def put[T <: Entity : Encoder](entity: T): Unit = {
    map.put(entity.id, entity.asJson)
  }

  def getUnsafe[T <: Entity : Decoder, K <: EntityId : Show](id: K): Either[String, T] =
    map.get(id) match {
      case Some(json) => parse(json)
      case None => Left(show"No entity for id: $id")
    }

  def getSafe[T <: Entity, K <: EntityId : Show](id: K)(implicit ev: EntityIdBinding[T, K]): Either[String, T] =
    getUnsafe(id)(ev.entityCodec, ev.entityIdShow)

  private def parse[T <: Entity : Decoder, K <: EntityId](json: Json): Either[String, T] =
    json.as[T] match {
      case Left(error) => Left(show"Cannot parse json: $error")
      case Right(value) => Right(value)
    }
}