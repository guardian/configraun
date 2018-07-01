package com.gu.configraun.aws

import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.autoscaling.model.{DescribeAutoScalingGroupsRequest, DescribeAutoScalingInstancesRequest}
import com.amazonaws.services.autoscaling.{AmazonAutoScaling, AmazonAutoScalingClientBuilder}
import com.amazonaws.services.ec2.model.{DescribeTagsRequest, Filter}
import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2ClientBuilder}
import com.amazonaws.util.EC2MetadataUtils
import com.gu.configraun.Errors.{ConfigException, ConfigraunError}

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

object AwsInstanceTags {

  lazy val instanceId: Either[ConfigException, String] = EC2MetadataUtils.getInstanceId match {
    case instanceId: String => Right(instanceId)
    case _ => Left(ConfigException("Unable to find instance id", null))
  }

  lazy val region: Either[ConfigException, Region] = Regions.getCurrentRegion match {
    case region: Region => Right(region)
    case _ => Left(ConfigException("Unable to find region", null))
  }

  lazy val asgClient: Either[ConfigraunError, AmazonAutoScaling] = Right(AmazonAutoScalingClientBuilder.defaultClient())
  lazy val ec2Client: Either[ConfigraunError, AmazonEC2] = Right(AmazonEC2ClientBuilder.defaultClient())

  private def readTagsFromASG(instanceId: String, asgClient: AmazonAutoScaling): Either[ConfigraunError, Map[String, String]] = try {
    val asgRequest = new DescribeAutoScalingInstancesRequest().withInstanceIds(instanceId)
    val asgResponse = asgClient.describeAutoScalingInstances(asgRequest)

    val asgName = asgResponse.getAutoScalingInstances.asScala.head.getAutoScalingGroupName
    val request = new DescribeAutoScalingGroupsRequest().withAutoScalingGroupNames(asgName)
    val response = asgClient.describeAutoScalingGroups(request)

    val tags = response.getAutoScalingGroups.asScala.head.getTags
    Right(tags.asScala.map { td => td.getKey -> td.getValue }(scala.collection.breakOut))
  } catch {
    case NonFatal(e) =>
      Left(ConfigException("Unable to read tags from AutoScalingGroup", e))
  }

  private def readTagsFromInstance(instanceId: String, ec2Client: AmazonEC2): Either[ConfigraunError, Map[String, String]] = try {
    val tags = ec2Client.describeTags(new DescribeTagsRequest().withFilters(
      new Filter("resource-type").withValues("instance"),
      new Filter("resource-id").withValues(instanceId))).getTags

    Right(tags.asScala.map { td => td.getKey -> td.getValue }(scala.collection.breakOut))
  } catch {
    case NonFatal(e) =>
      Left(ConfigException("Unable to read tags from instance", e))
  }

  // We read tags from the AutoScalingGroup rather than the instance itself to avoid problems where the
  // tags have not been applied to the instance before we start up (they are eventually consistent)
  private lazy val tags: Either[ConfigraunError, Map[String, String]] = {
    instanceId.flatMap { instanceId =>
      asgClient.flatMap(readTagsFromASG(instanceId, _)) match {
        case Right(asgTags) => Right(asgTags)
        case Left(_) => ec2Client.flatMap(readTagsFromInstance(instanceId, _))
      }
    }
  }

  def apply(tagName: String): Either[ConfigraunError, String] = tags match {
    case Left(a) => Left(a)
    case Right(b) => b.get(tagName) match {
      case Some(s) => Right(s)
      case _ => Left(ConfigException(s"No tag '$tagName' found in tags", null))
    }
  }

}
