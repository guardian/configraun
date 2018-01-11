package com.gu.configraun.models

object Stage {
  def fromString(s: String) = Set(DEV, CODE, PROD).find(a => a.name.equals(s))
}

sealed trait Stage {
  def name: String
}

case object DEV extends Stage { def name: String = "DEV" }
case object CODE extends Stage { def name: String = "CODE" }
case object PROD extends Stage { def name: String = "PROD" }

case class Stack(val value: String) extends AnyVal
case class App(val value: String) extends AnyVal

case class Identifier (stack: Stack, app: App, stage: Stage)
