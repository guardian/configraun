> **Warning**
> This library is deprecated and no longer maintained. [simple-configuration](https://github.com/guardian/simple-configuration) is an alternative.

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

Or, for an EC2 instance with appropriate IAM policies (see below):

```scala
  implicit val client: AWSSimpleSystemsManagement = AWSSimpleSystemsManagementFactory(region, profile)

  val config = Configraun.loadConfig()
```


## Usage

Each of the get methods returns an `Either[ConfigraunError, T]`, designed to be traversed within
a for comprehension.

```scala
config.getAsString("/mydomain/mykey")

```
or

```scala
config.getAsList("/mydomain/mykey")

```

## Key Format

Configraun expects that any parameters are keyed with a parameter hierarchy format. The hierarchy can have a maximum of
5 levels and must begin with /$stack/$app/$stage.

```
/$stack/$app/$stage/key
```
or
```
/$stack/$app/$stage/domain/key
```

e.g.
```
/content-api/porter/PROD/aws/region
```

## Key Value Creation

Keys can be created from the command line using the following:

```bash
aws --region $region --profile $profile ssm put-parameter --name '/mystack/myapp/PROD/mydomain/mykey' --value 'myvalue' --type String
```

## IAM Policies

Instances will need to have Describe Tags permission in a policy in their instance role, or via some other form of credentials provider:

```
    "Effect": "Allow",
    "Action": "ec2:DescribeTags",
    "Resource": "*"
```
