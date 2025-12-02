package com.example.aikotlin.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

/**
 * Base Fragment class with common functionality for all fragments in the app.
 */
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: VM

    /**
     * Called to create the view hierarchy associated with the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupListeners()
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Get the ViewBinding for the fragment.
     */
    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * Initialize views and UI components.
     * Override this method to set up view references and configure views.
     */

    /**
     * Initialize views and UI components.
     * Override this method to set up view references and configure views.
     */
    protected open fun initViews() {
        // Initialize views here
    }

    /**
     * Set up click listeners and other UI event listeners.
     */
    protected open fun setupListeners() {
        // Set up listeners here
    }

    /**
     * Observe LiveData or other data sources.
     */
    protected open fun observeData() {
        // Observe data here
    }

    /**
     * Show a toast message.
     * @param message The message to display.
     * @param duration The duration of the toast (default is LENGTH_SHORT).
     */
    protected fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        context?.let {
            Toast.makeText(it, message, duration).show()
        }
    }

    /**
     * Handle back press event.
     * @return true if the event is consumed, false otherwise.
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    /**
     * Show progress indicator.
     */
    protected fun showLoading() {
        // Implement progress dialog or progress bar visibility
    }

    /**
     * Hide progress indicator.
     */
    protected fun hideLoading() {
        // Hide progress dialog or progress bar
    }
}
