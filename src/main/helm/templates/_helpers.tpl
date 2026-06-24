{{/*
Expand the name of the chart.
*/}}
{{- define "application.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a fully qualified name for the app.
Respects fullnameOverride and nameOverride.
Ensures DNS-1123 compatibility, trims and truncates safely.
*/}}
{{- define "application.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | lower | replace "_" "-" | trunc 63 | trimSuffix "-" }}
{{- else }}
  {{- $name := default .Chart.Name .Values.nameOverride | lower | replace "_" "-" }}
  {{- if hasPrefix $name .Release.Name }}
    {{- .Release.Name | lower | replace "_" "-" | trunc 63 | trimSuffix "-" }}
  {{- else }}
    {{- printf "%s-%s" .Release.Name $name | lower | replace "_" "-" | trunc 63 | trimSuffix "-" }}
  {{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "application.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common annotations
*/}}
{{- define "application.annotations" -}}
meta.helm.sh/release-name: {{ .Release.Name | quote }}
meta.helm.sh/release-namespace: {{ .Release.Namespace | quote }}
{{- end }}

{{/*
Selector labels (used in selectors and matchLabels)
*/}}
{{- define "application.selectorLabels" -}}
app.kubernetes.io/component: {{ .Chart.Name | quote }}
app.kubernetes.io/name: {{ include "application.fullname" . | quote }}
app.kubernetes.io/instance: {{ .Release.Name | quote }}
app: {{ include "application.fullname" . | quote }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "application.labels" -}}
app.kubernetes.io/version: {{ .Chart.AppVersion }}
helm.sh/chart: {{ include "application.chart" . }}
app.kubernetes.io/managed-by: {{ .Release.Service | quote }}
{{ include "application.selectorLabels" . }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "application.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "application.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Deployment port (8080 for HTTP, 8443 for HTTPS)
*/}}
{{- define "application.deployment.port" -}}
{{- if .Values.network.ssl.enabled -}}
{{- printf "%d" 8443 -}}
{{- else -}}
{{- printf "%d" 8080 -}}
{{- end -}}
{{- end -}}

{{/*
HTTP scheme (HTTP or HTTPS)
*/}}
{{- define "application.deployment.httpScheme" -}}
{{- if .Values.network.ssl.enabled }}
{{- printf "%s" "HTTPS" -}}
{{- else -}}
{{- printf "%s" "HTTP" -}}
{{- end -}}
{{- end -}}

{{/*
Create the name of service cert secret
*/}}
{{- define "application.service-cert.name" -}}
{{- printf "%s-service-cert" (include "application.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create the name of the runtime trust store secret
*/}}
{{- define "application.platform.trustStoreSecret" -}}
{{- printf "k5-truststore" -}}
{{- end -}}

{{/*
Application context path (e.g., /deploytest)
Uses route.path if set, otherwise empty
*/}}
{{- define "application.contextPath" -}}
{{- if .Values.route.path -}}
{{- .Values.route.path -}}
{{- else -}}
{{- printf "" -}}
{{- end -}}
{{- end -}}

{{/*
Checks the runtime (Kubernetes | Openshift)
Dynamically detects OpenShift by checking for route.openshift.io/v1 API
*/}}
{{- define "application.platform.isOpenshift" -}}
{{- if .Capabilities.APIVersions.Has "route.openshift.io/v1" -}}
true
{{- else -}}
false
{{- end -}}
{{- end -}}

