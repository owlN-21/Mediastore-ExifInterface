package com.example.imageseditor.screen

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    imageUri: Uri,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit,
    viewModel: ImageScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val exifInfo by viewModel.exifInfo.collectAsState()

    LaunchedEffect(imageUri) {
        viewModel.loadExif(imageUri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Предпросмотр") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val encodedUri = Uri.encode(imageUri.toString())
                    onEditClick(encodedUri)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать EXIF"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "EXIF данные",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            val exif = exifInfo


            Text(
                text = "Дата создания: ${exif?.date ?: "—"}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "Широта: ${exif?.latitude ?: "—"}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "Долгота: ${exif?.longitude ?: "—"}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "Устройство: ${exif?.device ?: "—"}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "Модель: ${exif?.model ?: "—"}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
