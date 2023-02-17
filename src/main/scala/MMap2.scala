import cats.Show
import cats.implicits.showInterpolator
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax.EncoderOps
import io.circe.{Codec, Decoder, Encoder, Json}

import scala.collection.mutable

class MMap2() {
  val map = mutable.Map[String, mutable.Map[EntityId, Json]]()

  def putUser(entity: User): Unit = put[User](MMapKey.users, entity)

  def putAccount(entity: Account): Unit = put[Account](MMapKey.accounts, entity)

  def getUser(entityId: UserId): Either[String, User] = get[User, UserId](MMapKey.users, entityId)

  def getAccount(entityId: AccountId): Either[String, Account] = get[Account, AccountId](MMapKey.accounts, entityId)

  private def put[T <: Entity : Encoder](rootKey: String, entity: T): Unit = {
    map.getOrElseUpdate(rootKey, mutable.Map()).put(entity.id, entity.asJson)
  }

  private def get[T <: Entity : Decoder, K <: EntityId : Show](rootKey: String, entityId: K): Either[String, T] =
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
