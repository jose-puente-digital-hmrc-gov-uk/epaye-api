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
import uk.gov.hmrc.epayeapi.models.in._
import uk.gov.hmrc.epayeapi.models.{TaxMonth, TaxYear}

case class MonthlyStatementJson(
  taxOfficeNumber: String,
  taxOfficeReference: String,
  taxYear: TaxYear,
  taxMonth: TaxMonth,
  rtiCharges: Seq[ChargeJson],
  interest: Seq[ChargeJson],
  allocatedCredits: Seq[ChargeJson],
  allocatedPayments: Seq[PaymentJson],
  dueDate: LocalDate,
  summary: MonthlySummaryJson,
  _links: MonthlyStatementLinksJson
)

case class ChargeJson(
  code: String,
  amount: BigDecimal
)

case class PaymentJson(
  paymentDate: LocalDate,
  amount: BigDecimal
)

case class MonthlySummaryJson(
  amount: BigDecimal,
  interest: BigDecimal,
  clearedByCredits: BigDecimal,
  clearedByPayments: BigDecimal,
  balance: BigDecimal
)

case class MonthlyStatementLinksJson(
  empRefs: Link,
  statements: Link,
  annualStatement: Link,
  self: Link,
  next: Link,
  previous: Link
)

object MonthlyStatementJson {
  def apply(empRef: EmpRef, taxYear: TaxYear, taxMonth: TaxMonth, json: EpayeMonthlyStatement): MonthlyStatementJson =
    MonthlyStatementJson(
      taxOfficeNumber = empRef.taxOfficeNumber,
      taxOfficeReference = empRef.taxOfficeReference,
      taxYear = taxYear,
      taxMonth = taxMonth,
      rtiCharges = Charges(json.charges.fps) ++ Charges(json.charges.cis) ++ Charges(json.charges.eps),
      interest = if (json.charges.others == 0) Seq.empty else Seq(ChargeJson("INTEREST", json.charges.others)),
      allocatedCredits = Charges(json.credits.fps) ++ Charges(json.credits.cis) ++ Charges(json.credits.eps),
      allocatedPayments = Payments(json.payments),
      dueDate = json.balance.dueDate,
      summary = MonthlySummaryJson(json),
      _links = MonthlyStatementLinksJson(empRef, taxYear, taxMonth)
    )
}

object Charges {
  def apply(details: EpayeMonthlyChargesDetails): Seq[ChargeJson] =
    details.items.map { ChargeJson.apply }
}

object ChargeJson {
  def apply(item: EpayeMonthlyStatementItem): ChargeJson =
    new ChargeJson(item.code.name, item.amount)
}

object Payments {
  def apply(payments: EpayeMonthlyPaymentDetails): Seq[PaymentJson] =
    payments.items.map { PaymentJson.apply }
}

object PaymentJson {
  def apply(item: EpayeMonthlyPaymentItem): PaymentJson =
    new PaymentJson(item.dateOfPayment, item.amount)
}

object MonthlySummaryJson {
  def apply(json: EpayeMonthlyStatement): MonthlySummaryJson =
    MonthlySummaryJson(
      json.charges.total - json.charges.others,
      json.charges.others,
      json.credits.total,
      json.payments.total,
      json.balance.total
    )
}

object MonthlyStatementLinksJson {
  def apply(empRef: EmpRef, taxYear: TaxYear, taxMonth: TaxMonth): MonthlyStatementLinksJson =
    MonthlyStatementLinksJson(
      empRefs =
        Link.empRefsLink(),
      statements =
        Link.summaryLink(empRef),
      annualStatement =
        Link.anualStatementLink(empRef, taxYear),
      self =
        Link.monthlyStatementLink(empRef, taxYear, taxMonth),
      next =
        Link.monthlyStatementLink(empRef, if (taxMonth.isLast) taxYear.next else taxYear, taxMonth.next),
      previous =
        Link.monthlyStatementLink(empRef, if (taxMonth.isFirst) taxYear.previous else taxYear, taxMonth.previous)
    )
}
