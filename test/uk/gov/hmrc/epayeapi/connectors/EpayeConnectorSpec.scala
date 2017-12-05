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

import common.EmpRefGenerator
import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.in._
import uk.gov.hmrc.epayeapi.models.{JsonFixtures, TaxYear}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful
import scala.concurrent.duration._

class EpayeConnectorSpec extends UnitSpec with MockitoSugar with ScalaFutures {

  trait Setup {
    implicit val hc = HeaderCarrier()
    val http = mock[WSHttp]
    val config = EpayeApiConfig("https://EPAYE")
    val connector = EpayeConnector(config, http, global)
    val empRef = EmpRefGenerator.getEmpRef
    val urlTotals = s"${config.baseUrl}/epaye/${empRef.encodedValue}/api/v1/annual-statement"
    val urlTotalsByType = s"${config.baseUrl}/epaye/${empRef.encodedValue}/api/v1/totals/by-type"
    def urlAnnualStatement(taxYear: TaxYear): String =
      s"${config.baseUrl}/epaye/${empRef.encodedValue}/api/v1/annual-statement/${taxYear.asString}"
  }

  "EpayeConnector" should {
    "retrieve the total credit and debit for a given empRef" in new Setup {
      when(connector.http.GET(urlTotals)).thenReturn {
        successful {
          HttpResponse(Status.OK, responseString = Some(
            """
              |{
              |  "rti": {
              |    "totals": {
              |      "balance": {
              |        "debit": 100,
              |        "credit": 0
              |      }
              |    }
              |  },
              |  "nonRti": {
              |    "totals": {
              |      "balance": {
              |        "debit": 23,
              |        "credit": 0
              |      }
              |    }
              |  }
              |}
            """.stripMargin
          ))
        }
      }

      Await.result(connector.getTotal(empRef, hc), 2.seconds) shouldBe
        EpayeSuccess(
          EpayeTotalsResponse(
            EpayeTotalsItem(EpayeTotals(DebitAndCredit(100, 0))),
            EpayeTotalsItem(EpayeTotals(DebitAndCredit(23, 0)))
          )
        )
    }

    "retrieve summary for a given empRef" in new Setup {
      val taxYear = TaxYear(2016)

      when(connector.http.GET(urlAnnualStatement(taxYear))).thenReturn {
        successful {
          HttpResponse(Status.OK, responseString = Some(JsonFixtures.annualStatements.annualStatement))
        }
      }

      connector.getAnnualStatement(empRef, taxYear, hc).futureValue shouldBe
        EpayeSuccess(
          EpayeAnnualStatement(
            rti = AnnualStatementTable(
              List(LineItem(TaxYear(2017), Some(EpayeTaxMonth(1)), DebitAndCredit(100.2, 0), Cleared(0, 0), DebitAndCredit(100.2, 0), new LocalDate(2017, 5, 22), isSpecified = false, codeText = None)),
              AnnualTotal(DebitAndCredit(100.2, 0), Cleared(0, 0), DebitAndCredit(100.2, 0))
            ),
            nonRti = AnnualStatementTable(
              List(LineItem(TaxYear(2017), None, DebitAndCredit(20.0, 0), Cleared(0, 0), DebitAndCredit(20.0, 0), new LocalDate(2018, 2, 22), false, Some("P11D_CLASS_1A_CHARGE"))),
              AnnualTotal(DebitAndCredit(20.0, 0), Cleared(0, 0), DebitAndCredit(20.0, 0))
            ),
            unallocated = None
          )
        )
    }
  }

}
