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
      val summary = ChargesSummary(
        rti = Seq(
          Fixtures.rtiCharge(
            taxYear = api.TaxYear(2016, 2017),
            taxMonth = Some(3),
            debit = 1200,
            dueDate = Some(new LocalDate(2016, 10, 22)),
            isOverdue = true
          ),
          Fixtures.rtiCharge(
            taxYear = api.TaxYear(2016, 2017),
            taxMonth = Some(4),
            debit = 1300.0,
            dueDate = Some(new LocalDate(2018, 11, 22)),
            isOverdue = false
          ),
          Fixtures.rtiCharge(
            taxYear = api.TaxYear(2016, 2017),
            taxMonth = None,
            debit = 1200.0,
            dueDate = Some(new LocalDate(2016, 10, 22)),
            isOverdue = true
          )
        ),
        non_rti = Seq(
          Fixtures.nonRtiCharge(
            chargeCode = "NON_RTI_SPECIFIED_CHARGE",
            taxYear = api.TaxYear(2016, 2017),
            debit = 1200,
            dueDate = Some(new LocalDate(2016, 10, 22)),
            isOverdue = true
          ),
          Fixtures.nonRtiCharge(
            chargeCode = "NON_RTI_SPECIFIED_CHARGE",
            taxYear = api.TaxYear(2016, 2017),
            credit = 800,
            dueDate = Some(new LocalDate(2018, 10, 22)),
            isOverdue = false
          )
        )
      )
      val summaryJson = Json.toJson(summary)
      val exampleJson = Json.parse {
        val resource = getClass.getResource(s"/public/api/conf/1.0/examples/ChargesSummary.get.json")
        Source.fromURL(resource, "utf-8").mkString("")
      }

      summaryJson shouldEqual exampleJson
      exampleJson.validate[ChargesSummary] shouldEqual JsSuccess(summary)
    }
  }
}
