package com.example.imageseditor.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import com.example.imageseditor.dto.ImageExifInfo

object MediaStoreRepository {

    fun loadImagesFromMediaStore(context: Context): List<Uri> {
        val imageUris = mutableListOf<Uri>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                imageUris.add(ContentUris.withAppendedId(collection, id))
            }
        }
        return imageUris
    }

    // 🔥 НОВОЕ: создаём копию изображения с новым EXIF
    fun createEditedImage(
        context: Context,
        sourceUri: Uri,
        exifInfo: ImageExifInfo
    ): Uri {

        val newUri = createImageUri(context)

        copyImage(context, sourceUri, newUri)

        writeExif(context, newUri, exifInfo)

        return newUri
    }

    private fun createImageUri(context: Context): Uri {
        val values = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "edited_${System.currentTimeMillis()}.jpg"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/ImageEditor"
            )
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )!!
    }

    private fun copyImage(context: Context, source: Uri, target: Uri) {
        context.contentResolver.openInputStream(source).use { input ->
            context.contentResolver.openOutputStream(target).use { output ->
                input!!.copyTo(output!!)
            }
        }
    }

    private fun writeExif(
        context: Context,
        uri: Uri,
        exifInfo: ImageExifInfo
    ) {
        context.contentResolver.openFileDescriptor(uri, "rw")?.use { pfd ->
            val exif = ExifInterface(pfd.fileDescriptor)

            exifInfo.date?.let {
                exif.setAttribute(
                    ExifInterface.TAG_DATETIME_ORIGINAL,
                    it
                )
            }

            exifInfo.device?.let {
                exif.setAttribute(ExifInterface.TAG_MAKE, it)
            }

            exifInfo.model?.let {
                exif.setAttribute(ExifInterface.TAG_MODEL, it)
            }

            val lat = exifInfo.latitude
            val lng = exifInfo.longitude
            if (lat != null && lng != null) {
                exif.setAttribute(
                    ExifInterface.TAG_GPS_LATITUDE,
                    toExifDms(lat)
                )
                exif.setAttribute(
                    ExifInterface.TAG_GPS_LATITUDE_REF,
                    if (lat >= 0) "N" else "S"
                )
                exif.setAttribute(
                    ExifInterface.TAG_GPS_LONGITUDE,
                    toExifDms(lng)
                )
                exif.setAttribute(
                    ExifInterface.TAG_GPS_LONGITUDE_REF,
                    if (lng >= 0) "E" else "W"
                )
            }

            exif.saveAttributes()
        }
    }

    private fun toExifDms(value: Double): String {
        val abs = kotlin.math.abs(value)
        val degrees = abs.toInt()
        val minutesFull = (abs - degrees) * 60
        val minutes = minutesFull.toInt()
        val seconds = ((minutesFull - minutes) * 60 * 1000).toInt()
        return "$degrees/1,$minutes/1,$seconds/1000"
    }
}
