package com.example.imageseditor.screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.imageseditor.data.MediaStoreRepository


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SinglePickerScreen(
    onImagePicked: (Uri) -> Unit
) {
    val context = LocalContext.current
    var images by  remember { mutableStateOf<List<Uri>>(emptyList()) }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            images = MediaStoreRepository.loadImagesFromMediaStore(context)
        }
    }

    LaunchedEffect(Unit) {
        if (hasPermission) {
            images = MediaStoreRepository.loadImagesFromMediaStore(context)
        } else {
            permissionLauncher.launch(permission)
        }
    }

    when {
        !hasPermission -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нужно разрешение на чтение фото")
            }
        }

        images.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Картинок не найдено")
            }
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(images) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onImagePicked(uri) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}