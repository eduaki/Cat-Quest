package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoManager {

    private fun getPhotosDir(context: Context): File {
        val dir = File(context.filesDir, "cat_photos")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun createCameraOutputFile(context: Context): File {
        val photosDir = getPhotosDir(context)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Date())
        return File(photosDir, "CAM_CAT_$timeStamp.jpg")
    }

    fun createTempCameraUri(context: Context): Pair<Uri, File> {
        val photosDir = getPhotosDir(context)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val photoFile = File(photosDir, "IMG_CAT_$timeStamp.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        return Pair(uri, photoFile)
    }

    fun saveImageFromUri(context: Context, sourceUri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                saveBitmapToInternal(context, bitmap)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBitmapToInternal(context: Context, bitmap: Bitmap): String {
        val photosDir = getPhotosDir(context)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Date())
        val photoFile = File(photosDir, "PROVED_$timeStamp.jpg")

        FileOutputStream(photoFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return photoFile.absolutePath
    }

    /**
     * Generates a delightful cartoon cat illustration badge bitmap.
     * Perfect for testing or creating an instant photo proof!
     */
    fun createSampleCutePhoto(
        context: Context,
        categoryName: String,
        missionTitle: String,
        catName: String
    ): String {
        val width = 720
        val height = 720
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = when (categoryName) {
                "WATER" -> 0xFFE0F7FA.toInt()
                "FOOD" -> 0xFFFFF3E0.toInt()
                "HYGIENE" -> 0xFFE8F5E9.toInt()
                "PLAY" -> 0xFFFFF8E1.toInt()
                "GROOMING" -> 0xFFF3E5F5.toInt()
                else -> 0xFFFFEBEE.toInt()
            }
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Draw cute pastel border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFF7043.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 24f
        }
        canvas.drawRoundRect(RectF(16f, 16f, width - 16f, height - 16f), 48f, 48f, borderPaint)

        // Draw decorative inner frame
        val innerFramePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x22FFFFFF
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(40f, 40f, width - 40f, height - 40f), 36f, 36f, innerFramePaint)

        // Draw cute cat face
        val catFacePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            style = Paint.Style.FILL
        }
        val catOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF4E342E.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        // Cat head circle
        val cx = width / 2f
        val cy = height / 2f - 20f
        canvas.drawCircle(cx, cy, 170f, catFacePaint)
        canvas.drawCircle(cx, cy, 170f, catOutlinePaint)

        // Ears
        val earPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFF80AB.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx - 100f, cy - 140f, 45f, earPaint)
        canvas.drawCircle(cx + 100f, cy - 140f, 45f, earPaint)

        // Eyes
        val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF3E2723.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx - 55f, cy - 30f, 18f, eyePaint)
        canvas.drawCircle(cx + 55f, cy - 30f, 18f, eyePaint)

        // Eye shines
        val shinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
        }
        canvas.drawCircle(cx - 50f, cy - 36f, 6f, shinePaint)
        canvas.drawCircle(cx + 60f, cy - 36f, 6f, shinePaint)

        // Pink nose & cheeks
        val pinkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFF4081.toInt()
        }
        canvas.drawCircle(cx, cy + 15f, 16f, pinkPaint)

        val cheekPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x66FF8A80
        }
        canvas.drawCircle(cx - 95f, cy + 25f, 28f, cheekPaint)
        canvas.drawCircle(cx + 95f, cy + 25f, 28f, cheekPaint)

        // Text banner at bottom
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF4E342E.toInt()
            textSize = 34f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("🐾 MISSÃO CUMPRIDA! 🐾", cx, height - 120f, textPaint)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFD84315.toInt()
            textSize = 28f
            textAlign = Paint.Align.CENTER
        }
        val shortTitle = if (missionTitle.length > 30) missionTitle.take(28) + "..." else missionTitle
        canvas.drawText(shortTitle, cx, height - 75f, titlePaint)

        val stampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF795548.toInt()
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        val dateText = "Gatinho $catName • " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText(dateText, cx, height - 38f, stampPaint)

        return saveBitmapToInternal(context, bitmap)
    }

    /**
     * Share mural / monthly recap with friends!
     */
    fun shareMonthlyMural(
        context: Context,
        monthName: String,
        totalMissions: Int,
        totalPhotos: Int,
        streakDays: Int,
        catName: String,
        samplePhotoPath: String?
    ) {
        val shareText = """
            🐾✨ Retrospectiva do $catName - $monthName ✨🐾
            
            🐱 Total de Missões Cumpridas: $totalMissions
            📸 Fotos de Comprovação: $totalPhotos
            🔥 Sequência de Cuidados: $streakDays dias seguidos!
            💖 Felicidade Felina: 100% de amor e ronrons!
            
            Cuidado com muito amor pelo app Miau Missões! 🐾💖
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Mural do Mês do $catName 🐾")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        if (samplePhotoPath != null) {
            val file = File(samplePhotoPath)
            if (file.exists()) {
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                intent.type = "image/jpeg"
                intent.putExtra(Intent.EXTRA_STREAM, photoUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        val chooser = Intent.createChooser(intent, "Compartilhar Mural do $catName")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
