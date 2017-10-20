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

package uk.gov.hmrc.epayeapi


import org.joda.time.LocalDate
import uk.gov.hmrc.epayeapi.models.api._

object Fixtures {
  def rtiCharge(
    taxYear: TaxYear,
    taxMonth: Option[TaxMonth] = None,
    debit: BigDecimal = 0,
    credit: BigDecimal = 0,
    dueDate: Option[LocalDate] = None,
    isOverdue: Boolean = false
  ): RtiCharge = {
    RtiCharge(
      tax_year = taxYear,
      tax_month = taxMonth,
      balance = DebitCredit(debit = debit, credit = credit),
      due_date = dueDate,
      is_overdue = isOverdue
    )
  }

  def nonRtiCharge(
    chargeCode: String = "NON_RTI_SPECIFIED_CHARGE",
    taxYear: TaxYear = TaxYear.fromTaxYear(models.TaxYear(2016)),
    debit: BigDecimal = 0,
    credit: BigDecimal = 0,
    dueDate: Option[LocalDate] = None,
    isOverdue: Boolean = false
  ): NonRtiCharge = {
    NonRtiCharge(
      charge_code = chargeCode,
      tax_year = taxYear,
      balance = DebitCredit(debit = debit, credit = credit),
      due_date = dueDate,
      is_overdue = isOverdue
    )
  }
}
