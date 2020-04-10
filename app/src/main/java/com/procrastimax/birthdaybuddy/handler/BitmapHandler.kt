package com.procrastimax.birthdaybuddy.handler

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Log
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.models.EventBirthday
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object BitmapHandler {

    private var bitmapFolder = "Bitmaps"

    private var drawable_map: MutableMap<Int, Bitmap> = emptyMap<Int, Bitmap>().toMutableMap()

    private const val STANDARD_SCALING = 64 * 6

    /**
     * addDrawable adds a drawable to the drawable_map by reading a bitmap from the storage
     *
     * @param id : Int is the index for referencing in the EventHandler MAP not the list
     * @param uri : Uri
     * @param context : Context
     * @param scale : Int
     * @param readBitmapFromGallery : Boolean, when this boolean is true, it forces the function to read a new bitmap from the gallery files
     */
    fun addDrawable(
        id: Int,
        uri: Uri,
        context: Context,
        scale: Int = STANDARD_SCALING,
        readBitmapFromGallery: Boolean
    ): Boolean {
        var success = true

        //first try to load from files
        //if this doesn't succeed, then try to read from gallery and save edited bitmap to files
        if ((checkExistingBitmapInFiles(context, id) != null) && (!readBitmapFromGallery)) {
            var bitmap = getBitmapFromFile(context, id)
            if (bitmap != null) {
                // create circular bitmap from saved squared
                bitmap = getCircularBitmap(bitmap, context.resources)
                drawable_map[id] = bitmap
                return true
            }
        } else {
            try {
                var bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                //scale (square bitmap)
                bitmap = getScaledBitmap(bitmap, scale)

                //if the above succeeded, then save bitmap to files
                createBitmapFile(context, id, bitmap, 100)

                //round bitmap
                bitmap = getCircularBitmap(bitmap, context.resources)

                drawable_map[id] = bitmap

                //catch any exception, not nice but mostly like a FileNotFountException, when an image was deleted or moved
                //when this exception is caught, then delete uri reference in EventDate instance +  inform the user
            } catch (e: Exception) {
                e.printStackTrace()
                val event = EventHandler.getEventToEventIndex(id)
                if (event is EventBirthday) {
                    event.avatarImageUri = null
                    EventHandler.changeEventAt(
                        id,
                        event,
                        context,
                        true
                    )
                    removeBitmap(id, context)
                    showMissingImageAlertDialog(context)
                }
                success = false
            }
        }

        return success
    }

    fun removeAllDrawables(context: Context) {
        this.drawable_map.clear()
        context.getDir(this.bitmapFolder, Context.MODE_PRIVATE)?.deleteRecursively()
    }

    fun removeBitmap(id: Int, context: Context) {
        val event = EventHandler.getEventToEventIndex(id)
        if (event != null) {
            drawable_map.toMutableMap().remove(id)
            removeBitmapFromFiles(context, event.eventID)
        }
    }

    /**
     * loadAllDrawables iterates through eventhandler eventlist and loads all drawables into this map
     */
    fun loadAllBitmaps(context: Context): Boolean {
        var success = true
        for (i: Int in EventHandler.getList().indices) {
            try {
                if (EventHandler.getList()[i] is EventBirthday) {
                    if ((EventHandler.getList()[i] as EventBirthday).avatarImageUri != null) {
                        success =
                            addDrawable(
                                EventHandler.getList()[i].eventID,
                                Uri.parse((EventHandler.getList()[i] as EventBirthday).avatarImageUri),
                                context,
                                readBitmapFromGallery = false
                            )
                    }
                }
            } catch (e: IndexOutOfBoundsException) {
                Log.e("loadAllBitmaps", "could not load bitmaps")
            }
        }
        return success
    }

    fun getBitmapAt(index: Int): Bitmap? {
        if (drawable_map.isNotEmpty()) {
            return drawable_map[index]
        }
        return null
    }

    fun getBitmapFromFile(context: Context, eventID: Int): Bitmap? {
        return if (checkExistingBitmapInFiles(context, eventID) != null) {
            val bitmapDir = context.getDir(this.bitmapFolder, Context.MODE_PRIVATE)
            BitmapFactory.decodeFile(bitmapDir.absolutePath + File.separator.toString() + "$eventID.png")
        } else {
            null
        }
    }


    private fun createBitmapFile(
        context: Context,
        eventID: Int,
        bitmap: Bitmap,
        compressionRate: Int = 100
    ): Boolean {
        val bitmapDir = context.getDir(this.bitmapFolder, Context.MODE_PRIVATE)
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, compressionRate, outStream)

        val bitmapFile = File(bitmapDir.absolutePath + File.separator.toString() + "$eventID.png")

        return try {
            val fos = FileOutputStream(bitmapFile)
            fos.write(outStream.toByteArray())

            fos.flush()
            fos.close()

            true
        } catch (e: Exception) {
            e.printStackTrace()

            false
        }
    }

    private fun checkExistingBitmapInFiles(context: Context, eventID: Int): File? {
        context.getDir(this.bitmapFolder, Context.MODE_PRIVATE).let {
            if (it != null) {
                val bitmapFile = File(it.absolutePath + File.separator + "$eventID.png")
                return if (bitmapFile.exists()) {
                    bitmapFile
                } else {
                    null
                }
            }
        }
        return null
    }

    private fun removeBitmapFromFiles(context: Context, eventID: Int) {
        val bitmapFile = checkExistingBitmapInFiles(context, eventID)
        bitmapFile?.delete()
    }

    /**
     * getSquaredBitmap square given bitmap
     * this is important for nice looking circular images for avatar images
     */
    private fun getSquaredBitmap(bitmap: Bitmap): Bitmap {
        val halfWidth = bitmap.width / 2
        val halfHeight = bitmap.height / 2
        if (bitmap.width < bitmap.height) {
            return Bitmap.createBitmap(
                bitmap,
                0,
                halfHeight - halfWidth,
                bitmap.width,
                bitmap.width
            )
        } else if (bitmap.width > bitmap.height) {
            return Bitmap.createBitmap(
                bitmap,
                halfWidth - halfHeight,
                0,
                bitmap.height,
                bitmap.height
            )
        }
        return bitmap
    }

    fun getScaledBitmap(bitmap: Bitmap, scale: Int = STANDARD_SCALING): Bitmap {
        //first square bitmap
        val tempBitmap = getSquaredBitmap(bitmap)

        //then scale bitmap
        return Bitmap.createScaledBitmap(
            tempBitmap,
            scale,
            scale,
            false
        )
    }

    fun getCircularBitmap(bitmap: Bitmap, resources: Resources): Bitmap {
        RoundedBitmapDrawableFactory.create(resources, bitmap).let {
            it.isCircular = true
            return drawableToBitmap(it)
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        var width = drawable.intrinsicWidth
        width = if (width > 0) width else 1
        var height = drawable.intrinsicHeight
        height = if (height > 0) height else 1

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun showMissingImageAlertDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.alert_dialog_missing_avatar_img_title)
        builder.setMessage(R.string.alert_dialog_missing_avatar_img_text)
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setIcon(R.drawable.ic_error_outline)
        builder.show()
    }
}