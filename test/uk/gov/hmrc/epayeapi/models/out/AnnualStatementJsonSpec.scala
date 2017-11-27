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

package uk.gov.hmrc.epayeapi.models.out

import org.joda.time.LocalDate
import org.scalatest.WordSpec
import uk.gov.hmrc.epayeapi.models.in._

class AnnualStatementJsonSpec extends WordSpec {
  "AnnualStatementJson.apply" should {
    "convert a EpayeAnnualStatement object to a AnnualStatementJson object" in {
      val input =
        EpayeAnnualStatement(
          rti = AnnualStatementTable(
            lineItems = Seq(
              LineItem(
                taxYear = TaxYear(2016),
                // What if this is an Earlier Year Update?
                taxMonth = Some(TaxMonth(1)),
                charges = DebitAndCredit(debit = 10, credit = 20),
                cleared = Cleared(payment = 20, credit = 23),
                balance = DebitAndCredit(debit = 10, credit = 20),
                dueDate = new LocalDate(2016, 5, 22),
                // What if the month has specified charges?
                isSpecified = true,
                codeText = None
              )
            ),
            totals = AnnualTotal(
              charges = DebitAndCredit(23, 44),
              cleared = Cleared(13, 14),
              balance = DebitAndCredit(12, 23)
            )
          ),
          nonRti = AnnualStatementTable(
            lineItems = Seq(
              LineItem(
                taxYear = TaxYear(2016),
                taxMonth = None,
                charges = DebitAndCredit(23, 123),
                cleared = Cleared(124),
                balance = DebitAndCredit(13, 13),
                dueDate = new LocalDate(2016, 5, 22),
                isSpecified = false,
                codeText = Some("I am the walrus")
              )
            ),
            totals = AnnualTotal(
              charges = DebitAndCredit(131, 23),
              cleared = Cleared(3125),
              balance = DebitAndCredit(126, 3127)
            )
          )
        )

      val expectedOutput =
        AnnualStatementJson(
          taxYear = PeriodJson(new LocalDate(2017, 2, 3), new LocalDate(2017, 2, 3)),
          _embedded = EmbeddedRtiChargesJson(
            Seq(
              RtiChargesJson(
                taxMonth = TaxMonthJson(134, new LocalDate(2017, 2, 3), new LocalDate(2017, 2, 3)),
                amount = 135,
                clearedByCredits = 14141,
                clearedByPayments = 1414,
                balance = 136,
                dueDate = new LocalDate(),
                _links = SelfLinks(Link("wefwefw"))
              )
            )
          ),
          nonRtiCharges = Seq(
            NonRtiChargesJson(
              code = "Roger",
              taxPeriod = PeriodJson(new LocalDate(2017, 2, 3), new LocalDate(2017, 2, 3)),
              amount = 128,
              clearedByCredits = 124,
              clearedByPayments = 245,
              balance = 45,
              dueDate = new LocalDate(2017, 4, 23)
            )
          ),
          summary = SummaryJson(
            rtiCharges = ChargesSummaryJson(
              amount = ???,
              clearedByCredits = ???,
              clearedByPayments = ???,
              balance = ???
            ),
            nonRtiCharges = ChargesSummaryJson(
              amount = 129,
              clearedByCredits = 130,
              clearedByPayments = 131,
              balance = 132
            ),
            unallocated = PaymentsAndCreditsJson(133, 41414)
          ),
          _links = AnnualStatementLinksJson(
            empRefs = Link("wdw"),
            statements = Link("adw"),
            self = Link("wdbv"),
            next = Link("wddv"),
            previous = Link("errdw")
          )
        )
    }
  }
}

