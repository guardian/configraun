package com.gu.configraun.models

sealed trait Stage {
  def name: String
  def apply(value: String): Option[Stage] = {
    Vector(DEV, CODE, PROD).find(_.toString == value)
  }
}

case object DEV extends Stage { def name: String = "DEV" }
case object CODE extends Stage { def name: String = "CODE" }
case object PROD extends Stage { def name: String = "PROD" }

