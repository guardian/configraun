package com.gu.configraun.aws

import com.amazonaws.auth.{ AWSCredentialsProviderChain, InstanceProfileCredentialsProvider }
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.simplesystemsmanagement.{ AWSSimpleSystemsManagement, AWSSimpleSystemsManagementClientBuilder }

object AWSSimpleSystemsManagementFactory {

  def apply(region: String, profile: String): AWSSimpleSystemsManagement = {
    AWSSimpleSystemsManagementClientBuilder.standard()
      .withCredentials(new AWSCredentialsProviderChain(new ProfileCredentialsProvider(profile), InstanceProfileCredentialsProvider.getInstance()))
      .withRegion(region)
      .build
  }

}
