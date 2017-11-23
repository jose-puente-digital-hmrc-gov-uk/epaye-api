/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.epayeapi.models

object Example extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future
  import scala.concurrent.Await
  import scala.concurrent.duration.Duration
  import scala.util.{Success, Failure}

  sealed trait Resp
  case class Good(i: Int) extends Resp
  case class Bad(s: String) extends Resp

  object Foo {
    def run: Int = {
      val first: Future[Resp] = Future.successful(Good(1))
      val econd: Future[Resp] = Future.successful(Bad("nope"))


      val res = for {
        Good(one) <- first
        Good(two) <- econd
      } yield Good(one + two)

      //
      //    res.onComplete {
      //      case Success(res) =>
      //        println("OK")
      //        res
      //      case Failure(res) =>
      //        println(s"Fail: $res")
      //        res
      //    }

      val finalREsult: Resp = Await.result(res.recover {
        case ex: Exception =>
          println(ex)
          Bad("Exception handler")
      }, Duration.Inf)

      println(s"Final: $finalREsult")

      100
    }
  }

  Foo.run

}
