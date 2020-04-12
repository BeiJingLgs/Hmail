package com.fsck.k9.ui.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.fsck.k9.activity.K9Activity
import com.fsck.k9.ui.R
import com.fsck.k9.ui.dialog.CommonDialog
import com.fsck.k9.ui.dialog.CommonDialog.OnClickBottomListener
import com.fsck.k9.ui.findNavController
import com.fsck.k9.util.WifiOpenHelper

class OnboardingActivity : K9Activity() {
    private lateinit var navController: NavController
    //APP退出操作按钮
    private val mWaitTime: Long = 2000
    private var mTochTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout(R.layout.activity_onboarding)
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            Handler(this@OnboardingActivity.getMainLooper()).post(Runnable {
                //wifi未打开
                val dialog = CommonDialog(this@OnboardingActivity)
                dialog.dialog_title = "温馨提示"
                dialog.save_path1 = ""
                dialog.file_size = "                 当前网络不可用，是否连接网络？"
                dialog.fujian_names = ""
                dialog.setSingle(false).setOnClickBottomListener(object : OnClickBottomListener {
                    override fun onPositiveClick() {
                        val wifi = WifiOpenHelper(this@OnboardingActivity)
                        wifi.openWifi()
                        this@OnboardingActivity.startActivity(
                            Intent(
                                Settings.ACTION_WIFI_SETTINGS
                            )
                        )
                        dialog.dismiss()
                    }

                    override fun onNegtiveClick() {
                        dialog.dismiss()
                    }
                }).show()
            })
        }
        initializeActionBar()
    }

    private fun initializeActionBar() {
        val appBarConfiguration =
            AppBarConfiguration(topLevelDestinationIds = setOf(R.id.welcomeScreen))

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

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
//                android.os.Process.killProcess(android.os.Process.myPid())
//            }
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }
    companion object {
        @JvmStatic
        fun launch(activity: Activity) {
            val intent = Intent(activity, OnboardingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            activity.startActivity(intent)
        }
    }
}
