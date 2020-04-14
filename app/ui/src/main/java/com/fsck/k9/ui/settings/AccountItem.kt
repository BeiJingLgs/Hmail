package com.fsck.k9.ui.settings

import android.content.Context
import android.view.View
import com.fsck.k9.Account
import com.fsck.k9.fragment.ConfirmationDialogFragment
import com.fsck.k9.ui.R
import com.fsck.k9.ui.settings.account.AccountSettingsFragment
import com.fsck.k9.util.CustomDialog
import com.fsck.k9.util.UpdateUtil.DownNewApkTask
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.account_list_item.*

internal class AccountItem(val account: Account, val context: Context?, val accountSetting: AccountSettingsFragment, val settingsListFragment: SettingsListFragment) : Item() {

    override fun getLayout(): Int = R.layout.account_list_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.name.text = account.description
        viewHolder.email.text = account.email
        viewHolder.delete_user.setOnClickListener {

            val dialogFragment = ConfirmationDialogFragment.newInstance(
                1,
                context!!.getString(R.string.account_delete_dlg_title),
                context!!.getString(R.string.account_delete_dlg_instructions_fmt,account.description),
                context!!.getString(R.string.okay_action),
                context!!.getString(R.string.cancel_action),
                account
            )
            dialogFragment.setTargetFragment(settingsListFragment, 1)
            dialogFragment.show(settingsListFragment.requireFragmentManager(), "delete_account_confirmation")
        }
    }
}
