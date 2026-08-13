#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Configuration variables
IMAGE_NAME="srookhuizen/ai-scheduler"
TAG_NAME="latest"

echo "Logging in to Docker Hub..."
# Option 1: Interactive login (recommended for security)
docker login

# Option 2: Automated login (uncomment below if passing credentials via environment variables)
# echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

echo "Building the Docker image..."
docker build --platform linux/amd64 -t "${IMAGE_NAME}:${TAG_NAME}" .

echo "Pushing the Docker image to Docker Hub..."
docker push "${IMAGE_NAME}:${TAG_NAME}"

echo "Successfully built and pushed ${IMAGE_NAME}:${TAG_NAME}!"
