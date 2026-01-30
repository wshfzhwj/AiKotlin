package com.example.aikotlin.base

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import java.lang.reflect.ParameterizedType

/**
 * Base Activity class with common functionality for all activities in the app.
 */
abstract class BaseActivity<T : ViewBinding, VM : ViewModel> : AppCompatActivity() {
    private lateinit var _binding: T
    protected val binding get() = _binding;

    private lateinit var _viewModel: VM
    protected val viewModel get() = _viewModel;
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    /**
     * Toolbar instance for the activity
     */
    protected var toolbar: Toolbar? = null

    /**
     * Called when the activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase()
        _binding = getViewBinding()
        setContentView(binding.root)
        initViews()
        setupListeners()
        observeData()
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment = supportFragmentManager.findFragmentById(android.R.id.content)
                if (fragment is BaseFragment<*, *> && fragment.onBackPressed()) {
                    return
                }
                // This will finish the activity if the fragment didn't consume the back press
                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        // Add the callback to the dispatcher
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initBase() {
        //获取ViewModel类型
        val modelClass: Class<BaseViewModel>
        //获取带有泛型的父类
        val type = javaClass.genericSuperclass
        //ParameterizedType参数化类型，即泛型
        modelClass = if (type is ParameterizedType) {
            //getActualTypeArguments获取参数化类型的数组，泛型可能有多个，这里我们默认第二个泛型是ViewModel
            type.actualTypeArguments[1] as Class<BaseViewModel>
        } else {
            //如果没有指定泛型参数，则默认使用ViewModel
            BaseViewModel::class.java
        }

        //初始化viewModel
        _viewModel = createViewModel(this, modelClass as Class<VM>)
    }

    /**
     * Determine if the back button should be shown in the toolbar.
     * Override this in child activities to customize the behavior.
     */
    protected open fun shouldShowBackButton(): Boolean {
        return false
    }

    /**
     * Initialize views and UI components.
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
        Toast.makeText(this, message, duration).show()
    }

    /**
     * Handle options menu item selection.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle back button in the action bar
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Called when the user clicks the back button.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * Show a snackbar with the given message.
     * @param message The message to show
     * @param duration How long to display the message (default is LENGTH_SHORT)
     */
    protected fun showSnackbar(message: String, duration: Int = Toast.LENGTH_SHORT) {
        // Find the root view of the activity
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        com.google.android.material.snackbar.Snackbar.make(rootView, message, duration).show()
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

    /**
     * 创建ViewModel 如果 需要自己定义ViewModel 直接复写此方法
     *
     * @param cls
     * @param <T>
     * @return
    </T> */
    open fun <T : ViewModel> createViewModel(activity: FragmentActivity?, cls: Class<T>?): T {
        return ViewModelProvider(activity!!)[cls!!]
    }

    protected abstract fun getViewBinding(): T
}
