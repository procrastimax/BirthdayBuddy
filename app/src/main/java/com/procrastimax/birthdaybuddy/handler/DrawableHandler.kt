package com.procrastimax.birthdaybuddy.handler

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import com.procrastimax.birthdaybuddy.models.EventBirthday

/**
 * TODO:
 * -> throw error/ catch case that wrong image path or image was deleted
 * -> or when the persistance of the uri access is denied/deleted
 */
object DrawableHandler {

    private var drawable_map: MutableMap<Int, Drawable> = emptyMap<Int, Drawable>().toMutableMap()

    fun addDrawable(index: Int, uri: Uri, context: Context, scale: Int = 64) {
        //is valid index in EventHandler map
        if (EventHandler.containsKey(index)) {
            if ((EventHandler.getValueToKey(index) is EventBirthday) && (EventHandler.getValueToKey(index) as EventBirthday).avatarImageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                drawable_map[index] = getCircularDrawable(bitmap, context.resources, scale)
            }
        }
    }

    fun removeDrawable(index: Int) {
        if (EventHandler.containsKey(index)) {
            drawable_map.toMutableMap().remove(index)
        }
    }

    /**
     * loadAllDrawables iterates through eventhandler eventlist and loads all drawables into this map
     */
    fun loadAllDrawables(context: Context) {
        for (it in EventHandler.event_list) {
            if ((it.second is EventBirthday) && (it.second as EventBirthday).avatarImageUri != null) {
                addDrawable(it.first, Uri.parse((it.second as EventBirthday).avatarImageUri), context)
            }
        }
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

    fun getCircularDrawable(bitmap: Bitmap, resources: Resources, scale: Int = 64): Drawable {
        //TODO: change hardcoded numbers to some math
        val scaled_bitmap = Bitmap.createScaledBitmap(bitmap, scale * 4, scale * 4, false)
        val rounded_bmp: RoundedBitmapDrawable =
            RoundedBitmapDrawableFactory.create(resources, getSquaredBitmap(bitmap))
        rounded_bmp.isCircular = true
        return rounded_bmp
    }
}