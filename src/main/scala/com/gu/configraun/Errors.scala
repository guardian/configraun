package com.gu.configraun

object Errors {

  sealed trait ConfigraunError {
    val message: String
    val e: Throwable
  }

  case class InternalServerError(message: String, e: Throwable) extends ConfigraunError
  case class InvalidFilterKeyError(message: String, e: Throwable) extends ConfigraunError
  case class InvalidFilterOptionError(message: String, e: Throwable) extends ConfigraunError
  case class InvalidFilterValueError(message: String, e: Throwable) extends ConfigraunError
  case class InvalidKeyIdError(message: String, e: Throwable) extends ConfigraunError
  case class InvalidNextTokenError(message: String, e: Throwable) extends ConfigraunError
  case class UnknownParamTypeError(message: String, e: Throwable) extends ConfigraunError
  case class ParamNotOfTypeError(message: String, e: Throwable) extends ConfigraunError
  case class ParamNotExistError(message: String, e: Throwable) extends ConfigraunError
  case class ParameterVersionNotFoundError(message: String, e: Throwable) extends ConfigraunError
  case class FileDoesNotExist(message: String, e: Throwable) extends ConfigraunError
  case class ConfigException(message: String, e: Throwable) extends ConfigraunError

}
