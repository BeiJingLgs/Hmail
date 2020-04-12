package com.fsck.k9.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.fsck.k9.activity.K9Activity
import com.fsck.k9.activity.MessageList
import com.fsck.k9.ui.R
import com.fsck.k9.ui.findNavController

class SettingsActivity : K9Activity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout(R.layout.activity_settings)

        initializeActionBar()
    }

    private fun initializeActionBar() {
        // Empty set of top level destinations so the app bar's "up" button is also displayed at the start destination
        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds = emptySet())

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp() || navigateUpBySimulatedBackButtonPress()
    }

    private fun navigateUpBySimulatedBackButtonPress(): Boolean {
//        onBackPressed()
        val intent = Intent()
        //获取intent对象
        intent.setClass(this, MessageList::class.java)
        // 获取class是使用::反射(那么问题来了,反射是个什么鬼?👻👻👻👻小白的悲哀啊,赶紧研究研究去)
        startActivity(intent)
        return true
    }

    companion object {
        @JvmStatic
        fun launch(activity: Activity) {
            val intent = Intent(activity, SettingsActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
