package com.gu.configraun.models

import com.gu.configraun.Errors.{ ConfigraunError, ParamNotOfTypeError, ParamNotExistError }

case class Configuration(underlying: Map[String, Param]) {
  def getAsString(key: String): Either[ConfigraunError, String] = {
    underlying.get(key) match {
      case Some(StringParam(param)) => Right(param)
      case Some(ListParam(param)) => Left(ParamNotOfTypeError(s"Parameter with key $key is not of type String. Try calling .getAsList.", new UnsupportedOperationException))
      case None => Left(ParamNotExistError(s"Parameter with key $key is not present.", new UnsupportedOperationException))
      case Some(a) => Left(ParamNotOfTypeError(s"Parameter with key $key is not of type String. Type is not ${a.getClass()}.", new UnsupportedOperationException))
    }
  }
  def getAsList(key: String): Either[ConfigraunError, Seq[String]] = {
    underlying.get(key) match {
      case Some(ListParam(param)) => Right(param)
      case Some(StringParam(param)) => Left(ParamNotOfTypeError(s"Parameter with key $key is not of type List[String]. Try calling .getAsString.", new UnsupportedOperationException))
      case None => Left(ParamNotExistError(s"Parameter with key $key is not present.", new UnsupportedOperationException))
      case Some(a) => Left(ParamNotOfTypeError(s"Parameter with key $key is not of type List[String]. Type is ${a.getClass}.", new UnsupportedOperationException))

    }
  }
}
