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

package contract

import uk.gov.hmrc.domain.EmpRef

object Fixtures {

  val epayeAnnualStatement: String =
    """
      |{
      |  "rti": {
      |    "lineItems": [
      |      {
      |        "taxYear": {
      |          "yearFrom": 2017
      |        },
      |        "taxMonth": {
      |          "month": 7
      |        },
      |        "charges": {
      |          "debit": 1200,
      |          "credit": 0
      |        },
      |        "cleared": {
      |          "cleared": 0,
      |          "payment": 0,
      |          "credit": 0
      |        },
      |        "balance": {
      |          "debit": 1200,
      |          "credit": 0
      |        },
      |        "dueDate": "2017-11-22",
      |        "isSpecified": false,
      |        "itemType": "month"
      |      }
      |    ],
      |    "totals": {
      |      "charges": {
      |        "debit": 1200,
      |        "credit": 0
      |      },
      |      "cleared": {
      |        "cleared": 0,
      |        "payment": 0,
      |        "credit": 0
      |      },
      |      "balance": {
      |        "debit": 1200,
      |        "credit": 0
      |      }
      |    }
      |  },
      |  "nonRti": {
      |    "lineItems": [
      |      {
      |        "taxYear": {
      |          "yearFrom": 2017
      |        },
      |        "taxMonth": {
      |          "month": 1
      |        },
      |        "charges": {
      |          "debit": 100,
      |          "credit": 0
      |        },
      |        "cleared": {
      |          "cleared": 100,
      |          "payment": 0,
      |          "credit": 0
      |        },
      |        "balance": {
      |          "debit": 0,
      |          "credit": 0
      |        },
      |        "dueDate": "2017-05-22",
      |        "isSpecified": false,
      |        "itemType": "1481",
      |        "codeText": "NON_RTI_CIS_FIXED_PENALTY"
      |      }
      |    ],
      |    "totals": {
      |      "charges": {
      |        "debit": 100,
      |        "credit": 0
      |      },
      |      "cleared": {
      |        "cleared": 100,
      |        "payment": 100,
      |        "credit": 0
      |      },
      |      "balance": {
      |        "debit": 100,
      |        "credit": 100
      |      }
      |    }
      |  },
      |  "unallocated": 2000
      |}
    """.stripMargin

  def authorisedEnrolmentJson(empRef: EmpRef): String =
    s"""
       |{
       |  "authorisedEnrolments": [
       |    {
       |      "key": "IR-PAYE",
       |      "identifiers": [
       |        {
       |          "key": "TaxOfficeNumber",
       |          "value": "${empRef.taxOfficeNumber}"
       |        },
       |        {
       |          "key": "TaxOfficeReference",
       |          "value": "${empRef.taxOfficeReference}"
       |        }],
       |      "state": "Activated",
       |      "confidenceLevel": 0,
       |      "delegatedAuthRule": "epaye-auth",
       |      "enrolment": "IR-PAYE"
       |    }
       |  ]
       |}
      """.stripMargin


}
