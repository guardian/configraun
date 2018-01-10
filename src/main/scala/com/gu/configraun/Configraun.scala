package com.gu.configraun

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.gu.configraun.Errors.{ConfigException, ConfigraunError}
import com.gu.configraun.aws.{AwsInstanceTags, GetParametersByPathResultProcessor}
import com.gu.configraun.models.{Configuration, PROD, Stage}


object Configraun {

  def loadConfig(stack: String, app: String, stage: Stage)(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, Configuration] = loadRemoteConfiguration(stack, app, stage)
  def loadConfig(stack: String, app: String, stage: String)(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, Configuration] = loadRemoteConfiguration(stack, app, stage)

  def loadConfig(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, Configuration] = for {
    stack <- AwsInstanceTags("stack")
    app <- AwsInstanceTags("app")
    stage <- AwsInstanceTags("stage")
    config <- loadConfig(stack, app, stage)
  } yield config

  private def loadRemoteConfiguration(stack: String, app: String, stage: Stage)(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, Configuration] = {
    loadRemoteConfiguration(stack, app, stage.name)
  }

  private def loadRemoteConfiguration(stack: String, app: String, stage: String)(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, Configuration] = {
    val pathPrefix = s"/$stack/$app/${stage}"

    for {
      result <- ParameterStoreClient.getParametersByPath(pathPrefix, isRecursive = true, withEncryption = true)
      params <- GetParametersByPathResultProcessor.process(result, pathPrefix).map(_.toMap)
    } yield Configuration(params)

  }

}

