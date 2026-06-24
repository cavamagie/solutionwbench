#!/bin/bash

set -e

echo "Deploying Python Vulnerable App to OpenShift..."

NAMESPACE="vulnerable-app-test"

# Create ImageStream
oc create imagestream python-vuln-app -n $NAMESPACE 2>/dev/null || echo "ImageStream already exists"

# Build image from Dockerfile
echo "Building image..."
oc new-build --name=python-vuln-app --binary --strategy=docker -n $NAMESPACE 2>/dev/null || echo "BuildConfig already exists"

# Start build
oc start-build python-vuln-app --from-dir=. --follow -n $NAMESPACE

# Deploy
echo "Deploying application..."
cat openshift/deployment.yaml | sed "s/\${NAMESPACE}/$NAMESPACE/g" | oc apply -f - -n $NAMESPACE

echo "✓ Python app deployed successfully!"
echo "URL: https://$(oc get route python-vuln-app -n $NAMESPACE -o jsonpath='{.spec.host}')"

# Made with Bob
