#!/usr/bin/env bash
export AWS_ACCESS_KEY_ID=foobar
export AWS_SECRET_ACCESS_KEY=foobar
export AWS_DEFAULT_REGION=eu-west-2
aws --endpoint-url=http://localhost:4575 sns create-topic --name hmpps-domain-events

aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name hmpps_tier_to_delius_dlq
aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name hmpps_tier_to_delius_queue
aws --endpoint-url=http://localhost:4576 sqs set-queue-attributes --queue-url "http://localhost:4576/queue/hmpps_tier_to_delius_queue" --attributes '{"RedrivePolicy":"{\"maxReceiveCount\":\"3\", \"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:hmpps_tier_to_delius_dlq\"}"}'
aws --endpoint-url=http://localhost:4575 sns subscribe \
    --topic-arn arn:aws:sns:eu-west-2:000000000000:hmpps-domain-events \
    --protocol sqs \
    --notification-endpoint http://localhost:4576/queue/hmpps_tier_to_delius_queue \
    --attributes '{"FilterPolicy":"{\"eventType\":[ \"HMPPS_TIER_CALCULATION_COMPLETE\"] }"}'

echo All Ready