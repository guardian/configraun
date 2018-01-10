package com.gu.configraun

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.gu.configraun.Errors.{ConfigException, ConfigraunError}
import com.gu.configraun.aws.{AwsInstanceTags, GetParametersByPathResultProcessor}
import com.gu.configraun.models._


object Configraun {

  def loadConfig(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, Configuration] = for {
    stackName <- AwsInstanceTags("stack")
    stack = new Stack(stackName)
    appName <- AwsInstanceTags("app")
    app = new App(appName)
    stageName <- AwsInstanceTags("stage")
    stage <- Stage.fromString(stageName) match {
      case Some(s) => Right(s)
      case None => Left(ConfigException(s"No such Stage: ${stageName}", null))
    }
    identifier = Identifier(stack, app, stage)
    config <- loadConfig(identifier)
  } yield config

  def loadConfig(identifier: Identifier)(implicit client: AWSSimpleSystemsManagement) = {
    val pathPrefix = s"/${identifier.stack.value}/${identifier.app.value}/${identifier.stage.name}"
    for {
      result <- ParameterStoreClient.getParametersByPath(pathPrefix, isRecursive = true, withEncryption = true)
      params <- GetParametersByPathResultProcessor.process(result, pathPrefix).map(_.toMap)
    } yield Configuration(params)
  }

}


