package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.helpers

fun tierUpdateMessage() = """
{
  "Type": "Notification",
  "MessageId": "f39059e7-a62d-4157-929a-fb049015c993",
  "Token": null,
  "TopicArn": "arn:aws:sns:eu-west-2:000000000000:hmpps-domain",
  "Message": "{\"crn\":\"12345\",\"calculationId\":\"e45559d1-3460-4a0e-8281-c736de57c562\"}",
  "SubscribeURL": null,
  "Timestamp": "2021-10-21T06:27:57.028Z",
  "SignatureVersion": "1",
  "Signature": "EXAMPLEpH+..",
  "SigningCertURL": "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-0000000000000000000000.pem",
  "MessageAttributes": {
    "eventType": {
      "Type": "String",
      "Value": "TIER_CALCULATION_COMPLETE"
    }
  }
}   
""".trimIndent()
