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
import play.api.libs.json.Json
import uk.gov.hmrc.epayeapi.models.api.{ChargesSummary, DebitCredit, NonRtiCharge, RtiCharge}
import uk.gov.hmrc.epayeapi.models.Formats._

import scala.io.Source

class ChargesSummarySpec extends WordSpec with Matchers {
  "charges summary object" should {
    "convert to json" in {
      val chargesSummary = ChargesSummary(
        rti = Seq(
          RtiCharge(
            `type` = "monthly",
            tax_year = 2016,
            tax_month = Some(3),
            balance = DebitCredit(debit = 1200.0, credit = 0),
            due_date = Some(new LocalDate(2016, 10, 22)),
            overdue = true
          ),
          RtiCharge(
            `type` = "monthly",
            tax_year = 2016,
            tax_month = Some(4),
            balance = DebitCredit(debit = 1300.0, credit = 0),
            due_date = Some(new LocalDate(2018, 11, 22)),
            overdue = false
          ),
          RtiCharge(
            `type` = "eyu",
            tax_year = 2016,
            tax_month = None,
            balance = DebitCredit(debit = 1200.0, credit = 0),
            due_date = Some(new LocalDate(2016, 10, 22)),
            overdue = true
          )
        ),
        non_rti = Seq(
          NonRtiCharge(
            charge_code = "NON_RTI_SPECIFIED_CHARGE",
            tax_year = 2016,
            balance = DebitCredit(debit = 1200.0, credit = 0),
            due_date = Some(new LocalDate(2016, 10, 22)),
            overdue = true
          ),
          NonRtiCharge(
            charge_code = "NON_RTI_SPECIFIED_CHARGE",
            tax_year = 2016,
            balance = DebitCredit(debit = 0.0, credit = 800),
            due_date = Some(new LocalDate(2018, 10, 22)),
            overdue = false
          )
        )
      )

      val validateResults = List(
        Json.toJson(chargesSummary),
        Json.parse(Source.fromURL(getClass.getResource(s"/public/api/conf/1.0/examples/ChargesSummary.get.json"), "utf-8").mkString(""))
      ).map(_.validate[ChargesSummary].get)
      validateResults.head shouldBe validateResults(1)
    }
  }
}