---
name: android-xml-modern-architecture
description: Design and implement modern Android applications using **XML + View-based UI** while fully adhering to **current Android architecture best practices**, explicitly excluding Jetpack Compose.
This skill defines **how to structure, state-manage, and evolve Android apps** using XML layouts, ViewBinding, ViewModel, and unidirectional data flow.
---

## When to Use

Trigger this skill when **all** of the following apply:

* UI must be implemented using **XML layouts and Views**
* Jetpack Compose is **not permitted** by technical or organizational constraints
* The project targets **modern Android architecture standards**
* Long-term maintainability and testability are required

Typical signals:

* "Use latest Android architecture but no Compose"
* "XML-based Android app with clean architecture"
* "Modern MVVM/MVI without Compose"

---

## Hard Constraints

The following constraints are **non-negotiable**:

* Jetpack Compose **must not** be introduced
* No Compose runtime or interoperability APIs
* UI is defined only via **XML layouts**
* Business logic never resides in Views or XML
* Views do not own state

---

## Non-Goals (Out of Scope)

This skill does **not** attempt to:

* Emulate Compose-style declarative UI
* Achieve feature parity with Compose tooling
* Introduce custom Views to mimic Compose behavior
* Optimize rendering beyond standard View best practices
* Define visual design systems

---

## Alignment with Google Architecture Guidance (Compose-Excluded)

This skill follows **Google’s official Android Architecture Guidance** in principle, while explicitly excluding Jetpack Compose as a UI toolkit.

The alignment is **conceptual and architectural**, not framework-dependent.

---

## Core Architecture Model

Aligned concepts:

* **UI Layer (View system)**

  * XML layouts + ViewBinding
  * Activity / Fragment as lifecycle owners only
  * No business or decision logic

* **State Holder**

  * ViewModel as the single owner of UI state
  * Survives configuration changes

* **Unidirectional Data Flow**

  * UI emits events
  * ViewModel processes events
  * ViewModel exposes immutable UI state
  * UI renders state

* **Domain Layer (Optional but Recommended)**

  * Use cases encapsulate business rules
  * UI and data layers depend on domain abstractions

* **Data Layer**

  * Repositories abstract data sources
  * No direct data access from UI layer

Guiding rule:

> The UI observes state and emits events — it never makes decisions.

---

## State Management Rules

* All UI state is modeled as immutable data classes
* State is owned exclusively by ViewModel
* Views only observe and render state
* No mutable state stored in Views

Accepted patterns:

* MVVM with unidirectional data flow
* MVI-style reducer-based state updates

Rejected patterns:

* View-driven state mutation
* Two-way data binding

---

## UI Update Policy

* Views are updated **imperatively** in response to state changes
* Visibility is explicitly controlled
* No implicit or automatic recomposition exists

Rule:

> Every UI change must be traceable to a state update.

---

## Layout Design Rules

* XML layouts represent **static structure only**
* Prefer flat hierarchies over deep nesting
* Use `ConstraintLayout` as the default root when appropriate
* Extract reusable UI blocks into separate layout files

Do not:

* Encode logic in XML
* Mirror domain models in layout files

---

## Lists and Collections

* Use `RecyclerView` for all scrollable lists
* Adapter updates must use `DiffUtil`
* ViewHolder binds state only

Lazy or dynamic composition semantics are not recreated.

---

## Event Handling

* User events are forwarded to ViewModel
* ViewModel translates events into state changes
* Views never execute business logic

Examples of events:

* Clicks
* Text input
* Scroll actions

---

## Asynchronous Work

* All async work is executed outside the UI layer
* Use Kotlin Coroutines
* ViewModel scopes lifecycle-aware work

Views only observe results.

---

## Validation Checklist

An implementation is valid only if:

* [ ] No Compose dependencies exist
* [ ] UI logic is isolated from business logic
* [ ] ViewModel owns all UI state
* [ ] XML contains no conditional logic
* [ ] RecyclerView uses DiffUtil
* [ ] Architecture supports unit testing

---

## Failure Modes

Common indicators of architectural drift:

* Views mutate state directly
* XML used to express UI conditions
* Custom Views created to replace state management
* Tight coupling between UI and domain layers

---

## Guiding Principle

> Modern Android architecture is defined by **state ownership and boundaries**,
> not by the UI toolkit in use.

XML-based UI remains viable when architectural discipline is enforced.
