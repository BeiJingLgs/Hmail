package com.fsck.k9.ui.addaccount

import android.os.Bundle
import android.os.Process
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.fsck.k9.activity.K9Activity
import com.fsck.k9.jmap.R
import com.fsck.k9.ui.findNavController

class AddAccountActivity : K9Activity() {
    //APP退出操作按钮
    private val mWaitTime: Long = 2000
    private var mTochTime: Long = 0
    private lateinit var navController: NavController
    private val fg: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout(R.layout.activity_add_account)

        initializeActionBar()
    }

    private fun initializeActionBar() {
        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds = setOf(R.id.addJmapAccountScreen))

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (fg is AddAccountFragment) {
//            event?.let { (fg as AddAccountFragment).onKeyDown(keyCode, it) }
//        }
//        return false
//    }

    /**
     * 再按一次退出系统
     */
//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
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
//        return super.onKeyDown(keyCode, event)
//    }
}
