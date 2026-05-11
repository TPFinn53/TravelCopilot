# TravelCopilot Database Schema

## Persistent Data Architecture Specification

This document defines the authoritative persistence schema for TravelCopilot.

The schema is designed around:

* offline-first operation,
* deterministic recovery,
* contextual AI grounding,
* event timeline reconstruction,
* and future analytics expansion.

The Room database is the authoritative source of truth for all persistent application state.

---

# 1. Database Overview

## Database Name

```text
travelcopilot.db
```

## Persistence Technology

* Room Database
* SQLite backend
* Kotlin Coroutines + Flow integration

## Design Principles

* Immutable event history where possible
* Deterministic timeline ordering
* Minimal duplication
* Offline reliability
* Future migration compatibility
* Efficient reactive observation

---

# 2. Core Entity Relationship Overview

```text
TripEntity
    ├── TripEventEntity
    ├── ChatMessageEntity
    ├── RoutePointEntity
    ├── TripSummaryEntity
    └── MemoryLinkEntity

MemoryItemEntity
    └── MemoryReferenceEntity
```

---

# 3. TripEntity

## Purpose

Represents a complete travel session.

## Table Name

```text
trips
```

## Schema

| Column               | Type    | Constraints   | Description                 |
| -------------------- | ------- | ------------- | --------------------------- |
| id                   | String  | PRIMARY KEY   | Unique trip identifier      |
| name                 | String  | NOT NULL      | User-visible trip name      |
| state                | String  | NOT NULL      | Current trip state          |
| createdAt            | Long    | NOT NULL      | Creation timestamp          |
| startedAt            | Long    | NULLABLE      | Trip start timestamp        |
| endedAt              | Long    | NULLABLE      | Trip end timestamp          |
| lastUpdatedAt        | Long    | NOT NULL      | Last modification timestamp |
| originName           | String  | NULLABLE      | Human-readable origin       |
| destinationName      | String  | NULLABLE      | Human-readable destination  |
| totalDistanceMeters  | Double  | DEFAULT 0     | Total tracked distance      |
| totalDriveDurationMs | Long    | DEFAULT 0     | Total driving duration      |
| totalIdleDurationMs  | Long    | DEFAULT 0     | Total idle duration         |
| notes                | String  | NULLABLE      | User notes                  |
| archived             | Boolean | DEFAULT FALSE | Archive state               |

## Indexes

* state
* createdAt
* lastUpdatedAt

## Rules

* Only one ACTIVE trip allowed.
* ACTIVE trip must have startedAt.
* COMPLETED trip must have endedAt.

---

# 4. TripState Enum

## Purpose

Defines valid trip lifecycle states.

## Values

```kotlin
enum class TripState {
    CREATED,
    ACTIVE,
    PAUSED,
    IDLE,
    COMPLETED
}
```

---

# 5. TripEventEntity

## Purpose

Stores chronological trip events.

## Table Name

```text
trip_events
```

## Schema

| Column       | Type   | Constraints | Description            |
| ------------ | ------ | ----------- | ---------------------- |
| id           | String | PRIMARY KEY | Unique event ID        |
| tripId       | String | FOREIGN KEY | Associated trip        |
| type         | String | NOT NULL    | Event category         |
| timestamp    | Long   | NOT NULL    | Event timestamp        |
| latitude     | Double | NULLABLE    | Optional GPS latitude  |
| longitude    | Double | NULLABLE    | Optional GPS longitude |
| placeName    | String | NULLABLE    | Human-readable place   |
| title        | String | NULLABLE    | Event title            |
| description  | String | NULLABLE    | Event details          |
| metadataJson | String | NULLABLE    | Structured metadata    |
| source       | String | NULLABLE    | Event origin system    |
| createdAt    | Long   | NOT NULL    | Persistence timestamp  |

## Indexes

* tripId
* timestamp
* type
* tripId + timestamp

## Rules

* Events are append-oriented.
* Event ordering must be deterministic.
* Event mutation should be minimized.

---

# 6. TripEventType Enum

## Purpose

Defines event taxonomy.

## Suggested Values

