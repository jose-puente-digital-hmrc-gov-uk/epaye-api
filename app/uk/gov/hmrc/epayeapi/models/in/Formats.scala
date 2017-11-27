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

package uk.gov.hmrc.epayeapi.models.in

import play.api.libs.json.Json.format
import play.api.libs.json._

trait Formats {
  implicit lazy val taxYearFormat: Format[TaxYear] = format[TaxYear]
  implicit lazy val debitAndCreditFormat: Format[DebitAndCredit] = format[DebitAndCredit]
  implicit lazy val clearedFormat: Format[Cleared] = format[Cleared]
  implicit lazy val annualTotalFormat: Format[AnnualTotal] = format[AnnualTotal]
  implicit lazy val taxMonthFormat: Format[TaxMonth] = format[TaxMonth]
  implicit lazy val lineItemFormat: Format[LineItem] = format[LineItem]
  implicit lazy val annualSummaryFormat: Format[AnnualStatementTable] = format[AnnualStatementTable]
  implicit lazy val annualSummaryResponseFormat: Format[EpayeAnnualStatement] = format[EpayeAnnualStatement]

  implicit lazy val epayeTotals: Format[EpayeTotals] = format[EpayeTotals]
  implicit lazy val epayeTotalsItems: Format[EpayeTotalsItem] = format[EpayeTotalsItem]
  implicit lazy val epayeTotalsResponse: Format[EpayeTotalsResponse] = format[EpayeTotalsResponse]
}

object Formats extends Formats
