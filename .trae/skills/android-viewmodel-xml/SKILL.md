---
name: android-viewmodel-xml
description: Expert guidance on creating modern Android ViewModels specifically for XML-based layouts (View system). Follows latest standards including Hilt, StateFlow, and SavedStateHandle while strictly avoiding Jetpack Compose dependencies.
---

# Modern Android ViewModel (XML-based)

## Core Principles

The ViewModel's primary role is to provide state to the UI (XML/View) and encapsulate business logic. It must remain decoupled from the UI implementation and survive configuration changes.

### 1. State Management (Modern Standard)
*   **Use `StateFlow`**: Prefer `StateFlow` over `LiveData` for Kotlin-first applications.
*   **Backing Property Pattern**: Always use a private `MutableStateFlow` and a public read-only `StateFlow`.
*   **Initial State**: Always provide a default initial state in the constructor.

```kotlin
private val _uiState = MutableStateFlow(LoginUiState())
val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
```

### 2. Event Handling (One-time actions)
*   **Use `SharedFlow` or `Channel`**: For events like navigation, showing SnackBar/Toast, or closing the screen.
*   **Replay**: Use `replay = 0` for `SharedFlow` to ensure events are not re-processed on rotation.

```kotlin
private val _events = MutableSharedFlow<LoginEvent>()
val events: SharedFlow<LoginEvent> = _events.asSharedFlow()
```

### 3. Dependency Injection (Hilt)
*   Annotate with `@HiltViewModel`.
*   Use `constructor` injection for repositories and use cases.
*   Include `SavedStateHandle` to preserve state across process death.

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() { ... }
```

### 4. Scoping & Coroutines
*   Always use `viewModelScope` for launching coroutines.
*   Ensure operations are "Main-safe" by delegating to `Dispatchers.IO` in the repository layer.

### 5. Interaction with ViewBinding (Fragment/Activity)
The ViewModel should **never** reference a `Binding` class or any `View`. It only emits data that the `Binding` will use.

---

## Example Implementation

### UI State Definition
```kotlin
data class LoginUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### ViewModel Implementation
```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events.asSharedFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.login(_uiState.value.email)
            _uiState.update { it.copy(isLoading = false) }
            
            if (result.isSuccess) {
                _events.emit(LoginEvent.NavigateToHome)
            } else {
                _uiState.update { it.copy(error = "Login Failed") }
            }
        }
    }
}

sealed class LoginEvent {
    object NavigateToHome : LoginEvent()
}
```

### UI Consumption (XML/Fragment)
```kotlin
// In Fragment
viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { state ->
            binding.progressBar.isVisible = state.isLoading
            binding.etEmail.setText(state.email)
            binding.tilEmail.error = state.error
        }
    }
}
```

## Checklist
- [ ] Uses `@HiltViewModel` and constructor injection.
- [ ] Exposes state via `StateFlow` (not Compose `State`).
- [ ] Exposes events via `SharedFlow` or `Channel`.
- [ ] Strictly zero `androidx.compose.*` imports.
- [ ] Includes `SavedStateHandle` for process death resilience.
- [ ] No references to `android.view` or `Binding` classes.
