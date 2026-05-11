# TravelCopilot State Machine Specification

## Trip Lifecycle and Assistant Behavioral State Architecture

This document defines the authoritative state machine architecture for TravelCopilot.

The state machine system governs:

* trip lifecycle transitions,
* assistant operational behavior,
* voice interaction states,
* service lifecycle behavior,
* and recovery semantics.

The design prioritizes:

* deterministic transitions,
* crash recovery,
* offline reliability,
* and predictable contextual behavior.

---

# 1. State Machine Design Principles

## Core Principles

### Deterministic Transitions

All state transitions must be explicit and reproducible.

### Single Source of Truth

Persistent state is derived from Room-backed state models.

### Recovery Safety

State restoration after process death must be deterministic.

### Explicit Failure Handling

Error states are modeled intentionally.

### Reactive Observation

All state transitions emit observable reactive updates.

---

# 2. High-Level State Systems

TravelCopilot contains multiple coordinated state machines.

## Core State Domains

| State Machine          | Purpose                        |
| ---------------------- | ------------------------------ |
| TripStateMachine       | Trip lifecycle management      |
| VoiceStateMachine      | Voice interaction lifecycle    |
| AssistantStateMachine  | AI orchestration lifecycle     |
| ServiceStateMachine    | CopilotService lifecycle       |
| NavigationStateMachine | Navigation awareness           |
| ListeningStateMachine  | Continuous listening lifecycle |

---

# 3. Trip State Machine

## Purpose

Controls travel lifecycle behavior.

## Authoritative State

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

# 4. Trip State Definitions

## CREATED

### Meaning

Trip exists but travel has not started.

### Characteristics

* Initial trip state
* No driving activity yet
* Awaiting activation

### Allowed Actions

* Start trip
* Edit metadata
* Delete trip

---

## ACTIVE

### Meaning

User is actively traveling.

### Characteristics

* GPS monitoring active
* Context updates active
* Voice interaction enabled
* Event recording enabled

### Allowed Actions

* Pause trip
* Stop trip
* Add events
* Use assistant

---

## PAUSED

### Meaning

Trip temporarily suspended.

### Characteristics

* Trip preserved
* Limited tracking
* Context maintained
* Ready for resumption

### Allowed Actions

* Resume trip
* Complete trip

---

## IDLE

### Meaning

Trip active but user stationary.

### Characteristics

* Low movement detected
* Reduced location polling
* Context still active

### Allowed Actions

* Resume active driving
* Pause trip
* Complete trip

---

## COMPLETED

### Meaning

Trip permanently finalized.

### Characteristics

* Immutable historical record
* Analytics finalized
* Summaries generated

### Allowed Actions

* View history
* Export
* Archive

---

# 5. Trip State Transition Graph

```text
CREATED
   │
   ▼
ACTIVE ─────► PAUSED
   │             │
   │             ▼
   │◄──────── ACTIVE
   │
   ▼
IDLE
   │
   ▼
ACTIVE
   │
   ▼
COMPLETED
```

---

# 6. Legal Trip Transitions

| From      | To        | Allowed |
| --------- | --------- | ------- |
| CREATED   | ACTIVE    | YES     |
| CREATED   | COMPLETED | NO      |
| ACTIVE    | PAUSED    | YES     |
| ACTIVE    | IDLE      | YES     |
| ACTIVE    | COMPLETED | YES     |
| PAUSED    | ACTIVE    | YES     |
| PAUSED    | COMPLETED | YES     |
| IDLE      | ACTIVE    | YES     |
| IDLE      | PAUSED    | YES     |
| IDLE      | COMPLETED | YES     |
| COMPLETED | ANY       | NO      |

---

# 7. Trip State Transition Rules

## Rule 1 — Single Active Trip

Only one trip may exist in ACTIVE state.

---

## Rule 2 — Completion Finality

COMPLETED trips are immutable.

No transition out of COMPLETED allowed.

---

## Rule 3 — Timestamp Integrity

Transitions must update:

* startedAt
* endedAt
* lastUpdatedAt

atomically.

---

## Rule 4 — Event Emission

All transitions generate TripEvent records.

---

## Rule 5 — Recovery Consistency

State restoration after process death must preserve:

* active trip identity,
* timestamps,
* and context continuity.

---

# 8. Trip State Actions

## Actions

```kotlin
sealed class TripAction {
    object StartTrip
    object PauseTrip
    object ResumeTrip
    object EnterIdle
    object ExitIdle
    object CompleteTrip
}
```

---

# 9. Trip State Reducer Pattern

## Recommendation

Use deterministic reducer-based transitions.

## Example

```kotlin
fun reduce(
    current: TripState,
    action: TripAction
): TripState
```

## Requirements

* Pure transition logic
* Side effects isolated
* Unit test coverage mandatory

---

# 10. Voice State Machine

## Purpose

Controls voice interaction lifecycle.

## States

```kotlin
enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}
```

---

# 11. Voice State Definitions

## IDLE

Waiting for voice interaction.

---

## LISTENING

Microphone actively capturing speech.

---

## PROCESSING

Speech recognition or AI processing underway.

---

## SPEAKING

TTS playback active.

---

## ERROR

Voice pipeline failure occurred.

---

# 12. Voice State Transition Graph

```text
IDLE
  │
  ▼
LISTENING
  │
  ▼
PROCESSING
  │
  ▼
SPEAKING
  │
  ▼
IDLE

ERROR reachable from any state
```

