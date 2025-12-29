package com.example.aikotlin.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * A safe implementation of SwipeRefreshLayout that prevents NullPointerException
 * by explicitly finding its scrollable child.
 */
class SafeSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

    private var scrollableChild: View? = null

    /**
     * Finds the first scrollable child view and keeps a reference to it.
     */
    private fun ensureScrollableChild()
    {
        if (scrollableChild == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                // Check if the child can scroll vertically. This is a robust way
                // to find the scrollable target (e.g., RecyclerView, NestedScrollView).
                if (child.canScrollVertically(-1) || child.canScrollVertically(1)) {
                    scrollableChild = child
                    break
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // Ensure the scrollable child is found after the layout is established.
        ensureScrollableChild()
    }

    /**
     * Overrides the default implementation to provide a null-safe check.
     * It uses the explicitly found scrollableChild.
     */
    override fun canChildScrollUp(): Boolean {
        // If the scrollable child is not found yet, try to find it.
        ensureScrollableChild()

        return scrollableChild?.let {
            // Use ViewCompat for backward compatibility.
            ViewCompat.canScrollVertically(it, -1)
        } ?: super.canChildScrollUp() // Fallback to default behavior if no child is found
    }
}
