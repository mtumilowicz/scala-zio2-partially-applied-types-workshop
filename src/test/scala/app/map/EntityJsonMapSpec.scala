package app.map

import app.map.Account.codec
import app.map.AccountId.show
import app.map.EntityIdBinding._
import app.map.User.codec
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}
object EntityJsonMapSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("a")(
    test("b") {
      val jsonMap = new EntityJsonMap()

      jsonMap.put(User(UserId("1")))

      assertTrue(jsonMap.getUnsafe[User, UserId](UserId("1")) == Right(User(UserId("1"))))
    },
    test("bbb") {
      val jsonMap = new EntityJsonMap()

      jsonMap.put(User(UserId("1")))

      assertTrue(jsonMap.getUnsafe[User, UserId](UserId("2")) == Left("No entity for id: 2"))
    },
    test("c") {
      val jsonMap = new EntityJsonMap()

      jsonMap.put(User(UserId("1")))

      assertTrue(jsonMap.getUnsafe[User, UserId](UserId("1")) == Right(User(UserId("1"))))
    },
    test("cc") {
      val jsonMap = new EntityJsonMap()

      jsonMap.put(User(UserId("1")))

      assertTrue(jsonMap.getSafe(UserId("2")) == Left("No entity for id: 2"))
    },
    test("d") {
      val jsonMap = new EntityJsonMap()

      jsonMap.put(Account(AccountId("1")))

      assertTrue(jsonMap.getSafe(AccountId("1")) == Right(Account(AccountId("1"))))
    },
    test("dd") {
      val jsonMap = new EntityJsonMap()

      jsonMap.put(Account(AccountId("1")))

      assertTrue(jsonMap.getSafe(AccountId("2")) == Left("No entity for id: 2"))
    }
  )
}