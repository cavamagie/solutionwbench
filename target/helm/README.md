# Spring Boot Application Helm Chart

This is a Helm chart for deploying applications based on java and spring boot on Kubernetes and OpenShift clusters. The chart is designed to be used across multiple projects by simply overriding the image repository and application-specific configuration.

## Chart Status

✅ **Chart is validated and ready for deployment**
- All templates render successfully
- Helm lint passes with no errors
- Supports both Kubernetes and OpenShift (auto-detected)
- SSL/HTTPS enabled by default with HTTP fallback option

## Prerequisites

- Kubernetes 1.19+ or OpenShift 4.x+
- Helm 3.0+
- A container registry with your application image
- For SSL: cert-manager (Kubernetes) or OpenShift service certificates (OpenShift)

## Reusing This Chart for Different Projects

This chart is designed to be reusable across multiple Spring Boot projects. To use it for a different project:

1. **Override the image repository** (required):
   ```bash
   helm install my-app ./src/main/helm \
     --set image.repository=my-registry/my-app
   ```

2. **Use nameOverride for custom naming**:
   ```bash
   helm install my-app ./src/main/helm \
     --set image.repository=my-registry/my-app \
     --set nameOverride=my-app
   ```

3. **Use fullnameOverride for complete control**:
   ```bash
   helm install my-app ./src/main/helm \
     --set image.repository=my-registry/my-app \
     --set fullnameOverride=my-custom-name
   ```

4. **Create project-specific values file**:
   ```yaml
   # my-project-values.yaml
   image:
     repository: my-registry/my-project
     tag: "1.0.0"
   
   nameOverride: "my-project"
   
   bindings:
     oauth2:
       issuerUri: "https://keycloak.example.com/realms/my-project"
       clientId: "my-project-client"
   ```

## Installation

### Basic Installation

```bash
helm install my-app ./src/main/helm \
  --set image.repository=my-registry/my-app
```

### Installation with Custom Values

```bash
helm install my-app ./src/main/helm -f my-values.yaml
```

### Installation on OpenShift

The chart automatically detects OpenShift and configures certificates accordingly:

```bash
helm install my-app ./src/main/helm \
  --set image.repository=my-registry/my-app \
  --set ingress.enabled=false \
  --set route.enabled=true \
  --set route.host=my-app.apps.example.com
```

### Installation on Kubernetes

For Kubernetes, cert-manager will automatically create service certificates:

```bash
helm install my-app ./src/main/helm \
  --set image.repository=my-registry/my-app \
  --set ingress.enabled=true \
  --set services.certification.selfSigned.issuerName=my-issuer
```

## Features

- ✅ **Platform Detection**: Automatically detects Kubernetes vs OpenShift
- ✅ **SSL/HTTPS Support**: Configurable SSL with HTTP fallback option
- ✅ **Dual Platform**: Works on both Kubernetes and OpenShift
- ✅ **Certificate Management**: 
  - OpenShift: Uses serving certificate annotations
  - Kubernetes: Uses cert-manager for service certificates
- ✅ **Health Probes**: Liveness, readiness, and startup probes
- ✅ **Autoscaling**: Horizontal Pod Autoscaler support
- ✅ **Pod Disruption Budget**: Configurable PDB
- ✅ **Environment Variables**: Support for env and envFromSecrets
- ✅ **ConfigMap**: Spring Boot configuration via ConfigMap
- ✅ **Secrets**: Secure handling of sensitive data

## Configuration

### Network Configuration

```yaml
network:
  ssl:
    enabled: true  # Enable HTTPS (default) or set to false for HTTP only
  # The fully qualified hostname with protocol and port
  # Used by the application to generate absolute URLs
  host: "http://localhost:8080"
```

**Ports:**
- HTTPS (when `network.ssl.enabled: true`): Container port 8443, Service port 443
- HTTP (when `network.ssl.enabled: false`): Container port 8080, Service port 80

### Image Configuration

```yaml
image:
  repository: ""  # Override with your application image repository (required)
  tag: ""  # Image tag/version. If empty, defaults to Chart appVersion

# List of image pull secrets for private registries
imagePullSecrets: []
  # - name: regcred
```

### Naming Configuration

