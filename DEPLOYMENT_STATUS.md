# Deployment Status - Vulnerable Applications for IBM Concert Testing

## Overview
Three applications with intentionally vulnerable/outdated dependencies have been created and deployed to OpenShift for IBM Concert vulnerability detection testing.

## Current Status

### ✅ Node.js Application (vulnerable-app)
- **Status**: Running successfully
- **Pod**: Running in namespace `vulnerable-app-test`
- **Routes**: Configured and accessible via OpenShift routes
- **Vulnerable Dependencies**: 14 outdated packages including Express 4.16.0, Lodash 4.17.4, Moment 2.19.3, etc.
- **Self-signed Certificates**: ✅ Generated and configured
- **SBOM**: ✅ Generated (CycloneDX and SPDX formats)

### ⚠️ Python Application (python-vuln-app)
- **Status**: Build issues - CrashLoopBackOff
- **Issue**: Docker Hub rate limit preventing image pulls
- **Routes**: Created but application not running
- **Vulnerable Dependencies**: Flask 0.12.2, Django 1.11.0, requests 2.6.0, PyYAML 3.12, etc.
- **SBOM**: ✅ Generated (CycloneDX and SPDX formats)
- **Action Needed**: Updated Dockerfile to use Red Hat UBI images, needs rebuild

### ⚠️ Java Application (java-vuln-app)
- **Status**: Build issues - Docker Hub rate limit
- **Issue**: Cannot pull Maven and OpenJDK images from Docker Hub
- **Routes**: Created but application not running
- **Vulnerable Dependencies**: Spring 4.3.0, Log4j 2.14.0, Jackson 2.8.0, Hibernate 5.2.0, etc.
- **SBOM**: ✅ Generated (CycloneDX and SPDX formats)
- **Action Needed**: Updated Dockerfile to use Red Hat UBI images, needs rebuild

## Repository Structure

```
example-app/
├── app-cnc-vulne/          # Node.js vulnerable application (RUNNING)
│   ├── server.js
│   ├── package.json        # 14 vulnerable dependencies
│   ├── Dockerfile
│   ├── certs/              # Self-signed SSL certificates
│   ├── openshift/          # K8s manifests
│   ├── sbom-cyclonedx.json # SBOM in CycloneDX format
│   ├── sbom-spdx.json      # SBOM in SPDX format
│   └── deploy.sh
│
├── python-vuln-app/        # Python vulnerable application (NEEDS REBUILD)
│   ├── app.py
│   ├── requirements.txt    # 9 vulnerable dependencies
│   ├── Dockerfile          # Updated to use UBI
│   ├── openshift/
│   ├── sbom-cyclonedx.json # SBOM in CycloneDX format
│   ├── sbom-spdx.json      # SBOM in SPDX format
│   └── deploy.sh
│
└── java-vuln-app/          # Java vulnerable application (NEEDS REBUILD)
    ├── pom.xml             # 8 vulnerable dependencies
    ├── src/main/java/com/example/VulnApp.java
    ├── Dockerfile          # Updated to use UBI
    ├── openshift/
    ├── sbom-cyclonedx.json # SBOM in CycloneDX format
    ├── sbom-spdx.json      # SBOM in SPDX format
    └── deploy.sh
```

## SBOM (Software Bill of Materials)

All three applications have SBOM files generated using Syft in two standard formats:
- **CycloneDX JSON**: Industry-standard SBOM format
- **SPDX JSON**: Linux Foundation standard format

These SBOM files contain complete dependency information including:
- Package names and versions
- License information
- Dependency relationships
- Known vulnerabilities (when scanned)

## Next Steps

### To Complete Python and Java Deployments:

1. **Python Application**:
   ```bash
   cd python-vuln-app
   oc start-build python-vuln-app --from-dir=. --follow -n vulnerable-app-test
   ```

2. **Java Application**:
   ```bash
   cd java-vuln-app
   oc start-build java-vuln-app --from-dir=. --follow -n vulnerable-app-test
   ```

Both Dockerfiles have been updated to use Red Hat UBI (Universal Base Images) which don't have Docker Hub rate limits.

## Testing with IBM Concert

Once all three applications are running, IBM Concert should be able to detect:

1. **Vulnerable Dependencies**: All three apps contain intentionally outdated packages with known CVEs
2. **Self-signed Certificates**: Node.js app uses self-signed SSL certificates
3. **Multiple Language Stacks**: Node.js, Python, and Java applications
4. **Security Issues**: Hardcoded secrets, weak JWT tokens, SQL injection patterns, etc.
5. **SBOM Analysis**: Complete software bill of materials for dependency tracking

## Automatic Updates

All applications are configured with BuildConfigs that trigger automatic rebuilds when the Git repository is updated:
- Webhook secrets configured
- Git repository monitoring enabled
- Automatic deployment on successful builds

## Access URLs

Access URLs are available via OpenShift routes. Use `oc get routes -n vulnerable-app-test` to view them.

## Security Notes

- All sensitive information (cluster URLs, secrets, keys) have been removed from repository files
- Self-signed certificates are for testing purposes only
- Vulnerable dependencies are intentional for security testing
- Applications should only be used in isolated test environments

## Notes

- The namespace `vulnerable-app-test` contains all three applications
- BuildConfigs are configured but need Git repository URL to be updated for automatic builds
- SBOM files are included in each application directory for vulnerability scanning