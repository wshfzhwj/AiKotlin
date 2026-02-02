---
name: android-xml-layout-saint
description: Best practices for generating modern Android XML layouts using ConstraintLayout, Material Design components, and ViewBinding conventions. Use this skill when the user requests XML layouts, legacy View-based UI, or specifically asks to avoid Jetpack Compose.
---

# Android XML Layout Best Practices

## Core Principles

When generating Android XML layouts, adhere to these modern standards to ensure performance, maintainability, and accessibility.

### 1. Root Layout
*   **Default to `androidx.constraintlayout.widget.ConstraintLayout`** for almost all screens to create flat, efficient view hierarchies.
*   Use `LinearLayout` or `FrameLayout` only for very simple, nested containers where constraints are overkill.
*   **Namespace**: Always include `xmlns:app="http://schemas.android.com/apk/res-auto"` in the root element.

### 2. Material Components
*   Prefer **Material Design components** over standard widgets for better theming and features.
    *   Use `com.google.android.material.button.MaterialButton` instead of `Button`.
    *   Use `com.google.android.material.textfield.TextInputLayout` wrapping a `TextInputEditText` for input fields.
    *   Use `com.google.android.material.card.MaterialCardView` instead of `CardView`.
    *   Use `com.google.android.material.imageview.ShapeableImageView` for images requiring rounded corners or shapes.

### 3. Resource Extraction (Strict)
*   **Strings**: NEVER hardcode text. Extract all user-facing text to `res/values/strings.xml`.
*   **Colors**: Define semantic colors in `res/values/colors.xml` (e.g., `colorPrimary`, `colorSurface`). Reference them via `?attr/` or `@color/`.
*   **Dimensions**: Extract layout margins, text sizes, and corner radii to `res/values/dimens.xml`.
    *   Standard grid: 4dp, 8dp, 16dp, 24dp, etc.

### 4. Naming Conventions (ViewBinding Ready)
*   **IDs**: Use **camelCase** for IDs to align with ViewBinding generation.
    *   Format: `[elementType][Description]`
    *   Examples: `btnLogin`, `tvTitle`, `ivProfileAvatar`, `containerForm`.
*   **Files**: Use **snake_case** for layout filenames (e.g., `activity_login.xml`, `item_user_profile.xml`).

### 5. Accessibility
*   **contentDescription**: Mandatory for all non-decorative `ImageView`s and `ImageButton`s.
    *   Use `@null` or `importantForAccessibility="no"` for decorative images.
*   **Touch Targets**: Ensure clickable elements are at least 48dp x 48dp (minHeight/minWidth).

### 6. Tools Attributes
*   Use `tools:text`, `tools:visibility`, and `tools:src` to visualize the layout in the editor without affecting the runtime app.
*   Root element must have `xmlns:tools="http://schemas.android.com/tools"`.

## Example Template

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?attr/colorSurface">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="@dimen/margin_large"
        android:text="@string/screen_title"
        android:textAppearance="?attr/textAppearanceHeadline4"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:text="Sign In" />

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/tilEmail"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="@dimen/margin_medium"
        android:hint="@string/label_email"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvTitle">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etEmail"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textEmailAddress" />

    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnSubmit"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginBottom="@dimen/margin_large"
        android:text="@string/btn_submit"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

## Checklist Before Outputting
1. Is `ConstraintLayout` the root?
2. Are all strings extracted to `@string/`?
3. Are IDs in `camelCase`?
4. Are Material Components used?
5. Are `tools` attributes used for previewing?
