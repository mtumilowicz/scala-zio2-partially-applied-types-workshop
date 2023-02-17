import cats.Show
import cats.implicits.showInterpolator
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax.EncoderOps
import io.circe.{Codec, Decoder, Encoder, Json}

import scala.collection.mutable

sealed trait EntityId

case class UserId(raw: String) extends EntityId

object UserId {
  implicit val codec: Codec[UserId] = deriveCodec[UserId]
  implicit val show: Show[UserId] = Show.show(_.raw)
}

case class AccountId(raw: String) extends EntityId

object AccountId {
  implicit val codec: Codec[AccountId] = deriveCodec[AccountId]
  implicit val show: Show[AccountId] = Show.show(_.raw)
}

sealed trait Entity {
  val id: EntityId
}

case class User(id: UserId) extends Entity

object User {
  implicit val codec: Codec[User] = deriveCodec[User]
}

case class Account(id: AccountId) extends Entity

object Account {
  implicit val codec: Codec[Account] = deriveCodec[Account]
}

class MMap() {
  val map = mutable.Map[String, mutable.Map[EntityId, Json]]()

  def put[T <: Entity : Encoder](rootKey: String, entity: T): Unit = {
    map.getOrElseUpdate(rootKey, mutable.Map()).put(entity.id, entity.asJson)
  }

  def get[T <: Entity : Decoder, K <: EntityId : Show](rootKey: String, entityId: K): Either[String, T] =
    map.get(rootKey) match {
      case Some(jsonMap) => get(jsonMap, entityId)
      case None => Left(show"No collection for root: $rootKey")
    }

  private def get[T <: Entity : Decoder, K <: EntityId : Show](jsonMap: mutable.Map[EntityId, Json], id: K): Either[String, T] =
    jsonMap.get(id) match {
      case Some(json) => parse(json)
      case None => Left(show"No entity for id: $id")
    }

  private def parse[T <: Entity : Decoder](json: Json): Either[String, T] =
    json.as[T] match {
      case Left(error) => Left(show"Cannot parse json: $error")
      case Right(value) => Right(value)
    }
}
