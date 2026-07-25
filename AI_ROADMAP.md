# FitMate — AI Inclusion Roadmap

Ideas for adding AI to FitMate, ordered from quickest-win to most ambitious. The
current codebase is deliberately structured so these slot in without a rewrite:
matching lives behind `DiscoveryService`, so smarter ranking is a drop-in replacement.

_By the Digital COE Gen AI Team._

## 1. Smarter compatibility scoring (quick win)
Replace the rule-based score in `DiscoveryService#toCard` with a learned model that
weights goal, gym, location, experience, age gap, and schedule overlap. Start with a
simple logistic-regression / gradient-boosted model trained on historical swipe
outcomes (`swipes` table already captures LIKE/PASS labels).

## 2. Embeddings-based "people like you" recommendations
- Turn each profile (bio + goals + gym + activity) into a vector embedding.
- Store vectors in **pgvector** (Postgres extension) or a vector DB.
- Rank the feed by cosine similarity to people the user already liked.
- Cache nearest-neighbour results in the existing Redis layer.

## 3. AI onboarding & profile assistant (GenAI / LLM)
A chat assistant that helps a new user write a strong bio and pick realistic goals
("I want to start powerlifting 3x/week"). An LLM turns free text into structured
fields (`workoutGoal`, `experienceLevel`, schedule). Reuses a FastAPI microservice or
Spring AI; the frontend already has an auth + API pattern to extend.

## 4. Bio & photo moderation
Use an LLM / vision model to flag inappropriate bios or photos before they enter the
feed — protects users and keeps the marketplace healthy.

## 5. Icebreaker / conversation suggestions
On a new match, generate 2–3 personalized openers from both profiles
("You both train legs at Iron Paradise — ask about their squat program").

## 6. Churn & re-engagement signals
Predict users likely to go inactive and trigger nudges ("3 new lifters near you").

## 7. Natural-language search
"Find an advanced powerlifter near Kothrud who trains evenings" → parsed by an LLM
into structured discovery filters.

## Suggested first step
Ship **#1** (learned scoring) and **#2** (pgvector recommendations) first: highest
impact on core swipe quality, minimal new infrastructure, and both reuse the swipe
data and Redis cache already in place.
