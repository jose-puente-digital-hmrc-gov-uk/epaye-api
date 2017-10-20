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

import org.joda.time.LocalDate
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.epayeapi.Fixtures
import uk.gov.hmrc.epayeapi.models.Formats._
import uk.gov.hmrc.epayeapi.models.api.ChargesSummary

import scala.io.Source

class ChargesSummarySpec extends WordSpec with Matchers {
  "charges summary object" should {
    "convert to json" in {
      val taxYear = TaxYear(2016)
      val summary = ChargesSummary(
        rti = Seq(
          Fixtures.rtiCharge(
            taxYear = api.TaxYear.fromTaxYear(taxYear),
            taxMonth = Some(api.TaxMonth.fromTaxMonth(TaxMonth(3), taxYear)),
            debit = 1200,
            dueDate = Some(new LocalDate(2016, 10, 22)),
            isOverdue = true
          ),
          Fixtures.rtiCharge(
            taxYear = api.TaxYear.fromTaxYear(taxYear),
            taxMonth = Some(api.TaxMonth.fromTaxMonth(TaxMonth(4), taxYear)),
            debit = 1300.0,
            dueDate = Some(new LocalDate(2016, 11, 22)),
            isOverdue = true
          ),
          Fixtures.rtiCharge(
            taxYear = api.TaxYear.fromTaxYear(taxYear),
            taxMonth = Some(api.TaxMonth.fromTaxMonth(TaxMonth(5), taxYear)),
            debit = 1200.0,
            dueDate = Some(new LocalDate(2016, 10, 22)),
            isOverdue = true
          )
        ),
        non_rti = Seq(
          Fixtures.nonRtiCharge(
            chargeCode = "NON_RTI_IN_YEAR_PAYE_LATE_FILING_PENALTY",
            taxYear = api.TaxYear.fromTaxYear(taxYear),
            debit = 1200,
            dueDate = Some(new LocalDate(2016, 10, 22)),
            isOverdue = true
          ),
          Fixtures.nonRtiCharge(
            chargeCode = "NON_RTI_EOY_NIC1",
            taxYear = api.TaxYear.fromTaxYear(taxYear),
            credit = 800,
            dueDate = Some(new LocalDate(2016, 10, 22)),
            isOverdue = true
          )
        )
      )
      val summaryJson = Json.toJson(summary)
      val exampleJson = Json.parse {
        val resource = getClass.getResource(s"/public/api/conf/1.0/examples/ChargesSummary.get.json")
        Source.fromURL(resource, "utf-8").mkString("")
      }

      Json.prettyPrint(summaryJson).toString shouldEqual Json.prettyPrint(exampleJson).toString
      exampleJson.validate[ChargesSummary] shouldEqual JsSuccess(summary)
    }
  }
}
