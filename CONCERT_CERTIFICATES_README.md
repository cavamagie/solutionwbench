# Concert Platform - Certificate Management Guide

## 📋 Overview

This directory contains certificate files and tools for uploading SSL/TLS certificates to the Concert Platform. The certificates enable secure HTTPS communication for all Concert Platform services.

## 📁 Files Included

- **[`concert-certificates.yaml`](concert-certificates.yaml)** - Template YAML file with certificate structure for Concert Platform
- **[`prepare-concert-certs.sh`](prepare-concert-certs.sh)** - Automated script to prepare and upload certificates
- **`CONCERT_CERTIFICATES_README.md`** - This documentation file

## 🚀 Quick Start

### Option 1: Automated Setup (Recommended)

```bash
# Run the automated preparation script
./prepare-concert-certs.sh
```

This script will:
1. Check for existing certificates or generate new ones
2. Encode certificates to base64 format
3. Create a ready-to-deploy YAML file
4. Optionally apply certificates to your Kubernetes cluster

### Option 2: Manual Setup

#### Step 1: Generate Certificates

```bash
cd app-cnc-vulne
./generate-certs.sh
cd ..
```

#### Step 2: Encode Certificates

```bash
# Encode certificate
cat app-cnc-vulne/certs/server.crt | base64 | tr -d '\n' > cert.b64

# Encode private key
cat app-cnc-vulne/certs/server.key | base64 | tr -d '\n' > key.b64
```

#### Step 3: Update YAML File

Edit [`concert-certificates.yaml`](concert-certificates.yaml) and replace:
- `tls.crt` value with content from `cert.b64`
- `tls.key` value with content from `key.b64`

#### Step 4: Deploy to Cluster

```bash
# Create namespace
kubectl create namespace concert-platform

# Apply certificates
kubectl apply -f concert-certificates.yaml

# Verify deployment
kubectl get secrets,configmaps -n concert-platform
```

## 🔐 Certificate Details

### Default Configuration

- **Country**: IT (Italy)
- **State**: Italy
- **Locality**: Rome
- **Organization**: Concert Platform
- **Common Name**: concert.platform.local
- **Validity**: 365 days
- **Key Size**: RSA 2048 bits

### Subject Alternative Names (SANs)

The certificates include the following SANs:
- `concert.platform.local`
- `*.concert.platform.local`
- `localhost`
- `*.apps.example.com`
- `127.0.0.1`

## 📦 Kubernetes Resources Created

### 1. Secret: `concert-tls-certificates`
Contains the TLS certificate and private key in base64 format.

```yaml
type: kubernetes.io/tls
data:
  tls.crt: <base64-encoded-certificate>
  tls.key: <base64-encoded-private-key>
```

### 2. ConfigMap: `concert-ca-bundle`
Contains the Certificate Authority bundle (if applicable).

### 3. ConfigMap: `concert-cert-config`
Contains certificate metadata and configuration information.

## 🔧 Configuration

### Integrating with Concert Platform

Update your Concert Platform deployment to reference the certificates:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: concert-platform
spec:
  template:
    spec:
      containers:
      - name: concert
        volumeMounts:
        - name: tls-certs
          mountPath: /etc/certs
          readOnly: true
      volumes:
      - name: tls-certs
        secret:
          secretName: concert-tls-certificates
```

### Environment Variables

```yaml
env:
- name: TLS_CERT_PATH
  value: /etc/certs/tls.crt
- name: TLS_KEY_PATH
  value: /etc/certs/tls.key
- name: TLS_ENABLED
  value: "true"
```

## ✅ Verification

### Check Certificate Details

```bash
# View certificate information
kubectl get secret concert-tls-certificates -n concert-platform \
  -o jsonpath='{.data.tls\.crt}' | base64 -d | openssl x509 -text -noout

# Check expiration date
kubectl get secret concert-tls-certificates -n concert-platform \
  -o jsonpath='{.data.tls\.crt}' | base64 -d | openssl x509 -noout -dates
```

### Test HTTPS Endpoint

```bash
# Test with curl (skip certificate verification for self-signed)
curl -k https://concert.platform.local/health

