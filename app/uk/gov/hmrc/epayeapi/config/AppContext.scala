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

import uk.gov.hmrc.play.config.inject.DefaultServicesConfig

@Singleton
case class AppContext @Inject() (config: DefaultServicesConfig) {
  val current = config.runModeConfiguration
  val env = config.environment

  val appName = current.getString("appName").getOrElse(throw new RuntimeException("appName is not configured"))
  val appUrl = current.getString("appUrl").getOrElse(throw new RuntimeException("appUrl is not configured"))
  val serviceLocatorUrl: String = config.baseUrl("service-locator")
  val apiContext = current.getString(s"api.context").getOrElse(throw new RuntimeException(s"Missing Key $env.api.context"))
  val baseUrl = current.getString(s"$env.baseUrl").getOrElse(throw new RuntimeException(s"Missing Key $env.baseUrl"))
  val apiStatus = current.getString("api.status").getOrElse(throw new RuntimeException(s"Missing Key $env.api.status"))
  val desAuthToken = current.getString("desauthtoken").getOrElse(throw new RuntimeException(s"Missing Key $env.desauthtoken"))
  val desUrlHeaderEnv: String = current.getString("environment").getOrElse(throw new RuntimeException(s"Missing Key $env.environment"))
}
