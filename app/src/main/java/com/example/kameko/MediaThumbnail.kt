package com.example.kameko

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.key.Keyer
import coil.request.Options
import coil.size.Dimension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * グリッドや履歴で使うサムネイル用のモデル。
 *
 * 元画像（数MB〜数十MBのJPEG/RAW）を毎回デコードするのではなく、
 * OS（MediaStore）が保持しているサムネイルを使うことで、
 * 標準ギャラリーアプリと同じように起動直後でもすぐに表示できるようにする。
 * OS側のサムネイルはディスクにキャッシュされているため、アプリの再起動後も速い。
 */
data class MediaThumb(val filePath: String)

/** メモリキャッシュのキー。サイズ別にキャッシュされる（サイズはCoilが自動で付与）。 */
class MediaThumbKeyer : Keyer<MediaThumb> {
    override fun key(data: MediaThumb, options: Options): String = "thumb:${data.filePath}"
}

class MediaThumbFetcher(
    private val data: MediaThumb,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult? = withContext(Dispatchers.IO) {
        val context = options.context
        val px = (options.size.width as? Dimension.Pixels)?.px
            ?: (options.size.height as? Dimension.Pixels)?.px
            ?: 320

        val bitmap = loadFromMediaStore(context, px) ?: decodeSampledFile(px) ?: return@withContext null

        DrawableResult(
            drawable = BitmapDrawable(context.resources, bitmap),
            isSampled = true,
            dataSource = DataSource.DISK
        )
    }

    private fun loadFromMediaStore(context: Context, px: Int): Bitmap? {
        val contentUri = resolveContentUri(context, data.filePath) ?: return null
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.loadThumbnail(contentUri, Size(px, px), null)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Thumbnails.getThumbnail(
                    context.contentResolver,
                    ContentUris.parseId(contentUri),
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    /** MediaStoreにサムネイルがない場合のフォールバック（縮小デコード） */
    private fun decodeSampledFile(px: Int): Bitmap? {
        val path = data.filePath
        if (path.startsWith("content://")) return null
        val file = File(path)
        if (!file.exists()) return null
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, bounds)
            var sample = 1
            while (bounds.outWidth / (sample * 2) >= px && bounds.outHeight / (sample * 2) >= px) {
                sample *= 2
            }
            BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
        } catch (e: Exception) {
            null
        }
    }

    class Factory : Fetcher.Factory<MediaThumb> {
        override fun create(data: MediaThumb, options: Options, imageLoader: ImageLoader): Fetcher =
            MediaThumbFetcher(data, options)
    }

    companion object {
        // ファイルパス → MediaStore content Uri の対応表（プロセス内キャッシュ）
        private val uriCache = ConcurrentHashMap<String, Uri>()

        private fun resolveContentUri(context: Context, filePath: String): Uri? {
            if (filePath.startsWith("content://")) return Uri.parse(filePath)
            uriCache[filePath]?.let { return it }

            val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            return try {
                context.contentResolver.query(
                    collection,
                    arrayOf(MediaStore.Images.Media._ID),
                    "${MediaStore.Images.Media.DATA} = ?",
                    arrayOf(filePath),
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        ContentUris.withAppendedId(collection, cursor.getLong(0))
                            .also { uriCache[filePath] = it }
                    } else null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
