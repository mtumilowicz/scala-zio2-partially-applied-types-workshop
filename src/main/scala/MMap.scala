import cats.Show
import cats.implicits.showInterpolator
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

import scala.collection.mutable

sealed trait Abc[T <: Entity, K <: EntityId, L] {
  def decode(json: Json)(implicit ev: Decoder[T]): Either[String, T]
  def showKey: Show[K]
  def something: L
}

object Abc {
  implicit val userAbc = new Abc[User, UserId, String] {

    override def decode(json: Json)(implicit ev: Decoder[User]): Either[String, User] = ev.decodeJson(json) match {
      case Left(error) => Left(show"Cannot parse json: $error")
      case Right(value) => Right(value)
    }

    override def showKey: Show[UserId] = UserId.show

    override def something: String = "test"
  }

  implicit val accountAbc = new Abc[Account, AccountId, String] {

    override def decode(json: Json)(implicit ev: Decoder[Account]): Either[String, Account] = ev.decodeJson(json) match {
      case Left(error) => Left(show"Cannot parse json: $error")
      case Right(value) => Right(value)
    }

    override def showKey: Show[AccountId] = AccountId.show

    override def something: String = "test"
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

  def get2[T <: Entity : Decoder, K <: EntityId : Show, S](id: K)(implicit ev: Abc[T, K, S]): Either[String, T] =
    map.get(id) match {
      case Some(json) => ev.decode(json)
      case None => Left(show"No entity for id: $id")
    }

  private def parse[T <: Entity : Decoder, K <: EntityId](json: Json): Either[String, T] =
    json.as[T] match {
      case Left(error) => Left(show"Cannot parse json: $error")
      case Right(value) => Right(value)
    }
}