package com.gu.configraun.models

import org.scalatest.prop.{Checkers, PropertyChecks}
import org.scalatest.{FreeSpec, Matchers}


class ConfigurationTest extends FreeSpec with Matchers with Checkers with PropertyChecks {

  "put config in, get config out" in {
    Configuration.empty.withString("test1", "test2").getAsString("test1").right.toOption.get shouldBe "test2"
  }
}
