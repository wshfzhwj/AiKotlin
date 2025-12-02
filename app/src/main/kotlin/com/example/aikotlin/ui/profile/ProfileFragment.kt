package com.example.aikotlin.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.aikotlin.R
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.databinding.FragmentProfileBinding
import com.example.aikotlin.viewmodel.NewsViewModel
import kotlin.getValue

class ProfileFragment : BaseFragment<FragmentProfileBinding, BaseViewModel>() {

    override val viewModel: BaseViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化个人资料信息
        initProfileInfo(view)

        // 初始化功能列表
        initFunctionItems(view)

        val editFab =
            view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
                R.id.editProfileFab
            )
        editFab.setOnClickListener { showToast("编辑资料") }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    private fun initProfileInfo(view: View) {
        // 设置用户头像、昵称等信息
        val avatarImageView = view.findViewById<android.widget.ImageView>(R.id.avatarImageView)
        val nicknameTextView = view.findViewById<android.widget.TextView>(R.id.nicknameTextView)
        val signatureTextView = view.findViewById<android.widget.TextView>(R.id.signatureTextView)

        // 使用默认头像
        avatarImageView.setImageResource(R.drawable.ic_placeholder_avatar)
        nicknameTextView.text = "今日头条用户"
        signatureTextView.text = "这个人很懒，什么都没有留下"

        // 设置统计数据
        val followCountTextView =
            view.findViewById<android.widget.TextView>(R.id.followCountTextView)
        val followerCountTextView =
            view.findViewById<android.widget.TextView>(R.id.followerCountTextView)
        val likeCountTextView = view.findViewById<android.widget.TextView>(R.id.likeCountTextView)

        followCountTextView.text = "128"
        followerCountTextView.text = "32"
        likeCountTextView.text = "512"
    }

    private fun initFunctionItems(view: View) {
        // 设置功能项的点击事件
        val myWorksView = view.findViewById<View>(R.id.myWorksView)
        val myCollectionView = view.findViewById<View>(R.id.myCollectionView)
        val myHistoryView = view.findViewById<View>(R.id.myHistoryView)
        val myMessageView = view.findViewById<View>(R.id.myMessageView)
        val settingsView = view.findViewById<View>(R.id.settingsView)

        myWorksView.setOnClickListener {
            showToast("我的作品")
        }

        myCollectionView.setOnClickListener {
            showToast("我的收藏")
        }

        myHistoryView.setOnClickListener {
            showToast("浏览历史")
        }

        myMessageView.setOnClickListener {
            showToast("我的消息")
        }

        settingsView.setOnClickListener {
            showToast("设置")
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}