# Test with openssl
openssl s_client -connect concert.platform.local:443 -servername concert.platform.local
```

### Verify Kubernetes Resources

```bash
# List all certificate-related resources
kubectl get secrets,configmaps -n concert-platform -l app=concert

# Describe the secret
kubectl describe secret concert-tls-certificates -n concert-platform

# View ConfigMap
kubectl get configmap concert-cert-config -n concert-platform -o yaml
```

## 🔄 Certificate Rotation

### When to Rotate

- Before certificate expiration (recommended: 30 days before)
- After security incidents
- When changing domain names or SANs
- Regular security policy compliance (e.g., every 90 days)

### Rotation Process

```bash
# 1. Generate new certificates
cd app-cnc-vulne
./generate-certs.sh
cd ..

# 2. Prepare new certificate bundle
./prepare-concert-certs.sh

# 3. Apply to cluster (this will update the existing secret)
kubectl apply -f concert-certificates-ready.yaml

# 4. Restart affected pods to pick up new certificates
kubectl rollout restart deployment -n concert-platform
```

## 🛡️ Security Best Practices

### ⚠️ Important Security Notes

1. **Never commit private keys to version control**
   - Add `*.key`, `*.pem`, `certs/` to `.gitignore`
   - Use secrets management tools for production

2. **Use proper Certificate Authorities for production**
   - Self-signed certificates are for testing only
   - Obtain certificates from trusted CAs (Let's Encrypt, DigiCert, etc.)

3. **Implement certificate monitoring**
   - Set up alerts for certificate expiration
   - Monitor certificate validity regularly

4. **Secure key storage**
   - Use Kubernetes secrets with encryption at rest
   - Implement RBAC to restrict secret access
   - Consider using external secret managers (Vault, AWS Secrets Manager)

5. **Use strong encryption**
   - Minimum RSA 2048 bits (4096 recommended for production)
   - Consider ECC certificates for better performance
   - Use TLS 1.2 or higher

### Recommended .gitignore Entries

```gitignore
# Certificate files
*.key
*.crt
*.csr
*.pem
*.p12
*.pfx
certs/
*.b64
concert-certificates-ready.yaml
```

## 🐛 Troubleshooting

### Certificate Not Recognized

**Problem**: Application doesn't recognize the certificate

**Solutions**:
- Verify base64 encoding is correct (no extra whitespace)
- Check certificate format is PEM
- Ensure secret is mounted correctly in pods
- Verify file permissions in container

### Permission Denied

**Problem**: Cannot apply certificates to cluster

**Solutions**:
```bash
# Check kubectl access
kubectl auth can-i create secrets -n concert-platform

# Verify namespace exists
kubectl get namespace concert-platform

# Check RBAC permissions
kubectl describe rolebinding -n concert-platform
```

### Certificate Expired

**Problem**: Certificate has expired

**Solutions**:
```bash
# Check expiration
openssl x509 -in app-cnc-vulne/certs/server.crt -noout -dates

# Generate new certificates
cd app-cnc-vulne && ./generate-certs.sh && cd ..

# Update and redeploy
./prepare-concert-certs.sh
```

### Browser Security Warnings

**Problem**: Browser shows "Your connection is not private"

**Expected for self-signed certificates**:
- Click "Advanced" → "Proceed to site"
- Or add certificate to browser's trusted certificates
- For production, use CA-signed certificates

## 📞 Support

For issues or questions:
- Check Concert Platform documentation
- Review Kubernetes logs: `kubectl logs -n concert-platform -l app=concert`
- Contact Concert Platform support team

## 📝 Additional Resources

- [Kubernetes TLS Secrets Documentation](https://kubernetes.io/docs/concepts/configuration/secret/#tls-secrets)
- [OpenSSL Certificate Commands](https://www.openssl.org/docs/man1.1.1/man1/openssl-x509.html)
- [Let's Encrypt - Free SSL/TLS Certificates](https://letsencrypt.org/)
- [cert-manager for Kubernetes](https://cert-manager.io/)

---

**Last Updated**: 2026-06-17  
**Version**: 1.0.0  
**Maintainer**: Concert Platform Team