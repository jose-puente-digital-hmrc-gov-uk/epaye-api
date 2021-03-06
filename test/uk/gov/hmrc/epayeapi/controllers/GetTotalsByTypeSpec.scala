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

import org.mockito.Matchers._
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment, EnrolmentIdentifier}
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.Formats._
import uk.gov.hmrc.epayeapi.models.TotalsByTypeResponse
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import unit.AppSpec
import unit.auth.AuthComponents.AuthOk
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.Future.successful

class GetTotalsByTypeSpec extends AppSpec with BeforeAndAfterEach {
  val ton = EnrolmentIdentifier("TaxOfficeNumber", "840")
  val tor = EnrolmentIdentifier("TaxOfficeReference", "GZ00064")
  val empRef = EmpRef(ton.value, tor.value)

  implicit val hc = HeaderCarrier()
  val http = mock[WSHttp]

  val app = builder
    .update(_.overrides(bind(classOf[WSHttp]).toInstance(http)))

  val activeEnrolment =
    AuthOk(Enrolment("IR-PAYE", Seq(ton, tor), "activated", ConfidenceLevel.L300))

  val inactiveEnrolment =
    AuthOk(activeEnrolment.data.copy(state = "inactive"))

  val differentEnrolment =
    AuthOk(Enrolment("IR-Else", Seq(ton, tor), "activated", ConfidenceLevel.L300))

  def request(implicit a: Application): Future[Result] =
    inject[GetTotalsByTypeController].getTotalsByType(empRef)(FakeRequest())

  override protected def beforeEach(): FixtureParam = {
    reset(http)
  }

  "The Totals By Type endpoint" should {
    "return 200 OK on active enrolments" in new App(app.withAuth(activeEnrolment).build) {
      when(http.GET[HttpResponse](anyString)(anyObject(), anyObject())).thenReturn {
        successful {
          HttpResponse(200, responseString = Some(""" { "rti": {"credit": 100, "debit": 0}, "nonRti": {"credit": 0, "debit": 100} } """))
        }
      }
      contentAsString(request) shouldBe Json.parse(
        s"""
          |{"rti":{"credit":100,"debit":0},
          |"non_rti":{"credit":0,"debit":100},
          |"_links":{
          |"empRefs": {"href":"/organisation/paye/"},
          |"self":{"href":"/organisation/paye/${ton.value}/${tor.value}/"}}}
        """.stripMargin).toString
      status(request) shouldBe OK
    }
    "return 500 Internal Server Error and error message body when incorrect json format" in new App(app.withAuth(activeEnrolment).build) {
      when(http.GET[HttpResponse](anyString)(anyObject(), anyObject())).thenReturn {
        successful {
          HttpResponse(200, responseString = Some(""" { "rtix": {"credit": 100, "debit": 0} } """))
        }
      }
      contentAsString(request) shouldBe
        """{"code":"INTERNAL_SERVER_ERROR","message":"We are currently experiencing problems. Please try again later."}"""
      status(request) shouldBe INTERNAL_SERVER_ERROR
    }
    "return 403 Forbidden on inactive enrolments" in new App(app.withAuth(inactiveEnrolment).build) {
      status(request) shouldBe FORBIDDEN
    }
    "return 403 Forbidden with different enrolments" in new App(app.withAuth(differentEnrolment).build) {
      status(request) shouldBe FORBIDDEN
    }
  }

}
