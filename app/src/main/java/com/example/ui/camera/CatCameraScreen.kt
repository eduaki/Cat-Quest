package com.example.ui.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.models.CatMission
import com.example.ui.components.CartoonCatFace
import com.example.ui.components.CartoonPawPrint
import com.example.ui.theme.CatBrownMuted
import com.example.ui.theme.CatBrownText
import com.example.ui.theme.CatMint
import com.example.ui.theme.CatPeachPrimary
import com.example.ui.theme.CatPink
import com.example.ui.theme.CatPinkLight
import com.example.ui.theme.CatYellow
import com.example.utils.PhotoManager

private const val TAG = "CatCameraScreen"

@Composable
fun CatCameraScreen(
    mission: CatMission,
    catName: String,
    onPhotoCaptured: (savedPath: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept hardware and gesture back to cleanly exit the camera without closing the app
    BackHandler {
        onClose()
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Live permission tracking with resume re-check
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Re-check permission whenever the activity or composable resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasCameraPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(
                context,
                "Permissão de câmera negada. Você também pode usar a galeria!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Direct Photo Picker Launcher as instant fallback
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = PhotoManager.saveImageFromUri(context, uri)
            if (savedPath != null) {
                onPhotoCaptured(savedPath)
            }
        }
    }

    // If permission is not yet granted, display a friendly prompt
    if (!hasCameraPermission) {
        CameraPermissionPrompt(
            catName = catName,
            missionTitle = mission.title,
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onOpenAppSettings = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            },
            onPickFromGallery = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onGenerateMagicStamp = {
                val magicPath = PhotoManager.createSampleCutePhoto(
                    context = context,
                    categoryName = mission.category,
                    missionTitle = mission.title,
                    catName = catName
                )
                onPhotoCaptured(magicPath)
            },
            onClose = onClose
        )
        return
    }

    // Camera state
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var hasFlashUnit by remember { mutableStateOf(false) }
    var hasMultipleCameras by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }

    var cameraProviderInstance by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var currentCamera by remember { mutableStateOf<Camera?>(null) }
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .build()
    }

    // CRITICAL: Ensure camera is safely unbound when Composable leaves the screen
    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                cameraProviderInstance?.unbindAll()
                Log.d(TAG, "CameraX unbound successfully onDispose")
            } catch (e: Exception) {
                Log.e(TAG, "Error unbinding CameraX onDispose", e)
            }
        }
    }

    // Bind camera with lifecycle and hardware checks
    LaunchedEffect(lensFacing, previewViewRef) {
        val previewView = previewViewRef ?: return@LaunchedEffect

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProviderInstance = cameraProvider

                // Verify hardware camera availability
                val availableCameras = cameraProvider.availableCameraInfos
                if (availableCameras.isEmpty()) {
                    cameraError = "Nenhuma câmera física ou virtual foi detectada neste dispositivo."
                    return@addListener
                }

                hasMultipleCameras = availableCameras.size > 1

                // Select lens safely
                val targetSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
                val cameraSelector = if (cameraProvider.hasCamera(targetSelector)) {
                    targetSelector
                } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.Builder().build()
                }

                // Setup Preview
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                // Check lifecycle state before binding
                if (!lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                    Log.w(TAG, "Lifecycle not initialized; skipping CameraX bind")
                    return@addListener
                }

                // Unbind previous use cases before rebinding
                cameraProvider.unbindAll()

                val boundCamera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                currentCamera = boundCamera
                hasFlashUnit = boundCamera.cameraInfo.hasFlashUnit()
                cameraError = null
                Log.d(TAG, "CameraX successfully bound to lifecycle")
            } catch (exc: Exception) {
                Log.e(TAG, "CameraX binding failed", exc)
                cameraError = "Não foi possível inicializar a câmera: ${exc.localizedMessage ?: "Erro de hardware"}"
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Update flash mode safely on imageCapture instance
    LaunchedEffect(flashMode, hasFlashUnit) {
        try {
            imageCapture.flashMode = if (hasFlashUnit) flashMode else ImageCapture.FLASH_MODE_OFF
        } catch (e: Exception) {
            Log.w(TAG, "Unable to set flash mode", e)
        }
    }

    // Display fallback screen if camera hardware fails or throws
    if (cameraError != null) {
        CameraFallbackPrompt(
            catName = catName,
            errorMessage = cameraError,
            onPickFromGallery = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onGenerateMagicStamp = {
                val magicPath = PhotoManager.createSampleCutePhoto(
                    context = context,
                    categoryName = mission.category,
                    missionTitle = mission.title,
                    catName = catName
                )
                onPhotoCaptured(magicPath)
            },
            onClose = onClose
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera PreviewView inside AndroidView
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }.also {
                    previewViewRef = it
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Cute Cat Viewfinder Overlay
        CatViewfinderOverlay(
            modifier = Modifier.fillMaxSize(),
            missionPrompt = mission.getCategoryEnum().suggestionPrompt
        )

        // Top Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 42.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    try {
                        cameraProviderInstance?.unbindAll()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error unbinding camera on close", e)
                    }
                    onClose()
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .testTag("close_camera_button")
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Fechar Câmera",
                    tint = Color.White
                )
            }

            // Mission tag
            Surface(
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mission.getCategoryEnum().iconEmoji,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = mission.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }

            // Flash mode toggle (only enabled if camera has a flash unit)
            if (hasFlashUnit) {
                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                            else -> ImageCapture.FLASH_MODE_AUTO
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        when (flashMode) {
                            ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                            ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                            else -> Icons.Default.FlashAuto
                        },
                        contentDescription = "Flash",
                        tint = CatYellow
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(44.dp))
            }
        }

        // Bottom Capture Controls
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .padding(bottom = 36.dp, top = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery button
                IconButton(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape)
                        .testTag("camera_gallery_button")
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = "Escolher da Galeria",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Big Paw Shutter Button
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(CatPeachPrimary)
                        .border(4.dp, Color.White, CircleShape)
                        .clickable(enabled = !isCapturing) {
                            if (isCapturing) return@clickable
                            isCapturing = true

                            try {
                                val outputFile = PhotoManager.createCameraOutputFile(context)
                                val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                                imageCapture.takePicture(
                                    outputOptions,
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                            isCapturing = false
                                            if (outputFile.exists() && outputFile.length() > 0) {
                                                onPhotoCaptured(outputFile.absolutePath)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Erro ao salvar a imagem capturada.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            isCapturing = false
                                            Log.e(TAG, "Image capture failed", exception)
                                            Toast.makeText(
                                                context,
                                                "Falha ao tirar foto: ${exception.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            } catch (e: Exception) {
                                isCapturing = false
                                Log.e(TAG, "Error executing takePicture", e)
                                Toast.makeText(
                                    context,
                                    "Erro ao acionar a câmera: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .testTag("camera_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        CartoonPawPrint(
                            size = 46.dp,
                            color = Color.White
                        )
                    }
                }

                // Switch Camera (front/back) - shown if multiple cameras exist
                if (hasMultipleCameras) {
                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape)
                            .testTag("switch_camera_button")
                    ) {
                        Icon(
                            Icons.Default.Cameraswitch,
                            contentDescription = "Alternar Câmera",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(50.dp))
                }
            }
        }
    }
}

