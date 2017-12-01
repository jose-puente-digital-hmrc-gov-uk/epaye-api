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
import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.JsonFixtures.{baseUrl, baseUrlFor, emptyAnnualStatementJsonWith, emptyEpayeAnnualStatement}
import uk.gov.hmrc.epayeapi.models.in._

class AnnualStatementJsonSpec extends WordSpec with Matchers {
  val empRef = EmpRef("123", "AB45678")
  val taxYear = TaxYear(2016)
  val taxMonth = TaxMonth(2)
  val dueDate = new LocalDate(2017, 5, 22)

  "AnnualStatementJson.apply.taxYear" should {
    "contain the right taxYear" in {
      AnnualStatementJson(empRef, taxYear, emptyEpayeAnnualStatement).taxYear shouldBe TaxYearJson(taxYear.asString, taxYear.firstDay, taxYear.lastDay)
    }
  }

  "AnnualStatementJson.apply._links" should {
    "contain the right links" in {
      AnnualStatementJson(empRef, taxYear, emptyEpayeAnnualStatement)._links shouldBe
        AnnualStatementLinksJson(
          empRefs = Link(baseUrl),
          statements = Link(s"${baseUrlFor(empRef)}/statements"),
          self = Link(s"${baseUrlFor(empRef)}/statements/${taxYear.asString}"),
          next = Link(s"${baseUrlFor(empRef)}/statements/${taxYear.next.asString}"),
          previous = Link(s"${baseUrlFor(empRef)}/statements/${taxYear.previous.asString}")
        )
    }
  }

  "AnnualStatementJson.apply._embedded.earlierYearUpdate" should {
    "contain the earlier year update if it is present" in {
      val emptyTotals = AnnualTotal(
        charges = DebitAndCredit(),
        cleared = Cleared(),
        balance = DebitAndCredit()
      )

      val epayeAnnualStatement =
        emptyEpayeAnnualStatement
          .copy(
            rti =
              AnnualStatementTable(
                lineItems = Seq(
                  LineItem(
                    taxYear,
                    None,
                    DebitAndCredit(100),
                    Cleared(10, 20),
                    DebitAndCredit(100 - 20 - 10),
                    dueDate,
                    codeText = None,
                    itemType = Some("eyu")
                  )
                ),
                totals = emptyTotals
              )
          )

      AnnualStatementJson(empRef, taxYear, epayeAnnualStatement)._embedded.earlierYearUpdate shouldBe
        Some(EarlierYearUpdateJson(
          amount = 100,
          clearedByCredits = 20,
          clearedByPayments = 10,
          balance = 100 - 10 - 20,
          dueDate = dueDate
        ))
    }
    "return a None if it is not present" in {
      AnnualStatementJson(empRef, taxYear, emptyEpayeAnnualStatement)._embedded.earlierYearUpdate shouldBe None
    }
  }

  "MonthlyChargesJson.from(lineItem)" should {
    "convert an rti charge from the epaye annual statement" in {
      val taxMonth = TaxMonth(2)

      val lineItem =
        LineItem(
          taxYear = taxYear,
          taxMonth = Some(taxMonth),
          charges = DebitAndCredit(100, 0),
          cleared = Cleared(payment = 10, credit = 20),
          balance = DebitAndCredit(100 - 30, 0),
          dueDate = dueDate,
          isSpecified = true,
          codeText = None
        )

      MonthlyChargesJson.from(lineItem, empRef, taxYear) shouldBe
        Some(MonthlyChargesJson(
          taxMonth = TaxMonthJson(taxMonth.month, taxMonth.firstDay(taxYear), taxMonth.lastDay(taxYear)),
          amount = 100,
          clearedByCredits = 20,
          clearedByPayments = 10,
          balance = 100 - 10 - 20,
          dueDate = dueDate,
          isSpecified = true,
          _links = SelfLink(Link(s"${baseUrlFor(empRef)}/statements/${taxYear.asString}/${taxMonth.month}"))
        ))
    }
    "return a None if the taxMonth field is None" in {

      val lineItem =
        LineItem(
          taxYear = taxYear,
          taxMonth = None,
          charges = DebitAndCredit(100, 0),
          cleared = Cleared(payment = 10, credit = 20),
          balance = DebitAndCredit(100 - 30, 0),
          dueDate = dueDate,
          codeText = None
        )

      MonthlyChargesJson.from(lineItem, empRef, taxYear) shouldBe None
    }
  }

  "NonRtiChargesJson.from(lineItem)" should {
    "convert an non rti charge from the epaye annual statement" in {
      val code = "SOME_TEXT"

      val lineItem =
        LineItem(
          taxYear = taxYear,
          taxMonth = None,
          charges = DebitAndCredit(100, 0),
          cleared = Cleared(payment = 10, credit = 20),
          balance = DebitAndCredit(100 - 30, 0),
          dueDate = dueDate,
          codeText = Some(code)
        )

      NonRtiChargesJson.from(lineItem, taxYear) shouldBe
        Some(NonRtiChargesJson(
          code = code,
          taxPeriod = PeriodJson(taxYear.firstDay, taxYear.lastDay),
          amount = 100,
          clearedByCredits = 20,
          clearedByPayments = 10,
          balance = 100 - 10 - 20,
          dueDate = dueDate
        ))
    }
  }
}

