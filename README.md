Configraun
==========

Configraun is a thin wrapper around AWS's [Systems Manager Parameter Store](https://aws.amazon.com/ec2/systems-manager/parameter-store/).
Parameter store allows you to manage your configuration data in one place including plain data and secure data encrypted through AWS KMS. 

Using Parameter Store to store your applications configuration has a number of benefits: 

1. You can control who and what resources access specific config through IAM credentials at a granular level.
2. You can make use of AWS KMS to encrypt information and protect the security of your keys.
3. Any changes to configuration in Parameter Store are versioned providing an audit trail of what has changed and by whom. In fact all calls to Parameter Store may be audited via Cloudtrail.
4. Parameter store gives you some type safety around the type of the configuration item returned to you. I.e String | Secure String | String List.
5. Config items can be tagged.

## Setup

Add the following line to your SBT build definition, and set the version number to be the latest from the [releases page](https://github.com/guardian/configraun/releases):

```scala
libraryDependencies += "com.gu" %% "configraun" % "x.y"
```

You will then need to create a new instance of the client and set the key:

```scala
  implicit val client: AWSSimpleSystemsManagement = AWSSimpleSystemsManagementFactory(region, profile)

  val stack: String = "STACK"
  val stage: Stage = Stage.PROD
  val app: String = "APP"
  
  val config = Configraun.loadConfig(stack, app, stage)
```  

## Usage

```scala
config.getAsString("/my/config/value")

```
or 

```scala
config.getAsList("/my/config/value")

```

## Key Format

Configraun expects that any parameters are keyed with a parameter hierarchy format. The hierarchy can have a maximum of 
5 levels and must begin with /$stack/$app/$stage.

```
/$stack/$app/$stage/domain/value
```

e.g. 
```
/content-api/porter/PROD/aws/region
```


