import cats.Show
import cats.implicits.showInterpolator
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax.EncoderOps
import io.circe.{Codec, Decoder, Encoder, Json}

import scala.collection.mutable


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

  private def parse[T <: Entity : Decoder](json: Json): Either[String, T] =
    json.as[T] match {
      case Left(error) => Left(show"Cannot parse json: $error")
      case Right(value) => Right(value)
    }
}