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

package uk.gov.hmrc.epayeapi.controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.auth.core.Retrievals._
import uk.gov.hmrc.auth.core.{AuthConnector, Enrolment, InsufficientEnrolments}
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.Formats._
import uk.gov.hmrc.epayeapi.models.{ApiError, EmpRefsResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
case class GetEmpRefs @Inject() (
  authConnector: AuthConnector,
  implicit val ec: ExecutionContext
)
  extends ApiController {

  def getEmpRefs(): Action[AnyContent] = Action.async { implicit request =>
    authorised(epayeEnrolment).retrieve(authorisedEnrolments) { enrolments =>
      val empRefs = enrolments.enrolments.flatMap(enrolmentToEmpRef)

      Future.successful {
        Ok(Json.toJson(EmpRefsResponse.fromSeq(empRefs.toSeq)))
      }
    } recoverWith {
      case ex: InsufficientEnrolments =>
        Future.successful(Unauthorized(Json.toJson(ApiError.InsufficientEnrolments)))
      case ex =>
        Future.failed(ex)
    }
  }

  def enrolmentToEmpRef(enrolment: Enrolment): Option[EmpRef] = {
    for {
      "IR-PAYE" <- Option(enrolment.key)
      tn <- enrolment.identifiers.find(_.key == "TaxOfficeNumber")
      tr <- enrolment.identifiers.find(_.key == "TaxOfficeReference")
      if enrolment.isActivated
    } yield EmpRef(tn.value, tr.value)
  }
}
