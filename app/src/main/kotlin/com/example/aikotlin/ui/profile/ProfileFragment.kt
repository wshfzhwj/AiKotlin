package com.example.aikotlin.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.example.aikotlin.R
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.databinding.FragmentProfileBinding
import com.example.aikotlin.ui.login.LoginActivity
import com.example.aikotlin.viewmodel.NewsViewModel
import kotlin.getValue

class ProfileFragment : BaseFragment<FragmentProfileBinding, BaseViewModel>() {

    override val viewModel: BaseViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化个人资料信息
        initProfileInfo()

        // 初始化功能列表
        initFunctionItems()

        val editFab =
            view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
                R.id.editProfileFab
            )
        editFab.setOnClickListener {
//            showToast("编辑资料")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    private fun initProfileInfo() {
        // 设置用户头像、昵称等信息
        // 使用Coil加载并美化头像
        binding.avatarImageView.load(R.mipmap.messi) {
            crossfade(true)
            placeholder(R.drawable.ic_placeholder_avatar)
            error(R.drawable.ic_placeholder_avatar)
            transformations(CircleCropTransformation())
        }
        binding.nicknameTextView.text = "今日头条用户"
        binding.signatureTextView.text = "这个人很懒，什么都没有留下"

        binding.followCountTextView.text = "128"
        binding.followerCountTextView.text = "32"
        binding.likeCountTextView.text = "512"
    }

    private fun initFunctionItems() {
        // 设置功能项的点击事件
        binding.myWorksView.setOnClickListener {
            showToast("我的作品")
        }

        binding.myCollectionView.setOnClickListener {
            showToast("我的收藏")
        }

        binding.myHistoryView.setOnClickListener {
            showToast("浏览历史")
        }

        binding.myMessageView.setOnClickListener {
            showToast("我的消息")
        }

        binding.settingsView.setOnClickListener {
            showToast("设置")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
