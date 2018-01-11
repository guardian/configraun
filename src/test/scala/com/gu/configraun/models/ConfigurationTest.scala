package com.gu.configraun.models

import org.scalatest.prop.{Checkers, PropertyChecks}
import org.scalatest.{FreeSpec, Matchers}


class ConfigurationTest extends FreeSpec with Matchers with Checkers with PropertyChecks {

  "put config in, get config out" in {
    Configuration.empty.withString("test1", "test2").getAsString("test1").right.toOption.get shouldBe "test2"
  }

  "don't put config in, don't get config out" in {
    Configuration.empty.getAsString("test1").left.get.message shouldBe s"Parameter with key test1 is not present."
  }

  "put string config in, don't get seq string config out" in {
    Configuration.empty.withString("test1", "test2").getAsList("test1").left.get.message shouldBe s"Parameter with key test1 is not of type List[String]. Try calling .getAsString."
  }

  "put seq string config in, don't get string config out" in {
    Configuration.empty.withList("test1", Seq("test2", "test3")).getAsString("test1").left.get.message shouldBe s"Parameter with key test1 is not of type String. Try calling .getAsList."
  }

}
