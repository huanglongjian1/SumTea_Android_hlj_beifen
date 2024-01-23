package com.sum.video.videofragment.test7

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.annotation.RequiresApi


data class MusicData(
    val id: Int = 0,
    val name: String? = null,
    val singer: String? = null,
    val album: Bitmap? = null,
    val path: String? = null,
    val duration: Long = 0,
    val size: Long = 0,
    val uri: Uri
)

object MetadataReaderUtils {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getMusicDataList(context: Context): List<MusicData> {
        val list = mutableListOf<MusicData>()
        val data = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null, null, null,
            MediaStore.Audio.Media.IS_MUSIC
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                val uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                val musicData = MusicData(
                    id.toInt(), name, singer, getAlbumArt(context, albumId), path, duration.toLong(), size.toLong(), uri
                )
                list.add(musicData)
            }
            cursor.close()
        }
        return list
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAlbumArt(context: Context, albumId: Long): Bitmap? {
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            albumId
        )
        return try {
            context.contentResolver.loadThumbnail(
                contentUri, Size(640, 480), null)
        } catch (e: Exception) {
            return null
        }
    }
}