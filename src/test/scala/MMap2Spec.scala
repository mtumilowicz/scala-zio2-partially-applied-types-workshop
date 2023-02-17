import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object MMap2Spec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("a")(
    test("b") {
      val mmap = new MMap2()

      mmap.putUser(User(UserId("1")))

      assertTrue(mmap.getUser(UserId("1")) == Right(User(UserId("1"))))
    },
    test("bb") {
      val mmap = new MMap2()

      assertTrue(mmap.getUser(UserId("1")) == Left("No collection for root: users"))
    },
    test("bbb") {
      val mmap = new MMap2()

      mmap.putUser(User(UserId("1")))

      assertTrue(mmap.getUser(UserId("2")) == Left("No entity for id: 2"))
    }
  )
}
