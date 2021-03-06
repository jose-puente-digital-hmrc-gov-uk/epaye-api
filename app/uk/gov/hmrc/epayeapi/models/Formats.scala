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

import play.api.libs.json._
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.api.{ChargesSummary, DebitCredit, NonRtiCharge, RtiCharge}

trait Formats {
  implicit val empRefFormat: Writes[EmpRef] = new Writes[EmpRef] {
    override def writes(o: EmpRef): JsValue = JsString(s"${o.taxOfficeNumber}/${o.taxOfficeReference}")
  }
  implicit val linkFormat: Format[Link] = Json.format[Link]
  implicit val empRefsLinksFormat: Format[EmpRefsLinks] = Json.format[EmpRefsLinks]
  implicit val empRefLinksFormat: Format[EmpRefLinks]   = Json.format[EmpRefLinks]
  implicit val empRefItemFormat: Format[EmpRefItem]     = Json.format[EmpRefItem]
  implicit val empRefsResponseFormat: Format[EmpRefsResponse] = Json.format[EmpRefsResponse]
  implicit val apiErrorFormat: Format[ApiError] = Json.format[ApiError]
  implicit val aggregatedTotalsFormat: Format[AggregatedTotals] = Json.format[AggregatedTotals]
  implicit val aggregatedTotalsByTypeFormat: Format[AggregatedTotalsByType] = Json.format[AggregatedTotalsByType]
  implicit val breakdown: Format[Breakdown] = Json.format[Breakdown]
  implicit val outstandingCharges: Format[OutstandingCharges] = Json.format[OutstandingCharges]
  implicit val totalsLinksFormat: Format[SummaryLinks] = Json.format[SummaryLinks]
  implicit val totalsResponseFormat: Format[SummaryResponse] = Json.format[SummaryResponse]
  implicit val totalsByTypeResponseFormat: Format[TotalsByTypeResponse] = Json.format[TotalsByTypeResponse]
  implicit val debitAndCreditFormat: Format[DebitAndCredit] = Json.format[DebitAndCredit]
  implicit val clearedFormat: Format[Cleared] = Json.format[Cleared]
  implicit val annualTotalFormat: Format[AnnualTotal] = Json.format[AnnualTotal]
  implicit val taxYearFormat: Format[TaxYear] = Json.format[TaxYear]
  implicit val apiTaxYearFormat: Format[api.TaxYear] = Json.format[api.TaxYear]
  implicit val taxMonthFormat: Format[TaxMonth] = Json.format[TaxMonth]
  implicit val apiTaxMonthFormat: Format[api.TaxMonth] = Json.format[api.TaxMonth]
  implicit val lineItemFormat: Format[LineItem] = Json.format[LineItem]
  implicit val annualSummaryFormat: Format[AnnualSummary] = Json.format[AnnualSummary]
  implicit val annualSummaryResponseFormat: Format[AnnualSummaryResponse] = Json.format[AnnualSummaryResponse]

  implicit val debitCreditFormat: Format[DebitCredit] = Json.format[DebitCredit]
  implicit val rtiChargeFormat: Format[RtiCharge] = Json.format[RtiCharge]
  implicit val nonRtiChargeFormat: Format[NonRtiCharge] = Json.format[NonRtiCharge]
  implicit val chargesSummaryFormat: Format[ChargesSummary] = Json.format[ChargesSummary]
  implicit val epayeTotals: Format[EpayeTotals] = Json.format[EpayeTotals]
  implicit val epayeTotalsItems: Format[EpayeTotalsItem] = Json.format[EpayeTotalsItem]
  implicit val epayeTotalsResponse: Format[EpayeTotalsResponse] = Json.format[EpayeTotalsResponse]

}

object Formats extends Formats
