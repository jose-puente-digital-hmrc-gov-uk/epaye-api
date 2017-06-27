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

package uk.gov.hmrc.epayeapi.connectors

import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.api.ApiSuccess
import uk.gov.hmrc.epayeapi.models.domain.AggregatedTotals
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpResponse}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful

class EpayeConnectorSpec extends UnitSpec with MockitoSugar with ScalaFutures {

  trait Setup {
    implicit val hc = HeaderCarrier()
    val http = mock[WSHttp]
    val config = EpayeApiConfig("https://EPAYE")
    val connector = EpayeConnector(config, http, global)
    val empRef = EmpRef("123", "456")
    val url = s"${config.baseUrl}/epaye/${empRef.encodedValue}/api/v1/totals"
  }

  "EpayeConnector" should {
    "retrieve the total credit and debit for a given empRef" in new Setup {
      when(connector.http.GET(url)).thenReturn {
        successful {
          HttpResponse(Status.OK, responseString = Some(""" {"credit": 100, "debit": 0} """))
        }
      }

      connector.getTotals(empRef, hc).futureValue shouldBe
        ApiSuccess(AggregatedTotals(credit = BigDecimal(100), debit = BigDecimal(0)))
    }
  }
}