---

# 13. Voice State Rules

## Rule 1 — Single Active Audio Operation

Only one audio pipeline stage active at once.

---

## Rule 2 — TTS Interruption

LISTENING may interrupt SPEAKING.

---

## Rule 3 — Audio Focus Ownership

Voice system owns audio focus during interaction.

---

## Rule 4 — Recovery

Voice failures return safely to IDLE.

---

# 14. Assistant State Machine

## Purpose

Controls AI orchestration lifecycle.

## States

```kotlin
enum class AssistantState {
    INITIALIZING,
    READY,
    PROCESSING,
    RESPONDING,
    WAITING,
    ERROR,
    SHUTDOWN
}
```

---

# 15. Assistant State Definitions

| State        | Meaning                    |
| ------------ | -------------------------- |
| INITIALIZING | Loading dependencies       |
| READY        | Ready for requests         |
| PROCESSING   | Building context or prompt |
| RESPONDING   | Generating response        |
| WAITING      | Awaiting next interaction  |
| ERROR        | Failure occurred           |
| SHUTDOWN     | Service stopping           |

---

# 16. Assistant State Rules

## Rule 1 — ContextSnapshot Requirement

All AI processing requires valid ContextSnapshot.

---

## Rule 2 — Sequential Prompt Processing

Concurrent prompt generation prohibited unless explicitly supported later.

---

## Rule 3 — Recovery Safety

Errors transition safely back to READY or WAITING.

---

# 17. CopilotService State Machine

## Purpose

Controls long-running service lifecycle.

## States

```kotlin
enum class ServiceState {
    CREATED,
    STARTING,
    RUNNING,
    DEGRADED,
    STOPPING,
    STOPPED
}
```

---

# 18. Service State Rules

## CREATED

Service allocated but not operational.

---

## STARTING

Dependencies initializing.

---

## RUNNING

Primary operational state.

---

## DEGRADED

Partial subsystem failure.

Example:

* location unavailable,
* speech failure,
* AI unavailable.

---

## STOPPING

Graceful shutdown underway.

---

## STOPPED

Service fully terminated.

---

# 19. Continuous Listening State Machine

## Purpose

Controls future wake-word and continuous listening features.

## States

```kotlin
enum class ListeningState {
    DISABLED,
    PASSIVE,
    HOT_MIC,
    DETECTED,
    PROCESSING,
    SUSPENDED
}
```

---

# 20. Listening State Definitions

| State      | Meaning                   |
| ---------- | ------------------------- |
| DISABLED   | Listening unavailable     |
| PASSIVE    | Wake-word monitoring only |
| HOT_MIC    | Open microphone mode      |
| DETECTED   | Wake-word detected        |
| PROCESSING | Speech processing active  |
| SUSPENDED  | Temporarily disabled      |

---

# 21. Navigation Awareness State Machine

## Purpose

Tracks navigation engagement.

## States

```kotlin
enum class NavigationState {
    INACTIVE,
    ROUTE_SET,
    NAVIGATING,
    REROUTING,
    ARRIVED,
    ERROR
}
```

---

# 22. Recovery State Semantics

## Process Death Recovery

System must restore:

* active trip,
* assistant readiness,
* recent context,
* conversation continuity,
* and route continuity.

---

## Recovery Priority Order

```text
Database Recovery
        ↓
Trip Restoration
        ↓
Context Reconstruction
        ↓
Service Recovery
        ↓
Voice Restoration
        ↓
Assistant Readiness
```

---

# 23. State Persistence Rules

## Persisted State

The following states persist:

* TripState
* Active trip ID
* Conversation history
* Route history
* Memory state
* Settings state

## Non-Persisted State

The following remain transient:

* current microphone amplitude,
* temporary streaming buffers,
* active TTS playback frame,
* UI animation state.

---

# 24. Event Bus Integration

## Purpose

All state transitions should emit observable events.

## Recommended Architecture

```text
StateMachine
    ↓
StateTransitionEvent
    ↓
CopilotEventBus
    ↓
Subscribers
```

---

# 25. Testing Requirements

## Mandatory Coverage

### TripStateMachine

* Full transition coverage
* Illegal transition tests
* Recovery tests
* Persistence synchronization tests

---

### VoiceStateMachine

* Interrupt handling
* Audio focus behavior
* Error recovery

---

### ServiceStateMachine

* Restart handling
* Foreground/background transitions
* Degraded mode recovery

---

# 26. Future Expansion Areas

## Planned Future State Systems

Potential future additions:

* fatigue monitoring state,
* AI proactive engagement state,
* vehicle telemetry state,
* media playback state,
* charging/power management state.

---

# 27. Recommended Implementation Structure

## Suggested Package Layout

```text
core/
    statemachine/
        trip/
        voice/
        assistant/
        service/
        navigation/
        listening/
```

---

# 28. Recommended Architectural Pattern

## Preferred Pattern

```text
Action
    ↓
Reducer
    ↓
New State
    ↓
Side Effects
    ↓
Persistence
    ↓
Event Bus
```

## Benefits

* deterministic behavior,
* testability,
* replay capability,
* and reduced hidden side effects.

---

# 29. Final State Machine Philosophy

The TravelCopilot state architecture is designed as:

> A deterministic, recoverable, reactive orchestration system enabling persistent contextual travel companionship under unreliable mobile operating conditions.

The state machine design prioritizes:

* predictability,
* safety,
* recoverability,
* and long-running operational stability.
