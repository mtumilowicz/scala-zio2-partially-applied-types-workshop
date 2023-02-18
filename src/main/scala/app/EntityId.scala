package app

import cats.Show
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

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