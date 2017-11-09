package com.gu.configraun.models

import com.gu.configraun.Errors.{ ConfigraunError, ParamNotOfTypeError }


case class Configuration(underlying: Map[String, Param]) {
  def getAsString(key: String): Either[ConfigraunError, String] = {
    underlying.get(key) match {
      case Some(StringParam(param)) => Right(param)
      case _ => Left(ParamNotOfTypeError(s"Parameter with key $key is not of type String. Try calling .getAsList.", new UnsupportedOperationException))
    }
  }
  def getAsList(key: String): Either[ConfigraunError, Seq[String]] = {
    underlying.get(key) match {
      case Some(ListParam(param)) => Right(param)
      case _ => Left(ParamNotOfTypeError(s"Parameter with key $key is not of type List[String]. Try calling .getAsString.", new UnsupportedOperationException))
    }
  }
}
