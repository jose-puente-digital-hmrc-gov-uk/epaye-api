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
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.in._
import uk.gov.hmrc.epayeapi.models.out._

object JsonFixtures {
  object annualStatements {
    lazy val annualStatement: String = getJsonData("epaye/annual-statement/annual-statement.json")
  }

  def getJsonData(fname: String): String =
    scala.io.Source.fromURL(getClass.getResource(s"/${fname}"), "utf-8").mkString("")

  val baseUrl = "/organisations/paye"

  def baseUrlFor(empRef: EmpRef): String =
    s"$baseUrl/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}"

  def emptyAnnualStatementJsonWith(empRef: EmpRef, taxYear: TaxYear): AnnualStatementJson =
    AnnualStatementJson(
      taxYear = PeriodJson(taxYear.firstDay, taxYear.lastDay),
      _embedded = EmbeddedRtiChargesJson(Seq()),
      nonRtiCharges = Seq(),
      summary = SummaryJson(
        rtiCharges = ChargesSummaryJson(
          amount = 0,
          clearedByCredits = 0,
          clearedByPayments = 0,
          balance = 0
        ),
        nonRtiCharges = ChargesSummaryJson(
          amount = 0,
          clearedByCredits = 0,
          clearedByPayments = 0,
          balance = 0
        ),
        unallocated = PaymentsAndCreditsJson(0, 0)
      ),
      _links = AnnualStatementLinksJson(
        empRefs = Link(baseUrl),
        statements = Link(s"${baseUrlFor(empRef)}/statements"),
        self = Link(s"${baseUrlFor(empRef)}/statements/${taxYear.asString}"),
        next = Link(s"{${baseUrlFor(empRef)}/statements/${taxYear.next.asString}"),
        previous = Link(s"${baseUrlFor(empRef)}/statements/${taxYear.previous.asString}")
      )
    )

  val emptyEpayeAnnualStatement =
    EpayeAnnualStatement(
      rti = AnnualStatementTable(
        lineItems = Seq(),
        totals = AnnualTotal(
          charges = DebitAndCredit(0, 0),
          cleared = Cleared(0, 0),
          balance = DebitAndCredit(0, 0)
        )
      ),
      nonRti = AnnualStatementTable(
        lineItems = Seq(),
        totals = AnnualTotal(
          charges = DebitAndCredit(0, 0),
          cleared = Cleared(0),
          balance = DebitAndCredit(0, 0)
        )
      )
    )

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

  lazy val expectedOutput =
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
