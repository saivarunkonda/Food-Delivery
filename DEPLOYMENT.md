# EKS Deployment Guide

## Prerequisites
- AWS CLI configured with appropriate permissions
- kubectl installed
- eksctl installed
- Docker installed

## Step 1: Create EKS Cluster

```bash
# Create cluster configuration
eksctl create cluster \
  --name food-delivery \
  --region us-east-1 \
  --nodes 3 \
  --node-type t3.medium \
  --with-oidc \
  --ssh-access \
  --ssh-public-key your-key-pair

# Wait for cluster creation (takes 10-15 minutes)
```

## Step 2: Configure kubectl

```bash
# Update kubeconfig
aws eks update-kubeconfig --name food-delivery --region us-east-1

# Verify connection
kubectl get nodes
```

## Step 3: Create ECR Repository

```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

# Create repository
aws ecr create-repository --repository-name order-management --region us-east-1

# Create repository for frontend
aws ecr create-repository --repository-name food-delivery-frontend --region us-east-1
```

## Step 4: Build and Push Docker Images

```bash
# Build backend image
cd backend
docker build -t order-management .
docker tag order-management:latest YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/order-management:latest
docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/order-management:latest

# Build frontend image
cd ../frontend
docker build -t food-delivery-frontend .
docker tag food-delivery-frontend:latest YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/food-delivery-frontend:latest
docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/food-delivery-frontend:latest
```

## Step 5: Update Kubernetes Manifests

Replace `your-ecr-repo` in `kubernetes/backend-deployment.yaml` with your actual ECR repository URL.

## Step 6: Deploy to EKS

```bash
# Deploy PostgreSQL
kubectl apply -f kubernetes/postgres-deployment.yaml

# Deploy Redis
kubectl apply -f kubernetes/redis-deployment.yaml

# Deploy RabbitMQ
kubectl apply -f kubernetes/rabbitmq-deployment.yaml

# Wait for databases to be ready
kubectl wait --for=condition=ready pod -l app=postgres --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis --timeout=300s
kubectl wait --for=condition=ready pod -l app=rabbitmq --timeout=300s

# Deploy Backend
kubectl apply -f kubernetes/backend-deployment.yaml

# Deploy HPA
kubectl apply -f kubernetes/hpa.yaml

# Verify deployment
kubectl get pods
kubectl get services
```

## Step 7: Access Application

```bash
# Get backend service URL
kubectl get service backend-service

# Port forward for local testing
kubectl port-forward service/backend-service 8080:80

# Access application at http://localhost:8080
```

## Step 8: Monitor and Scale

```bash
# Check HPA status
kubectl get hpa

# Check pod metrics
kubectl top pods

# Scale manually if needed
kubectl scale deployment order-management-backend --replicas=5

# View logs
kubectl logs -f deployment/order-management-backend
```

## Step 9: Cleanup

```bash
# Delete all resources
kubectl delete -f kubernetes/

# Delete cluster
eksctl delete cluster --name food-delivery --region us-east-1
```

## Troubleshooting

### Pods not starting
```bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

### Service not accessible
```bash
kubectl get endpoints
kubectl describe service <service-name>
```

### Database connection issues
```bash
kubectl exec -it <postgres-pod> -- psql -U fooddelivery -d fooddelivery
```

## Cost Optimization

- Use Spot Instances for worker nodes
- Enable Cluster Autoscaler
- Set appropriate resource limits
- Use reserved instances for production workloads
