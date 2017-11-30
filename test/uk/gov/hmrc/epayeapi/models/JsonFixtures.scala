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

import org.joda.time.LocalDate
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.in._
import uk.gov.hmrc.epayeapi.models.out._

object JsonFixtures {
  object annualStatements {
    lazy val annualStatement: String = getJsonData("epaye/annual-statement/annual-statement.json")
  }

  def getJsonData(fname: String): String =
    scala.io.Source.fromURL(getClass.getResource(s"/${fname}"), "utf-8").mkString("")

  val baseUrl = "/organisations/paye"

  def baseUrlFor(empRef: EmpRef): String =
    s"$baseUrl/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}"

  def emptyAnnualStatementJsonWith(empRef: EmpRef, taxYear: TaxYear): AnnualStatementJson =
    AnnualStatementJson(
      taxYear = TaxYearJson(taxYear.asString, taxYear.firstDay, taxYear.lastDay),
      _embedded = EmbeddedRtiChargesJson(None, Seq()),
      nonRtiCharges = Seq(),
      _links = AnnualStatementLinksJson(
        empRefs = Link(baseUrl),
        statements = Link(s"${baseUrlFor(empRef)}/statements"),
        self = Link(s"${baseUrlFor(empRef)}/statements/${taxYear.asString}"),
        next = Link(s"{${baseUrlFor(empRef)}/statements/${taxYear.next.asString}"),
        previous = Link(s"${baseUrlFor(empRef)}/statements/${taxYear.previous.asString}")
      )
    )

  val emptyEpayeAnnualStatement =
    EpayeAnnualStatement(
      rti = AnnualStatementTable(
        lineItems = Seq(),
        totals = AnnualTotal(
          charges = DebitAndCredit(0, 0),
          cleared = Cleared(0, 0),
          balance = DebitAndCredit(0, 0)
        )
      ),
      nonRti = AnnualStatementTable(
        lineItems = Seq(),
        totals = AnnualTotal(
          charges = DebitAndCredit(0, 0),
          cleared = Cleared(0),
          balance = DebitAndCredit(0, 0)
        )
      ),
      unallocated = None
    )
}
