package com.gu.configraun.aws

import com.amazonaws.services.simplesystemsmanagement.model.{GetParametersByPathResult, ParameterType}
import com.gu.configraun.Errors.{ConfigraunError, UnknownParamTypeError}
import com.gu.configraun.models.{ListParam, Param, StringParam}

import scala.collection.JavaConverters._
import scala.util.Try
import cats.implicits._
import cats.syntax.either._


object GetParametersByPathResultProcessor {

  type EitherFunctor[A] = Either[ConfigraunError, A]

  def process(getParametersByPathResult: GetParametersByPathResult, pathPrefix: String): Either[ConfigraunError, List[(String, Param)]] = {

    val result: List[Either[ConfigraunError, (String, Param)]] = getParametersByPathResult.getParameters.asScala.map { param =>
      for {
        paramType <- Try(ParameterType.fromValue(param.getType)).toEither.leftMap(e => UnknownParamTypeError(e.getMessage, e))
      } yield {
        val paramKeyValue: (String, Param) = paramType match {
          case ParameterType.String | ParameterType.SecureString => param.getName.stripPrefix(pathPrefix) -> StringParam(param.getValue)
          case ParameterType.StringList => param.getName.stripPrefix(pathPrefix) -> ListParam(param.getValue.split(",").map(_.trim).toSeq)
        }
        paramKeyValue
      }
    }.toList

    result.sequence[EitherFunctor, (String, Param)]
  }

}