```yaml
# Override the default name used for resources
# By default: {release-name}-{chart-name}
# If set: {release-name}-{nameOverride}
nameOverride: ""

# Override the fully qualified name used for resources
# If set, resources will be named exactly as specified
fullnameOverride: ""
```

### Service Account Configuration

```yaml
serviceAccount:
  create: true  # Specifies whether a service account should be created
  name: ""  # The name of the service account. If not set, a name is generated
```

### Pod Configuration

```yaml
# Annotations to add to all pods
podAnnotations: {}
  # prometheus.io/scrape: "true"
  # prometheus.io/port: "8080"

# Pod-level security context settings
podSecurityContext: {}
  # fsGroup: 2000

# Container-level security context settings
securityContext: {}
  # capabilities:
  #   drop:
  #   - ALL
  # readOnlyRootFilesystem: true
  # runAsNonRoot: true
  # runAsUser: 1000
```

### Ingress Configuration

```yaml
ingress:
  enabled: false  # Set to true to create an Ingress resource
  className: "nginx"  # Ingress class name (e.g., "nginx", "traefik", "istio")
  annotations:
    kubernetes.io/ingress.class: nginx  # Legacy annotation for ingress class
    # kubernetes.io/tls-acme: "true"  # Enable automatic TLS certificate management with cert-manager
  hosts:
    - host: app.local  # Override with your application hostname
      paths:
        - path: /  # URL path to route
          pathType: Prefix  # Path matching type: Exact, Prefix, or ImplementationSpecific
  tls: []  # TLS configuration for HTTPS
    # - secretName: app-tls  # Name of the secret containing TLS certificate
    #   hosts:
    #     - app.local  # List of hostnames covered by this certificate
```

### OpenShift Route Configuration

```yaml
route:
  enabled: true
  annotations:
    haproxy.router.openshift.io/hsts_header: max-age=31536000;includeSubDomains;preload
    haproxy.router.openshift.io/rate-limit-connections: "true"
    haproxy.router.openshift.io/rate-limit-connections.concurrent-tcp: "50"
  host: ""  # Route hostname. If empty, OpenShift will generate one
  tls:
    enabled: true
    termination: reencrypt  # TLS termination type: edge, passthrough, or reencrypt
    insecureEdgeTerminationPolicy: Redirect  # Policy for insecure HTTP: Allow, Redirect, or None
    # certificate: ""  # TLS certificate (PEM format) for custom certificates
    # key: ""  # TLS private key (PEM format) for custom certificates
    # caCertificate: ""  # CA certificate (PEM format) for reencrypt termination
```

### Bindings Configuration

Bindings are used for external service connections and sensitive data. All bindings sections are optional - environment variables are only created if the corresponding values are defined:

```yaml
bindings:
  # OAuth2/OIDC authentication configuration
  oauth2:
    issuerUri: ""  # OAuth2 issuer URI (e.g., "https://keycloak.example.com/realms/myrealm")
    clientId: ""   # OAuth2 client ID for authentication
  # Database connection configuration
  datasource:
    url: ""        # JDBC database URL (e.g., "jdbc:postgresql://db:5432/mydb")
    username: ""   # Database username
    password: ""   # Database password (stored in Secret)
  # External API integration configuration
  dummyjson:
    baseUrl: "https://dummyjson.com"  # Base URL for the DummyJSON API service
```

**Note:** If a bindings section is not provided, the corresponding environment variables will not be created. This allows for flexible configuration where not all bindings are required.

### Replica Configuration

```yaml
# Number of pod replicas to deploy
# Increase for high availability and load distribution
replicaCount: 1
```

### Resources

```yaml
# Resource limits and requests for containers
# Limits: Maximum resources a container can use (hard cap)
# Requests: Minimum resources guaranteed to a container (used for scheduling)
resources:
  limits:
    cpu: 1000m  # 1 CPU core maximum
    memory: 1Gi  # 1 Gibibyte maximum
  requests:
    cpu: 500m  # 0.5 CPU core guaranteed
    memory: 512Mi  # 512 Mebibytes guaranteed
```

### Autoscaling

```yaml
autoscaling:
  enabled: false
  minReplicas: 1
  maxReplicas: 5
  targetCPUUtilizationPercentage: 80
  targetMemoryUtilizationPercentage: 80
```

