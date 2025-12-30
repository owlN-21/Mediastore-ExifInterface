package com.example.imageseditor.screen

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.imageseditor.data.MediaStoreRepository
import com.example.imageseditor.dto.ImageExifInfo
class ImageEditViewModel : ViewModel() {

    var date by mutableStateOf("")
    var latitude by mutableStateOf("")
    var longitude by mutableStateOf("")
    var device by mutableStateOf("")
    var model by mutableStateOf("")

    fun saveAsNewImage(
        context: Context,
        sourceUri: Uri
    ): Uri {

        val exifInfo = ImageExifInfo(
            date = date.ifBlank { null },
            latitude = latitude.toDoubleOrNull(),
            longitude = longitude.toDoubleOrNull(),
            device = device.ifBlank { null },
            model = model.ifBlank { null }
        )

        return MediaStoreRepository.createEditedImage(
            context = context,
            sourceUri = sourceUri,
            exifInfo = exifInfo
        )
    }

    fun onDateChange(value: String) { date = value }
    fun onLatitudeChange(value: String) { latitude = value }
    fun onLongitudeChange(value: String) { longitude = value }
    fun onDeviceChange(value: String) { device = value }
    fun onModelChange(value: String) { model = value }
}
