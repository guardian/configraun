package com.gu.configraun

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.gu.configraun.Errors.ConfigraunError
import com.gu.configraun.aws.GetParametersByPathResultProcessor
import com.gu.configraun.models._


object Configraun {

  def loadConfig(stack: String, app: String, stage: Stage)(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, Configuration] = loadRemoteConfiguration(stack, app, stage)

  private def loadRemoteConfiguration(stack: String, app: String, stage: Stage)(implicit client: AWSSimpleSystemsManagement): Either[ConfigraunError, Configuration] = {
    val pathPrefix = s"/$stack/$app/${stage.name}"

    for {
      result <- ParameterStoreClient.getParametersByPath(pathPrefix, isRecursive = true, withEncryption = true)
      params <- GetParametersByPathResultProcessor.process(result, pathPrefix).map(_.toMap)
    } yield Configuration(params)

  }

}

