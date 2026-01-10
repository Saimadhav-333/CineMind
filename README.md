# 🎬 CineMind Movie Recommendation System (Microservices + AI)

A **production-style Netflix-inspired movie recommendation platform** built using **Spring Boot microservices**, **API Gateway**, **JWT authentication**, **Kafka-based async processing**, **AI recommendations**, and a **React frontend**.

This project is designed to reflect **real-world backend engineering**, **system design**, and **scalable architecture**.

---

## 🚀 Key Features

- Microservices architecture
- Centralized API Gateway
- JWT-based authentication & authorization
- Role-ready Auth Service
- Event-driven Watch History using Kafka
- AI-powered movie recommendations
- Fully async, scalable design
- React + Vite + Tailwind frontend
- Clean separation of concerns (industry-grade)

---




## 🧠 System Architecture

┌───────────────┐
│ Frontend │ React + Vite + Tailwind
└───────┬───────┘
│
▼
┌───────────────────────┐
│ API Gateway │ Spring Cloud Gateway
│ - JWT Validation │
│ - CORS Handling │
│ - X-User-Id Inject │
└─────────┬─────────────┘
│
────────────────────────────────────────────────────────────
│ │ │ │ │
▼ ▼ ▼ ▼ ▼
Auth Content Watch History Recommendation AI Service
Svc Service Service Service (Python)
8084 8081 8082 8083 8090
│ │
│ ▼
│ Kafka
│ (Async Event Stream)


---
## 🛠️ Tech Stack

### Backend
- Java 21
- Spring Boot 3.x
- Spring Cloud Gateway
- Spring Data JPA
- PostgreSQL (Auth Service)
- H2 / MongoDB (Other services)
- Apache Kafka
- JWT (JJWT)

### AI / Recommendation
- Python
- FastAPI
- NumPy
- Math-based content filtering
- Genre diversity & recency bias

### Frontend
- React (Vite)
- Axios
- Tailwind CSS
- React Router DOM

---

## 🔐 Authentication & Security

- User registration & login handled by **Auth Service**
- Passwords securely hashed
- JWT issued on login
- API Gateway:
  - Validates JWT
  - Extracts userId
  - Injects `X-User-Id` header
- Stateless authentication → horizontally scalable

---

## 📦 Microservices Breakdown

### 1️⃣ Auth Service (Port: 8084)
**Responsibilities**
- User registration
- User login
- Password hashing
- JWT generation
- Role-ready (USER / ADMIN)

**Database**
- PostgreSQL

**Endpoints**
POST /auth/register
POST /auth/login

---
### 2️⃣ API Gateway (Port: 8080)
**Responsibilities**
- Single entry point
- JWT validation
- CORS handling
- Route forwarding
- Injects X-User-Id header

**Why API Gateway?**
- Centralized security
- Simplified downstream services
- Clean microservice boundaries

---

### 3️⃣ Content Service (Port: 8081)
**Responsibilities**
- Store movie metadata
- Admin-only movie ingestion
- Serve movie catalog

**Endpoints**
GET /content/movies
POST /content/admin/sync-movies


---

### 4️⃣ Watch History Service (Port: 8082)
**Responsibilities**
- Track user interactions
- Publish watch events to Kafka
- Fully asynchronous persistence

**Key Design**
- Controller publishes Kafka events
- Consumer writes to DB
- Idempotent consumer to prevent duplicates
- Eventual consistency

**Endpoints**
POST /watch


---

### 5️⃣ Recommendation Service (Port: 8083)
**Responsibilities**
- Orchestrates recommendation pipeline
- Fetches:
  - Watch history
  - Movie catalog
- Calls AI service
- Rule-based fallback if AI fails

**Endpoints**
GET /recommendations



---

### 6️⃣ AI Recommendation Service (Port: 8090)
**Responsibilities**
- Ranks movies using AI logic
- Content similarity scoring
- Genre diversity
- Recency bias

**Tech**
- FastAPI
- NumPy
- Deterministic math-based scoring

**Endpoints**
POST /recommend
GET /health



---

## 🎨 Frontend Overview

**Features**
- User registration & login
- JWT-based authentication
- Protected routes
- Personalized movie recommendations
- Logout functionality

**Design Principles**
- Axios API layer (`src/api`)
- Auth handled via React Context
- Route protection using `ProtectedRoute`
- Clean UI with Tailwind CSS

---

## 🔄 End-to-End Flow

1. User registers / logs in
2. Auth Service issues JWT
3. Frontend sends JWT to API Gateway
4. Gateway validates token
5. Gateway injects X-User-Id
6. User watches a movie
7. Watch event published to Kafka
8. Kafka consumer writes watch history
9. Recommendation Service aggregates data
10. AI Service ranks movies
11. Frontend displays personalized recommendations

---

## ⚡ Engineering Concepts Demonstrated

- Microservices architecture
- API Gateway pattern
- JWT authentication
- Event-driven architecture (Kafka)
- Async processing
- Idempotent consumers
- Service orchestration
- Scalable system design

---

## ▶️ Running the Project Locally

### Backend
1. Start Kafka (Docker)
2. Start services in this order:
   - Auth Service
   - Content Service
   - Watch History Service
   - Recommendation Service
   - AI Recommendation Service
   - API Gateway

### Frontend
```bash
npm install
npm run dev
http://localhost:5173
http://localhost:8080




