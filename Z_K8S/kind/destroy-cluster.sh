#!/bin/sh

echo "Destroying Kubernetes cluster..."

kind delete cluster --name bainsight-cluster
