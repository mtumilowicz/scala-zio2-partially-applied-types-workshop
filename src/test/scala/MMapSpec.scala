import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object MMapSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("a")(
    test("b") {
      val mmap = new MMap()

      mmap.put("users", User(UserId("1")))

      assertTrue(mmap.get[User]("users", UserId("1")) == Right(User(UserId("1"))))
    },
    test("bb") {
      val mmap = new MMap()

      assertTrue(mmap.get[User]("users", UserId("1")) == Left("No collection for users"))
    },
    test("bbb") {
      val mmap = new MMap()

      mmap.put("users", User(UserId("1")))

      assertTrue(mmap.get[User]("users", UserId("2")) == Left("No entity for: UserId(2)"))
    }
  )
}
