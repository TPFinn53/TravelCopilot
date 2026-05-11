# TravelCopilot Architecture Decisions

## Immutable Architectural Decision Record (ADR)

This document defines the foundational architectural decisions for TravelCopilot.

These decisions are considered locked unless a future change is justified by:

* critical reliability concerns,
* severe scalability limitations,
* platform incompatibility,
* or substantial product evolution.

The purpose of this document is to:

* prevent architectural drift,
* maintain long-term consistency,
* support onboarding,
* reduce refactor risk,
* and preserve system integrity.

---

# ADR-001 — Offline-First System Design

## Status

LOCKED

## Decision

TravelCopilot is designed as an offline-first application.

Core functionality must continue operating without internet connectivity.

## Rationale

Road travel environments frequently experience:

* poor cellular coverage,
* intermittent connectivity,
* and complete network loss.

The application must remain reliable during:

* remote travel,
* overnight stays,
* cross-country trips,
* and vehicle-based living.

## Requirements

The following systems must remain functional offline:

* trip tracking,
* event logging,
* conversation history,
* voice interaction,
* persistence,
* local AI orchestration,
* and context management.

## Implications

* Network access is optional, not foundational.
* Local persistence takes precedence over cloud state.
* Remote synchronization must never block primary workflows.
* Future cloud systems must operate asynchronously.

---

# ADR-002 — Room Database as Source of Truth

## Status

LOCKED

## Decision

Room Database is the authoritative persistence layer for all primary application state.

## Rationale

The application requires:

* reliable persistence,
* transactional consistency,
* offline operation,
* reactive observation,
* and structured query support.

Room satisfies these requirements while integrating naturally with:

* Kotlin Flow,
* Coroutines,
* and MVVM architecture.

## Requirements

The following data domains persist through Room:

* trips,
* trip events,
* chat history,
* memory items,
* route history,
* and future analytics data.

## Rules

* Repositories read/write through DAOs.
* UI never directly accesses DAOs.
* In-memory caches are considered temporary optimization layers only.
* Room remains authoritative after process death.

## Implications

* Database schema evolution must be carefully versioned.
* All migrations require backward compatibility.
* Persistent recovery logic depends on Room consistency.

---

# ADR-003 — Single Active Trip Constraint

## Status

LOCKED

## Decision

TravelCopilot supports only one active trip at a time.

## Rationale

The application models a single continuous travel context.

Multiple concurrent active trips would introduce:

* ambiguous context ownership,
* conflicting event streams,
* unreliable AI grounding,
* and complex recovery semantics.

## Rules

* Only one trip may exist in ACTIVE state.
* Starting a new trip must finalize or pause the previous trip.
* All active events attach to the current active trip.
* ContextSnapshot references only one active trip.

## Implications

* Simplified AI context generation.
* Simplified persistence recovery.
* Deterministic timeline ownership.
* Reduced synchronization complexity.

---

# ADR-004 — Voice-First Interaction Model

## Status

LOCKED

## Decision

TravelCopilot is fundamentally a voice-first application.

## Rationale

The application is designed primarily for:

* driving,
* hands-free interaction,
* and low-distraction operation.

Text input is supported, but secondary.

## Requirements

Core workflows must support voice interaction:

* assistant invocation,
* messaging,
* navigation commands,
* trip actions,
* and summaries.

## UX Principles

* Minimize required touch interaction.
* Prioritize large driving-safe controls.
* Optimize glanceability.
* Provide clear audio feedback.

## Implications

* Voice lifecycle reliability is critical.
* Audio focus management becomes foundational.
* Continuous listening may become a future requirement.
* Wake-word support is architecturally anticipated.

---

# ADR-005 — Background Service Ownership

## Status

LOCKED

## Decision

Long-running assistant orchestration belongs to CopilotService.

## Rationale

The assistant must survive:

* activity recreation,
* UI navigation,
* process lifecycle changes,
* and extended travel sessions.

Background orchestration cannot depend on Compose UI lifecycle.

## Responsibilities of CopilotService

* voice coordination,
* speech orchestration,
* conversation management,
* proactive behaviors,
* long-running operations,
* and future wake-word coordination.

## Rules

* UI layers do not own assistant state.
* ViewModels coordinate with the service but do not replace it.
* Background assistant behavior must remain service-driven.

## Implications

* Foreground service behavior may be required.
* Notification architecture becomes important.
* Battery optimization strategies are required.

---

# ADR-006 — ContextSnapshot Schema Authority

## Status

LOCKED

## Decision

ContextSnapshot is the authoritative contextual aggregation model for AI grounding.

## Rationale

AI systems require a unified, deterministic representation of application context.

Without a centralized context contract, the application risks:

* fragmented prompts,
* inconsistent AI behavior,
* duplicated logic,
* and unreliable assistant reasoning.

## ContextSnapshot Must Aggregate

* active trip state,
* current location,
* place information,
* current timing,
* driving duration,
* idle duration,
* recent events,
* recent messages,
* and future memory context.

## Rules

