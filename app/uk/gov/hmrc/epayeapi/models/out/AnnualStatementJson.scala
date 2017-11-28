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
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.in.{EpayeAnnualStatement, LineItem, TaxYear}

case class PeriodJson(firstDay: LocalDate, lastDay: LocalDate)

case class NonRtiChargesJson(
  code: String,
  taxPeriod: PeriodJson,
  amount: BigDecimal,
  clearedByCredits: BigDecimal,
  clearedByPayments: BigDecimal,
  balance: BigDecimal,
  dueDate: LocalDate
)

object NonRtiChargesJson {
  def from(lineItem: LineItem, taxYear: TaxYear): Option[NonRtiChargesJson] = {
    for {
      code <- lineItem.codeText
    } yield NonRtiChargesJson(
      code = code,
      amount = lineItem.charges.debit,
      clearedByCredits = lineItem.cleared.credit,
      clearedByPayments = lineItem.cleared.payment,
      balance = lineItem.balance.debit,
      dueDate = lineItem.dueDate,
      taxPeriod = PeriodJson(taxYear.firstDay, taxYear.lastDay)
    )
  }
}

case class SummaryJson(
  rtiCharges: ChargesSummaryJson,
  nonRtiCharges: ChargesSummaryJson
)
case class ChargesSummaryJson(
  amount: BigDecimal,
  clearedByCredits: BigDecimal,
  clearedByPayments: BigDecimal,
  balance: BigDecimal
)
case class PaymentsAndCreditsJson(
  payments: BigDecimal,
  credits: BigDecimal
)

case class EmbeddedRtiChargesJson(rtiCharges: Seq[RtiChargesJson])

case class RtiChargesJson(
  taxMonth: TaxMonthJson,
  amount: BigDecimal,
  clearedByCredits: BigDecimal,
  clearedByPayments: BigDecimal,
  balance: BigDecimal,
  dueDate: LocalDate,
  _links: SelfLink
)

object RtiChargesJson {
  def from(lineItem: LineItem, empRef: EmpRef, taxYear: TaxYear): Option[RtiChargesJson] = {
    for {
      taxMonth <- lineItem.taxMonth
    } yield RtiChargesJson(
      taxMonth = TaxMonthJson(taxMonth.month, taxMonth.firstDay(taxYear), taxMonth.lastDay(taxYear)),
      amount = lineItem.charges.debit,
      clearedByCredits = lineItem.cleared.credit,
      clearedByPayments = lineItem.cleared.payment,
      balance = lineItem.balance.debit,
      dueDate = lineItem.dueDate,
      _links = SelfLink(Link(s"${AnnualStatementJson.baseUrlFor(empRef)}/statements/${taxYear.asString}"))
    )

  }
}

case class TaxMonthJson(
  number: Int,
  firstDay: LocalDate,
  lastDay: LocalDate
)
case class SelfLink(
  self: Link
)

case class AnnualStatementLinksJson(
  empRefs: Link,
  statements: Link,
  self: Link,
  next: Link,
  previous: Link
)

case class AnnualStatementJson(
  taxYear: PeriodJson,
  nonRtiCharges: Seq[NonRtiChargesJson],
  summary: SummaryJson,
  _embedded: EmbeddedRtiChargesJson,
  _links: AnnualStatementLinksJson
)

object AnnualStatementJson {
  val baseUrl = "/organisations/paye"

  def baseUrlFor(empRef: EmpRef): String =
    s"$baseUrl/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}"

  def apply(empRef: EmpRef, taxYear: TaxYear, epayeAnnualStatement: EpayeAnnualStatement): AnnualStatementJson =
    AnnualStatementJson(
      taxYear = PeriodJson(taxYear.firstDay, taxYear.lastDay),
      _embedded = EmbeddedRtiChargesJson(
        epayeAnnualStatement.rti.lineItems.flatMap(RtiChargesJson.from(_, empRef, taxYear))
      ),
      nonRtiCharges = epayeAnnualStatement.nonRti.lineItems.flatMap(NonRtiChargesJson.from(_, taxYear)),
      summary = SummaryJson(
        rtiCharges = ChargesSummaryJson(
          amount = epayeAnnualStatement.rti.totals.charges.debit,
          clearedByCredits = epayeAnnualStatement.rti.totals.cleared.credit,
          clearedByPayments = epayeAnnualStatement.rti.totals.cleared.payment,
          balance = epayeAnnualStatement.rti.totals.balance.debit - epayeAnnualStatement.rti.totals.balance.credit
        ),
        nonRtiCharges = ChargesSummaryJson(
          amount = epayeAnnualStatement.nonRti.totals.charges.debit,
          clearedByCredits = epayeAnnualStatement.nonRti.totals.cleared.credit,
          clearedByPayments = epayeAnnualStatement.nonRti.totals.cleared.payment,
          balance = epayeAnnualStatement.nonRti.totals.balance.debit - epayeAnnualStatement.nonRti.totals.balance.credit
        )
      ),
      _links = AnnualStatementLinksJson(
        empRefs = Link(baseUrl),
        statements = Link(s"${baseUrlFor(empRef)}/statements"),
        self = Link(s"${baseUrlFor(empRef)}/statements/${taxYear.asString}"),
        next = Link(s"{${baseUrlFor(empRef)}/statements/${taxYear.next.asString}"),
        previous = Link(s"${baseUrlFor(empRef)}/statements/${taxYear.previous.asString}")
      )
    )

}