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

package uk.gov.hmrc.epayeapi.controllers

import akka.stream.Materializer
import akka.util.ByteString
import org.mockito.Matchers._
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, fixture}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.streams.Accumulator
import play.api.mvc.Result
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment, EnrolmentIdentifier}
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import unit.AppSpec
import unit.auth.AuthComponents.AuthOk

import scala.concurrent.Future
import scala.concurrent.Future.successful

class GetTotalsSpec extends AppSpec with BeforeAndAfterEach {

  "The Totals endpoint" should {
    "return 200 OK on active enrolments" in new Setup {
      when(http.GET[HttpResponse](anyString)(anyObject(), anyObject())).thenReturn {
        successful {
          HttpResponse(200, responseString = Some(""" {"credit": 100, "debit": 0} """))
        }
      }
      status(request) shouldBe OK
    }
    "return 403 Forbidden on inactive enrolments" in new Setup {
      status(request) shouldBe FORBIDDEN
    }
    "return 403 Forbidden with different enrolments" in new Setup {
      status(request) shouldBe FORBIDDEN
    }
  }

  trait Setup extends fixture.NoArg {
    val ton = EnrolmentIdentifier("TaxOfficeNumber", "840")
    val tor = EnrolmentIdentifier("TaxOfficeReference", "GZ00064")
    val empRef = EmpRef(ton.value, tor.value)
    val http: WSHttp = mock[WSHttp]

    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit lazy val app: Application = new GuiceApplicationBuilder().overrides(bind(classOf[WSHttp]).toInstance(http)).build()

    val activeEnrolment =
      AuthOk(Enrolment("IR-PAYE", Seq(ton, tor), "activated", ConfidenceLevel.L300))

    val inactiveEnrolment =
      AuthOk(activeEnrolment.data.copy(state = "inactive"))

    val differentEnrolment =
      AuthOk(Enrolment("IR-Else", Seq(ton, tor), "activated", ConfidenceLevel.L300))

    def request(implicit a: Application): Future[Result] =
      inject[GetTotals].getTotals(empRef)(FakeRequest()).run()(
        inject[Materializer]
      )

    override def apply(): Unit = {
      def callSuper = super.apply()
      Helpers.running(app)(callSuper)
    }
  }
}
