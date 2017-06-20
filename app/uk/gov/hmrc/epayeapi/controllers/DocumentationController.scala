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

import play.api.http.HttpErrorHandler
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.api.controllers.{DocumentationController => ApiDocsController}
import uk.gov.hmrc.epayeapi.config.AppContext

@Singleton
case class DocumentationController @Inject() (
  httpErrorHandler: HttpErrorHandler,
  context: AppContext
)
  extends ApiDocsController(httpErrorHandler) {

  override def documentation(version: String, endpointName: String): Action[AnyContent] = {
    super.at(s"/public/api/documentation/$version", s"${endpointName.replaceAll(" ", "-")}.xml")
  }

  override def definition(): Action[AnyContent] = Action {
    Ok(views.txt.definition(context.apiContext, context.apiStatus))
  }

  def raml(version: String, file: String): Action[AnyContent] = {
    super.at(s"/public/api/conf/$version", file)
  }
}
