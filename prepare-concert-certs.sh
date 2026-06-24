#!/bin/bash

# Script to prepare and upload certificates to Concert Platform
# Usage: ./prepare-concert-certs.sh [options]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
NAMESPACE="concert-platform"
CERT_DIR="certs"
OUTPUT_FILE="concert-certificates-ready.yaml"

echo "================================================"
echo "Concert Platform Certificate Preparation Tool"
echo "================================================"
echo ""

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo "ℹ $1"
}

# Check if certificates exist
check_certificates() {
    print_info "Checking for existing certificates..."
    
    if [ ! -d "$CERT_DIR" ]; then
        print_warning "Certificate directory not found. Generating new certificates..."
        if [ -f "app-cnc-vulne/generate-certs.sh" ]; then
            cd app-cnc-vulne
            ./generate-certs.sh
            cd ..
            CERT_DIR="app-cnc-vulne/certs"
        else
            print_error "Certificate generation script not found!"
            exit 1
        fi
    fi
    
    if [ ! -f "$CERT_DIR/server.crt" ] || [ ! -f "$CERT_DIR/server.key" ]; then
        print_error "Certificate files not found in $CERT_DIR"
        exit 1
    fi
    
    print_success "Certificates found"
}

# Encode certificates to base64
encode_certificates() {
    print_info "Encoding certificates to base64..."
    
    # Encode certificate (single line, no wrapping)
    CERT_B64=$(cat "$CERT_DIR/server.crt" | base64 | tr -d '\n')
    
    # Encode private key (single line, no wrapping)
    KEY_B64=$(cat "$CERT_DIR/server.key" | base64 | tr -d '\n')
    
    print_success "Certificates encoded"
}

# Create the final YAML file with actual certificates
create_yaml() {
    print_info "Creating Concert Platform certificate YAML..."
    
    cat > "$OUTPUT_FILE" << EOF
# Certificate Bundle for Concert Platform
# Generated: $(date -u +"%Y-%m-%dT%H:%M:%SZ")
# Purpose: SSL/TLS certificates for secure communication

---
apiVersion: v1
kind: Secret
metadata:
  name: concert-tls-certificates
  namespace: $NAMESPACE
  labels:
    app: concert
    component: security
    environment: production
    created-by: prepare-concert-certs
type: kubernetes.io/tls
data:
  tls.crt: $CERT_B64
  tls.key: $KEY_B64

---
apiVersion: v1
kind: ConfigMap
metadata:
  name: concert-cert-config
  namespace: $NAMESPACE
  labels:
    app: concert
    component: configuration
data:
  certificate-info.txt: |
    Certificate Information
    ======================
    Generated: $(date -u +"%Y-%m-%d %H:%M:%S UTC")
    
    Certificate Details:
    $(openssl x509 -in "$CERT_DIR/server.crt" -noout -subject -issuer -dates)
    
    Subject Alternative Names:
    $(openssl x509 -in "$CERT_DIR/server.crt" -noout -text | grep -A1 "Subject Alternative Name" || echo "    None")
EOF
    
    print_success "YAML file created: $OUTPUT_FILE"
}

# Display certificate information
show_cert_info() {
    print_info "Certificate Information:"
    echo ""
    openssl x509 -in "$CERT_DIR/server.crt" -noout -text | grep -E "(Subject:|Issuer:|Not Before|Not After|DNS:|IP Address:)" || true
    echo ""
}

# Apply to Kubernetes cluster
apply_to_cluster() {
    print_info "Applying certificates to Concert Platform..."
    
    # Check if kubectl is available
    if ! command -v kubectl &> /dev/null; then
        print_warning "kubectl not found. Skipping cluster deployment."
        print_info "To deploy manually, run: kubectl apply -f $OUTPUT_FILE"
        return
    fi
    
    # Create namespace if it doesn't exist
    kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f - 2>/dev/null || true
    
    # Apply the certificates
    if kubectl apply -f "$OUTPUT_FILE"; then
        print_success "Certificates applied to cluster"
        
        # Verify
        echo ""
        print_info "Verifying deployment..."
        kubectl get secret concert-tls-certificates -n "$NAMESPACE" 2>/dev/null && print_success "Secret created"
        kubectl get configmap concert-cert-config -n "$NAMESPACE" 2>/dev/null && print_success "ConfigMap created"
    else
        print_error "Failed to apply certificates to cluster"
        print_info "You can apply manually with: kubectl apply -f $OUTPUT_FILE"
    fi
}

# Main execution
main() {
    echo ""
    check_certificates
    encode_certificates
    create_yaml
    show_cert_info
    
    echo ""
    read -p "Do you want to apply certificates to the cluster now? (y/N): " -n 1 -r
    echo ""
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        apply_to_cluster
    else
        print_info "Skipping cluster deployment"
        print_info "To deploy later, run: kubectl apply -f $OUTPUT_FILE"
    fi
    
    echo ""
    print_success "Certificate preparation complete!"
    echo ""
    echo "Next steps:"
    echo "1. Review the generated file: $OUTPUT_FILE"
    echo "2. Apply to cluster: kubectl apply -f $OUTPUT_FILE"
    echo "3. Verify deployment: kubectl get secrets,configmaps -n $NAMESPACE"
    echo "4. Configure Concert Platform to use the certificates"
    echo ""
}

# Run main function
main

# Made with Bob
