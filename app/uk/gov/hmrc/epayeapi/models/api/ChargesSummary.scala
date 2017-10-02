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

case class TaxYear(
  start_year: Int,
  end_year: Int
)

case class DebitCredit(
  debit: BigDecimal,
  credit: BigDecimal
)

case class RtiCharge(
  tax_year: TaxYear,
  tax_month: Option[Int],
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
