package com.github.fres621.aliucord

import android.content.Context
import android.view.View
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.wrappers.embeds.MessageEmbedWrapper.Companion.title
import com.discord.models.user.CoreUser
import com.discord.stores.StoreUserTyping
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.discord.widgets.chat.list.entries.MessageEntry

@AliucordPlugin(requiresRestart = false)
class MyFirstPatch : Plugin() {
    override fun start(context: Context) {
        val itemMessage = WidgetChatListAdapterItemMessage::class.java
        val itemAvatarField = itemMessage.getDeclaredField("itemAvatar").apply { isAccessible = true }
        patcher.patch(
            itemMessage.getDeclaredMethod("onConfigure", Int::class.javaPrimitiveType, ChatListEntry::class.java),
            Hook {
                val itemAvatar = itemAvatarField[it.thisObject] as View? ?: return@Hook
                itemAvatar.apply {
                    setPadding(12, 12, 12, 12)
                    background = null
                }
            }
        )
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}
