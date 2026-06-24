#!/bin/bash

# Java Vulnerable Application Deployment Script
# This script deploys the Java application with vulnerable dependencies to OpenShift

set -e

echo "=========================================="
echo "Java Vulnerable App - OpenShift Deployment"
echo "=========================================="

# Check if oc is installed
if ! command -v oc &> /dev/null; then
    echo "Error: oc CLI is not installed"
    exit 1
fi

# Check if logged in
if ! oc whoami &> /dev/null; then
    echo "Error: Not logged in to OpenShift"
    echo "Please login first with: oc login"
    exit 1
fi

# Create namespace if it doesn't exist
echo ""
echo "Creating namespace if it doesn't exist..."
oc create namespace vulnerable-app-test --dry-run=client -o yaml | oc apply -f -

# Switch to namespace
echo "Switching to namespace..."
oc project vulnerable-app-test

# Apply ImageStream
echo ""
echo "Creating ImageStream..."
oc apply -f openshift/imagestream.yaml

# Apply BuildConfig
echo ""
echo "Creating BuildConfig..."
echo "NOTE: Update the Git repository URL in openshift/buildconfig.yaml before building"
oc apply -f openshift/buildconfig.yaml

# Apply Deployment, Service, and Route
echo ""
echo "Creating Deployment, Service, and Route..."
oc apply -f openshift/deployment.yaml

echo ""
echo "=========================================="
echo "Deployment manifests applied successfully!"
echo "=========================================="
echo ""
echo "To start a build from local source:"
echo "  oc start-build java-vuln-app --from-dir=. --follow"
echo ""
echo "To check build status:"
echo "  oc get builds"
echo "  oc logs -f bc/java-vuln-app"
echo ""
echo "To check deployment status:"
echo "  oc get pods"
echo "  oc get routes"
echo ""
echo "To get the application URL:"
echo "  oc get route java-vuln-app -o jsonpath='{.spec.host}'"
echo ""

# Made with Bob
