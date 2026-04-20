# 🚀 Backend Engineering Assignment: Core API & Guardrails

## 📌 Overview
This project is a **Spring Boot microservice** that acts as a centralized API and guardrail system for managing posts, comments, and interactions.

It is designed to:
- Handle **high concurrency**
- Enforce **strict guardrails using Redis**
- Maintain **stateless architecture**
- Simulate real-world backend systems

---

## 🛠️ Tech Stack
- **Java 21**
- **Spring Boot 3**
- **PostgreSQL** – Persistent storage (source of truth)
- **Redis** – Real-time processing & guardrails
- **Spring Data JPA**
- **Spring Data Redis**
- **Docker**

---

## ⚙️ Features

---

### ✅ Phase 1: Core API & Database

#### APIs Implemented

| Feature | Endpoint |
|--------|--------|
| Create Post | `POST /api/posts` |
| Add Comment | `POST /api/posts/{postId}/comments` |
| Like Post | `POST /api/posts/{postId}/like` |

---

#### Entities

- **User**
- **Bot**
- **Post**
- **Comment**
  - Supports nested replies using `parentCommentId`
  - Depth calculated dynamically

---

### ⚡ Phase 2: Redis Virality Engine & Guardrails

| Action | Points |
|------|--------|
| Bot Reply | +1 |
| Human Comment | +50 |
| Human Like | +20 |

---

#### 🔒 Guardrails (Atomic & Thread-Safe)

---

### 1. Horizontal Cap (Bot Limit)

- Max **100 bot comments per post**
- Redis Key: post:{id}:bot_count
---

- Uses **atomic INCR operation**
- Includes rollback if limit exceeded

---

### 2. Vertical Cap (Depth Limit)

- Max depth = **20**
- Depth calculated using parent comment:
  depth = parent.depth + 1
- Prevents deep recursion

---

### 3. Cooldown Cap

- A bot cannot interact with the same user more than once in **10 minutes**
- Redis Key:cooldown:bot_{botId}:human_{userId}

---
### 🔔 Phase 3: Notification Engine (Smart Batching)

---

#### 📌 Redis Throttler

- First interaction → immediate notification
- Subsequent interactions → stored in Redis

Keys:notif:user:{id}
user:{id}:pending_notifs
users_with_notifications

#### ⏰ Scheduled Notification Processing

- Runs every **5 minutes** using `@Scheduled`
- Fetches pending notifications
- Outputs summary:

  
- Clears processed notifications

---

## 🧠 Architecture
Controller → Service → Redis (Guardrails) → PostgreSQL (Persistence)
- **Redis = decision layer**
- **PostgreSQL = source of truth**

---

## ⚡ Concurrency Handling

- Redis atomic operations (`INCR`) ensure:
  - No race conditions
  - Safe concurrent updates
- Bot comment limit strictly enforced (max 100)

---



