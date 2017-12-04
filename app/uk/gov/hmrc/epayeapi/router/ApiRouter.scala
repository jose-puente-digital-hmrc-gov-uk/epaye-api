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

package uk.gov.hmrc.epayeapi.router

import javax.inject.{Inject, Singleton}

import play.api.routing.Router.Routes
import play.api.routing.{Router, SimpleRouter}
import play.api.routing.sird._
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.controllers.{GetAnnualStatementController, GetSummaryController}
import uk.gov.hmrc.epayeapi.models.in.{ExtractTaxYear, TaxYear}

@Singleton
case class ApiRouter @Inject() (
  prodRoutes: prod.Routes,
  getTotalsController: GetSummaryController,
  getAnnualStatementController: GetAnnualStatementController
) extends SimpleRouter {

  val appRoutes = Router.from {
    case GET(p"/$taxOfficeNumber/$taxOfficeReference/") =>
      getTotalsController.getSummary(EmpRef(taxOfficeNumber, taxOfficeReference))
    case GET(p"/$taxOfficeNumber/$taxOfficeReference/statements/${ExtractTaxYear(taxYear)}") =>
      getAnnualStatementController.getAnnualStatement(EmpRef(taxOfficeNumber, taxOfficeReference), taxYear)
  }

  val routes: Routes = appRoutes.routes.orElse(prodRoutes.routes)
}
