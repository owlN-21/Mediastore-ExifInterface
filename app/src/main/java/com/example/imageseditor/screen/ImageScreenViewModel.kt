package com.example.imageseditor.screen

import android.app.Application
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import com.example.imageseditor.dto.ImageExifInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ImageScreenViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _exifInfo = MutableStateFlow<ImageExifInfo?>(null)
    val exifInfo: StateFlow<ImageExifInfo?> = _exifInfo

    fun loadExif(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()

            val inputStream = context.contentResolver.openInputStream(uri)

            val metadata = inputStream?.use {
                ImageMetadataReader.readMetadata(it)
            }

            val exifIfd0 = metadata?.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
            val exifSub = metadata?.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
            val gps = metadata?.getFirstDirectoryOfType(GpsDirectory::class.java)

            val result = ImageExifInfo(
                date = exifSub?.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL),
                latitude = gps?.geoLocation?.latitude,
                longitude = gps?.geoLocation?.longitude,
                device = exifIfd0?.getString(ExifIFD0Directory.TAG_MAKE),
                model = exifIfd0?.getString(ExifIFD0Directory.TAG_MODEL)
            )

            _exifInfo.value = result
        }
    }
}