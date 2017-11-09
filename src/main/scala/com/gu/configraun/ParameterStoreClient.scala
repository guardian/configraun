package com.gu.configraun

import cats.syntax.either._
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.model._
import com.gu.configraun.Errors._

object ParameterStoreClient {

  def getParametersByPath(path: String, isRecursive: Boolean, withEncryption: Boolean = false)(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, GetParametersByPathResult] = {
    def op = client.getParametersByPath(new GetParametersByPathRequest().withPath(path).withRecursive(isRecursive).withWithDecryption(withEncryption))

    Either.catchNonFatal(op).bimap(
      {
        case e: InternalServerErrorException => InternalServerError(e.getMessage, e)
        case e: InvalidFilterKeyException => InvalidFilterKeyError(e.getMessage, e)
        case e: InvalidFilterOptionException => InvalidFilterOptionError(e.getMessage, e)
        case e: InvalidFilterValueException => InvalidFilterValueError(e.getMessage, e)
        case e: InvalidKeyIdException => InvalidKeyIdError(e.getMessage, e)
        case e: InvalidNextTokenException => InvalidNextTokenError(e.getMessage, e)
      },
      result => result)
  }

  def getParameter(name: String, withDecryption: Boolean = false)(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, GetParameterResult] = {
    def op = client.getParameter(new GetParameterRequest().withName(name).withWithDecryption(withDecryption))

    Either.catchNonFatal(op).bimap(
      {
        case e: InternalServerErrorException => InternalServerError(e.getMessage, e)
        case e: InvalidKeyIdException => InvalidKeyIdError(e.getMessage, e)
        case e: ParameterNotFoundException => InvalidNextTokenError(e.getMessage, e)
        case e: ParameterVersionNotFoundException => ParameterVersionNotFoundError(e.getMessage, e)
      },
      result => result)
  }

  def getParameters(withEncryption: Boolean, names: String*)(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, GetParametersResult] = {
    def op = client.getParameters(new GetParametersRequest().withNames(names: _*).withWithDecryption(withEncryption))

    Either.catchNonFatal(op).bimap(
      {
        case e: InternalServerErrorException => InternalServerError(e.getMessage, e)
        case e: InvalidKeyIdException => InvalidKeyIdError(e.getMessage, e)
      },
      result => result)
  }
}