@Composable
private fun CameraFallbackPrompt(
    catName: String,
    errorMessage: String? = null,
    onPickFromGallery: () -> Unit,
    onGenerateMagicStamp: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CartoonCatFace(size = 90.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Câmera Indisponível 🐾",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CatBrownText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = errorMessage ?: "O hardware de câmera não pôde ser iniciado neste dispositivo ou emulador. Não se preocupe! Você pode comprovar a missão pela Galeria ou com o Selo Mágico do $catName!",
                    fontSize = 13.sp,
                    color = CatBrownMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onPickFromGallery,
                    modifier = Modifier.fillMaxWidth().testTag("fallback_pick_gallery_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Escolher Foto da Galeria 🖼️", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onGenerateMagicStamp,
                    modifier = Modifier.fillMaxWidth().testTag("fallback_magic_stamp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CatMint),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("✨ Gerar Selo Mágico Fofo", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Voltar", color = CatBrownText)
                }
            }
        }
    }
}

@Composable
private fun CatViewfinderOverlay(
    missionPrompt: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val frameWidth = w * 0.82f
            val frameHeight = frameWidth * 1.15f
            val left = (w - frameWidth) / 2f
            val top = h * 0.22f
            val right = left + frameWidth
            val bottom = top + frameHeight

            val cornerLen = 36.dp.toPx()
            val strokeW = 4.dp.toPx()

            // Viewfinder pastel corners
            drawLine(CatPink, Offset(left, top), Offset(left + cornerLen, top), strokeW)
            drawLine(CatPink, Offset(left, top), Offset(left, top + cornerLen), strokeW)

            drawLine(CatPink, Offset(right, top), Offset(right - cornerLen, top), strokeW)
            drawLine(CatPink, Offset(right, top), Offset(right, top + cornerLen), strokeW)

            drawLine(CatPink, Offset(left, bottom), Offset(left + cornerLen, bottom), strokeW)
            drawLine(CatPink, Offset(left, bottom), Offset(left, bottom - cornerLen), strokeW)

            drawLine(CatPink, Offset(right, bottom), Offset(right - cornerLen, bottom), strokeW)
            drawLine(CatPink, Offset(right, bottom), Offset(right, bottom - cornerLen), strokeW)

            // Cat ears on top edge of the frame
            val earWidth = 40.dp.toPx()
            val earHeight = 36.dp.toPx()

            // Left ear
            val leftEarPath = Path().apply {
                moveTo(left + 24.dp.toPx(), top)
                lineTo(left + 24.dp.toPx() + earWidth / 2f, top - earHeight)
                lineTo(left + 24.dp.toPx() + earWidth, top)
                close()
            }
            drawPath(leftEarPath, color = Color.White.copy(alpha = 0.85f))
            drawPath(leftEarPath, color = CatPeachPrimary, style = Stroke(width = 3.dp.toPx()))

            // Right ear
            val rightEarPath = Path().apply {
                moveTo(right - 24.dp.toPx() - earWidth, top)
                lineTo(right - 24.dp.toPx() - earWidth / 2f, top - earHeight)
                lineTo(right - 24.dp.toPx(), top)
                close()
            }
            drawPath(rightEarPath, color = Color.White.copy(alpha = 0.85f))
            drawPath(rightEarPath, color = CatPeachPrimary, style = Stroke(width = 3.dp.toPx()))
        }

        // Viewfinder Prompt Badge
        Surface(
            color = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp)
        ) {
            Text(
                text = "🐾 $missionPrompt",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun CameraPermissionPrompt(
    catName: String,
    missionTitle: String,
    onRequestPermission: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onPickFromGallery: () -> Unit,
    onGenerateMagicStamp: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                CartoonCatFace(size = 90.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Acesso à Câmera 📸",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CatBrownText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Para comprovar a missão \"$missionTitle\" e ganhar peixinhos dourados para o $catName, precisamos de acesso à câmera do seu aparelho.",
                    fontSize = 13.sp,
                    color = CatBrownMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth().testTag("request_camera_perm_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Permitir Uso da Câmera 🐾", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onOpenAppSettings,
                    modifier = Modifier.fillMaxWidth().testTag("open_settings_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Abrir Configurações do App")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onPickFromGallery,
                    modifier = Modifier.fillMaxWidth().testTag("perm_pick_gallery_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CatMint),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Escolher Foto da Galeria 🖼️")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onGenerateMagicStamp,
                    modifier = Modifier.fillMaxWidth().testTag("perm_magic_stamp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CatPink),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("✨ Gerar Selo Mágico Fofo")
                }
            }
        }
    }
}
