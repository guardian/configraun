package com.gu.configraun.models

sealed trait Param
case class StringParam(param: String) extends Param
case class ListParam(param: Seq[String]) extends Param
