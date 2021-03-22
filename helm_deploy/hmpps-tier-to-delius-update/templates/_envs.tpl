    {{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for web and worker containers
*/}}
{{- define "deployment.envs" }}
env:
  - name: SERVER_PORT
    value: "{{ .Values.image.port }}"

  - name: JAVA_OPTS
    value: "{{ .Values.env.JAVA_OPTS }}"

  - name: OAUTH_CLIENT_ID
    valueFrom:
      secretKeyRef:
         name: {{ template "app.name" . }}
         key: OAUTH_CLIENT_ID

  - name: OAUTH_CLIENT_SECRET
    valueFrom:
      secretKeyRef:
         name: {{ template "app.name" . }}
         key: OAUTH_CLIENT_SECRET

  - name: SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI
    value: "{{ .Values.env.SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI }}"

  - name: OAUTH_ENDPOINT_URL
    value: "{{ .Values.env.OAUTH_ROOT_URL }}"

  - name: COMMUNITY_ENDPOINT_URL
    value: "{{ .Values.env.COMMUNITY_ENDPOINT_URL }}"

  - name: HMPPS_TIER_ENDPOINT_URL
    value: "{{ .Values.env.HMPPS_TIER_ENDPOINT_URL }}"

  - name: SPRING_PROFILES_ACTIVE
    value: "aws,logstash,stdout"

  - name: AWS_PROBATION_EVENTS_ACCESS_KEY
    valueFrom:
      secretKeyRef:
        name: sqs-tier-to-delius-update-secret
        key: access_key_id

  - name: AWS_PROBATION_EVENTS_SECRET_ACCESS_KEY
    valueFrom:
      secretKeyRef:
        name: sqs-tier-to-delius-update-secret
        key: secret_access_key

  - name: AWS_PROBATION_EVENTS_QUEUE
    valueFrom:
      secretKeyRef:
        name: sqs-tier-to-delius-update-secret
        key: sqs_queue_url

  - name: APPINSIGHTS_INSTRUMENTATIONKEY
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: APPINSIGHTS_INSTRUMENTATIONKEY

  - name: APPLICATIONINSIGHTS_CONNECTION_STRING
    value: "InstrumentationKey=$(APPINSIGHTS_INSTRUMENTATIONKEY)"
{{- end -}}