### Health Probes

```yaml
startupProbe:
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 30

readinessProbe:
  timeoutSeconds: 2
  periodSeconds: 10

livenessProbe:
  timeoutSeconds: 5
  periodSeconds: 10
```

### Environment Variables

```yaml
# Custom environment variables to inject into containers
env: []
  # - name: CUSTOM_VAR
  #   value: "custom-value"
  # - name: SECRET_VAR
  #   valueFrom:
  #     secretKeyRef:
  #       name: my-secret
  #       key: my-key

# Environment variables loaded from Kubernetes secrets
# All keys from the specified secrets will be exposed as environment variables
envFromSecrets: []
  # - name: my-secret  # Name of the Kubernetes secret (all keys will be exposed)
```

### ConfigMap Configuration

```yaml
# Application log level
# Valid values: TRACE, DEBUG, INFO, WARN, ERROR
logLevel: "INFO"

configmap:
  # Spring Boot application properties
  spring: |
    spring.servlet.multipart.max-file-size: 5MB
    spring.servlet.multipart.max-request-size: 10MB
    spring.servlet.multipart.file-size-threshold: 1MB
  # Logging configuration
  logging: |
    logging.level.root: {{ default "INFO" .Values.logLevel | quote }}
    logging.level.org: WARN
    logging.level.de.knowis: {{ default "INFO" .Values.logLevel | quote }}
  # Additional Spring Boot configuration
  extraConfiguration: |
    # Optional additional configuration
    # server.tomcat.min-spare-threads=10
    # server.http2.enabled=false
```

### Java Options

```yaml
# JVM options and arguments
# Additional Java Virtual Machine options passed to the application
javaOptions: []
  # - "-Xmx512m"
  # - "-Xms512m"
  # - "-XX:+UseG1GC"
  # - "-Djava.security.egd=file:/dev/./urandom"
```

### Pod Disruption Budget

```yaml
podDisruptionBudget:
  enabled: false  # Set to true to enable PDB
  minAvailable: 1  # Minimum number of pods that must be available
  # maxUnavailable: 1  # Alternative: maximum number of pods that can be unavailable
```

## Certificate Management

### OpenShift

On OpenShift, the chart automatically:
- Adds `service.beta.openshift.io/serving-cert-secret-name` annotation to Service
- OpenShift automatically generates the certificate secret
- Secret name: `{release-name}-service-cert`

### Kubernetes

On Kubernetes, the chart automatically:
- Creates a cert-manager Certificate resource for internal service certificates
- Requires cert-manager to be installed
- Uses issuer from `services.certification.selfSigned.issuerName`
- Secret name: `{release-name}-service-cert`

**Required configuration:**
```yaml
services:
  certification:
    selfSigned:
      issuerName: k5-cert-issuer  # Name of the cert-manager Issuer for certificates
      caIssuerName: k5-ca-issuer  # Name of the cert-manager Issuer for CA certificates
```

### Truststore

The chart mounts a truststore secret for additional CA certificates:
- Secret name: `k5-truststore` (hardcoded)
- Mount path: `/etc/k5/security/truststore`

## Secrets

Sensitive configuration is handled via the `bindings` section. Values are stored in a Kubernetes Secret:

```yaml
bindings:
  oauth2:
    issuerUri: "https://keycloak.example.com/realms/my-realm"
    clientId: "my-client-id"
  datasource:
    password: "my-password"
```

The secret is automatically created with name: `{release-name}-secrets`

## Generated Resources

The chart creates the following Kubernetes resources:

1. **Deployment** - Main application deployment
2. **Service** - ClusterIP service (port 443/80 based on SSL setting)
3. **ServiceAccount** - Service account for the pods
4. **ConfigMap** - Spring Boot configuration
5. **Secret** - Sensitive data (OAuth2, datasource)
6. **Ingress** (optional) - If `ingress.enabled: true`
7. **Route** (optional) - If `route.enabled: true` and on OpenShift
8. **HorizontalPodAutoscaler** (optional) - If `autoscaling.enabled: true`
9. **PodDisruptionBudget** (optional) - If `podDisruptionBudget.enabled: true`
10. **Certificate** (Kubernetes only) - cert-manager Certificate for service cert