```kotlin
enum class TripEventType {
    TRIP_STARTED,
    TRIP_PAUSED,
    TRIP_RESUMED,
    TRIP_COMPLETED,

    LOCATION_UPDATE,
    STOP_DETECTED,
    REST_STOP,
    FUEL_STOP,
    OVERNIGHT_STOP,

    USER_NOTE,
    VOICE_COMMAND,
    AI_INTERACTION,

    NAVIGATION_EVENT,
    ROUTE_DEVIATION,

    SYSTEM_EVENT,
    ERROR_EVENT
}
```

---

# 7. ChatMessageEntity

## Purpose

Stores persistent AI conversation history.

## Table Name

```text
chat_messages
```

## Schema

| Column            | Type    | Constraints          | Description               |
| ----------------- | ------- | -------------------- | ------------------------- |
| id                | String  | PRIMARY KEY          | Unique message ID         |
| tripId            | String  | FOREIGN KEY NULLABLE | Associated trip           |
| role              | String  | NOT NULL             | USER / ASSISTANT / SYSTEM |
| content           | String  | NOT NULL             | Message body              |
| timestamp         | Long    | NOT NULL             | Message timestamp         |
| voiceInput        | Boolean | DEFAULT FALSE        | Originated from speech    |
| spokenOutput      | Boolean | DEFAULT FALSE        | Spoken to user            |
| streamingComplete | Boolean | DEFAULT TRUE         | Streaming state           |
| metadataJson      | String  | NULLABLE             | Additional metadata       |

## Indexes

* tripId
* timestamp
* role
* tripId + timestamp

## Rules

* Message ordering must remain chronological.
* Streaming messages should update atomically.
* System prompts should remain distinguishable.

---

# 8. ChatRole Enum

## Values

```kotlin
enum class ChatRole {
    USER,
    ASSISTANT,
    SYSTEM
}
```

---

# 9. MemoryItemEntity

## Purpose

Persistent long-term AI memory storage.

## Table Name

```text
memory_items
```

## Schema

| Column       | Type    | Constraints  | Description                    |
| ------------ | ------- | ------------ | ------------------------------ |
| id           | String  | PRIMARY KEY  | Unique memory ID               |
| type         | String  | NOT NULL     | Memory category                |
| content      | String  | NOT NULL     | Memory content                 |
| importance   | Int     | DEFAULT 0    | Relevance score                |
| createdAt    | Long    | NOT NULL     | Creation timestamp             |
| updatedAt    | Long    | NOT NULL     | Last modification timestamp    |
| expiresAt    | Long    | NULLABLE     | Optional expiration            |
| active       | Boolean | DEFAULT TRUE | Soft deletion flag             |
| metadataJson | String  | NULLABLE     | Additional structured metadata |

## Indexes

* type
* importance
* createdAt
* active

---

# 10. MemoryType Enum

## Suggested Values

```kotlin
enum class MemoryType {
    USER_PREFERENCE,
    FAVORITE_PLACE,
    TRAVEL_PATTERN,
    PERSONAL_NOTE,
    VEHICLE_INFO,
    ROUTINE,
    REMINDER
}
```

---

# 11. RoutePointEntity

## Purpose

Stores geographic breadcrumb history.

## Table Name

```text
route_points
```

## Schema

| Column    | Type   | Constraints | Description           |
| --------- | ------ | ----------- | --------------------- |
| id        | String | PRIMARY KEY | Unique route point ID |
| tripId    | String | FOREIGN KEY | Associated trip       |
| latitude  | Double | NOT NULL    | GPS latitude          |
| longitude | Double | NOT NULL    | GPS longitude         |
| altitude  | Double | NULLABLE    | Altitude              |
| speed     | Float  | NULLABLE    | Speed estimate        |
| bearing   | Float  | NULLABLE    | Direction             |
| accuracy  | Float  | NULLABLE    | GPS accuracy          |
| timestamp | Long   | NOT NULL    | Point timestamp       |

## Indexes

* tripId
* timestamp
* tripId + timestamp

## Rules

* Route points append chronologically.
* Data thinning may occur later.
* Replay systems depend on timestamp integrity.

---

# 12. TripSummaryEntity

## Purpose

Stores generated trip summaries.

## Table Name

```text
trip_summaries
```

## Schema