* ContextSnapshot generation is centralized.
* Downstream AI systems consume snapshots rather than raw repositories.
* Snapshot structure changes require deliberate versioning.
* Event ordering rules must remain deterministic.

## Implications

* PromptBuilder depends on ContextSnapshot consistency.
* Recovery systems depend on snapshot reproducibility.
* Future analytics may consume snapshot streams.

---

# ADR-007 — Repository Ownership Boundaries

## Status

LOCKED

## Decision

Repositories are the exclusive business-domain access layer between persistence/services and UI/ViewModels.

## Rationale

The repository layer exists to:

* isolate persistence concerns,
* simplify testing,
* centralize business rules,
* and reduce coupling.

## Rules

### UI Layer

* Must not directly access DAOs.
* Must not directly manipulate persistence.

### ViewModels

* Coordinate repositories.
* Expose immutable state.
* Avoid persistence logic.

### Repositories

* Own business-domain data coordination.
* Handle DAO orchestration.
* Coordinate persistence transactions.
* Expose reactive Flows.

### Services

* May interact with repositories.
* Must not bypass repository boundaries.

## Implications

* Business logic consolidation becomes easier.
* Testing becomes simpler.
* Future backend replacement becomes feasible.

---

# ADR-008 — Compose-Only UI Policy

## Status

LOCKED

## Decision

TravelCopilot uses Jetpack Compose as the exclusive UI framework.

## Rationale

Compose provides:

* reactive UI patterns,
* cleaner state observation,
* faster iteration,
* modern Android architecture alignment,
* and simplified dynamic UI construction.

Maintaining mixed XML + Compose architecture would:

* increase maintenance complexity,
* duplicate UI paradigms,
* and slow development.

## Rules

* New UI screens use Compose exclusively.
* XML layouts are not introduced for new features.
* UI state is reactive and Flow-driven.
* Compose navigation remains the navigation framework.

## Implications

* View-based legacy patterns are avoided.
* UI consistency improves.
* Compose lifecycle rules become foundational.

---

# ADR-009 — Kotlin Coroutines and Flow Standardization

## Status

LOCKED

## Decision

Kotlin Coroutines and Flow are the standard concurrency and reactive stream model.

## Rationale

The application depends heavily on:

* asynchronous processing,
* reactive UI updates,
* streaming state,
* and lifecycle-safe concurrency.

## Rules

* Suspend functions preferred for async operations.
* Flow preferred for reactive state.
* StateFlow preferred for UI state exposure.
* Blocking calls prohibited on main thread.

## Implications

* Structured concurrency becomes standard.
* Lifecycle-safe collection patterns are required.
* Flow consistency simplifies architecture.

---

# ADR-010 — Local Data Ownership Philosophy

## Status

LOCKED

## Decision

User data belongs locally to the user by default.

## Rationale

TravelCopilot handles sensitive data including:

* location history,
* voice interactions,
* conversation logs,
* travel routines,
* and personal notes.

The default architecture prioritizes privacy and user control.

## Rules

* Cloud sync is optional.
* Local persistence remains primary.
* User export capabilities are required.
* Remote systems must remain transparent and optional.

## Implications

* Encryption may become required.
* Backup systems must remain user-controlled.
* Sync conflicts must preserve local integrity.

---

# ADR-011 — Modular Service-Oriented Core Architecture

## Status

LOCKED

## Decision

Core systems are modular and independently replaceable.

## Rationale

The project is expected to evolve significantly over time.

Major systems may eventually change:

* AI providers,
* wake-word engines,
* mapping providers,
* navigation systems,
* and memory engines.

## Rules

* Core systems communicate through stable interfaces.
* Implementations remain replaceable.
* Avoid direct vendor lock-in.

## Implications

* Adapter layers become important.
* Interface-first design remains preferred.
* Dependency injection remains foundational.

---

# ADR-012 — Deterministic Event Timeline Model

## Status

LOCKED

## Decision

Trip events form a deterministic chronological timeline.

## Rationale

Future features depend heavily on event integrity:

* replay,
* summaries,
* analytics,
* proactive suggestions,
* and memory extraction.

## Rules

* Events require timestamps.
* Event ordering must be reproducible.
* Event duplication must be prevented.
* Event mutation should be minimized.

## Implications

* Timeline reconstruction becomes reliable.
* Replay systems become feasible.
* AI summarization improves.

---

# Governance Rules

## Architecture Change Policy

Changes to locked ADRs require:

1. documented rationale,
2. migration analysis,
3. reliability impact analysis,
4. and explicit approval.

## Allowed Evolution Areas

The following areas remain intentionally flexible:

* AI provider implementations,
* prompt engineering,
* UI refinement,
* navigation integrations,
* cloud systems,
* analytics,
* and proactive behavior strategies.

---

# Final Architectural Identity

TravelCopilot is architecturally defined as:

> An offline-first, voice-first, context-aware AI travel companion built on a modular Android architecture using Kotlin, Compose, Room, Coroutines, and persistent contextual orchestration.

This identity should remain stable across future development phases.
