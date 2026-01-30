---
name: compose-to-xml-migration
description: Convert Jetpack Compose UI to Android XML layouts. Use when asked to migrate Composables to XML, legacy code integration, or when Compose is not suitable for a specific use case.
---

# Compose to XML Migration

## Overview

Systematically convert Jetpack Compose UI to traditional Android XML layouts. This skill covers component mapping, state management adaptation, and integrating XML views back into a Compose-heavy or legacy codebase.

## Workflow

### 1. Analyze the Composable
- Identify the layout structure (`Column`, `Row`, `Box`, `Scaffold`).
- List all UI components (`Text`, `Button`, `Image`, etc.).
- Identify state variables (`remember`, `StateFlow`, `mutableStateOf`).
- Note any Modifiers used for layout, styling, or interactions.
- Check for custom Composables that need to be flattened or converted to custom Views.

### 2. Plan the Migration
- Choose the root XML layout: `ConstraintLayout` (recommended), `LinearLayout`, or `FrameLayout`.
- Decide on the integration strategy: **Full XML Activity/Fragment** or **Hybrid** (using `ComposeView`).
- Plan resource extraction: Colors, Strings, Dimensions, and Styles must be moved to `res/values/`.
- Identify necessary View components (Standard Views or Material Components).

### 3. Convert UI Components
Apply the component mapping table below to convert each Composable to its XML View equivalent.

### 4. Migrate State & Logic
- Move UI logic from Composable to `ViewModel`, `Activity`, or `Fragment`.
- Replace `State<T>` with `LiveData<T>` or `StateFlow<T>` observed in the UI controller.
- Convert `onClick` lambdas to `View.OnClickListener` implementations.
- Adopt **ViewBinding** for type-safe view interaction.

### 5. Test and Verify
- Verify layout fidelity using "Layout Inspector" or visual comparison.
- Check accessibility attributes (`contentDescription`, `importantForAccessibility`).
- Ensure state restoration works (replacing `rememberSaveable` with `SavedStateHandle` or `onSaveInstanceState`).

---

## Component Mapping Reference

### Container Layouts

| Compose Composable | XML Layout Equivalent | Notes |
|--------------------|-----------------------|-------|
| `Column` | `LinearLayout` (vertical) | Or `ConstraintLayout` with vertical chains |
| `Row` | `LinearLayout` (horizontal) | Or `ConstraintLayout` with horizontal chains |
| `Box` | `FrameLayout` | Or `ConstraintLayout` (centering/stacking) |
| `ConstraintLayout` | `ConstraintLayout` | Direct mapping, logic is similar |
| `Scaffold` | `CoordinatorLayout` + `AppBarLayout` | For top bars, FABs, and scrolling |
| `LazyColumn` | `RecyclerView` | Requires Adapter + ViewHolder |
| `LazyRow` | `RecyclerView` | Requires LayoutManager (horizontal) |
| `LazyGrid` | `RecyclerView` | Requires GridLayoutManager |
| `Surface` | `CardView` / `MaterialCardView` | Or any View with background/elevation |
| `Spacer` | `Space` / `View` | Or layout margins |

### Common Widgets

| Compose Composable | XML Widget Equivalent | Notes |
|--------------------|-----------------------|-------|
| `Text` | `TextView` | Extract styles to `styles.xml` |
| `TextField` / `OutlinedTextField` | `TextInputLayout` + `TextInputEditText` | Material Components |
| `Button` | `Button` / `MaterialButton` | |
| `Image` | `ImageView` | |
| `Icon` | `ImageView` | Use `srcCompat` for vectors |
| `IconButton` | `ImageButton` | |
| `Checkbox` | `CheckBox` | |
| `RadioButton` | `RadioButton` | Wrap in `RadioGroup` |
| `Switch` | `Switch` / `SwitchCompat` | |
| `CircularProgressIndicator` | `ProgressBar` | Style="?android:attr/progressBarStyle" |
| `LinearProgressIndicator` | `ProgressBar` | Style="?android:attr/progressBarStyleHorizontal" |
| `Slider` | `SeekBar` / `Slider` (Material) | |
| `Card` | `CardView` / `MaterialCardView` | |
| `TopAppBar` | `Toolbar` | Inside `AppBarLayout` |
| `NavigationBar` | `BottomNavigationView` | |
| `FloatingActionButton` | `FloatingActionButton` | |
| `Divider` | `View` | Height/Width = 1dp, background color |

### Modifier Mapping

| Compose Modifier | XML Attribute |
|------------------|---------------|
| `fillMaxWidth()` | `android:layout_width="match_parent"` |
| `fillMaxHeight()` | `android:layout_height="match_parent"` |
| `wrapContentWidth()` | `android:layout_width="wrap_content"` |
| `padding(x)` | `android:padding="x"` or `android:layout_margin="x"` |
| `background(color)` | `android:background="@color/..."` |
| `alpha(x)` | `android:alpha="x"` |
| `clickable { }` | `android:clickable="true"` + `android:focusable="true"` |
| `align(Alignment.Center)` | `android:gravity="center"` (parent) or `layout_gravity` |
| `weight(x)` | `android:layout_weight="x"` |
| `size(x)` | `android:layout_width="x"`, `android:layout_height="x"` |
| `clip(RoundedCornerShape(x))` | `CardView` radius or custom drawable background |
| `border(...)` | Custom drawable (shape/stroke) |
| `shadow(elevation)` | `android:elevation="..."` |

---

## Common Patterns

### Column to LinearLayout

```kotlin
// Compose
Column(modifier = Modifier.padding(16.dp)) {
    Text("Title")
    Text("Subtitle")
}
```

```xml
<!-- XML -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Title" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Subtitle" />
</LinearLayout>
```

### LazyColumn to RecyclerView

```kotlin
// Compose
LazyColumn {
    items(users) { user ->
        UserRow(user)
    }
}
```

```xml
<!-- XML -->
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
```
*Note: Requires creating an Adapter class and ViewHolder.*

### State Hoisting to ViewModel + ViewBinding

```kotlin
// Compose
var text by remember { mutableStateOf("") }
TextField(value = text, onValueChange = { text = it })
```

```xml
<!-- XML -->
<EditText
    android:id="@+id/editText"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```kotlin
// Activity/Fragment
binding.editText.doAfterTextChanged { text ->
    viewModel.updateText(text.toString())
}
viewModel.textState.observe(this) { text ->
    if (binding.editText.text.toString() != text) {
        binding.editText.setText(text)
    }
}
```

---

## Checklist

- [ ] **Root Layout**: Selected appropriate root (ConstraintLayout/LinearLayout).
- [ ] **Resources**: Extracted hardcoded strings, colors, and dims to XML resources.
- [ ] **IDs**: Assigned IDs (`@+id/name`) using camelCase for ViewBinding.
- [ ] **Adapters**: Created RecyclerView Adapters for any `LazyColumn`/`LazyRow`.
- [ ] **Logic**: Moved UI logic to Fragment/Activity or ViewModel.
- [ ] **Navigation**: Updated navigation graph or Intent logic.
- [ ] **Cleanup**: Removed unused Compose dependencies if full migration.
- [ ] **Accessibility**: Verified `contentDescription` and focus handling.
