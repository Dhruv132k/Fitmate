# FitMate 🏋️

A **Tinder-style matching app for gym partners.** Swipe right on people who share your
**workout goals**, **gym**, and **location**; when two people like each other it's a match.

Built by the **Digital COE Gen AI Team**.

## Tech stack

| Layer     | Technology                                              |
| --------- | ------------------------------------------------------- |
| Backend   | Java 17, Spring Boot 3.2 (Web, Data JPA, Security, Data Redis, Validation, Actuator) |
| Auth      | Stateless JWT (jjwt), BCrypt password hashing           |
| Database  | PostgreSQL 16                                           |
| Cache     | Redis 7 (discovery-feed + Spring cache abstraction)     |
| Frontend  | React 18 + TypeScript + Vite, React Router, Axios       |
| API docs  | OpenAPI / Swagger UI (springdoc)                        |
| Tests     | JUnit 5, Mockito, MockMvc, `@DataJpaTest` (H2), Testcontainers; Vitest + Testing Library |
| Packaging | Docker multi-stage builds + Docker Compose              |

## Architecture

```
React SPA (nginx :8081) ──/api──▶ Spring Boot API (:8080) ──▶ PostgreSQL (:5432)
                                          │
                                          └──▶ Redis (:6379)  (discovery feed cache)
```

Backend packages are organized by feature: `auth`, `user`/`profile`, `discovery`,
`swipe`, `match`, plus `security`, `config`, and `common` (error handling).

### How matching works
1. `GET /api/discovery/feed` returns a ranked, de-duplicated deck of candidates
   (excludes yourself and anyone you already swiped). Shared **goal**, **gym**, then
   **city** rank highest and produce a `compatibilityScore` + human-readable reasons.
   The deck is **cached in Redis** per user and evicted when you swipe or edit your profile.
2. `POST /api/swipes` records a `LIKE`/`PASS`. A **mutual LIKE** atomically creates a
   `Match` (stored once with ordered ids).
3. `GET /api/matches` lists everyone you matched with.

---

## Quick start (only Docker required) ✅

This is the recommended path for a fresh machine — **no Java/Node install needed.**

```bash
cp .env.example .env          # optional; sensible defaults are baked in
docker compose up --build
```

Then open:

- **App:** http://localhost:8081
- **API docs (Swagger UI):** http://localhost:8080/swagger-ui.html
- **Health:** http://localhost:8080/actuator/health

Demo accounts are seeded on first boot (password for all: `password123`):
`alex@fitmate.dev`, `bella@fitmate.dev`, `chris@fitmate.dev`, … or just **Sign up**.

Stop and reset:

```bash
docker compose down          # stop
docker compose down -v       # stop + wipe the Postgres volume
```

---

## Local development (without Docker)

Requires **JDK 17+**, **Maven 3.9+**, **Node 20+**, and a running Postgres + Redis
(you can start just those two with `docker compose up postgres redis`).

**Backend**

```bash
cd backend
mvn spring-boot:run
# API on http://localhost:8080
```

**Frontend**

```bash
cd frontend
npm install
npm run dev
# App on http://localhost:5173 (vite proxies /api to :8080)
```

---

## Testing 🧪

**Backend — unit + slice tests (fast, no Docker):**

```bash
cd backend
mvn test
```

Covers: swipe/match logic, auth, discovery scoring, JWT (unit, Mockito),
repository queries (`@DataJpaTest` on H2), and the auth controller (`@WebMvcTest` + MockMvc).

**Backend — full integration tests (Testcontainers, needs Docker running):**

```bash
cd backend
mvn verify
```

Spins up **real Postgres + Redis containers** and exercises the end-to-end flow:
register → mutual like → match created → feed refreshed, plus auth enforcement.

**Frontend:**

```bash
cd frontend
npm install
npm test          # Vitest + Testing Library
```

---

## Configuration

All backend settings are env-overridable (see `backend/src/main/resources/application.yml`
and `.env.example`). Key ones: `SPRING_DATASOURCE_*`, `SPRING_REDIS_*`,
`FITMATE_JWT_SECRET` (**change in production**), `FITMATE_CORS_ALLOWED_ORIGINS`,
`FITMATE_SEED_ENABLED`.

## API summary

| Method | Endpoint                | Auth | Purpose                        |
| ------ | ----------------------- | ---- | ------------------------------ |
| POST   | `/api/auth/register`    | ❌   | Create account, returns JWT    |
| POST   | `/api/auth/login`       | ❌   | Log in, returns JWT            |
| GET    | `/api/profile/me`       | ✅   | Current user's profile         |
| PUT    | `/api/profile/me`       | ✅   | Update profile                 |
| GET    | `/api/discovery/feed`   | ✅   | Ranked swipe deck (Redis-cached) |
| POST   | `/api/swipes`           | ✅   | Like/pass; may create a match  |
| GET    | `/api/matches`          | ✅   | List matches                   |

## Future: AI features
See **[AI_ROADMAP.md](AI_ROADMAP.md)** for planned AI enhancements (smart compatibility
scoring, embeddings-based recommendations, an AI onboarding assistant, and more).
