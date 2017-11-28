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

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.JsonFixtures.{emptyAnnualStatementJsonWith, emptyEpayeAnnualStatement}
import uk.gov.hmrc.epayeapi.models.in.TaxYear

class AnnualStatementJsonSpec extends WordSpec with Matchers {
  "AnnualStatementJson.apply" should {
    val empRef = EmpRef("123", "AB45678")
    val taxYear = TaxYear(2016)

    "return an empty response with links given an empty annual statement json" in {
      AnnualStatementJson(empRef, taxYear, emptyEpayeAnnualStatement) shouldBe emptyAnnualStatementJsonWith(empRef, taxYear)
    }
  }

}

