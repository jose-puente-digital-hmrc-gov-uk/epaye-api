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

import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models._
import uk.gov.hmrc.epayeapi.models.api.ApiSuccess
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful

class EpayeConnectorSpec extends UnitSpec with MockitoSugar with ScalaFutures {

  trait Setup {
    implicit val hc = HeaderCarrier()
    val http = mock[WSHttp]
    val config = EpayeApiConfig("https://EPAYE")
    val connector = EpayeConnector(config, http, global)
    val empRef = EmpRef("123", "456")
    val urlTotals = s"${config.baseUrl}/epaye/${empRef.encodedValue}/api/v1/annual-statement"
    val urlTotalsByType = s"${config.baseUrl}/epaye/${empRef.encodedValue}/api/v1/totals/by-type"
    val urlAnnualStatement = s"${config.baseUrl}/epaye/${empRef.encodedValue}/api/v1/annual-statement"
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

      connector.getTotal(empRef, hc).futureValue shouldBe
        ApiSuccess(
          EpayeTotalsResponse(
            EpayeTotalsItem(EpayeTotals(DebitAndCredit(100, 0))),
            EpayeTotalsItem(EpayeTotals(DebitAndCredit(23, 0)))
          )
        )
    }
    "retrieve the total by type for a given empRef" in new Setup {
      when(connector.http.GET(urlTotalsByType)).thenReturn {
        successful {
          HttpResponse(Status.OK, responseString = Some(""" { "rti": {"credit": 100, "debit": 0}, "nonRti": {"credit": 100, "debit": 0} } """))
        }
      }

      connector.getTotalsByType(empRef, hc).futureValue shouldBe
        ApiSuccess(AggregatedTotalsByType(
          rti = AggregatedTotals(credit = 100, debit = 0),
          nonRti = AggregatedTotals(credit = 100, debit = 0)
        ))
    }
    "retrieve summary for a given empRef" in new Setup {
      when(connector.http.GET(urlAnnualStatement)).thenReturn {
        successful {
          HttpResponse(Status.OK, responseString = Some(JsonFixtures.annualStatements.annualStatement))
        }
      }

      connector.getAnnualSummary(empRef, hc, None).futureValue shouldBe
        ApiSuccess(
          AnnualSummaryResponse(
            AnnualSummary(
              List(LineItem(TaxYear(2017), Some(TaxMonth(1)), DebitAndCredit(100.2, 0), Cleared(0, 0), DebitAndCredit(100.2, 0), new LocalDate(2017, 5, 22), isSpecified = false, codeText = None)),
              AnnualTotal(DebitAndCredit(100.2, 0), Cleared(0, 0), DebitAndCredit(100.2, 0))
            ),
            AnnualSummary(
              List(LineItem(TaxYear(2017), None, DebitAndCredit(20.0, 0), Cleared(0, 0), DebitAndCredit(20.0, 0), new LocalDate(2018, 2, 22), false, Some("P11D_CLASS_1A_CHARGE"))),
              AnnualTotal(DebitAndCredit(20.0, 0), Cleared(0, 0), DebitAndCredit(20.0, 0))
            )
          )
        )
    }
  }

}
