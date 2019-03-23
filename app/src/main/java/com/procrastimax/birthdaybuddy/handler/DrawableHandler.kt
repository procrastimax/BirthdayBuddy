package com.procrastimax.birthdaybuddy.handler

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.models.EventBirthday

/**
 * TODO:
 * -> throw error/ catch case that wrong image path or image was deleted
 * -> or when the persistance of the uri access is denied/deleted
 */
object DrawableHandler {

    private var drawable_map: MutableMap<Int, Drawable> = emptyMap<Int, Drawable>().toMutableMap()

    /**
     * addDrawable adds a drawable to the drawable_map by reading a bitmap from the storage
     *
     * @param index : Int is the index for referencing in the EventHandler MAP not the list
     * @param uri : Uri
     * @param context : Context
     * @param scale : Int
     */
    fun addDrawable(id: Int, index: Int, uri: Uri, context: Context, scale: Int = 64): Boolean {
        var success = true
        //is valid index in EventHandler map

        try {
            //TODO: dont load whole bitmap, load compressed bitmap
            val bitmap =
                getScaledBitmap(MediaStore.Images.Media.getBitmap(context.contentResolver, uri), 64 * 4)
            drawable_map[id] = getCircularDrawable(bitmap, context.resources)

            //catch any exception, not nice but mostly like a filenotfound exception, when an image was deleted or moved
            //when this exception is catched, then delete uri reference in EventDatee instance +  inform the user
        } catch (e: Exception) {
            println(e)
            val birthday = EventHandler.getList().last().second as EventBirthday
            birthday.avatarImageUri = null
            val list = EventHandler.getList()
            val lastIndex = EventHandler.getList().lastIndex
            println("list: " + list)
            println("last index: " + lastIndex)
            EventHandler.changeEventAt(EventHandler.getList().lastIndex, birthday, context, true)
            success = false
        }
        return success
    }

    fun loadSquaredDrawable(index: Int, uri: Uri, context: Context, scale: Int = 64): Bitmap? {
        if (EventHandler.containsIndex(index)) {
            if ((EventHandler.getValueToIndex(index) is EventBirthday) && (EventHandler.getValueToIndex(index) as EventBirthday).avatarImageUri != null) {
                // we mostly dont need a try catch here, because this function should only be called after all drawables have once been loaded into the map
                //TODO: dont load whole bitmap, load compressed bitmap
                val bitmap =
                    getScaledBitmap(MediaStore.Images.Media.getBitmap(context.contentResolver, uri), scale)
                return bitmap
            }
        }
        return null
    }

    fun removeDrawable(index: Int) {
        if (EventHandler.containsIndex(index)) {
            drawable_map.toMutableMap().remove(index)
        }
    }

    /**
     * loadAllDrawables iterates through eventhandler eventlist and loads all drawables into this map
     */
    fun loadAllDrawables(context: Context): Boolean {
        var success = true

        for (i in 0 until EventHandler.getList().size) {
            if ((EventHandler.getList()[i].second is EventBirthday) && ((EventHandler.getList()[i].second as EventBirthday).avatarImageUri != null)) {
                success =
                    addDrawable(
                        EventHandler.getList()[i].first,
                        i,
                        Uri.parse((EventHandler.getList()[i].second as EventBirthday).avatarImageUri),
                        context
                    )
            }
        }
        return success
    }

    fun getDrawableAt(index: Int): Drawable? {
        if (drawable_map.isNotEmpty()) {
            return drawable_map[index]
        }
        return null
    }

    /**
     * getSquaredBitmap square given bitmap
     * this is important for nice looking circular images for avatar images
     */
    private fun getSquaredBitmap(bitmap: Bitmap): Bitmap {
        if (bitmap.width < bitmap.height) {
            val half_width = bitmap.width / 2
            val half_height = bitmap.height / 2

            val cutted_bmp = Bitmap.createBitmap(
                bitmap,
                0,
                half_height - half_width,
                bitmap.width,
                bitmap.width
            )
            return cutted_bmp
        } else if (bitmap.width > bitmap.height) {
            val half_width = bitmap.width / 2
            val half_height = bitmap.height / 2

            val cutted_bmp = Bitmap.createBitmap(
                bitmap,
                half_width - half_height,
                0,
                bitmap.height,
                bitmap.height
            )
            return cutted_bmp
        }
        return bitmap
    }

    private fun getScaledBitmap(bitmap: Bitmap, scale: Int = 64): Bitmap {
        if (bitmap.width > bitmap.height) {
            return Bitmap.createScaledBitmap(
                bitmap,
                (scale * (bitmap.width.toFloat() / bitmap.height.toFloat())).toInt(),
                scale,
                false
            )

        } else if (bitmap.width < bitmap.height) {
            return Bitmap.createScaledBitmap(
                bitmap,
                scale,
                (scale * (bitmap.height.toFloat() / bitmap.width.toFloat())).toInt(),
                false
            )

        } else {
            return Bitmap.createScaledBitmap(bitmap, scale, scale, false)
        }
    }

    fun getCircularDrawable(bitmap: Bitmap, resources: Resources): Drawable {
        val rounded_bmp: RoundedBitmapDrawable =
            RoundedBitmapDrawableFactory.create(resources, getSquaredBitmap(bitmap))
        rounded_bmp.isCircular = true
        return rounded_bmp
    }

    fun showMissingImageAlertDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.alert_dialog_missing_avatar_img_title)
        builder.setMessage(R.string.alert_dialog_missing_avatar_img_text)
        builder.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_error_outline)
        builder.show()
    }
}