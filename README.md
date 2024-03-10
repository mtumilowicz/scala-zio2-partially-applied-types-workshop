[![Build Status](https://app.travis-ci.com/mtumilowicz/scala-zio2-partially-applied-types-workshop.svg?branch=master)](https://app.travis-ci.com/mtumilowicz/scala-zio2-partially-applied-types-workshop)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

# scala-zio2-partially-applied-types-workshop
* references
    * [Zymposium - ZIO API Design Techniques](https://www.youtube.com/watch?v=48fpPffgnMo)
    * https://circe.github.io/circe/codec.html
    * https://www.baeldung.com/scala/circe-json
    * https://typelevel.org/cats/typeclasses/show.html

## preface
* goals of this workshop
* workshop plan
    1. implement `ZIOCustom.serviceWithZIO` to infer `R`, `E`, `A` types
        * it may be valuable to first take a look at: https://github.com/mtumilowicz/scala-cats-functional-dependency-injection-workshop#izumi-tag
    1. implement `TransactionService.inTransaction` to infer `R`, `E`, `A` types

## partially applied arguments 
* good analogy to set intuition on partially applied types
* example
    * problem
        ```
        def map[A, B](list: List[A], f: A => B): List[B] =
          list.map(f)

        val nums = List(1,2,3)

        mapping function(nums, _ + 1) // not compiling
        mapping function(nums, (i: Int) => i + 1) // compiling
        ```
    * solution - partial application of arguments
        ```
        def map[A, B](list: List[A])(f: A => B): List[B] =
          list.map(f)

        val nums = List(1,2,3)

        mapping function(nums)(_ + 1)
        ```

## partially applied types
* in scala, you can specify either all types or none params
* example
    * problem
        ```
        def serviceWithZIO[Service: Tag, R, E, A](f: Service => ZIO[R, E, A]): ZIO[R with Service, E, A] =
          ZIO.service[Service].flatMap(service => f(service))
        ```
        * `Service` and `R`, `E`, `A` are not connected - `Service` has to be specified, rest can be inferred
    * solution - partial application of types
        ```
        def serviceWithZIO2[Service] =
          new ServiceWithZIOPartiallyApplied[Service]

        final class ServiceWithZIOPartiallyApplied[Service]() {
          def apply[R, E, A](f: Service => ZIO[R, E, A])(implicit tag: Tag[Service]): ZIO[R with Service, E, A] =
            ZIO.service[Service].flatMap(service => f(service)) // tag is needed by ZIO.service[Service]
        }
        ```
* usually used for wrappers, where we have groups of unconnected types
* performance
    * to keep it at zero cost, we have to use 'AnyVal'
        ```
        final class ServiceWithZIOPartiallyApplied[Service](private val dummy: Boolean = true) extends AnyVal {
            ...
        }
        ```