| Column       | Type   | Constraints | Description            |
| ------------ | ------ | ----------- | ---------------------- |
| id           | String | PRIMARY KEY | Unique summary ID      |
| tripId       | String | FOREIGN KEY | Associated trip        |
| generatedAt  | Long   | NOT NULL    | Generation timestamp   |
| summaryType  | String | NOT NULL    | DAILY / FINAL / CUSTOM |
| content      | String | NOT NULL    | Generated summary text |
| metadataJson | String | NULLABLE    | Analytical metadata    |

## Indexes

* tripId
* generatedAt
* summaryType

---

# 13. ContextSnapshotCacheEntity

## Purpose

Optional cached snapshots for debugging, replay, or analytics.

## Table Name

```text
context_snapshots
```

## Schema

| Column       | Type   | Constraints          | Description         |
| ------------ | ------ | -------------------- | ------------------- |
| id           | String | PRIMARY KEY          | Snapshot ID         |
| tripId       | String | FOREIGN KEY NULLABLE | Associated trip     |
| createdAt    | Long   | NOT NULL             | Snapshot timestamp  |
| snapshotJson | String | NOT NULL             | Serialized snapshot |

## Indexes

* tripId
* createdAt

---

# 14. SettingsEntity

## Purpose

Persistent application settings.

## Recommended Storage

Prefer DataStore for lightweight preferences.

Only persist complex structured settings in Room if necessary.

## Suggested Fields

| Setting             | Type    |
| ------------------- | ------- |
| companionMode       | String  |
| voiceEnabled        | Boolean |
| autoSpeakResponses  | Boolean |
| wakeWordEnabled     | Boolean |
| continuousListening | Boolean |
| preferredVoice      | String  |
| drivingModeEnabled  | Boolean |
| exportLocation      | String  |

---

# 15. DAO Structure

## TripDao

### Responsibilities

* Active trip lookup
* Trip lifecycle updates
* Timeline queries
* Archive queries

---

## TripEventDao

### Responsibilities

* Event insertion
* Event timeline retrieval
* Event filtering
* Event aggregation

---

## ChatDao

### Responsibilities

* Message insertion
* Conversation retrieval
* Message streaming updates

---

## MemoryDao

### Responsibilities

* Memory retrieval
* Memory scoring
* Expiration cleanup

---

## RoutePointDao

### Responsibilities

* Route recording
* Replay retrieval
* Distance queries

---

# 16. Repository Mapping Rules

## Principles

* Entities are persistence models only.
* Domain models remain independent.
* Mapping layers isolate database schema from business logic.

## Required Layers

```text
Room Entity
    ↓
Mapper
    ↓
Domain Model
    ↓
Repository
```

---

# 17. Migration Strategy

## Rules

* All schema changes require explicit migrations.
* Destructive migration prohibited in production.
* Backward compatibility prioritized.
* Schema versioning mandatory.

## Recommended Strategy

* Incremental migrations
* Migration testing
* Schema export enabled

---

# 18. Performance Considerations

## Requirements

* Indexed timeline queries
* Efficient Flow observation
* Batched inserts where possible
* Minimized large object duplication

## Future Optimizations

* Route point thinning
* Trip archival compression
* Analytics materialization tables
* Partial loading strategies

---

# 19. Reliability Requirements

## Persistence Guarantees

The database must support:

* process death recovery,
* crash recovery,
* trip continuity,
* conversation continuity,
* and deterministic timeline replay.

## Critical Integrity Rules

* One active trip maximum.
* Event timestamps immutable.
* Message ordering preserved.
* Context reconstruction reproducible.

---

# 20. Future Expansion Areas

## Planned Schema Expansion

### Potential Future Tables

| Table                 | Purpose                 |
| --------------------- | ----------------------- |
| proactive_suggestions | AI proactive history    |
| navigation_sessions   | Navigation tracking     |
| fatigue_scores        | Driver fatigue analysis |
| vehicle_metrics       | Vehicle telemetry       |
| weather_snapshots     | Environmental awareness |
| media_attachments     | Trip photos/audio       |

---

# 21. Final Database Philosophy

The TravelCopilot database is architected as:

> A deterministic, offline-first contextual timeline engine supporting persistent AI companionship, travel continuity, replayability, and future intelligent analytics.

The schema prioritizes:

* reliability,
* contextual integrity,
* recoverability,
* and long-term extensibility.
