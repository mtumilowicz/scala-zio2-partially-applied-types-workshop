import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax.EncoderOps
import io.circe.{Codec, Decoder, Encoder, Json}

import scala.collection.mutable

sealed trait EntityId

case class UserId(raw: String) extends EntityId

object UserId {
  implicit val codec: Codec[UserId] = deriveCodec[UserId]
}

case class AccountId(raw: String) extends EntityId

object AccountId {
  implicit val codec: Codec[AccountId] = deriveCodec[AccountId]
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

  def get[T <: Entity : Decoder](rootKey: String, entityId: EntityId): Either[String, T] =
    map.get(rootKey) match {
      case Some(jsonMap) => get(jsonMap, entityId)
      case None => Left(s"No collection for $rootKey")
    }

  private def get[T <: Entity : Decoder](jsonMap: mutable.Map[EntityId, Json], id: EntityId): Either[String, T] =
    jsonMap.get(id) match {
      case Some(json) => parse(json)
      case None => Left(s"No entity for: $id")
    }

  private def parse[T <: Entity : Decoder](json: Json): Either[String, T] =
    json.as[T] match {
      case Left(error) => Left(s"Cannot parse json: $error")
      case Right(value) => Right(value)
    }
}
