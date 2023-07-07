# Spring Boot CRUD with Docker and Kind Kubernetes 🚀

An example of deploying a Spring Boot CRUD application using Docker and Kind Kubernetes, featuring MySQL database integration.

## 🎯 Prerequisites

Before you begin, ensure you have the following installed:
- Java 21 (OpenJDK) ☕
- Docker 🐳
- Kind - Kubernetes in Docker 🎮
- kubectl - Kubernetes command-line tool ⌨️
- Maven (or use the included wrapper) 🛠️

## 🏗️ Project Structure

```
.
├── kubernetes/           # Kubernetes manifests
│   ├── app-deploy.yml   # Spring Boot deployment
│   ├── app-service.yml  # Spring Boot service
│   ├── mysql-deploy.yml # MySQL deployment
│   └── mysql-service.yml# MySQL service
├── src/                 # Application source code
├── Dockerfile          # Docker image definition
├── pom.xml            # Maven dependencies
├── kind-config.yaml   # Kind cluster configuration
└── local-deploy.sh    # Local deployment script
```

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd springboot-docker-kubernetes-setup
```

### 2. Build the Application

For Unix-based systems:
```bash
./mvnw clean package -DskipTests
```

For Windows:
```bash
mvnw.cmd clean package -DskipTests
```

### 3. Local Deployment with Kind

The project includes a comprehensive script for local deployment. Run:

```bash
./local-deploy.sh
```

This script will:
1. Create a new Kind cluster named 'spring-crud-cluster' 🏗️
2. Pull and load the MySQL image into Kind 📥
3. Build the Spring Boot application 🔨
4. Create a Docker image and load it into Kind 🐳
5. Deploy all Kubernetes resources 🚀

### 4. Verify the Deployment

Check the status of your pods:
```bash
kubectl get pods
```

You should see both MySQL and Spring Boot pods running:
```
NAME                              READY   STATUS    RESTARTS   AGE
backend-deploy-xxxxxx-xxxx        1/1     Running   0          1m
mysql-deploy-xxxxxx-xxxx          1/1     Running   0          1m
```

### 5. Access the Application

The application is exposed on your host machine through NodePort 30006. You can access it at:
```
http://localhost:30006
```

## 📝 API Endpoints

The application provides the following REST endpoints:

- `GET /employees/all` - List all employees
- `POST /employees/create` - Create a new employee
- `GET /employees/{id}` - Get employee by ID
- `PUT /employees/{id}` - Update employee
- `DELETE /employees/{id}` - Delete employee

## 🛠️ Configuration

### Environment Variables

The application uses the following environment variables (configured in kubernetes/app-deploy.yml):

```yaml
- DB_HOST: mysqldb
- DB_NAME: crud-db
- DB_USERNAME: root
- DB_PASSWORD: Password123
```

### Kind Configuration

The Kind cluster is configured with a NodePort mapping (kind-config.yaml):

```yaml
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
name: spring-crud-cluster
nodes:
  - role: control-plane
    extraPortMappings:
      - containerPort: 30006
        hostPort: 30006
        protocol: TCP
```

## 🔧 Development

### Building the Docker Image Manually

```bash
docker build -t crud-spring-boot:local .
```

### Loading Images into Kind Manually

```bash
kind load docker-image crud-spring-boot:local --name spring-crud-cluster
```

### Manual Kubernetes Deployment

```bash
kubectl apply -f kubernetes/mysql-deploy.yml
kubectl apply -f kubernetes/mysql-service.yml
kubectl apply -f kubernetes/app-deploy.yml
kubectl apply -f kubernetes/app-service.yml
```