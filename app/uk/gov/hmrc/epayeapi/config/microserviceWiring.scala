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

package uk.gov.hmrc.epayeapi.config

import javax.inject.{Inject, Singleton}

import play.api.{Environment, Logger}
import uk.gov.hmrc.auth.core.{PlayAuthConnector => CoreAuthConnector}
import uk.gov.hmrc.play.auth.microservice.connectors.{AuthConnector => PlayAuthConnector}
import uk.gov.hmrc.play.audit.http.config.LoadAuditingConfig
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.config.inject.ServicesConfig
import uk.gov.hmrc.play.config.inject.RunMode
import uk.gov.hmrc.play.http.HttpPost
import uk.gov.hmrc.play.http.hooks.HttpHook
import uk.gov.hmrc.play.http.ws._

@Singleton
case class WSHttpImpl @Inject() (context: AppContext)
  extends WSHttp
  with WSGet
  with WSPost
  with WSPut
  with WSDelete
  with WSPatch {
  Logger.info(s"Starting: ${getClass.getName}")
  override val hooks: Seq[HttpHook] = NoneRequired
}

@Singleton
case class MicroserviceAuditConnector @Inject() (
  mode: RunMode,
  environment: Environment
)
  extends AuditConnector {
  Logger.info(s"Starting: ${getClass.getName}")
  override lazy val auditingConfig = LoadAuditingConfig(s"auditing")
}

@Singleton
case class MicroserviceAuthConnector @Inject() (servicesConfig: ServicesConfig)
  extends PlayAuthConnector {
  Logger.info(s"Starting: ${getClass.getName}")
  override val authBaseUrl: String = servicesConfig.baseUrl("auth")
}

@Singleton
case class ActualAuthConnector @Inject() (
  servicesConfig: ServicesConfig,
  http: HttpPost
)
  extends CoreAuthConnector {
  val serviceUrl: String = servicesConfig.baseUrl("auth")
}
