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

package uk.gov.hmrc.epayeapi.models.api

import org.joda.time.LocalDate
import uk.gov.hmrc.epayeapi.models


case class TaxYear(
  year: String,
  first_day: LocalDate,
  last_day: LocalDate
)
object TaxYear {
  def fromTaxYear(taxYear: models.TaxYear): TaxYear = {
    TaxYear(taxYear.asString, taxYear.firstDay, taxYear.lastDay)
  }
}

case class TaxMonth(
  month: Int,
  first_day: LocalDate,
  last_day: LocalDate
)
object TaxMonth {
  def fromTaxMonth(taxMonth: models.TaxMonth, taxYear: models.TaxYear): TaxMonth = {
    TaxMonth(
      taxMonth.month,
      taxMonth.firstDay(taxYear),
      taxMonth.lastDay(taxYear)
    )
  }
}

case class DebitCredit(
  debit: BigDecimal,
  credit: BigDecimal
)

case class RtiCharge(
  tax_year: TaxYear,
  tax_month: Option[TaxMonth],
  balance: DebitCredit,
  due_date: Option[LocalDate],
  is_overdue: Boolean
)

case class NonRtiCharge(
  charge_code: String,
  tax_year: TaxYear,
  balance: DebitCredit,
  due_date: Option[LocalDate],
  is_overdue: Boolean
)

case class ChargesSummary(
  rti: Seq[RtiCharge],
  non_rti: Seq[NonRtiCharge]
)
