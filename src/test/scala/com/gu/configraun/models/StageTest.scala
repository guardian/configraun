package com.gu.configraun.models

import org.scalatest.prop.{Checkers, PropertyChecks}
import org.scalatest.{FreeSpec, Matchers}


class StageTest extends FreeSpec with Matchers with Checkers with PropertyChecks {

  "Get stage from string" in {
    Stage.fromString("DEV").get shouldBe DEV
    Stage.fromString("CODE").get shouldBe CODE
    Stage.fromString("PROD").get shouldBe PROD
  }
}
