#!/bin/bash

# Function to handle cluster creation/deletion
setup_cluster() {
    if kind get clusters | grep -q "^spring-crud-cluster$"; then
        echo "Deleting existing cluster..."
        kind delete cluster --name spring-crud-cluster
    fi
    echo "Creating new cluster..."
    kind create cluster --config kind-config.yaml
}

# Function to pull and load MySQL image
setup_mysql() {
    echo "Pulling MySQL image..."
    # Pull with platform specification
    docker pull --platform linux/amd64 mysql:8.0.36
    echo "Loading MySQL image into kind cluster..."
    kind load docker-image mysql:8.0.36 --name spring-crud-cluster
}

# Main execution
echo "Setting up cluster..."
setup_cluster

echo "Setting up MySQL..."
setup_mysql

echo "Building application..."
chmod +x mvnw
./mvnw clean package -DskipTests

# Build and load application image
JAR_FILE=$(ls target/*.jar 2>/dev/null | head -n 1)
if [ -f "$JAR_FILE" ]; then
    echo "Building Docker image with JAR: $JAR_FILE"
    docker build --build-arg JAR_FILE=$JAR_FILE -t crud-spring-boot:local .

    echo "Loading image into kind cluster..."
    kind load docker-image crud-spring-boot:local --name spring-crud-cluster

    echo "Deploying to Kubernetes..."
    kubectl apply -f kubernetes/mysql-deploy.yml
    echo "Waiting for MySQL deployment..."
    sleep 10

    kubectl apply -f kubernetes/mysql-service.yml
    echo "Waiting for MySQL service..."
    sleep 5

    kubectl apply -f kubernetes/app-deploy.yml
    echo "Waiting for backend deployment..."
    sleep 5

    kubectl apply -f kubernetes/app-service.yml

    echo "Waiting for all pods to be ready..."
    kubectl wait --for=condition=ready pod -l app=mysqldb --timeout=120s || true
    kubectl wait --for=condition=ready pod -l app=backend-app --timeout=120s || true

    echo "Deployment status:"
    kubectl get pods

    echo "Pod logs:"
    kubectl logs -l app=backend-app
else
    echo "Error: JAR file not found!"
    exit 1
fi