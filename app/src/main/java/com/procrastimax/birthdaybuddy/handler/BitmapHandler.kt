package com.procrastimax.birthdaybuddy.handler

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.models.EventBirthday
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


/**
 * TODO:
 * -> throw error/ catch case that wrong image path or image was deleted
 * -> or when the persistance of the uri access is denied/deleted
 */
object BitmapHandler {

    private var bitmapFolder = "Bitmaps"

    private var drawable_map: MutableMap<Int, Bitmap> = emptyMap<Int, Bitmap>().toMutableMap()

    /**
     * addDrawable adds a drawable to the drawable_map by reading a bitmap from the storage
     *
     * @param index : Int is the index for referencing in the EventHandler MAP not the list
     * @param uri : Uri
     * @param context : Context
     * @param scale : Int
     * @param readBitmapFromGallery : Boolean, when this boolean is true, it forces the function to read a new bitmap from the gallery files
     */
    fun addDrawable(id: Int, uri: Uri, context: Context, scale: Int = 64 * 4, readBitmapFromGallery: Boolean): Boolean {
        var success = true

        //first try to load from files
        //if this doesnt succeed, then try to read from gallery and save edited bitmap to files
        if ((checkExistingBitmapInFiles(context, id) != null) && (!readBitmapFromGallery)) {
            val bitmap = getBitmapFromFile(context, id)
            if (bitmap != null) {
                drawable_map[id] = bitmap
                return true
            }
        } else {
            try {
                var bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                //scale (square bitmap)
                bitmap = getScaledBitmap(bitmap, scale)

                //round bitmap
                bitmap = getCircularBitmap(bitmap, context.resources)

                drawable_map[id] = bitmap

                //if the above succeeded, then save bitmap to files
                createBitmapFile(context, id, bitmap, 100)

                //catch any exception, not nice but mostly like a filenotfound exception, when an image was deleted or moved
                //when this exception is catched, then delete uri reference in EventDatee instance +  inform the user
            } catch (e: Exception) {
                e.printStackTrace()
                val birthday = EventHandler.getList().last() as EventBirthday
                birthday.avatarImageUri = null
                EventHandler.changeEventAt(EventHandler.getList().lastIndex, birthday, context, true)
                removeBitmap(id, context)
                showMissingImageAlertDialog(context)
                success = false
            }
        }

        return success
    }

    fun removeAllDrawables(context: Context) {
        this.drawable_map.clear()
        val bitmap_dir = context.getDir(this.bitmapFolder, Context.MODE_PRIVATE)
        bitmap_dir.deleteRecursively()
    }

    fun loadSquaredDrawable(index: Int, uri: Uri, context: Context, scale: Int = 64): Bitmap? {
        val event = EventHandler.getEvent(index)
        if (event != null) {
            if (event is EventBirthday && event.avatarImageUri != null) {
                try {
                    // we mostly dont need a try catch here, because this function should only be called after all drawables have once been loaded into the map
                    //TODO: dont load whole bitmap, load compressed bitmap
                    val bitmap =
                        getScaledBitmap(MediaStore.Images.Media.getBitmap(context.contentResolver, uri), scale)
                    return bitmap

                } catch (e: Exception) {
                    //when gallery file has been corrupted, or deleted, or renamed
                    //delete reference in map and delete created bitmap in files
                    e.printStackTrace()
                    event.avatarImageUri = null
                    EventHandler.changeEventAt(index, event, context, writeAfterChange = true)
                    removeBitmap(index, context)
                    showMissingImageAlertDialog(context)
                    return null
                }
            }
        }
        return null
    }

    fun removeBitmap(id: Int, context: Context) {
        val event = EventHandler.getEvent(id)
        if (event != null) {
            drawable_map.toMutableMap().remove(id)
            removeBitmapFromFiles(context, event.eventID)
        }
    }

    /**
     * for testing purposes
     */
    fun getAllBitmaps(): List<Bitmap> {
        return this.drawable_map.values.toList()
    }

    /**
     * loadAllDrawables iterates through eventhandler eventlist and loads all drawables into this map
     */
    fun loadAllBitmaps(context: Context): Boolean {
        var success = true
        for (i in 0 until EventHandler.getList().size) {
            if ((EventHandler.getList()[i] is EventBirthday) && ((EventHandler.getList()[i] as EventBirthday).avatarImageUri != null)) {
                success =
                    addDrawable(
                        EventHandler.getList()[i].eventID,
                        Uri.parse((EventHandler.getList()[i] as EventBirthday).avatarImageUri),
                        context,
                        readBitmapFromGallery = false
                    )
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

    private fun getBitmapFromFile(context: Context, eventID: Int): Bitmap? {
        val bitmapDir = context.getDir(this.bitmapFolder, Context.MODE_PRIVATE)
        return BitmapFactory.decodeFile(bitmapDir.absolutePath + File.separator.toString() + "$eventID.png")
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
        //TODO: change this to one time check, and not iterate all
        val bitmap_dir = context.getDir(this.bitmapFolder, Context.MODE_PRIVATE)
        val bitmapFile = File(bitmap_dir.absolutePath + File.separator + "$eventID.png")
        return if (bitmapFile.exists()) {
            bitmapFile
        } else {
            null
        }
    }

    private fun removeBitmapFromFiles(context: Context, eventID: Int) {
        val bitmapFile = checkExistingBitmapInFiles(context, eventID)
        bitmapFile?.delete()
    }

///
/// I know that all the following functions are already implemented by f.e. ThumbnailUtils, but I noticed this to late, and wanted to do this on my own for possible optimizing later
///

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

    fun getCircularBitmap(bitmap: Bitmap, resources: Resources): Bitmap {
        val rounded_bmp: RoundedBitmapDrawable =
            RoundedBitmapDrawableFactory.create(resources, getSquaredBitmap(bitmap))
        rounded_bmp.isCircular = true
        return drawableToBitmap(rounded_bmp)
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

    fun showMissingImageAlertDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.alert_dialog_missing_avatar_img_title)
        builder.setMessage(R.string.alert_dialog_missing_avatar_img_text)
        builder.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_error_outline)
        builder.show()
    }
}