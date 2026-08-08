# Real-Time Food Delivery Order Management System

## Problem Statement
Build a scalable, real-time order management system for food delivery that handles:
- Real-time order placement and status updates
- Instant notifications for restaurants and delivery drivers
- High concurrency during peak hours (1000+ orders/minute)
- Order tracking and management dashboard
- Multi-tenant architecture for multiple restaurants

## Tech Stack
- **Backend**: Spring Boot 3.2 (Java 17)
- **Real-time Communication**: WebSocket + STOMP
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Message Queue**: RabbitMQ
- **Frontend**: React 18 + TypeScript
- **Container**: Docker
- **Orchestration**: AWS EKS (Kubernetes)
- **CI/CD**: GitHub Actions

## Architecture
```
┌─────────────┐          ┌──────────────┐       ┌─────────────┐
│   React     │────────▶ |  Spring Boot │ ────▶ │ PostgreSQL  │
│  Frontend   │WebSocket │   Backend    │ JDBC  │  Database   │
└─────────────┘          └──────────────┘       └─────────────┘
                                │
                                ▼
                         ┌──────────────┐
                         │    Redis     │
                         │    Cache     │
                         └──────────────┘
                                │
                                ▼
                         ┌──────────────┐
                         │   RabbitMQ   │
                         │   Message    │
                         │    Queue     │
                         └──────────────┘
```

## Features
1. **Real-time Order Updates**: WebSocket-based instant notifications
2. **Order Management**: CRUD operations for orders
3. **Restaurant Dashboard**: Live order tracking
4. **Driver Assignment**: Automatic driver assignment algorithm
5. **Analytics**: Real-time order statistics
6. **Scalability**: Horizontal scaling on EKS

## Project Structure
```
project/
├── backend/                 # Spring Boot application
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                # React application
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── kubernetes/              # EKS manifests
│   ├── backend-deployment.yaml
│   ├── frontend-deployment.yaml
│   ├── postgres-deployment.yaml
│   ├── redis-deployment.yaml
│   └── rabbitmq-deployment.yaml
├── docker-compose.yml       # Local development
└── README.md
```

## Getting Started

### Prerequisites
- Docker Desktop
- kubectl
- AWS CLI (for EKS)
- Java 17
- Node.js 18+

### Local Development
```bash
docker-compose up -d
```

### EKS Deployment
```bash
# Create EKS cluster
eksctl create cluster --name food-delivery --region us-east-1

# Deploy applications
kubectl apply -f kubernetes/
```

## API Endpoints
- POST /api/orders - Create new order
- GET /api/orders/{id} - Get order details
- PUT /api/orders/{id}/status - Update order status
- GET /api/orders/restaurant/{id} - Get restaurant orders
- WebSocket /ws/orders - Real-time order updates

## Real-time Events
- ORDER_CREATED
- ORDER_ACCEPTED
- ORDER_PREPARING
- ORDER_READY
- ORDER_PICKED_UP
- ORDER_DELIVERED
- ORDER_CANCELLED
