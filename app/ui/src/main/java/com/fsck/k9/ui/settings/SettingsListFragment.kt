package com.fsck.k9.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fsck.k9.Account
import com.fsck.k9.account.BackgroundAccountRemover
import com.fsck.k9.fragment.ConfirmationDialogFragment
import com.fsck.k9.ui.R
import com.fsck.k9.ui.observeNotNull
import com.fsck.k9.ui.settings.account.AccountSettingsActivity
import com.fsck.k9.ui.settings.account.AccountSettingsFragment
import com.fsck.k9.util.UpdateUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_settings_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsListFragment : Fragment(), ConfirmationDialogFragment.ConfirmationDialogFragmentListener {
    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var accountSetting: AccountSettingsFragment
    private lateinit var settingsAdapter: GroupAdapter<ViewHolder>
    private val accountRemover: BackgroundAccountRemover by inject()
    private lateinit var accounts1: Account
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = view.context
        var updateUtil = UpdateUtil(context)
        var task = updateUtil.CheckApkTask1()
        task.execute()
        initializeSettingsList()
        populateSettingsList()
    }

    private fun initializeSettingsList() {
        accountSetting = AccountSettingsFragment()
        settingsAdapter = GroupAdapter()
        settingsAdapter.setOnItemClickListener { item, _ ->
            handleItemClick(item)
        }

        with(settings_list) {
            adapter = settingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun populateSettingsList() {
        viewModel.accounts.observeNotNull(this) { accounts ->
            if (accounts.isEmpty()) {
                launchOnboarding()
            } else {
                populateSettingsList(accounts)
            }
        }
    }

    private fun populateSettingsList(accounts: List<Account>) {
        settingsAdapter.clear()

//        val generalSection = Section().apply {
//            val generalSettingsActionItem = SettingsActionItem(
//                    getString(R.string.general_settings_title),
//                    R.id.action_settingsListScreen_to_generalSettingsScreen,
//                    R.attr.iconSettingsGeneral
//            )
//            add(generalSettingsActionItem)
//        }
//        settingsAdapter.add(generalSection)
        val accountSection = Section().apply {
            for (account in accounts) {
                accounts1 = account
                if (account.description != null) {
                    add(AccountItem(account, context, accountSetting, this@SettingsListFragment))
                }
            }

            val addAccountActionItem = SettingsActionItem(
                getString(R.string.add_account_action),
                R.id.action_settingsListScreen_to_addAccountScreen,
                R.attr.iconSettingsAccountAdd,
                context
            )
            add(addAccountActionItem)
        }
        accountSection.setHeader(SettingsDividerItem(getString(R.string.accounts_title)))
        settingsAdapter.add(accountSection)

//        val backupSection = Section().apply {
//            val exportSettingsActionItem = SettingsActionItem(
//                    getString(R.string.settings_export_title),
//                    R.id.action_settingsListScreen_to_settingsExportScreen,
//                    R.attr.iconSettingsExport
//            )
//            add(exportSettingsActionItem)
//
//            val importSettingsActionItem = SettingsActionItem(
//                    getString(R.string.settings_import_title),
//                    R.id.action_settingsListScreen_to_settingsImportScreen,
//                    R.attr.iconSettingsImport
//            )
//            add(importSettingsActionItem)
//        }
//        backupSection.setHeader(SettingsDividerItem(getString(R.string.settings_list_backup_category)))
//        settingsAdapter.add(backupSection)

        val miscSection = Section().apply {
            val accountActionItem = SettingsActionItem(
                getString(R.string.about_action),
                R.id.action_settingsListScreen_to_aboutScreen,
                R.attr.iconSettingsAbout,
                context
            )
            add(accountActionItem)
        }
        miscSection.setHeader(SettingsDividerItem(getString(R.string.settings_list_miscellaneous_category)))
        settingsAdapter.add(miscSection)
    }

    /**
     * item 点击事件
     */
    //Todo   在这加更新操作
    private fun handleItemClick(item: Item<*>) {
        when (item) {
//            is AccountItem -> launchAccountSettings(item.account)
            is SettingsActionItem -> findNavController().navigate(item.navigationAction)
        }
    }

    private fun launchAccountSettings(account: Account) {
        AccountSettingsActivity.start(requireActivity(), account.uuid)
    }

    private fun launchOnboarding() {
        findNavController().navigate(R.id.action_settingsListScreen_to_onboardingScreen)
        requireActivity().finish()
    }

    override fun dialogCancelled(dialogId: Int) {
    }

    override fun doPositiveClick(dialogId: Int) {
        accountRemover.removeAccountAsync(accounts1.uuid)
//        closeAccountSettings()
    }

    override fun doNegativeClick(dialogId: Int) {
    }
}
