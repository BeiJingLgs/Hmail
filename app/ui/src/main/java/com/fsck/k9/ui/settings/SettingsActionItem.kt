package com.fsck.k9.ui.settings

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.IdRes
import com.fsck.k9.contacts.contactsModule
import com.fsck.k9.ui.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.text_icon_list_item.*

internal class SettingsActionItem(val text: String, @IdRes val navigationAction: Int, val icon: Int,val context: Context?) : Item() {

    override fun getLayout(): Int = R.layout.text_icon_list_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.text.text = text
        val outValue = TypedValue()
        viewHolder.icon.context.theme.resolveAttribute(icon, outValue, true)
        viewHolder.icon.setImageResource(outValue.resourceId)

    }
}
