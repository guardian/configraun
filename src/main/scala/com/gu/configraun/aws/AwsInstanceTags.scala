package com.gu.configraun.aws

import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2Client, AmazonEC2ClientBuilder}
import com.amazonaws.services.ec2.model.{DescribeTagsRequest, Filter}
import com.amazonaws.util.EC2MetadataUtils
import com.gu.configraun.Errors.{ConfigException, ConfigraunError}

import scala.collection.JavaConverters._

object AwsInstanceTags {

  lazy val instanceId= EC2MetadataUtils.getInstanceId match {
    case s:String => Right(s)
    case _ => Left(ConfigException("Unable to find instance id", null))
  }

  lazy val region: Either[ConfigraunError, Region]  = Regions.getCurrentRegion match {
    case r:Region => Right(r)
    case _ => Left(ConfigException("Unable to find region", null))
  }
  lazy val ec2Client: Either[ConfigraunError, AmazonEC2] = Right(AmazonEC2ClientBuilder.defaultClient())

  private lazy val tags: Either[ConfigraunError, Map[String, String]] = for {
      theInstanceId <- instanceId
      theClient <- ec2Client
      tagsResult = theClient.describeTags(new DescribeTagsRequest().withFilters(
        new Filter("resource-type").withValues("instance"),
        new Filter("resource-id").withValues(theInstanceId))).getTags
    } yield {
      tagsResult.asScala.map{td => td.getKey -> td.getValue }.toMap
    }

  def apply(s:String): Either[ConfigraunError, String] = tags match {
    case Left(a) => Left(a)
    case Right(b) => b.get(s) match {
      case Some(s) => Right(s)
      case _ => Left(ConfigException(s"No tag '${s}' found in tags", null))
    }

  }

}
