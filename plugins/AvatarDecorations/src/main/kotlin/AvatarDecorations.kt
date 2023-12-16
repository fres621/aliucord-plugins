package com.github.fres621.aliucord

import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aliucord.Main
import com.aliucord.widgets.LinearLayout
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
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.discord.utilities.color.ColorCompat  
import com.lytefast.flexinput.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.widget.FrameLayout
import android.view.Gravity
import androidx.constraintlayout.widget.ConstraintSet


class ImageLoader(private val callback: (Bitmap?) -> Unit) : AsyncTask<String, Void, Bitmap?>() {

    override fun doInBackground(vararg params: String): Bitmap? {
        val urlString = params[0]
        var connection: HttpURLConnection? = null

        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            val inputStream = connection.inputStream
            return BitmapFactory.decodeStream(inputStream)

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }

        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        callback.invoke(result)
    }
}


@AliucordPlugin(requiresRestart = false)
class AvatarDecorations : Plugin() {
    override fun start(context: Context) {
        val itemMessage = WidgetChatListAdapterItemMessage::class.java
        val itemAvatarField = itemMessage.getDeclaredField("itemAvatar").apply { isAccessible = true }
        patcher.patch(
            itemMessage.getDeclaredMethod("onConfigure", Int::class.javaPrimitiveType, ChatListEntry::class.java),
            Hook {
                val itemAvatar = itemAvatarField[it.thisObject] as ImageView? ?: return@Hook

                val parentView = itemAvatar.parent as? ConstraintLayout ?: return@Hook

                /* Create the "avatar decoration" view */
                val avatarDecoration = View(parentView.context)
                avatarDecoration.setBackgroundColor(Color.RED)
                val params = ConstraintLayout.LayoutParams(64, 64)
                avatarDecoration.layoutParams = itemAvatar.layoutParams
                avatarDecoration.setAlpha(0.7f)

                val constraintSet = ConstraintSet()
                constraintSet.clone(parentView)
                
                // IM GIVING UP
                
                constraintSet.constrainWidth(itemAvatar.id, 71)
                constraintSet.constrainHeight(itemAvatar.id, 71)

                constraintSet.applyTo(parentView)
                //constraintSet.constrainWidth(itemAvatar.id, 64)
                //constraintSet.constrainHeight(itemAvatar.id, 64)
                //constraintSet.connect(itemAvatar.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 16)
                //constraintSet.connect(itemAvatar.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 16)
                //constraintSet.connect(itemAvatar.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
                //constraintSet.connect(itemAvatar.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)

                //constraintSet.applyTo(parentView)

                //val params2 = ConstraintLayout.LayoutParams(48, 48)
                //itemAvatar.layoutParams = params2

                //parentView.addView(avatarDecoration)



                /*

                val layout = ConstraintLayout(parentView.context)

                val avatarDecoration = View(parentView.context)
                avatarDecoration.setBackgroundColor(Color.RED)
                val params = ConstraintLayout.LayoutParams(240, 240)
                params.setMargins(64, 64, 64, 64)
                avatarDecoration.layoutParams = itemAvatar.layoutParams
                avatarDecoration.setAlpha(0.5f)
                //itemAvatar.layoutParams = params
                //itemAvatar.setImageAlpha(50)
                var newItemAvatar = ImageView(parentView.context)
                newItemAvatar.setImageDrawable(itemAvatar.getDrawable())
                newItemAvatar.layoutParams = itemAvatar.layoutParams

                layout.addView(newItemAvatar)
                layout.addView(avatarDecoration)
                //layout.addView(itemAvatar)

                parentView.removeView(itemAvatar)
                parentView.addView(layout)
                */
            }
        )
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}
