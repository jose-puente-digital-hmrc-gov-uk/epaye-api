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

import uk.gov.hmrc.domain.EmpRef

case class Breakdown(
  rti: BigDecimal,
  nonRti: BigDecimal
)

case class OutstandingCharges(
  amount: BigDecimal,
  breakdown: Breakdown
)

case class SummaryResponse(
  outstandingCharges: OutstandingCharges,
  _links: SummaryLinks
)

object SummaryResponse {
  def apply(empRef: EmpRef, totals: AggregatedTotals, byType: AggregatedTotalsByType): SummaryResponse =
    SummaryResponse(
      OutstandingCharges(
        totals.debit,
        Breakdown(
          byType.rti.debit,
          byType.nonRti.debit
        )
      ),
      SummaryLinks(empRef)
    )
}

case class TotalsByTypeResponse(
  rti: AggregatedTotals,
  non_rti: AggregatedTotals,
  _links: SummaryLinks
)

object TotalsByTypeResponse {
  def apply(empRef: EmpRef, totals: AggregatedTotalsByType): TotalsByTypeResponse =
    TotalsByTypeResponse(
      AggregatedTotals(totals.rti.credit, totals.rti.debit),
      AggregatedTotals(totals.nonRti.credit, totals.nonRti.debit),
      SummaryLinks(empRef)
    )
}

case class SummaryLinks(
  empRefs: Link,
  self: Link
)

object SummaryLinks {
  def apply(empRef: EmpRef): SummaryLinks = SummaryLinks(
    Link.empRefsLink(),
    Link.summaryLink(empRef)
  )
}

