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
import uk.gov.hmrc.epayeapi.models.in.{EpayeAnnualStatement, TaxYear}

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

case class SummaryJson(
  rtiCharges: ChargesSummaryJson,
  nonRtiCharges: ChargesSummaryJson,
  unallocated: PaymentsAndCreditsJson
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
  _links: SelfLinks
)
case class TaxMonthJson(
  number: Int,
  firstDay: LocalDate,
  lastDay: LocalDate
)
case class SelfLinks(
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

}