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

package uk.gov.hmrc.epayeapi.services

import org.joda.time.LocalDate
import uk.gov.hmrc.epayeapi.models.{AnnualSummary, AnnualSummaryResponse}
import uk.gov.hmrc.epayeapi.models.api.{ChargesSummary, DebitCredit, NonRtiCharge, RtiCharge}

object ChargesSummaryService {

  def toChargesSummary(annualSummaryResponse: AnnualSummaryResponse): ChargesSummary = {
    ChargesSummary(
      rti = toChargesSummaryRti(annualSummaryResponse.rti),
      non_rti = toChargesSummaryNonRti(annualSummaryResponse.nonRti)
    )
  }

  def toChargesSummaryRti(annualSummary: AnnualSummary): Seq[RtiCharge] = {
    annualSummary.lineItems.map(lineItem =>
      RtiCharge(
        `type` = lineItem.bla,
        tax_year = lineItem.taxYear,
        tax_month = lineItem.taxMonth,
        balance = DebitCredit(lineItem.charges.debit, lineItem.charges.credit),
        due_date = lineItem.dueDate,
        overdue = false // TODO
      ))
  }

  def toChargesSummaryNonRti(annualSummary: AnnualSummary): Seq[NonRtiCharge] = {
    annualSummary.lineItems.map(lineItem =>
      NonRtiCharge(
        charge_code = lineItem.bla,
        tax_year = lineItem.taxYear,
        balance = DebitCredit(lineItem.charges.debit, lineItem.charges.credit),
        due_date = lineItem.dueDate,
        overdue = false // TODO
      ))
  }

}
