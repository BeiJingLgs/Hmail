package com.fsck.k9.ui.addaccount

import android.os.Bundle
import android.os.Process
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fsck.k9.jmap.R
import com.fsck.k9.jmap.databinding.FragmentAddAccountBinding
import com.fsck.k9.ui.observeNotNull
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddAccountFragment : Fragment() {
    private val viewModel: AddAccountViewModel by viewModel()
    //APP退出操作按钮
    private val mWaitTime: Long = 2000
    private var mTochTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getActionEvents().observeNotNull(this) { handleActionEvents(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentAddAccountBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    private fun handleActionEvents(action: Action) {
        when (action) {
            is Action.GoToMessageList -> goToMessageList()
        }
    }

    private fun goToMessageList() {
        findNavController().navigate(R.id.action_addJmapAccountScreen_to_messageListScreen)
    }
    /**
     * 再按一次退出系统
     */
//    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        if (event.action == KeyEvent.ACTION_DOWN
//            && KeyEvent.KEYCODE_BACK == keyCode) {
//            val currentTime = System.currentTimeMillis()
//            if (currentTime - mTochTime >= mWaitTime) { //"再按返回键退出应用！"
//                mTochTime = currentTime
//            } else {
//                Process.killProcess(Process.myPid())
//            }
//            return true
//        }
//        return false
//    }
}


