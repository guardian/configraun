package com.gu.configraun.models

import org.scalatest.prop.{Checkers, PropertyChecks}
import org.scalatest.{FreeSpec, Matchers}


class ConfigurationTest extends FreeSpec with Matchers with Checkers with PropertyChecks {

  "put string config in, get string config out" in {
    val conf = Configuration.empty.withString("test1", "test2").getAsString("test1")
    conf.right.toOption.get shouldBe "test2"
    conf.left.toOption shouldBe None
  }

  "put seq string config in, get seq string config out" in {
    val conf = Configuration.empty.withList("test1", List("test2", "test3")).getAsList("test1")
    conf.right.toOption.get shouldBe List("test2", "test3")
    conf.left.toOption shouldBe None
  }

  "don't put config in, don't get config out" in {
    val conf = Configuration.empty
    conf.getAsString("test1").left.get.message.startsWith(s"Parameter with key test1 is not present.") shouldBe true
    conf.getAsString("test1").right.toOption shouldBe None
    conf.getAsList("test1").left.get.message.startsWith(s"Parameter with key test1 is not present.") shouldBe true
    conf.getAsList("test1").right.toOption shouldBe None
  }

  "put string config in, don't get seq string config out" in {
    val conf = Configuration.empty.withString("test1", "test2").getAsList("test1")
    conf.left.get.message.startsWith(s"Parameter with key test1 is not of type List[String].") shouldBe true
    conf.right.toOption shouldBe None
  }

  "put seq string config in, don't get string config out" in {
    val conf = Configuration.empty.withList("test1", Seq("test2", "test3")).getAsString("test1")
    conf.left.get.message.startsWith(s"Parameter with key test1 is not of type String.") shouldBe true
    conf.right.toOption shouldBe None
  }

}
