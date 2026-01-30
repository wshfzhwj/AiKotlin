---
name: android-architecture-views-saint
description: Expert guidance on setting up and maintaining a traditional Android application architecture using XML Layouts, ViewBinding, Clean Architecture, and Hilt. Use this when asked about project structure or architecture for non-Compose (View-based) projects.
---

# Android View-based Architecture & Modularization

## Instructions

When designing or refactoring a View-based Android application, adhere to the **Guide to App Architecture** and **Clean Architecture** principles, prioritizing XML layouts and ViewBinding.

### 1. High-Level Layers
Structure the application into three primary layers. Dependencies must strictly flow **inwards** (or downwards) to the core logic.

*   **UI Layer (Presentation)**:
    *   **Responsibility**: Displaying data via XML Layouts and handling user interactions.
    *   **Components**: Activities, Fragments, Custom Views, ViewModels, ViewBinding.
    *   **Dependencies**: Depends on the Domain Layer (or Data Layer if simple). **Never** depends on the Data Layer implementation details directly.
*   **Domain Layer (Business Logic) [Optional but Recommended]**:
    *   **Responsibility**: Encapsulating complex business rules and reuse.
    *   **Components**: Use Cases (e.g., `GetLatestNewsUseCase`), Domain Models (pure Kotlin data classes).
    *   **Pure Kotlin**: Must NOT contain any Android framework dependencies (no `android.*` imports).
    *   **Dependencies**: Depends on Repository Interfaces.
*   **Data Layer**:
    *   **Responsibility**: Managing application data (fetching, caching, saving).
    *   **Components**: Repositories (implementations), Data Sources (Retrofit APIs, Room DAOs).
    *   **Dependencies**: Depends only on external sources and libraries.

### 2. Dependency Injection with Hilt
Use **Hilt** for all dependency injection.

*   **@HiltAndroidApp**: Annotate the `Application` class.
*   **@AndroidEntryPoint**: Annotate Activities, Fragments, and Views (if needed).
*   **@HiltViewModel**: Annotate ViewModels; use standard `constructor` injection.
*   **Modules**:
    *   Use `@Module` and `@InstallIn(SingletonComponent::class)` for app-wide singletons (e.g., Network, Database).
    *   Use `@Binds` in an abstract class to bind interface implementations.

### 3. Modularization Strategy
For production apps, use a multi-module strategy to improve build times and separation of concerns.

*   **:app**: The main entry point, connects features.
*   **:core:model**: Shared domain models (Pure Kotlin).
*   **:core:data**: Repositories, Data Sources, Database, Network.
*   **:core:domain**: Use Cases and Repository Interfaces.
*   **:core:ui**: Shared Custom Views, Styles (themes.xml, styles.xml), Resources (dimens, colors), and View utilities.
*   **:feature:[name]**: Standalone feature modules containing their own UI (Fragments/Activities), XML Layouts, and ViewModels. Depends on `:core:domain` and `:core:ui`.

### 4. UI Implementation Patterns
*   **ViewBinding**: Always use ViewBinding instead of `findViewById`.
    *   In Fragments, handle the binding lifecycle carefully (null out reference in `onDestroyView`).
*   **State Management**:
    *   ViewModels expose state via `StateFlow` or `LiveData`.
    *   Fragments/Activities observe state in `onViewCreated` (using `viewLifecycleOwner`) and update Views accordingly.
*   **Navigation**: Use Jetpack Navigation Component with XML navigation graphs (`nav_graph.xml`).

### 5. Checklist for implementation
- [ ] Ensure `Domain` layer has no Android dependencies.
- [ ] Repositories should default to main-safe suspend functions.
- [ ] UI Logic must be in ViewModel, not in Activity/Fragment.
- [ ] XML Layouts should use `ConstraintLayout` as default root.
- [ ] Do not use Jetpack Compose dependencies or artifacts.
