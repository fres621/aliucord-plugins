package com.github.fres621

import android.content.Context
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
        unpatchChatRadialStatus = patcher.patch(
            itemMessage.getDeclaredMethod("onConfigure", Int::class.javaPrimitiveType, ChatListEntry::class.java),
            Hook {
                val itemAvatar = itemAvatarField[it.thisObject] as View? ?: return@Hook
                val entry = it.args[1] as MessageEntry

                itemAvatar.setPadding(12, 12, 12, 12)
                itemAvatar.background = null
            }
        )
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}