## Validation

### Lint Chart

```bash
helm lint ./src/main/helm
```

### Render Templates

```bash
helm template test-release ./src/main/helm
```

### Dry-Run Installation

```bash
helm install test-release ./src/main/helm --dry-run
```

### Validate Against Cluster

```bash
helm install test-release ./src/main/helm --dry-run=server
```

## Examples

### Development Environment (HTTP only)

```yaml
replicaCount: 1
network:
  ssl:
    enabled: false  # Use HTTP
resources:
  limits:
    cpu: 500m
    memory: 512Mi
  requests:
    cpu: 250m
    memory: 256Mi
logLevel: "DEBUG"
bindings:
  datasource:
    url: "jdbc:h2:mem:dev"
```

### Production Environment

```yaml
replicaCount: 3
network:
  ssl:
    enabled: true  # HTTPS
autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 10
  targetCPUUtilizationPercentage: 80
resources:
  limits:
    cpu: 2000m
    memory: 2Gi
  requests:
    cpu: 1000m
    memory: 1Gi
podDisruptionBudget:
  enabled: true
  minAvailable: 2
bindings:
  oauth2:
    issuerUri: "https://keycloak.prod.example.com/realms/prod"
    clientId: "prod-client"
  datasource:
    url: "jdbc:postgresql://db.prod:5432/custservreqmgmt"
    username: "custservreqmgmt"
    password: "secure-password"
```

### With Custom Environment Variables

```yaml
env:
  - name: CUSTOM_CONFIG
    value: "custom-value"
  - name: SECRET_VALUE
    valueFrom:
      secretKeyRef:
        name: external-secret
        key: secret-key

envFromSecrets:
  - name: shared-secrets
```

## Uninstallation

```bash
helm uninstall custservreqmgmt
```

## Troubleshooting

### Check Pod Status

```bash
kubectl get pods -l app.kubernetes.io/name=custservreqmgmt
```

### View Logs

```bash
kubectl logs -l app.kubernetes.io/name=custservreqmgmt
```

### Check Service

```bash
kubectl get svc custservreqmgmt
```

### Check Certificates

```bash
# Service certificate
kubectl get secret custservreqmgmt-service-cert

# Application secrets
kubectl get secret custservreqmgmt-secrets
```

### Port Forward for Local Testing

```bash
# HTTPS
kubectl port-forward svc/custservreqmgmt 8443:443

# HTTP (if network.ssl.enabled: false)
kubectl port-forward svc/custservreqmgmt 8080:80
```

Then access the application:
- HTTPS: `https://localhost:8443`
- HTTP: `http://localhost:8080`

### Verify Platform Detection

The chart automatically detects the platform. To verify:

```bash
# Check if Route API is available (OpenShift)
kubectl api-resources | grep route

# Check generated resources
helm template test-release ./src/main/helm | grep -i "kind:"
```

## Chart Structure

```
helm/
├── Chart.yaml              # Chart metadata
├── values.yaml             # Default configuration values
├── README.md               # This file
└── templates/              # Kubernetes manifest templates
    ├── _helpers.tpl        # Template helper functions
    ├── deployment.yaml     # Deployment resource
    ├── service.yaml        # Service resource + Certificate (K8s)
    ├── serviceaccount.yaml # ServiceAccount resource
    ├── configmap.yaml      # ConfigMap resource
    ├── secret.yaml         # Secret resource
    ├── ingress.yaml        # Ingress resource (optional)
    ├── route.yaml          # OpenShift Route (optional)
    ├── hpa.yaml            # HorizontalPodAutoscaler (optional)
    └── pdb.yaml            # PodDisruptionBudget (optional)
```

## Notes

- The chart uses dynamic platform detection (OpenShift vs Kubernetes)
- SSL certificates are automatically managed based on platform
- All sensitive values should be provided via `bindings` section (all bindings are optional)
- Environment variables are only created if the corresponding bindings values are defined
- The chart follows best practices and is validated with `helm lint`
- Image pull policy is hardcoded to `IfNotPresent`
- Service type is hardcoded to `ClusterIP`
- Default values from `values.yaml` are always used as the base, with values files and `--set` flags overriding them
