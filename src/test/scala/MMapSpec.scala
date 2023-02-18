import User.codec
import Account.codec
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}
object MMapSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("a")(
    test("b") {
      val mmap = new MMap()

      mmap.put(User(UserId("1")))

      assertTrue(mmap.get[User, UserId](UserId("1")) == Right(User(UserId("1"))))
    },
    test("bbb") {
      val mmap = new MMap()

      mmap.put(User(UserId("1")))

      assertTrue(mmap.get[User, UserId](UserId("2")) == Left("No entity for id: 2"))
    },
    test("c") {
      val mmap = new MMap()

      mmap.put(User(UserId("1")))

      assertTrue(mmap.get2[User, UserId, String](UserId("1")) == Right(User(UserId("1"))))
    },
    test("cc") {
      val mmap = new MMap()

      mmap.put(User(UserId("1")))

      assertTrue(mmap.get2[User, UserId, String](UserId("2")) == Left("No entity for id: 2"))
    },
    test("d") {
      val mmap = new MMap()

      mmap.put(Account(AccountId("1")))

      assertTrue(mmap.get2[Account, AccountId, String](AccountId("1")) == Right(Account(AccountId("1"))))
    },
    test("dd") {
      val mmap = new MMap()

      mmap.put(Account(AccountId("1")))

      assertTrue(mmap.get2[Account, AccountId, String](AccountId("2")) == Left("No entity for id: 2"))
    }
  )
}