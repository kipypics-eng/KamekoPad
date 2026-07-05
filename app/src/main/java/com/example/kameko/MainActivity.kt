package com.example.kameko

import android.Manifest
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import androidx.exifinterface.media.ExifInterface
import android.content.ComponentName
import androidx.core.content.FileProvider
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// デザイン設定
val NuanceLavender = Color(0xFFFBF8FF)
val DeepCoolGray = Color(0xFF2C2C2E)
val SoftBeige = Color(0xFFF5F2ED)
val PostGreen = Color(0xFF81C784)

enum class KamekoThemeType {
    LAVENDER, BLUE, GREEN, YELLOW, PINK, RED, PURPLE, ORANGE, WHITE
}

enum class PhotoSortType {
    DATE_ADDED, DATE_TAKEN, DATE_MODIFIED
}

@Composable
fun KamekoTheme(
    themeType: KamekoThemeType = KamekoThemeType.YELLOW,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        KamekoThemeType.LAVENDER -> lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005D),
            secondary = Color(0xFF625B71),
            onSecondary = Color.White,
            background = NuanceLavender,
            surface = Color.White
        )
        KamekoThemeType.BLUE -> lightColorScheme(
            primary = Color(0xFF0061A4),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFD1E4FF),
            onPrimaryContainer = Color(0xFF001D36),
            secondary = Color(0xFF535F70),
            onSecondary = Color.White,
            background = Color(0xFFFDFBFF),
            surface = Color.White
        )
        KamekoThemeType.GREEN -> lightColorScheme(
            primary = Color(0xFF006E1C),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFB6F2AF),
            onPrimaryContainer = Color(0xFF002204),
            secondary = Color(0xFF52634F),
            onSecondary = Color.White,
            background = Color(0xFFF7FFEE),
            surface = Color.White
        )
        KamekoThemeType.YELLOW -> lightColorScheme(
            primary = Color(0xFF606200),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE7E970),
            onPrimaryContainer = Color(0xFF1C1D00),
            background = Color(0xFFFFFBE6),
            surface = Color.White
        )
        KamekoThemeType.PINK -> lightColorScheme(
            primary = Color(0xFF984061),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFD9E2),
            onPrimaryContainer = Color(0xFF3E001D),
            background = Color(0xFFFFF8F8),
            surface = Color.White
        )
        KamekoThemeType.RED -> lightColorScheme(
            primary = Color(0xFFBA1A1A),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDAD6),
            onPrimaryContainer = Color(0xFF410002),
            background = Color(0xFFFFF8F7),
            surface = Color.White
        )
        KamekoThemeType.PURPLE -> lightColorScheme(
            primary = Color(0xFF914277),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFD8E9),
            onPrimaryContainer = Color(0xFF3B002B),
            background = Color(0xFFFFFBFF),
            surface = Color.White
        )
        KamekoThemeType.ORANGE -> lightColorScheme(
            primary = Color(0xFF944B00),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDCC0),
            onPrimaryContainer = Color(0xFF301400),
            background = Color(0xFFFFFBFF),
            surface = Color.White
        )
        KamekoThemeType.WHITE -> lightColorScheme(
            primary = Color(0xFF1B1B1F),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3E2E6),
            onPrimaryContainer = Color(0xFF1B1B1F),
            background = Color.White,
            surface = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// 写真アイテムのデータモデル
data class LocalPhotoItem(
    val uri: Uri,
    val filePath: String,
    val fileType: String,
    val dateTaken: Long,
    val dateModified: Long,
    val dateAdded: Long, // 追加日時
    val dateStr: String,
    val cameraMake: String? = null
)

data class PhotoMetaInfo(
    val fileName: String,
    val exifDate: String,
    val modifiedDate: String,
    val shutterSpeed: String,
    val aperture: String,
    val iso: String,
    val focalLength: String,
    val cameraMake: String? = null
)

fun formatShutterSpeed(exposureTime: Double?): String {
    if (exposureTime == null || exposureTime <= 0) return "---"
    return if (exposureTime < 1.0) {
        val denominator = (1.0 / exposureTime).let {
            if (it.toInt().toDouble() == it.let { v -> Math.round(v) }.toDouble()) Math.round(it) else Math.round(it)
        }
        "1/${denominator}"
    } else {
        val seconds = exposureTime
        if (seconds == seconds.toLong().toDouble()) "${seconds.toLong()}秒"
        else "${"%.1f".format(seconds)}秒"
    }
}

data class PostStats(val photoCount: Int, val postCount: Int)

// ViewModel
class PhotoViewModel(
    private val photoDao: PhotoDao,
    private val eventDao: EventDao,
    private val historyDao: StatusHistoryDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var localPhotos by mutableStateOf<List<LocalPhotoItem>>(emptyList())
    var isRefreshing by mutableStateOf(false)

    // テーマ選択の保持
    var currentTheme by mutableStateOf(
        runCatching { savedStateHandle.get<KamekoThemeType>("app_theme") }.getOrNull() ?: KamekoThemeType.YELLOW
    )
    fun updateTheme(theme: KamekoThemeType) {
        currentTheme = theme
        savedStateHandle["app_theme"] = theme
    }

    // SavedStateHandle によるフィルター条件保持
    private var _searchQuery by mutableStateOf(savedStateHandle.get<String>("search_query") ?: "")
    var searchQuery: String
        get() = _searchQuery
        set(value) { _searchQuery = value; savedStateHandle["search_query"] = value }

    private var _selectedStatusFilter by mutableStateOf(savedStateHandle.get<String>("selected_status_filter") ?: "ALL")
    var selectedStatusFilter: String
        get() = _selectedStatusFilter
        set(value) { _selectedStatusFilter = value; savedStateHandle["selected_status_filter"] = value }

    private var _selectedTypeFilter by mutableStateOf(savedStateHandle.get<String>("selected_type_filter") ?: "ALL")
    var selectedTypeFilter: String
        get() = _selectedTypeFilter
        set(value) { _selectedTypeFilter = value; savedStateHandle["selected_type_filter"] = value }

    private var _selectedEventFilterId by mutableStateOf(savedStateHandle.get<Long?>("selected_event_filter_id"))
    var selectedEventFilterId: Long?
        get() = _selectedEventFilterId
        set(value) { _selectedEventFilterId = value; savedStateHandle["selected_event_filter_id"] = value }

    // 機材フィルタの保持
    private var _selectedMakeFilter by mutableStateOf(savedStateHandle.get<String?>("selected_make_filter"))
    var selectedMakeFilter: String?
        get() = _selectedMakeFilter
        set(value) { _selectedMakeFilter = value; savedStateHandle["selected_make_filter"] = value }

    // 並び替えの保持
    private var _selectedSortType by mutableStateOf(
        runCatching { savedStateHandle.get<PhotoSortType>("selected_sort_type") }.getOrNull() ?: PhotoSortType.DATE_ADDED
    )
    var selectedSortType: PhotoSortType
        get() = _selectedSortType
        set(value) { _selectedSortType = value; savedStateHandle["selected_sort_type"] = value }

    // 機材フィルタ用の写真選択モード
    var isSelectingMakeReference by mutableStateOf(false)

    // 共有後の投稿確認待ちリスト
    var pendingPostPhotos by mutableStateOf<List<PhotoEntity>>(emptyList())

    // 撮影設定フィルタの保持
    private var _selectedIsoFilter by mutableStateOf(savedStateHandle.get<String?>("selected_iso_filter"))
    var selectedIsoFilter: String?
        get() = _selectedIsoFilter
        set(value) { _selectedIsoFilter = value; savedStateHandle["selected_iso_filter"] = value }

    private var _selectedFocalFilter by mutableStateOf(savedStateHandle.get<String?>("selected_focal_filter"))
    var selectedFocalFilter: String?
        get() = _selectedFocalFilter
        set(value) { _selectedFocalFilter = value; savedStateHandle["selected_focal_filter"] = value }

    private var _selectedSsFilter by mutableStateOf(savedStateHandle.get<String?>("selected_ss_filter"))
    var selectedSsFilter: String?
        get() = _selectedSsFilter
        set(value) { _selectedSsFilter = value; savedStateHandle["selected_ss_filter"] = value }

    private var _selectedApertureFilter by mutableStateOf(savedStateHandle.get<String?>("selected_aperture_filter"))
    var selectedApertureFilter: String?
        get() = _selectedApertureFilter
        set(value) { _selectedApertureFilter = value; savedStateHandle["selected_aperture_filter"] = value }

    private var _shareActionStarted by mutableStateOf(savedStateHandle.get<Boolean>("share_action_started") ?: false)
    var shareActionStarted: Boolean
        get() = _shareActionStarted
        set(value) { _shareActionStarted = value; savedStateHandle["share_action_started"] = value }

    private var _showPostConfirmDialog by mutableStateOf(savedStateHandle.get<Boolean>("show_post_confirm_dialog") ?: false)
    var showPostConfirmDialog: Boolean
        get() = _showPostConfirmDialog
        set(value) { _showPostConfirmDialog = value; savedStateHandle["show_post_confirm_dialog"] = value }

    fun restorePendingPhotos(allPhotos: List<PhotoEntity>) {
        val savedPaths = savedStateHandle.get<List<String>>("pending_photo_paths") ?: emptyList()
        if (savedPaths.isNotEmpty() && pendingPostPhotos.isEmpty()) {
            pendingPostPhotos = savedPaths.mapNotNull { path -> allPhotos.find { it.filePath == path } }
        }
    }

    fun savePendingPhotos(photos: List<PhotoEntity>) {
        pendingPostPhotos = photos
        savedStateHandle["pending_photo_paths"] = photos.map { it.filePath }
    }

    fun completePendingPost() {
        if (pendingPostPhotos.isEmpty()) return
        
        viewModelScope.launch(Dispatchers.IO) {
            val groupId = System.currentTimeMillis()
            val now = System.currentTimeMillis()
            pendingPostPhotos.forEach { photo ->
                updatePhotoDetails(
                    photo.copy(status = PhotoStatus.POSTED.name, sharedAt = now),
                    groupId = groupId
                )
            }
            withContext(Dispatchers.Main) {
                clearPendingPhotos()
            }
        }
    }

    fun clearPendingPhotos() {
        pendingPostPhotos = emptyList()
        savedStateHandle.remove<List<String>>("pending_photo_paths")
        showPostConfirmDialog = false
    }

    // 指定されたUriから機材(Make)を抽出してフィルタに設定する
    fun setMakeFilterFromUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val make = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val exif = ExifInterface(inputStream)
                    (exif.getAttribute(ExifInterface.TAG_MAKE) ?: exif.getAttribute(ExifInterface.TAG_MODEL))?.trim()
                } ?: return@launch

                withContext(Dispatchers.Main) {
                    selectedMakeFilter = make
                    isRefreshing = true // スキャン中であることを示す
                }

                val allDbPhotos = photoDao.getAllPhotosOnce().associateBy { it.filePath }
                val toUpdate = mutableListOf<PhotoEntity>()
                val toInsert = mutableListOf<PhotoEntity>()

                // 並列処理で全写真のExifをチェック (50個ずつのチャンクで実行)
                coroutineScope {
                    localPhotos.chunked(50).forEach { chunk ->
                        val results = chunk.map { item ->
                            async {
                                try {
                                    context.contentResolver.openInputStream(item.uri)?.use { photoStream ->
                                        val photoExif = ExifInterface(photoStream)
                                        val photoMake = (photoExif.getAttribute(ExifInterface.TAG_MAKE) ?: 
                                                        photoExif.getAttribute(ExifInterface.TAG_MODEL))?.trim()
                                        
                                        if (photoMake == make) {
                                            val existing = allDbPhotos[item.filePath]
                                            if (existing != null) {
                                                if (existing.cameraMake != make) {
                                                    existing.copy(cameraMake = make)
                                                } else null
                                            } else {
                                                PhotoEntity(
                                                    filePath = item.filePath,
                                                    status = PhotoStatus.SHOT.name,
                                                    cameraMake = make,
                                                    fileType = item.fileType
                                                )
                                            }
                                        } else null
                                    }
                                } catch (e: Exception) { null }
                            }
                        }.awaitAll().filterNotNull()

                        results.forEach { photo ->
                            if (photo.id != 0L) toUpdate.add(photo) else toInsert.add(photo)
                        }
                    }
                }

                // まとめてDB更新
                if (toUpdate.isNotEmpty()) photoDao.updatePhotos(toUpdate)
                if (toInsert.isNotEmpty()) photoDao.insertPhotos(toInsert)

                withContext(Dispatchers.Main) {
                    isRefreshing = false
                }
            } catch (e: Exception) {
                Log.e("KamekoPad", "Failed to extract Make", e)
                withContext(Dispatchers.Main) { isRefreshing = false }
            }
        }
    }

    val photos = photoDao.getAllPhotos().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val events = eventDao.getAllEvents().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val availableBodies = photoDao.getUniqueMakes().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val postStatsLast24h = kotlinx.coroutines.flow.combine(
        historyDao.getPostedPhotoCountSince(System.currentTimeMillis() - 24 * 60 * 60 * 1000L),
        historyDao.getUniquePostCountSince(System.currentTimeMillis() - 24 * 60 * 60 * 1000L)
    ) { photos, posts -> PostStats(photos, posts) }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), PostStats(0, 0)
    )

    val allTimePostStats = historyDao.getAllPostedHistory().map { list ->
        PostStats(list.size, list.distinctBy { it.groupId }.size)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), PostStats(0, 0)
    )

    fun updatePhotoDetails(photo: PhotoEntity, groupId: Long = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = photoDao.getPhotoByPath(photo.filePath)
            val oldStatus = existing?.status

            if (existing != null) {
                photoDao.updatePhoto(photo.copy(id = existing.id))
            } else {
                photoDao.insertPhoto(photo)
            }

            if (oldStatus != photo.status) {
                val finalGroupId = if (photo.status == PhotoStatus.POSTED.name && groupId == 0L) {
                    System.currentTimeMillis()
                } else {
                    groupId
                }
                historyDao.insert(
                    StatusHistoryEntity(photoFilePath = photo.filePath, status = photo.status, groupId = finalGroupId)
                )
            }
        }
    }

    suspend fun registerPhotosWithAutoEvent(photoItems: List<LocalPhotoItem>, context: Context) = withContext(Dispatchers.IO) {
        val allEvents = eventDao.getAllEventsOnce()
        val allDbPhotos = photoDao.getAllPhotosOnce().associateBy { it.filePath }
        
        photoItems.forEach { item ->
            val path = item.filePath
            val existing = allDbPhotos[path]
            
            // 現場の自動割り付け判定
            var photoDate = item.dateStr.replace("/", "-")
            
            // EXIF情報の抽出
            // 撮影日がズレる問題に対応するため、現場が未設定 or 整合性が不十分な場合にEXIFを優先する
            if (existing == null || existing.iso == null || existing.cameraMake == null || existing.eventId == null) {
                var extractedIso: Int? = null
                var extractedFocal: Double? = null
                var extractedAperture: Double? = null
                var extractedSs: Double? = null
                var extractedMake: String? = null
                var extractedDate: String? = null

                try {
                    context.contentResolver.openInputStream(item.uri)?.use { inputStream ->
                        val exif = ExifInterface(inputStream)
                        extractedIso = exif.getAttributeInt(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, -1)
                            .let { if (it == -1) exif.getAttributeInt(ExifInterface.TAG_ISO_SPEED, -1) else it }
                            .let { if (it == -1) null else it }
                        
                        extractedFocal = exif.getAttributeInt(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM, -1)
                            .let { if (it == -1) null else it.toDouble() }
                        
                        extractedAperture = exif.getAttributeDouble(ExifInterface.TAG_F_NUMBER, -1.0)
                            .let { if (it <= 0) null else it }
                        
                        extractedSs = exif.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME, -1.0)
                            .let { if (it <= 0) null else it }
                        
                        extractedMake = (exif.getAttribute(ExifInterface.TAG_MAKE) ?: exif.getAttribute(ExifInterface.TAG_MODEL))?.trim()

                        val exifDate = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                            ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
                        if (exifDate != null) {
                            extractedDate = exifDate.substringBefore(" ").replace(":", "-")
                        }
                    }
                } catch (e: Exception) { }

                if (extractedDate != null) {
                    photoDate = extractedDate!!
                }

                val matchingEventId = allEvents.find { 
                    val eventDate = it.eventDate.trim().replace("/", "-")
                    eventDate.isNotEmpty() && eventDate == photoDate
                }?.id

                if (existing == null) {
                    photoDao.insertPhoto(PhotoEntity(
                        filePath = path, 
                        fileType = item.fileType, 
                        status = PhotoStatus.SHOT.name, 
                        eventId = matchingEventId,
                        iso = extractedIso,
                        focalLength = extractedFocal,
                        aperture = extractedAperture,
                        shutterSpeed = extractedSs,
                        cameraMake = extractedMake
                    ))
                } else {
                    // 撮影日と現場の日付が一致しているか厳格にチェック
                    val currentEvent = allEvents.find { it.id == existing.eventId }
                    val currentEventDate = currentEvent?.eventDate?.trim()?.replace("/", "-")
                    
                    val finalEventId = if (currentEventDate != null && currentEventDate != photoDate) {
                        // 日付が一致しない場合（イベントを消した、または間違って紐付いた）
                        // 正しい日付のイベントがあればそこへ、なければ未割り当て(null)に強制移動
                        matchingEventId
                    } else {
                        existing.eventId ?: matchingEventId
                    }

                    photoDao.updatePhoto(existing.copy(
                        eventId = finalEventId,
                        iso = existing.iso ?: extractedIso,
                        focalLength = existing.focalLength ?: extractedFocal,
                        aperture = existing.aperture ?: extractedAperture,
                        shutterSpeed = existing.shutterSpeed ?: extractedSs,
                        cameraMake = existing.cameraMake ?: extractedMake
                    ))
                }
            }
        }
    }

    fun addEvent(event: EventEntity, currentPhotos: List<LocalPhotoItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            // 重複チェック
            val existing = eventDao.findEvent(event.name, event.venue, event.eventDate)
            val eventId = existing?.id ?: eventDao.insert(event)

            if (event.startTime <= 0L) return@launch

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val eventDateString = sdf.format(Date(event.startTime))

            currentPhotos.forEach { localPhoto ->
                val photoDateString = localPhoto.dateStr.replace("/", "-")
                if (photoDateString == eventDateString.replace("/", "-")) {
                    val existingDbPhoto = photoDao.getPhotoByPath(localPhoto.filePath)
                    if (existingDbPhoto != null) {
                        if (existingDbPhoto.eventId == null) {
                            photoDao.updatePhoto(existingDbPhoto.copy(eventId = eventId))
                        }
                    } else {
                        photoDao.insertPhoto(PhotoEntity(filePath = localPhoto.filePath, fileType = localPhoto.fileType, status = PhotoStatus.SHOT.name, eventId = eventId, memo = ""))
                    }
                }
            }
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) { eventDao.delete(event) }
    }

    fun insertBulkEventsFromText(rawText: String, onComplete: ((Int) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val blocks = rawText.split(Regex("\n\\s*\n"))
            var count = 0
            for (block in blocks) {
                val lines = block.lines().map { it.trim() }.filter { it.isNotEmpty() }
                if (lines.size >= 3) {
                    val name = lines[0]
                    val venue = lines[1]
                    val dateStr = lines[2]
                    
                    // 重複チェック
                    val existing = eventDao.findEvent(name, venue, dateStr)
                    if (existing == null) {
                        val startTimeTimestamp = try { sdf.parse(dateStr)?.time ?: 0L } catch (e: Exception) { 0L }
                        eventDao.insert(EventEntity(id = 0, name = name, venue = venue, eventDate = dateStr, startTime = startTimeTimestamp, endTime = 0L))
                        count++
                    }
                }
            }
            withContext(Dispatchers.Main) {
                onComplete?.invoke(count)
            }
        }
    }

    /**
     * GitHubなどのURLからテキスト形式のイベントリストを取得してインポートする
     */
    fun importEventsFromUrl(url: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                if (connection.responseCode == 200) {
                    val text = connection.inputStream.bufferedReader().use { it.readText() }
                    insertBulkEventsFromText(text) { count ->
                        onResult(true, "${count}件の新規イベントを取り込みました")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(false, "サーバーエラー: ${connection.responseCode}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, "通信失敗: ${e.message}")
                }
            }
        }
    }

    /**
     * 全イベントをテキスト形式（3行ブロック）で書き出す
     */
    fun exportEventsToText(): String {
        val allEvents = events.value.sortedByDescending { it.startTime }
        return allEvents.joinToString("\n\n") { event ->
            "${event.name}\n${event.venue}\n${event.eventDate}"
        }
    }

    val allPostedHistory = historyDao.getAllPostedHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun decrementPostedCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val since = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
            historyDao.deleteLatestGroupPostedSince(since)
        }
    }

    fun deleteHistoryGroup(groupId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val histories = historyDao.getHistoryByGroupId(groupId)
            histories.forEach { history ->
                val photo = photoDao.getPhotoByPath(history.photoFilePath)
                if (photo != null && photo.status == PhotoStatus.POSTED.name) {
                    photoDao.updatePhoto(photo.copy(status = PhotoStatus.SHOT.name, sharedAt = null))
                }
            }
            historyDao.deleteHistoryGroup(groupId)
        }
    }

    // イベント単位の投稿履歴
    fun getPostedHistoryForEvent(eventId: Long): kotlinx.coroutines.flow.Flow<List<StatusHistoryEntity>> {
        return kotlinx.coroutines.flow.flow {
            val photosInEvent = photoDao.getPhotosByEventId(eventId)
            val paths = photosInEvent.map { it.filePath }
            if (paths.isEmpty()) {
                emit(emptyList())
            } else {
                historyDao.getPostedHistoryByPaths(paths).collect { emit(it) }
            }
        }
    }

    fun clearBodyHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            photoDao.clearAllCameraMakes()
            withContext(Dispatchers.Main) {
                selectedMakeFilter = null
            }
        }
    }

    fun clearCameraMake(make: String) {
        viewModelScope.launch(Dispatchers.IO) {
            photoDao.clearCameraMake(make)
            if (selectedMakeFilter == make) {
                withContext(Dispatchers.Main) {
                    selectedMakeFilter = null
                }
            }
        }
    }

    // --- パス修復（機種変更対応）ロジック ---
    fun autoRepairPaths(onComplete: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val allDbPhotos = photoDao.getAllPhotosOnce()
            val currentLocalPaths = localPhotos.map { it.filePath }.toSet()
            
            val orphanedPhotos = allDbPhotos.filter { it.filePath !in currentLocalPaths }
            if (orphanedPhotos.isEmpty()) {
                withContext(Dispatchers.Main) { onComplete(0) }
                return@launch
            }

            val localByName = localPhotos.groupBy { it.filePath.substringAfterLast("/") }
            val updatedPhotos = mutableListOf<PhotoEntity>()
            val pathMapping = mutableMapOf<String, String>()

            orphanedPhotos.forEach { dbPhoto ->
                val fileName = dbPhoto.filePath.substringAfterLast("/")
                val matches = localByName[fileName]
                if (matches != null && matches.size == 1) {
                    val newPath = matches[0].filePath
                    updatedPhotos.add(dbPhoto.copy(filePath = newPath))
                    pathMapping[dbPhoto.filePath] = newPath
                }
            }

            if (updatedPhotos.isNotEmpty()) {
                photoDao.updatePhotos(updatedPhotos)
                
                // 2. 履歴テーブルのパスも個別更新
                pathMapping.forEach { (oldPath, newPath) ->
                    historyDao.updatePath(oldPath, newPath)
                }

                // 3. 念のため共通プレフィックスの一括置換も試みる（マッピング漏れ対策）
                try {
                    val firstMapping = pathMapping.entries.first()
                    val oldPrefix = firstMapping.key.substringBeforeLast("/")
                    val newPrefix = firstMapping.value.substringBeforeLast("/")
                    
                    if (oldPrefix.isNotEmpty() && oldPrefix != newPrefix) {
                        photoDao.replaceFilePathPrefix(oldPrefix, newPrefix)
                        historyDao.replaceFilePathPrefix(oldPrefix, newPrefix)
                    }
                } catch (e: Exception) {}
            }
            
            withContext(Dispatchers.Main) {
                onComplete(updatedPhotos.size)
            }
        }
    }

    fun assignEventToPhotos(uris: Set<Uri>, eventId: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            val allDbPhotos = photoDao.getAllPhotosOnce().associateBy { it.filePath }
            val toUpdate = mutableListOf<PhotoEntity>()
            val toInsert = mutableListOf<PhotoEntity>()

            uris.forEach { uri ->
                val local = localPhotos.find { it.uri == uri } ?: return@forEach
                val existing = allDbPhotos[local.filePath]
                if (existing != null) {
                    if (existing.eventId != eventId) {
                        toUpdate.add(existing.copy(eventId = eventId))
                    }
                } else {
                    toInsert.add(
                        PhotoEntity(
                            filePath = local.filePath,
                            fileType = local.fileType,
                            status = PhotoStatus.SHOT.name,
                            eventId = eventId
                        )
                    )
                }
            }

            if (toUpdate.isNotEmpty()) photoDao.updatePhotos(toUpdate)
            if (toInsert.isNotEmpty()) photoDao.insertPhotos(toInsert)
        }
    }
}

class PhotoViewModelFactory(
    private val photoDao: PhotoDao,
    private val eventDao: EventDao,
    private val historyDao: StatusHistoryDao,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return PhotoViewModel(photoDao, eventDao, historyDao, handle) as T
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // エッジ・ツー・エッジ（全画面表示）を有効化
        enableEdgeToEdge()

        // 通知バーのアイコンの色を「黒」に反転させる（ライトモード用設定）
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.isAppearanceLightStatusBars = true

        setContent {
            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)
            val factory = PhotoViewModelFactory(
                db.photoDao(),
                db.eventDao(),
                db.statusHistoryDao(),
                this
            )
            val viewModel: PhotoViewModel = viewModel(factory = factory)

            KamekoTheme(themeType = viewModel.currentTheme) {
                PhotoListScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(viewModel: PhotoViewModel) {
    val photos by viewModel.photos.collectAsState()
    val dbEvents by viewModel.events.collectAsState()
    var currentTab by remember { mutableIntStateOf(0) } // 0: Gallery, 1: Timeline, 2: Stats, 3: Setting

    // 機材選択モード中かどうかのラベル
    val cameraFilterLabel = "BODY"

    // DBから写真が読み込まれた瞬間に未確認リストを復元する
    LaunchedEffect(photos) {
        if (photos.isNotEmpty()) {
            viewModel.restorePendingPhotos(photos)
        }
    }

    var eventFilterDropdownExpanded by remember { mutableStateOf(false) }
    var statusMenuExpanded by remember { mutableStateOf(false) }
    var formatMenuExpanded by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var bodyMenuExpanded by remember { mutableStateOf(false) }
    var isoMenuExpanded by remember { mutableStateOf(false) }
    var focalMenuExpanded by remember { mutableStateOf(false) }
    var ssMenuExpanded by remember { mutableStateOf(false) }
    var apertureMenuExpanded by remember { mutableStateOf(false) }
    var isExifFilterExpanded by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf<Int?>(null) }
    var selectedUris by remember { mutableStateOf(setOf<Uri>()) } // 複数選択用
    var showMultiEventAssignDialog by remember { mutableStateOf(false) }

    // 追加：OSによってViewModelが初期化された場合でも、DBから写真が読み込まれた瞬間に未確認リストを復元する
    LaunchedEffect(photos) {
        if (photos.isNotEmpty()) {
            viewModel.restorePendingPhotos(photos)
        }
    }
    var isStatsExpanded by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val dbPhotoMap = remember(photos) { photos.associateBy { it.filePath } }
    val sortedDbEvents = remember(dbEvents) { dbEvents.sortedByDescending { it.startTime } }

    val context = LocalContext.current
    
    // スクロール位置の管理
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    // 複数選択アクションの共通処理
    val onMultiShare: (String?) -> Unit = { packageId ->
        val uris = selectedUris.toList()
        val selectedPhotoEntities = uris.mapNotNull { uri ->
            val local = viewModel.localPhotos.find { it.uri == uri }
            local?.let { dbPhotoMap[it.filePath] ?: PhotoEntity(filePath = it.filePath, fileType = it.fileType) }
        }
        
        scope.launch(Dispatchers.IO) {
            val shareableUris = uris.map { uri ->
                val local = viewModel.localPhotos.find { it.uri == uri }
                prepareShareUri(context, local?.filePath ?: "", uri)
            }
            if (shareableUris.isEmpty()) return@launch

            withContext(Dispatchers.Main) {
                shareToPackage(context, packageId, shareableUris, null) {
                    viewModel.savePendingPhotos(selectedPhotoEntities)
                    viewModel.shareActionStarted = true
                    selectedUris = emptySet()
                }
            }
        }
    }

    val onMultiPost: () -> Unit = {
        val uris = selectedUris.toList()
        val selectedPhotoEntities = uris.mapNotNull { uri ->
            val local = viewModel.localPhotos.find { it.uri == uri }
            local?.let { dbPhotoMap[it.filePath] ?: PhotoEntity(filePath = it.filePath, fileType = it.fileType) }
        }
        viewModel.savePendingPhotos(selectedPhotoEntities)
        viewModel.showPostConfirmDialog = true
        selectedUris = emptySet()
    }

    // 機材フィルタ用：画像選択ランチャー
    val makePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setMakeFilterFromUri(context, it) }
    }

    var showRestoreSuccessDialog by remember { mutableStateOf(false) }

    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                val os = context.contentResolver.openOutputStream(it)
                if (os != null) {
                    val success = DatabaseBackupManager(context).backupDatabase(os)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(context, "バックアップが完了しました", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "バックアップに失敗しました", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                val `is` = context.contentResolver.openInputStream(it)
                if (`is` != null) {
                    val success = DatabaseBackupManager(context).restoreDatabase(`is`)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            showRestoreSuccessDialog = true
                        } else {
                            Toast.makeText(context, "復元に失敗しました", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    if (showRestoreSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("復元完了") },
            text = { Text("データベースの復元が完了しました。設定を反映させるためアプリを再起動します。") },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(intent)
                    Runtime.getRuntime().exit(0)
                }) {
                    Text("再起動")
                }
            },
            dismissButton = null
        )
    }

    // 戻る操作の統合管理（詳細画面・複数選択）
    BackHandler(enabled = selectedPhotoIndex != null || selectedUris.isNotEmpty() || currentTab != 0 || viewModel.isSelectingMakeReference) {
        if (selectedPhotoIndex != null) {
            selectedPhotoIndex = null
        } else if (selectedUris.isNotEmpty()) {
            selectedUris = emptySet()
        } else if (viewModel.isSelectingMakeReference) {
            viewModel.isSelectingMakeReference = false
        } else if (currentTab != 0) {
            currentTab = 0 // Galleryに戻る
        }
    }

    val showScrollToTopButton by remember {
        derivedStateOf { gridState.firstVisibleItemIndex > 5 }
    }

    // 写真アクセス許可の要求
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scope.launch {
                delay(500)
                val photos = loadPhotosWithMimeAsync(context)
                viewModel.localPhotos = photos
                viewModel.registerPhotosWithAutoEvent(photos, context)
            }
        }
    }

    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(permission)
        } else {
            // 既にデータがある場合はストリーミングをスキップ
            if (viewModel.localPhotos.isEmpty()) {
                viewModel.isRefreshing = true

                loadPhotosWithMimeStreaming(context) { batch ->
                    val currentUris = viewModel.localPhotos.map { it.uri.toString() }.toSet()
                    val uniqueBatch = batch.filter { it.uri.toString() !in currentUris }
                    if (uniqueBatch.isNotEmpty()) {
                        viewModel.localPhotos = viewModel.localPhotos + uniqueBatch
                    }
                    
                    viewModel.registerPhotosWithAutoEvent(batch, context)
                }
                viewModel.isRefreshing = false
            }
        }
    }

    // 写真リストを最新に更新する共通処理
    val refreshPhotos: (Boolean) -> Unit = { isForce ->
        scope.launch {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                viewModel.isRefreshing = true
                val latestPhotos = loadPhotosWithMimeAsync(context)
                
                // 変更検知を強化：件数変化、またはURI変化
                val isChanged = latestPhotos.size != viewModel.localPhotos.size ||
                        (latestPhotos.isNotEmpty() && viewModel.localPhotos.isNotEmpty() && 
                         (latestPhotos.first().uri != viewModel.localPhotos.first().uri || 
                          latestPhotos.last().uri != viewModel.localPhotos.last().uri))

                if (isChanged || isForce) {
                    // 1. UIをまず更新
                    viewModel.localPhotos = latestPhotos

                    // 2. 新規写真をDBに登録
                    viewModel.registerPhotosWithAutoEvent(latestPhotos, context)
                    
                    delay(300)
                    viewModel.isRefreshing = false
                } else {
                    delay(300)
                    viewModel.isRefreshing = false
                }
            }
        }
    }

    // リアルタイム監視 (ContentObserver)
    DisposableEffect(Unit) {
        val observer = object : android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                refreshPhotos(false)
            }
        }
        context.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
        onDispose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    // アプリに戻ってきたときの検知
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                // 共有ボタンを押して外部アプリに遷移した後、戻ってきた瞬間に確認ダイアログを表示
                if (viewModel.shareActionStarted) {
                    viewModel.showPostConfirmDialog = true
                    viewModel.shareActionStarted = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val filteredPhotoItems = remember(
        viewModel.localPhotos, viewModel.searchQuery, viewModel.selectedStatusFilter, 
        viewModel.selectedTypeFilter, viewModel.selectedEventFilterId, viewModel.selectedMakeFilter,
        viewModel.selectedIsoFilter, viewModel.selectedFocalFilter, viewModel.selectedSsFilter, viewModel.selectedApertureFilter,
        viewModel.selectedSortType,
        dbPhotoMap, dbEvents
    ) {
        val selectedEvent = dbEvents.find { it.id == viewModel.selectedEventFilterId }

        val filtered = viewModel.localPhotos.filter { item ->
            val dbPhoto = dbPhotoMap[item.filePath]

            // 現場フィルタの判定
            val matchesEvent = when {
                viewModel.selectedEventFilterId == null -> true
                // 1. DB上で明示的にこの現場に紐付いている場合のみOK
                dbPhoto?.eventId == viewModel.selectedEventFilterId -> true
                else -> false
            }

            val matchesSearch = viewModel.searchQuery.isBlank() ||
                    item.filePath.contains(viewModel.searchQuery, ignoreCase = true) ||
                    (dbPhoto?.memo?.contains(viewModel.searchQuery, ignoreCase = true) == true)
            val matchesStatus = viewModel.selectedStatusFilter == "ALL" ||
                    (dbPhoto?.status ?: "SHOT") == viewModel.selectedStatusFilter
            val matchesType = viewModel.selectedTypeFilter == "ALL" ||
                    (dbPhoto?.fileType ?: item.fileType) == viewModel.selectedTypeFilter
            
            // 機材フィルタの判定
            val matchesMake = viewModel.selectedMakeFilter == null || 
                              (dbPhoto?.cameraMake ?: item.cameraMake)?.contains(viewModel.selectedMakeFilter!!, ignoreCase = true) == true

            // 撮影設定フィルタの判定
            val iso = dbPhoto?.iso ?: -1
            val matchesIso = when(viewModel.selectedIsoFilter) {
                "LOW" -> iso in 1..400
                "MID" -> iso in 401..3200
                "HIGH" -> iso > 3200
                else -> true
            }

            val focal = dbPhoto?.focalLength ?: -1.0
            val matchesFocal = when(viewModel.selectedFocalFilter) {
                "U-WIDE" -> focal in 1.0..23.9
                "WIDE" -> focal in 24.0..35.0
                "STD" -> focal in 35.1..70.0
                "TELE" -> focal in 70.1..200.0
                "S-TELE" -> focal > 200.0
                else -> true
            }

            val ss = dbPhoto?.shutterSpeed ?: -1.0
            val matchesSs = when(viewModel.selectedSsFilter) {
                "FAST" -> ss in 0.0..0.002 // <= 1/500
                "MID" -> ss in 0.0021..0.0333 // 1/500 ~ 1/30
                "SLOW" -> ss > 0.0333 // > 1/30
                else -> true
            }

            val f = dbPhoto?.aperture ?: -1.0
            val matchesAperture = when(viewModel.selectedApertureFilter) {
                "OPEN" -> f in 1.0..2.8
                "MID" -> f in 2.81..8.0
                "CLOSED" -> f > 8.0
                else -> true
            }

            matchesSearch && matchesStatus && matchesType && matchesEvent && matchesMake && 
            matchesIso && matchesFocal && matchesSs && matchesAperture
        }

        val sorted = when (viewModel.selectedSortType) {
            PhotoSortType.DATE_TAKEN -> filtered.sortedByDescending { it.dateTaken }
            PhotoSortType.DATE_MODIFIED -> filtered.sortedByDescending { it.dateModified }
            else -> filtered.sortedByDescending { it.dateAdded }
        }

        sorted.distinctBy { it.uri.toString() }
    }

    // 共有後の投稿確認ダイアログ
    if (viewModel.showPostConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.clearPendingPhotos() },
            title = { Text("投稿完了の確認", fontWeight = FontWeight.Bold) },
            text = { Text("${viewModel.pendingPostPhotos.size}枚の写真を「POSTED」に更新しますか？") },
            confirmButton = {
                Button(onClick = { viewModel.completePendingPost() }) {
                    Text("はい")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearPendingPhotos() }) {
                    Text("いいえ")
                }
            }
        )
    }

    // 統計ダイアログは削除 (インライン表示に移行)


    var showEventDialog by remember { mutableStateOf(false) }
    var isBulkInsertMode by remember { mutableStateOf(false) }
    var bulkInputText by remember { mutableStateOf("") }
    var newEventName by remember { mutableStateOf("") }
    var newEventVenue by remember { mutableStateOf("") }
    var newEventDate by remember { mutableStateOf("2001-07-09") }

    var editingEvent by remember { mutableStateOf<EventEntity?>(null) }

    Scaffold(
        bottomBar = {
            if (selectedUris.isNotEmpty() && currentTab == 0) {
                // 複数選択用アクションバー (案1: ボトムバー)
                BottomAppBar(
                    modifier = Modifier.height(88.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    val canMinorActions = selectedUris.size <= 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SelectionActionButton(
                            icon = Icons.Default.Share,
                            label = "共有",
                            enabled = canMinorActions,
                            onClick = { onMultiShare(null) }
                        )
                        SelectionActionButton(
                            text = "X",
                            label = "X Post",
                            enabled = canMinorActions,
                            onClick = { onMultiShare("com.twitter.android") }
                        )
                        SelectionActionButton(
                            text = "Lr",
                            label = "Lr",
                            textColor = Color(0xFF001E36),
                            enabled = canMinorActions,
                            onClick = { onMultiShare("com.adobe.lrmobile") }
                        )
                        SelectionActionButton(
                            icon = Icons.Default.Place,
                            label = "現場設定",
                            onClick = { showMultiEventAssignDialog = true }
                        )
                        SelectionActionButton(
                            icon = Icons.Default.CheckCircle,
                            label = "POST済",
                            iconColor = PostGreen,
                            enabled = canMinorActions,
                            onClick = { onMultiPost() }
                        )
                    }
                }
            } else {
                NavigationBar(
                    modifier = Modifier.height(88.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("Gallery", fontSize = 11.sp, letterSpacing = 0.5.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Default.DateRange, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("Timeline", fontSize = 11.sp, letterSpacing = 0.5.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = { Icon(Icons.Default.Info, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("Stats", fontSize = 11.sp, letterSpacing = 0.5.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        icon = { Icon(Icons.Default.Settings, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("Setting", fontSize = 11.sp, letterSpacing = 0.5.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentTab) {
                1 -> ModernTimelineScreen(
                    events = dbEvents,
                    viewModel = viewModel,
                    onShowEventDialog = { showEventDialog = it },
                    onEditEvent = { event ->
                        editingEvent = event
                        newEventName = event.name
                        newEventVenue = event.venue
                        newEventDate = event.eventDate
                        showEventDialog = true
                    }
                )
                2 -> ModernDashboardScreen(viewModel)
                3 -> ModernSettingScreen(
                    viewModel = viewModel,
                    onManageEvents = { showEventDialog = true },
                    onSelectMakeReference = { 
                        viewModel.isSelectingMakeReference = true
                        currentTab = 0 // Galleryタブへ移動
                    },
                    onBackup = { backupLauncher.launch("KamekoPad_Backup_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.zip") },
                    onRestore = { restoreLauncher.launch(arrayOf("application/zip")) }
                )
                else -> {
                    // --- 従来のギャラリー表示 ---
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 機材選択モード中のバナー
                        if (viewModel.isSelectingMakeReference) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "フィルタに使用する写真を選択してください",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(onClick = { viewModel.isSelectingMakeReference = false }) {
                                        Text("キャンセル")
                                    }
                                }
                            }
                        }

                        // 複数選択中の上部バー (カウントと解除のみ)
                        if (selectedUris.isNotEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                tonalElevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { selectedUris = emptySet() }) {
                                        Icon(Icons.Default.Close, contentDescription = "解除")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${selectedUris.size} 枚選択中 (最大10枚)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        } else {
                            // ① 上部バー (カウンターと検索・更新ボタン)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 24時間投稿数カウンター (Activity Chip スタイル)
                                val postStats by viewModel.postStatsLast24h.collectAsState()
                                val isActive = postStats.photoCount > 0

                                Surface(
                                    color = if (isActive) PostGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .animateContentSize()
                                        .clickable { isStatsExpanded = !isStatsExpanded }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Info,
                                            contentDescription = null,
                                            tint = if (isActive) PostGreen else Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (isStatsExpanded) {
                                                "24H: ${postStats.photoCount} PHOTOS / ${postStats.postCount} POSTS"
                                            } else {
                                                "${postStats.photoCount} / ${postStats.postCount}"
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.2.sp,
                                            color = if (isActive) PostGreen else Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // 検索ボタン (トグル)
                                IconButton(
                                    onClick = { isSearchExpanded = !isSearchExpanded },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                                        contentDescription = "検索",
                                        tint = if (isSearchExpanded) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // 手動更新ボタン
                                IconButton(
                                    onClick = { refreshPhotos(true) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "再読み込み",
                                        tint = if (viewModel.isRefreshing) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // --- モダン検索＆フィルタパネル ---
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // 1. 折りたたみ式検索バー
                            androidx.compose.animation.AnimatedVisibility(visible = isSearchExpanded) {
                                TextField(
                                    value = viewModel.searchQuery,
                                    onValueChange = { viewModel.searchQuery = it },
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                    placeholder = { Text("Search memories...", fontSize = 14.sp, color = Color.Gray) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    )
                                )
                            }

                            // 2. フィルター（2行構成）
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // --- ステータス集約ボタン ---
                                Box {
                                    val statusLabel = when (viewModel.selectedStatusFilter) {
                                        "SHOT" -> "SHOT"
                                        "FAVORITE" -> "FAV"
                                        "POSTED" -> "POST"
                                        else -> "STATUS"
                                    }
                                    FilterChip(
                                        selected = viewModel.selectedStatusFilter != "ALL",
                                        onClick = { statusMenuExpanded = true },
                                        label = { Text(text = statusLabel.uppercase(), fontSize = 10.sp, letterSpacing = 1.sp, maxLines = 1) },
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    )
                                    DropdownMenu(
                                        expanded = statusMenuExpanded,
                                        onDismissRequest = { statusMenuExpanded = false },
                                        shape = RoundedCornerShape(12.dp),
                                        containerColor = Color.White
                                    ) {
                                        listOf("ALL", "SHOT", "FAVORITE", "POSTED").forEach { status ->
                                            val itemText = when(status) {
                                                "SHOT" -> "SHOT"
                                                "FAVORITE" -> "FAV"
                                                "POSTED" -> "POST"
                                                else -> "ALL STATUS"
                                            }
                                            DropdownMenuItem(
                                                text = { Text(text = itemText.uppercase(), fontSize = 12.sp) },
                                                onClick = {
                                                    viewModel.selectedStatusFilter = status
                                                    statusMenuExpanded = false
                                                },
                                                trailingIcon = {
                                                    if (viewModel.selectedStatusFilter == status) {
                                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                // --- 形式（フォーマット）集約ボタン ---
                                Box {
                                    val formatLabel = if (viewModel.selectedTypeFilter == "ALL") "FORMAT" else viewModel.selectedTypeFilter
                                    FilterChip(
                                        selected = viewModel.selectedTypeFilter != "ALL",
                                        onClick = { formatMenuExpanded = true },
                                        label = { Text(text = formatLabel.uppercase(), fontSize = 10.sp, letterSpacing = 1.sp, maxLines = 1) },
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    )
                                    DropdownMenu(
                                        expanded = formatMenuExpanded,
                                        onDismissRequest = { formatMenuExpanded = false },
                                        shape = RoundedCornerShape(12.dp),
                                        containerColor = Color.White
                                    ) {
                                        listOf("ALL", "RAW", "JPEG").forEach { type ->
                                            val itemText = if(type == "ALL") "ALL FORMAT" else type
                                            DropdownMenuItem(
                                                text = { Text(text = itemText.uppercase(), fontSize = 12.sp) },
                                                onClick = {
                                                    viewModel.selectedTypeFilter = type
                                                    formatMenuExpanded = false
                                                },
                                                trailingIcon = {
                                                    if (viewModel.selectedTypeFilter == type) {
                                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                // --- 並び替え集約ボタン ---
                                Box {
                                    val sortLabel = when (viewModel.selectedSortType) {
                                        PhotoSortType.DATE_ADDED -> "追加順"
                                        PhotoSortType.DATE_TAKEN -> "撮影順"
                                        PhotoSortType.DATE_MODIFIED -> "更新順"
                                    }
                                    FilterChip(
                                        selected = viewModel.selectedSortType != PhotoSortType.DATE_ADDED,
                                        onClick = { sortMenuExpanded = true },
                                        label = { Text(text = sortLabel.uppercase(), fontSize = 10.sp, letterSpacing = 1.sp, maxLines = 1) },
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    )
                                    DropdownMenu(
                                        expanded = sortMenuExpanded,
                                        onDismissRequest = { sortMenuExpanded = false },
                                        shape = RoundedCornerShape(12.dp),
                                        containerColor = Color.White
                                    ) {
                                        listOf(
                                            PhotoSortType.DATE_ADDED to "追加日（新しい順）",
                                            PhotoSortType.DATE_TAKEN to "撮影日（新しい順）",
                                            PhotoSortType.DATE_MODIFIED to "編集日（新しい順）"
                                        ).forEach { (type, label) ->
                                            DropdownMenuItem(
                                                text = { Text(text = label.uppercase(), fontSize = 12.sp) },
                                                onClick = {
                                                    viewModel.selectedSortType = type
                                                    sortMenuExpanded = false
                                                },
                                                trailingIcon = {
                                                    if (viewModel.selectedSortType == type) {
                                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                // --- BODY（機材）集約ボタン ---
                                Box {
                                    val bodyLabel = if (viewModel.selectedMakeFilter == null) "BODY" else viewModel.selectedMakeFilter!!
                                    FilterChip(
                                        selected = viewModel.selectedMakeFilter != null,
                                        onClick = { bodyMenuExpanded = true },
                                        label = { Text(text = bodyLabel.uppercase(), fontSize = 10.sp, letterSpacing = 1.sp, maxLines = 1) },
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp).widthIn(max = 120.dp)
                                    )
                                    DropdownMenu(
                                        expanded = bodyMenuExpanded,
                                        onDismissRequest = { bodyMenuExpanded = false },
                                        shape = RoundedCornerShape(12.dp),
                                        containerColor = Color.White
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(text = "ALL BODIES", fontSize = 12.sp) },
                                            onClick = {
                                                viewModel.selectedMakeFilter = null
                                                bodyMenuExpanded = false
                                            },
                                            trailingIcon = {
                                                if (viewModel.selectedMakeFilter == null) {
                                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        )
                                        
                                        // 選択中の機材のみを表示
                                        viewModel.selectedMakeFilter?.let { body ->
                                            DropdownMenuItem(
                                                text = { Text(text = body.uppercase(), fontSize = 12.sp) },
                                                onClick = {
                                                    bodyMenuExpanded = false
                                                },
                                                trailingIcon = {
                                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                                }
                                            )
                                        }

                                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                        DropdownMenuItem(
                                            text = { Text(text = "📷 写真から選択...", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary) },
                                            onClick = {
                                                viewModel.isSelectingMakeReference = true
                                                bodyMenuExpanded = false
                                            }
                                        )
                                    }
                                }

                                // --- EXIF詳細フィルタの展開ボタン ---
                                val isExifActive = viewModel.selectedIsoFilter != null || viewModel.selectedFocalFilter != null || 
                                                 viewModel.selectedSsFilter != null || viewModel.selectedApertureFilter != null
                                FilterChip(
                                    selected = isExifFilterExpanded || isExifActive,
                                    onClick = { isExifFilterExpanded = !isExifFilterExpanded },
                                    label = { Text(text = "EXIF", fontSize = 10.sp, letterSpacing = 1.sp, maxLines = 1) },
                                    leadingIcon = { Icon(if (isExifFilterExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.Settings, null, modifier = Modifier.size(14.dp)) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                )
                            }

                            // 展開される撮影設定フィルタ行（ISO, Focal, SS, Aperture）
                            androidx.compose.animation.AnimatedVisibility(visible = isExifFilterExpanded) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // --- ISO ---
                                    Box {
                                        val isoLabel = when(viewModel.selectedIsoFilter) {
                                            "LOW" -> "ISO: LOW"
                                            "MID" -> "ISO: MID"
                                            "HIGH" -> "ISO: HIGH"
                                            else -> "ISO"
                                        }
                                        FilterChip(
                                            selected = viewModel.selectedIsoFilter != null,
                                            onClick = { isoMenuExpanded = true },
                                            label = { Text(isoLabel, fontSize = 10.sp, letterSpacing = 1.sp) },
                                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        )
                                        DropdownMenu(expanded = isoMenuExpanded, onDismissRequest = { isoMenuExpanded = false }) {
                                            DropdownMenuItem(text = { Text("ALL ISO") }, onClick = { viewModel.selectedIsoFilter = null; isoMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("LOW (<=400)") }, onClick = { viewModel.selectedIsoFilter = "LOW"; isoMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("MID (401-3200)") }, onClick = { viewModel.selectedIsoFilter = "MID"; isoMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("HIGH (>3200)") }, onClick = { viewModel.selectedIsoFilter = "HIGH"; isoMenuExpanded = false })
                                        }
                                    }

                                    // --- FOCAL ---
                                    Box {
                                        val focalLabel = when(viewModel.selectedFocalFilter) {
                                            "U-WIDE" -> "U-WIDE"
                                            "WIDE" -> "WIDE"
                                            "STD" -> "STD"
                                            "TELE" -> "TELE"
                                            "S-TELE" -> "S-TELE"
                                            else -> "FOCAL"
                                        }
                                        FilterChip(
                                            selected = viewModel.selectedFocalFilter != null,
                                            onClick = { focalMenuExpanded = true },
                                            label = { Text(focalLabel, fontSize = 10.sp, letterSpacing = 1.sp) },
                                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        )
                                        DropdownMenu(expanded = focalMenuExpanded, onDismissRequest = { focalMenuExpanded = false }) {
                                            DropdownMenuItem(text = { Text("ALL FOCAL") }, onClick = { viewModel.selectedFocalFilter = null; focalMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("超広角 (<24mm)") }, onClick = { viewModel.selectedFocalFilter = "U-WIDE"; focalMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("広角 (24-35mm)") }, onClick = { viewModel.selectedFocalFilter = "WIDE"; focalMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("標準 (35-70mm)") }, onClick = { viewModel.selectedFocalFilter = "STD"; focalMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("望遠 (70-200mm)") }, onClick = { viewModel.selectedFocalFilter = "TELE"; focalMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("超望遠 (>200mm)") }, onClick = { viewModel.selectedFocalFilter = "S-TELE"; focalMenuExpanded = false })
                                        }
                                    }

                                    // --- SS ---
                                    Box {
                                        val ssLabel = when(viewModel.selectedSsFilter) {
                                            "FAST" -> "SS: FAST"
                                            "MID" -> "SS: MID"
                                            "SLOW" -> "SS: SLOW"
                                            else -> "SS"
                                        }
                                        FilterChip(
                                            selected = viewModel.selectedSsFilter != null,
                                            onClick = { ssMenuExpanded = true },
                                            label = { Text(ssLabel, fontSize = 10.sp, letterSpacing = 1.sp) },
                                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        )
                                        DropdownMenu(expanded = ssMenuExpanded, onDismissRequest = { ssMenuExpanded = false }) {
                                            DropdownMenuItem(text = { Text("ALL SS") }, onClick = { viewModel.selectedSsFilter = null; ssMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("高速 (<=1/500)") }, onClick = { viewModel.selectedSsFilter = "FAST"; ssMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("中速 (1/500-1/30)") }, onClick = { viewModel.selectedSsFilter = "MID"; ssMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("低速 (>1/30)") }, onClick = { viewModel.selectedSsFilter = "SLOW"; ssMenuExpanded = false })
                                        }
                                    }

                                    // --- APERTURE ---
                                    Box {
                                        val fLabel = when(viewModel.selectedApertureFilter) {
                                            "OPEN" -> "F: OPEN"
                                            "MID" -> "F: MID"
                                            "CLOSED" -> "F: CLOSED"
                                            else -> "F"
                                        }
                                        FilterChip(
                                            selected = viewModel.selectedApertureFilter != null,
                                            onClick = { apertureMenuExpanded = true },
                                            label = { Text(fLabel, fontSize = 10.sp, letterSpacing = 1.sp) },
                                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        )
                                        DropdownMenu(expanded = apertureMenuExpanded, onDismissRequest = { apertureMenuExpanded = false }) {
                                            DropdownMenuItem(text = { Text("ALL F-STOP") }, onClick = { viewModel.selectedApertureFilter = null; apertureMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("開放 (<=F2.8)") }, onClick = { viewModel.selectedApertureFilter = "OPEN"; apertureMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("中間 (F2.8-F8.0)") }, onClick = { viewModel.selectedApertureFilter = "MID"; apertureMenuExpanded = false })
                                            DropdownMenuItem(text = { Text("絞り込み (>F8.0)") }, onClick = { viewModel.selectedApertureFilter = "CLOSED"; apertureMenuExpanded = false })
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // イベント選択
                                Box(modifier = Modifier.weight(1f)) {
                                    val selectedEvent = dbEvents.find { it.id == viewModel.selectedEventFilterId }
                                    val eventLabel = selectedEvent?.let { "${it.eventDate} ${it.name} @ ${it.venue}" } ?: "EVENTS"
                                    FilterChip(
                                        selected = viewModel.selectedEventFilterId != null,
                                        onClick = { eventFilterDropdownExpanded = true },
                                        label = {
                                            Text(
                                                text = eventLabel.uppercase(),
                                                fontSize = 10.sp,
                                                letterSpacing = 1.sp,
                                                maxLines = 1
                                            )
                                        },
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(32.dp)
                                    )
                                    DropdownMenu(
                                        expanded = eventFilterDropdownExpanded,
                                        onDismissRequest = { eventFilterDropdownExpanded = false },
                                        shape = RoundedCornerShape(12.dp),
                                        containerColor = Color.White,
                                        modifier = Modifier.widthIn(max = 300.dp)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("ALL EVENTS", fontSize = 12.sp, color = Color.Gray) },
                                            onClick = {
                                                viewModel.selectedEventFilterId = null
                                                eventFilterDropdownExpanded = false
                                            }
                                        )
                                        sortedDbEvents.forEach { event ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(event.eventDate, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                                        Text(event.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                        Text(event.venue, fontSize = 10.sp, color = Color.Gray)
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.selectedEventFilterId = event.id
                                                    eventFilterDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // 現場追加ボタン
                                AssistChip(
                                    onClick = { showEventDialog = true },
                                    label = { Text("ADD EVENT", fontSize = 10.sp) },
                                    leadingIcon = { Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp)) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                )
                            }
                        }

                        // 更新中インジケータ（スキャン中のみ表示）
                        if (viewModel.isRefreshing) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(2.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.Transparent
                            )
                        }

                        // グリッド表示エリア
                        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                            if (filteredPhotoItems.isEmpty()) {
                                if (!viewModel.isRefreshing) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("No photos found", color = Color.Gray)
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    state = gridState,
                                    columns = GridCells.Fixed(3), // 3列
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    items(filteredPhotoItems, key = { it.uri.toString() }) { item ->
                                        val pathString = item.filePath
                                        val dbPhoto = dbPhotoMap[pathString]
                                        val isSelected = selectedUris.contains(item.uri)

                                        ModernPhotoItem(
                                            item = item,
                                            dbPhoto = dbPhoto,
                                            isSelected = isSelected,
                                            isSelectionMode = selectedUris.isNotEmpty(),
                                            onClick = {
                                                if (viewModel.isSelectingMakeReference) {
                                                    // 機材選択モード時
                                                    viewModel.setMakeFilterFromUri(context, item.uri)
                                                    viewModel.isSelectingMakeReference = false
                                                } else if (selectedUris.isNotEmpty()) {
                                                    if (isSelected) {
                                                        selectedUris = selectedUris - item.uri
                                                    } else if (selectedUris.size < 10) {
                                                        selectedUris = selectedUris + item.uri
                                                    }
                                                } else {
                                                    selectedPhotoIndex = filteredPhotoItems.indexOf(item)
                                                }
                                            },
                                            onLongClick = {
                                                if (selectedUris.isEmpty() && !viewModel.isSelectingMakeReference) {
                                                    selectedUris = setOf(item.uri)
                                                }
                                            }
                                        )
                                    }
                                }

                                // スクロールバー追加
                                VerticalGridScrollbar(
                                    gridState = gridState,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 2.dp, top = 4.dp, bottom = 4.dp)
                                )
                            }

                            // FABエリア：最上位へ戻るボタン
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(bottom = 32.dp, end = 24.dp),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (showScrollToTopButton) {
                                    SmallFloatingActionButton(
                                        onClick = {
                                            scope.launch {
                                                gridState.animateScrollToItem(0)
                                            }
                                        },
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        shape = CircleShape
                                    ) {
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "最上位へスクロール")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showMultiEventAssignDialog) {
        AlertDialog(
            onDismissRequest = { showMultiEventAssignDialog = false },
            title = { Text("選択した写真に現場を割り当て", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${selectedUris.size} 枚の写真に割り当てる現場を選択してください", fontSize = 14.sp)
                    
                    Box(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                        LazyColumn {
                            item {
                                DropdownMenuItem(
                                    text = { Text("❌ 現場の紐付けを解除", color = Color.Red) },
                                    onClick = {
                                        viewModel.assignEventToPhotos(selectedUris, null)
                                        showMultiEventAssignDialog = false
                                        selectedUris = emptySet()
                                    }
                                )
                                HorizontalDivider()
                            }
                            items(sortedDbEvents) { event ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(event.eventDate, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                            Text(event.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(event.venue, fontSize = 12.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        viewModel.assignEventToPhotos(selectedUris, event.id)
                                        showMultiEventAssignDialog = false
                                        selectedUris = emptySet()
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showMultiEventAssignDialog = false }) { Text("キャンセル") }
            }
        )
    }

    // --- 現場登録ダイアログ ---
    if (showEventDialog) {
        AlertDialog(
            onDismissRequest = { 
                showEventDialog = false
                editingEvent = null
            },
            title = { Text(if (isBulkInsertMode) "現場一括登録" else if (editingEvent != null) "現場編集" else "現場登録", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 新規登録時のみ一括モードを許可
                    if (editingEvent == null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("一括モード", fontSize = 14.sp)
                            Spacer(Modifier.weight(1f))
                            Switch(checked = isBulkInsertMode, onCheckedChange = { isBulkInsertMode = it })
                        }
                    } else {
                        // 編集時は強制的に一括モードをOFF
                        LaunchedEffect(Unit) { isBulkInsertMode = false }
                    }
                    
                    if (isBulkInsertMode && editingEvent == null) {
                        Text(
                            text = "【記載例】\nイベント名\n会場名\n2024-04-18\n\n※空行区切りで複数登録できます",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                        OutlinedTextField(
                            value = bulkInputText,
                            onValueChange = { bulkInputText = it },
                            placeholder = { Text("イベント情報を入力してください") },
                            modifier = Modifier.fillMaxWidth().height(150.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = newEventName,
                            onValueChange = { newEventName = it },
                            label = { Text("イベント名") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newEventVenue,
                            onValueChange = { newEventVenue = it },
                            label = { Text("会場名") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newEventDate,
                            onValueChange = { newEventDate = it },
                            label = { Text("日付 (yyyy-MM-dd)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (isBulkInsertMode) {
                        viewModel.insertBulkEventsFromText(bulkInputText)
                    } else {
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        val ts = try { sdf.parse(newEventDate)?.time ?: 0L } catch (e: Exception) { 0L }
                        viewModel.addEvent(
                            EventEntity(
                                id = editingEvent?.id ?: 0,
                                name = newEventName, 
                                venue = newEventVenue, 
                                eventDate = newEventDate, 
                                startTime = ts
                            ),
                            viewModel.localPhotos
                        )
                    }
                    showEventDialog = false
                    editingEvent = null
                    newEventName = ""; newEventVenue = ""; bulkInputText = ""
                }) {
                    Text(if (editingEvent != null) "更新" else "登録")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showEventDialog = false
                    editingEvent = null
                }) { Text("キャンセル") }
            }
        )
    }

    // 詳細画面の表示
    androidx.compose.animation.AnimatedVisibility(
        visible = selectedPhotoIndex != null,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { it / 2 }),
        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { it / 2 })
    ) {
        val index = selectedPhotoIndex ?: return@AnimatedVisibility

        PhotoDetailView(
            allPhotos = filteredPhotoItems,
            initialIndex = index,
            dbPhotoMap = dbPhotoMap,
            events = dbEvents,
            onDismiss = { selectedPhotoIndex = null },
            onSaveStatus = { photoEntity, status, eventId, memo ->
                viewModel.updatePhotoDetails(photoEntity.copy(status = status, eventId = eventId, memo = memo))
                // 保存しても画面は閉じず、スライドを続けられるようにする
            },
            onShare = { photoEntity, localPhoto, packageId ->
                scope.launch(Dispatchers.IO) {
                    try {
                        val shareUri = prepareShareUri(context, photoEntity.filePath, localPhoto.uri)

                        withContext(Dispatchers.Main) {
                            shareToPackage(context, packageId, listOf(shareUri), photoEntity.memo) {
                                viewModel.savePendingPhotos(listOf(photoEntity))
                                viewModel.shareActionStarted = true
                                // 共有開始後も画面を閉じず、戻ってきたときにそのまま確認できるようにする
                            }
                        }
                    } catch (e: Exception) { Log.e("KamekoPad", "Share failed", e) }
                }
            }
        )
    }
}

@Composable
fun ModernDashboardScreen(viewModel: PhotoViewModel) {
    val postStats by viewModel.postStatsLast24h.collectAsState()
    val allTimeStats by viewModel.allTimePostStats.collectAsState()
    val allHistory by viewModel.allPostedHistory.collectAsState()
    val sdfFull = remember { java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()) }

    var showAllTime by remember { mutableStateOf(false) }
    var historyToDeleteByGroupId by remember { mutableStateOf<Long?>(null) }

    if (historyToDeleteByGroupId != null) {
        AlertDialog(
            onDismissRequest = { historyToDeleteByGroupId = null },
            title = { Text("投稿履歴の削除") },
            text = { Text("この投稿履歴を削除しますか？\n関連する写真のステータスは「SHOT」に戻ります。") },
            confirmButton = {
                Button(
                    onClick = {
                        historyToDeleteByGroupId?.let { viewModel.deleteHistoryGroup(it) }
                        historyToDeleteByGroupId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("削除する")
                }
            },
            dismissButton = {
                TextButton(onClick = { historyToDeleteByGroupId = null }) {
                    Text("キャンセル")
                }
            }
        )
    }

    val filteredHistory = remember(allHistory, showAllTime) {
        if (showAllTime) {
            allHistory
        } else {
            val since = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
            allHistory.filter { it.changedAt >= since }
        }
    }

    val groupedHistory = remember(filteredHistory) {
        filteredHistory.groupBy { it.groupId }.values.toList()
            .sortedByDescending { it.firstOrNull()?.changedAt ?: 0L }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // タイトル
        Text(
            text = "Stats",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )

        // 24h / 全期間 切り替え
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FilterChip(
                selected = !showAllTime,
                onClick = { showAllTime = false },
                label = { Text("Last 24h") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = showAllTime,
                onClick = { showAllTime = true },
                label = { Text("All Time") }
            )
        }

        // 統計セクション（大きな数字）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showAllTime) {
                ModernStatItem(label = "TOTAL PHOTOS", value = allTimeStats.photoCount.toString())
                ModernStatItem(label = "TOTAL POSTS", value = allTimeStats.postCount.toString())
            } else {
                ModernStatItem(label = "PHOTOS", value = postStats.photoCount.toString())
                ModernStatItem(label = "POSTS", value = postStats.postCount.toString())
            }
            ModernStatItem(label = "EVENTS", value = viewModel.events.collectAsState().value.size.toString())
        }

        // 投稿履歴
        Text(
            text = "HISTORY",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = Color.Gray
        )

        if (groupedHistory.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showAllTime) "投稿履歴がありません" else "直近24時間の投稿はありません",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(groupedHistory) { group ->
                    GroupedHistoryItem(
                        historyGroup = group,
                        sdfFull = sdfFull,
                        onDelete = { historyToDeleteByGroupId = group.firstOrNull()?.groupId }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ModernStatItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraLight,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = (-2).sp
        )
    }
}

@Composable
fun ModernTimelineScreen(
    events: List<EventEntity>,
    viewModel: PhotoViewModel,
    onShowEventDialog: (Boolean) -> Unit,
    onEditEvent: (EventEntity) -> Unit
) {
    val sdfDay = remember { java.text.SimpleDateFormat("dd", java.util.Locale.getDefault()) }
    val sdfMonth = remember { java.text.SimpleDateFormat("yyyy.MM", java.util.Locale.getDefault()) }
    var selectedEventId by remember { mutableStateOf<Long?>(null) }
    var eventToDelete by remember { mutableStateOf<EventEntity?>(null) }
    var historyToDeleteByGroupId by remember { mutableStateOf<Long?>(null) }

    BackHandler(enabled = selectedEventId != null) {
        selectedEventId = null
    }

    if (historyToDeleteByGroupId != null) {
        AlertDialog(
            onDismissRequest = { historyToDeleteByGroupId = null },
            title = { Text("投稿履歴の削除") },
            text = { Text("この投稿履歴を削除しますか？\n関連する写真のステータスは「SHOT」に戻ります。") },
            confirmButton = {
                Button(
                    onClick = {
                        historyToDeleteByGroupId?.let { viewModel.deleteHistoryGroup(it) }
                        historyToDeleteByGroupId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("削除する")
                }
            },
            dismissButton = {
                TextButton(onClick = { historyToDeleteByGroupId = null }) {
                    Text("キャンセル")
                }
            }
        )
    }

    if (eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("イベントの削除") },
            text = { Text("「${eventToDelete?.name}」を削除してもよろしいですか？\nこのイベントに関連付けられた写真のデータは削除されませんが、イベントとの紐付けは解除されます。") },
            confirmButton = {
                Button(
                    onClick = {
                        eventToDelete?.let { viewModel.deleteEvent(it) }
                        eventToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("削除する")
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("キャンセル")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (selectedEventId != null) {
                IconButton(onClick = { selectedEventId = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
            Text(
                text = if (selectedEventId == null) "Timeline" else "Event History",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // ヘッダー右側にひっそりと配置
            IconButton(onClick = { onShowEventDialog(true) }) {
                Icon(Icons.Default.Add, contentDescription = "現場追加", tint = Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedEventId != null) {
            val event = remember(selectedEventId) { events.find { it.id == selectedEventId } }
            val history by viewModel.getPostedHistoryForEvent(selectedEventId!!).collectAsState(initial = emptyList())
            val sdfFull = remember { java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()) }

            Column(modifier = Modifier.fillMaxSize()) {
                event?.let {
                    Text(
                        text = "${it.name} @ ${it.venue}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (history.isEmpty()) {
                    Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Text("このイベントの投稿履歴はありません", color = Color.Gray)
                    }
                } else {
                    val groupedHistory = remember(history) {
                        history.groupBy { it.groupId }.values.toList()
                            .sortedByDescending { it.firstOrNull()?.changedAt ?: 0L }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(groupedHistory) { group ->
                            GroupedHistoryItem(
                                historyGroup = group,
                                sdfFull = sdfFull,
                                onDelete = { historyToDeleteByGroupId = group.firstOrNull()?.groupId }
                            )
                        }
                    }
                }
            }
        } else if (events.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No events recorded", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // 日付順にソート（新しい順）
                val sortedEvents = events.sortedByDescending { it.startTime }
                items(sortedEvents) { event ->
                    val date = java.util.Date(event.startTime)
                    val history by viewModel.getPostedHistoryForEvent(event.id).collectAsState(initial = emptyList())

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedEventId = event.id }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 左側：日付
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
                            Text(
                                text = sdfDay.format(date),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraLight,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = sdfMonth.format(date),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Light,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // 右側：イベント詳細
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                letterSpacing = 0.5.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = event.venue,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            
                            if (history.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = PostGreen, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "${history.size} photos posted",
                                        fontSize = 11.sp,
                                        color = PostGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // 編集・削除ボタン
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onEditEvent(event) }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "編集",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(onClick = { eventToDelete = event }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "削除",
                                    tint = Color.Red.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ModernSettingScreen(
    viewModel: PhotoViewModel, 
    onManageEvents: () -> Unit,
    onSelectMakeReference: () -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit
) {
    val context = LocalContext.current
    var showResetConfirm by remember { mutableStateOf(false) }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("機材リストのリセット") },
            text = { Text("これまでにスキャンされた機材名（BODY）のリストをすべてリセットしますか？\n写真データそのものは削除されません。") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearBodyHistory()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("リセットする")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("キャンセル")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Setting",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- 🎨 テーマ選択 ---
        Text(
            text = "APPEARANCE (THEME)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = Color.Gray
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KamekoThemeType.values().forEach { theme ->
                val isSelected = viewModel.currentTheme == theme
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateTheme(theme) },
                    label = { Text(theme.name, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // BODY（機材）フィルタ設定
        Text(
            text = "BODY FILTER",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = Color.Gray
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectMakeReference() },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.List, null, tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("特定機材で絞り込む (EXIF Make)", fontSize = 15.sp)
                        Text(
                            text = viewModel.selectedMakeFilter ?: "未設定 (すべての機材を表示)",
                            fontSize = 12.sp,
                            color = if (viewModel.selectedMakeFilter != null) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
                Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
            }
        }

        // 選択中の機材のみを表示
        viewModel.selectedMakeFilter?.let { body ->
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InputChip(
                    selected = true,
                    onClick = { viewModel.selectedMakeFilter = null },
                    label = { Text(body.uppercase(), fontSize = 10.sp) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "削除",
                            modifier = Modifier.size(12.dp).clickable { viewModel.clearCameraMake(body) }
                        )
                    },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // 機材リセットボタン
        TextButton(
            onClick = { showResetConfirm = true },
            modifier = Modifier.align(Alignment.Start),
            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red.copy(alpha = 0.6f))
        ) {
            Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("機材リストをリセット", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 🌐 外部イベントインポート ---
        Text(
            text = "EXTERNAL EVENT IMPORT",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = Color.Gray
        )
        var importUrl by remember { mutableStateOf("") }
        var isImporting by remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = importUrl,
                    onValueChange = { importUrl = it },
                    label = { Text("GitHub Raw URL (Text形式)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("https://raw.githubusercontent.com/...", fontSize = 10.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (importUrl.isNotBlank()) {
                                isImporting = true
                                viewModel.importEventsFromUrl(importUrl) { success, message ->
                                    isImporting = false
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    if (success) importUrl = ""
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isImporting && importUrl.isNotBlank()
                    ) {
                        if (isImporting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("インポート")
                        }
                    }
                    
                    OutlinedButton(
                        onClick = {
                            val text = viewModel.exportEventsToText()
                            if (text.isBlank()) {
                                Toast.makeText(context, "出力するイベントがありません", Toast.LENGTH_SHORT).show()
                            } else {
                                // クリップボードにコピー
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Events", text)
                                clipboard.setPrimaryClip(clip)
                                
                                // 共有ダイアログも表示
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "イベントリストを出力"))
                                
                                Toast.makeText(context, "クリップボードにコピーしました", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("エクスポート")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "DATABASE MANAGEMENT",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = Color.Gray
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onManageEvents() },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.List, null, tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.width(16.dp))
                    Text("現場（イベント）の管理・一括登録", fontSize = 15.sp)
                }
                Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // バックアップ
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBackup() },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, null, tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("データベースのバックアップ", fontSize = 15.sp)
                        Text("現在の情報をZIPファイルとして保存します", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
            }
        }

        // 復元
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRestore() },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("バックアップから復元", fontSize = 15.sp)
                        Text("以前保存したZIPファイルから情報を復元します", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // パス修復ツール（機種変更サポート）
        var showRepairConfirm by remember { mutableStateOf(false) }
        if (showRepairConfirm) {
            AlertDialog(
                onDismissRequest = { showRepairConfirm = false },
                title = { Text("写真パスの一括修復") },
                text = { Text("機種変更などで写真の保存場所が変わった場合、ファイル名をもとにデータベースの情報を再紐付けします。実行しますか？") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.autoRepairPaths { count ->
                            Toast.makeText(context, "${count}枚の写真を再紐付けしました", Toast.LENGTH_LONG).show()
                        }
                        showRepairConfirm = false
                    }) {
                        Text("実行する")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRepairConfirm = false }) {
                        Text("キャンセル")
                    }
                }
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showRepairConfirm = true },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, null, tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("写真パスの修復 (機種変更対応)", fontSize = 15.sp)
                        Text("不整合なパスをファイル名で再紐付けします", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
            }
        }

        // 他の設定項目（将来用）
        Text(
            text = "APP INFO",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        Text("Version ${BuildConfig.VERSION_NAME}", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))

        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernPhotoItem(
    item: LocalPhotoItem,
    dbPhoto: PhotoEntity?,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    val lightweightLoader = remember { getLightweightImageLoader(context) }

    // 解像度を320pxに制限し、キャッシュの再利用を優先
    val imageRequest = remember(item.uri) {
        ImageRequest.Builder(context)
            .data(item.uri)
            .size(320)
            .precision(Precision.INEXACT)
            .build()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // 正方形固定
            .clip(RoundedCornerShape(4.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = imageRequest,
            imageLoader = lightweightLoader,
            contentDescription = null,
            contentScale = ContentScale.Crop, // 正方形に収める
            modifier = Modifier.fillMaxSize()
        )

        // 選択時
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.padding(6.dp))
                }
            }
        }

        // Status Overlays
        Box(
            modifier = Modifier.matchParentSize().padding(10.dp)
        ) {
            // 1. RAW indicator (Top Left) - Minimalist & Low contrast
            if (item.fileType == "RAW") {
                Text(
                    text = "RAW",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            // 2. Status Icons (Top Right) - Modern System Icons
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (dbPhoto?.status) {
                    "FAVORITE" -> Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD600).copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    "POSTED" -> Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PostGreen.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    "SHOT" -> {
                        // "SHOT" (or "HOT") status: Simple refined dot
                        Box(
                            Modifier
                                .size(6.dp)
                                .background(Color.White.copy(alpha = 0.4f), CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    text: String? = null,
    label: String,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.3f
    Column(
        modifier = Modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
        } else if (text != null) {
            Text(text, fontWeight = FontWeight.Black, fontSize = 18.sp, color = textColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun VerticalGridScrollbar(
    gridState: LazyGridState,
    modifier: Modifier = Modifier
) {
    var showScrollbar by remember { mutableStateOf(false) }

    // スクロール中、またはスクロール停止直後のみ表示
    LaunchedEffect(gridState.isScrollInProgress) {
        if (gridState.isScrollInProgress) {
            showScrollbar = true
        } else {
            delay(1500) // 停止後1.5秒待機
            showScrollbar = false
        }
    }

    val scrollbarAlpha by animateFloatAsState(
        targetValue = if (showScrollbar) 0.5f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "scrollbar_alpha"
    )

    if (scrollbarAlpha > 0f) {
        Canvas(modifier = modifier.fillMaxHeight().width(4.dp).alpha(scrollbarAlpha)) {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val visibleItems = layoutInfo.visibleItemsInfo

            if (totalItems > 0 && visibleItems.isNotEmpty()) {
                val firstItem = visibleItems.first()
                val lastItem = visibleItems.last()

                // 3列を基本とした行数計算
                val totalRows = (totalItems + 2) / 3
                val firstRow = firstItem.index / 3
                val lastRow = lastItem.index / 3
                val visibleRows = lastRow - firstRow + 1

                if (totalRows > 1) {
                    val scrollbarHeight = (visibleRows.toFloat() / totalRows) * size.height
                    val scrollbarOffsetY = (firstRow.toFloat() / totalRows) * size.height

                    drawRoundRect(
                        color = Color.Gray,
                        topLeft = Offset(0f, scrollbarOffsetY),
                        size = Size(size.width, scrollbarHeight.coerceAtLeast(32.dp.toPx())),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailView(
    allPhotos: List<LocalPhotoItem>,
    initialIndex: Int,
    dbPhotoMap: Map<String, PhotoEntity>,
    events: List<EventEntity>,
    onDismiss: () -> Unit,
    onSaveStatus: (PhotoEntity, String, Long?, String) -> Unit,
    onShare: (PhotoEntity, LocalPhotoItem, String?) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { allPhotos.size })
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentLocalPhoto = allPhotos[pagerState.currentPage]
    val dbPhoto = dbPhotoMap[currentLocalPhoto.filePath]
    val photoEntity = dbPhoto ?: PhotoEntity(filePath = currentLocalPhoto.filePath, fileType = currentLocalPhoto.fileType)

    val sdfFull = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // メタデータ解析
    val metaData = remember(currentLocalPhoto.filePath) {
        var name = currentLocalPhoto.filePath.substringAfterLast("/")
        var exifStr = "不明 (Exifなし)"
        var modifiedStr = "不明"
        var ss = "---"
        var f = "---"
        var iso = "---"
        var focal = "---"
        var make: String? = null

        fun extractExif(exif: ExifInterface) {
            val dateStr = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
            if (dateStr != null) exifStr = dateStr

            val exposureTime = exif.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME, -1.0)
            ss = formatShutterSpeed(if (exposureTime > 0) exposureTime else null)

            val apertureValue = exif.getAttributeDouble(ExifInterface.TAG_F_NUMBER, -1.0)
            if (apertureValue > 0) f = "F${"%.1f".format(apertureValue)}"

            val isoValue = exif.getAttributeInt(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, -1)
                .let { if (it == -1) exif.getAttributeInt(ExifInterface.TAG_ISO_SPEED, -1) else it }
            if (isoValue > 0) iso = "ISO$isoValue"

            val focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            if (focalLength != null) {
                val parts = focalLength.split("/")
                val value = if (parts.size == 2) parts[0].toDouble() / parts[1].toDouble() else focalLength.toDoubleOrNull()
                if (value != null) {
                    val focal35 = exif.getAttributeInt(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM, -1)
                    focal = "${value.toInt()}mm" + if (focal35 > 0) " (フルサイズ換算: ${focal35}mm)" else ""
                }
            }
            
            make = (exif.getAttribute(ExifInterface.TAG_MAKE) ?: exif.getAttribute(ExifInterface.TAG_MODEL))?.trim()
        }

        try {
            val uri = currentLocalPhoto.uri
            
            // 1. ContentResolver経由でExifを取得
            if (currentLocalPhoto.filePath.startsWith("content://")) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val exif = ExifInterface(inputStream)
                    extractExif(exif)
                }

                // 2. MediaStoreからファイル名と最終更新日時を取得
                val projection = arrayOf(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_MODIFIED
                )
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                        val modifiedIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                        
                        name = cursor.getString(nameIndex)
                        val modifiedSeconds = cursor.getLong(modifiedIndex)
                        modifiedStr = sdfFull.format(Date(modifiedSeconds * 1000L))
                    }
                }
            } else {
                val file = File(currentLocalPhoto.filePath)
                name = file.name
                modifiedStr = sdfFull.format(Date(file.lastModified()))
                val exif = ExifInterface(currentLocalPhoto.filePath)
                extractExif(exif)
            }
        } catch (e: Exception) {
            exifStr = "解析エラー (Exif)"
            modifiedStr = "解析エラー (File)"
        }

        PhotoMetaInfo(name, exifStr, modifiedStr, ss, f, iso, focal, make ?: dbPhoto?.cameraMake)
    }

    val resolvedEventId = remember(currentLocalPhoto.filePath, dbPhoto?.eventId, events, metaData.exifDate) {
        dbPhoto?.eventId ?: run {
            // EXIFの日付を優先して候補を探す
            val exifDateOnly = if (metaData.exifDate.contains(":")) {
                metaData.exifDate.substringBefore(" ").replace(":", "-")
            } else null
            
            val targetDate = exifDateOnly ?: currentLocalPhoto.dateStr.replace("/", "-")
            events.find { it.eventDate.trim().replace("/", "-") == targetDate }?.id
        }
    }

    var selectedStatus by remember(currentLocalPhoto.filePath, dbPhoto?.status) {
        mutableStateOf(dbPhoto?.status ?: "SHOT")
    }
    var memoText by remember(currentLocalPhoto.filePath, dbPhoto?.memo) {
        mutableStateOf(dbPhoto?.memo ?: "")
    }
    var selectedEventId by remember(currentLocalPhoto.filePath, resolvedEventId) {
        mutableStateOf(resolvedEventId)
    }

    val fileName = metaData.fileName
    val exifDate = metaData.exifDate

    val sortedEvents = remember(events) { events.sortedByDescending { it.startTime } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // --- ① 写真表示エリア (Pager) ---
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .background(Color.Black)
            ) { page ->
                val photo = allPhotos[page]
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // --- ② 操作・情報エリア ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // メタデータ表示
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // 1. 基本ファイル情報
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.AutoMirrored.Filled.List, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Text("FILE: $fileName", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 0.5.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Text("SHOT: $exifDate", fontSize = 11.sp, color = Color.DarkGray, letterSpacing = 0.5.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Text("EDIT: ${metaData.modifiedDate}", fontSize = 11.sp, color = Color.DarkGray, letterSpacing = 0.5.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Info, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Text("TYPE: ${currentLocalPhoto.fileType}", fontSize = 11.sp, color = Color.Gray, letterSpacing = 0.5.sp)
                            }
                            metaData.cameraMake?.let {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Face, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    Text("BODY: $it", fontSize = 11.sp, color = Color.Gray, letterSpacing = 0.5.sp)
                                }
                            }
                        }

                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                        // 2. 撮影設定 (EXIF)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Settings, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Text("CAMERA SETTINGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 露出3点セット
                                listOf(
                                    "SS" to metaData.shutterSpeed,
                                    "F" to metaData.aperture,
                                    "ISO" to metaData.iso
                                ).forEach { (label, value) ->
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                                        }
                                    }
                                }
                            }

                            // 焦点距離は別行で詳しく
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("FOCAL LENGTH", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    Text(metaData.focalLength, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                // 現場選択セクション
                val currentEvent = remember(selectedEventId, events) {
                    events.find { it.id == selectedEventId }
                }

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Info, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Text(
                                text = "EVENT LINK",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        var expandedEvent by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expandedEvent,
                            onExpandedChange = { expandedEvent = !expandedEvent }
                        ) {
                            OutlinedTextField(
                                value = currentEvent?.let { "${it.eventDate} ${it.name} (${it.venue})" } ?: "⚠️ 現場未割り付け (自動判定なし)",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEvent) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedTextColor = if (currentEvent != null) Color.Black else Color.Red,
                                    unfocusedTextColor = if (currentEvent != null) Color.Black else Color.Red
                                ),
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontWeight = if (currentEvent != null) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedEvent,
                                onDismissRequest = { expandedEvent = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("❌ 現場の紐付けを解除（未割り付けにする）", color = Color.Red, fontSize = 14.sp) },
                                    onClick = {
                                        selectedEventId = null
                                        expandedEvent = false
                                        // 現場解除を即時反映
                                        onSaveStatus(photoEntity, selectedStatus, null, memoText)
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                sortedEvents.forEach { event ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(event.eventDate, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                                Text(event.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Place, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(event.venue, fontSize = 12.sp, color = Color.Gray)
                                                }
                                            }
                                        },
                                        onClick = {
                                            selectedEventId = event.id
                                            expandedEvent = false
                                            // 現場選択を即時反映
                                            onSaveStatus(photoEntity, selectedStatus, event.id, memoText)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ステータス変更
                Text("ステータス変更", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("SHOT" to "SHOT", "FAVORITE" to "FAV", "POSTED" to "POST").forEach { (statusKey, label) ->
                        val isSelected = selectedStatus == statusKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { 
                                selectedStatus = statusKey
                                // ステータス変更を即時反映
                                onSaveStatus(photoEntity, statusKey, selectedEventId, memoText)
                            },
                            label = { Text(label, fontSize = 11.sp, letterSpacing = 1.sp) }
                        )
                    }
                }

                // 現像メモ
                OutlinedTextField(
                    value = memoText,
                    onValueChange = { memoText = it },
                    label = { Text("現像メモ・タグ設定") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // アクションボタン
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onShare(photoEntity, currentLocalPhoto, null) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("共有", fontSize = 12.sp)
                    }

                    // X (Twitter)
                    OutlinedButton(
                        onClick = { onShare(photoEntity, currentLocalPhoto, "com.twitter.android") },
                        modifier = Modifier.weight(0.8f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("X", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }

                    // Lightroom
                    OutlinedButton(
                        onClick = { onShare(photoEntity, currentLocalPhoto, "com.adobe.lrmobile") },
                        modifier = Modifier.weight(0.8f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Lr", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF001E36))
                    }

                    Button(
                        onClick = {
                            onSaveStatus(photoEntity, selectedStatus, selectedEventId, memoText)
                        },
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("保存", fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun GroupedHistoryItem(
    historyGroup: List<StatusHistoryEntity>, 
    sdfFull: SimpleDateFormat,
    onDelete: (() -> Unit)? = null
) {
    val first = historyGroup.firstOrNull() ?: return
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                    Text("投稿完了", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Text("(${historyGroup.size}枚)", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        sdfFull.format(Date(first.changedAt)),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                
                if (onDelete != null) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "履歴削除", tint = Color.Red.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 写真の横並び表示
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
            ) {
                historyGroup.forEach { history ->
                    HistorySmallThumbnail(history.photoFilePath)
                }
            }
        }
    }
}

@Composable
fun HistorySmallThumbnail(filePath: String) {
    val context = LocalContext.current
    val uri = Uri.parse(filePath)
    val lightweightLoader = remember { getLightweightImageLoader(context) }
    val imageRequest = remember(uri) {
        ImageRequest.Builder(context)
            .data(uri)
            .size(100, 100)
            .build()
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .background(Color.LightGray, RoundedCornerShape(4.dp))
    ) {
        AsyncImage(
            model = imageRequest,
            imageLoader = lightweightLoader,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().background(Color.LightGray, RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun HistoryThumbnailItem(history: StatusHistoryEntity, sdfFull: SimpleDateFormat) {
    val context = LocalContext.current
    // URIからファイル名を取得
    val fileName = remember(history.photoFilePath) {
        var name = history.photoFilePath.substringAfterLast("/")
        try {
            val uri = Uri.parse(history.photoFilePath)
            val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    name = cursor.getString(0)
                }
            }
        } catch (e: Exception) { }
        name
    }
    val uri = Uri.parse(history.photoFilePath)
    val lightweightLoader = remember { getLightweightImageLoader(context) }
    val imageRequest = remember(uri) {
        ImageRequest.Builder(context)
            .data(uri)
            .crossfade(true)
            .size(120, 120)
            .build()
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // サムネイル
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.LightGray, RoundedCornerShape(6.dp))
            ) {
                AsyncImage(
                    model = imageRequest,
                    imageLoader = lightweightLoader,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray, RoundedCornerShape(6.dp))
                )
            }

            // ファイル名と日時
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    fileName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    sdfFull.format(Date(history.changedAt)),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun EventItemWithHistory(
    event: EventEntity,
    viewModel: PhotoViewModel,
    selectedEventId: Long?,
    onHistoryClick: (EventEntity) -> Unit,
    onCloseHistory: () -> Unit,
    onEditClick: (EventEntity, String) -> Unit,
    onDeleteClick: (EventEntity) -> Unit
) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val sdfFull = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val dateStr = if (event.startTime > 0) sdf.format(Date(event.startTime)) else "日付未登録"

    val eventHistory by viewModel.getPostedHistoryForEvent(event.id).collectAsState(initial = emptyList())

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val photoCount = eventHistory.size
                    val postCount = eventHistory.distinctBy { it.groupId }.size
                    Text(event.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, null, tint = Color.Gray, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(event.venue, fontSize = 11.sp, color = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(dateStr, fontSize = 11.sp, color = Color.Gray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("投稿済み: ${photoCount}枚 / ${postCount}回", fontSize = 11.sp, color = Color(0xFF2E7D32))
                    }
                }
                Row {
                    IconButton(onClick = { onHistoryClick(event) }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "履歴", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { onEditClick(event, dateStr) }) {
                        Icon(Icons.Default.Edit, contentDescription = "編集", tint = Color.Blue, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { onDeleteClick(event) }) {
                        Icon(Icons.Default.Delete, contentDescription = "削除", tint = Color.Red, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // イベント個別履歴
            if (selectedEventId == event.id) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                if (eventHistory.isEmpty()) {
                    Text("まだ投稿履歴がありません", fontSize = 12.sp, color = Color.Gray)
                } else {
                    val groupedEventHistory = eventHistory.groupBy { it.groupId }.values.toList()
                        .sortedByDescending { it.firstOrNull()?.changedAt ?: 0L }

                    groupedEventHistory.forEach { group ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🟢 ${group.size}枚 投稿", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(sdfFull.format(Date(group.firstOrNull()?.changedAt ?: 0L)), fontSize = 10.sp, color = Color.Gray)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                group.forEach { history ->
                                    HistorySmallThumbnail(history.photoFilePath)
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFFB2DFDB))
                    }
                }

                TextButton(onClick = onCloseHistory) {
                    Text("閉じる", fontSize = 12.sp)
                }
            }
        }
    }
}

suspend fun loadPhotosWithMimeAsync(context: Context): List<LocalPhotoItem> = withContext(Dispatchers.IO) {
    val items = mutableListOf<LocalPhotoItem>()

    val baseProjection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_MODIFIED,
        MediaStore.Images.Media.DATA
    )
    
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    val resolver = context.contentResolver
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    resolver.query(uri, baseProjection, null, null, sortOrder)?.use { c ->
        val idCol = c.getColumnIndex(MediaStore.Images.Media._ID)
        val mimeCol = c.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
        val takenCol = c.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
        val addedCol = c.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
        val modCol = c.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
        val dataCol = c.getColumnIndex(MediaStore.Images.Media.DATA)

        while (c.moveToNext()) {
            try {
                val id = if (idCol != -1) c.getLong(idCol) else continue
                val mimeType = if (mimeCol != -1) (c.getString(mimeCol) ?: "").lowercase() else ""
                val dataPath = if (dataCol != -1) (c.getString(dataCol) ?: "") else ""

                val realDateTaken = if (takenCol != -1) c.getLong(takenCol) else 0L
                val dateAdded = if (addedCol != -1) c.getLong(addedCol) * 1000L else 0L
                val dateModified = if (modCol != -1) c.getLong(modCol) else 0L
                val photoUri = Uri.withAppendedPath(uri, id.toString())

                val isRaw = mimeType.contains("raw") ||
                        mimeType.contains("x-adobe-dng") ||
                        mimeType.contains("image/x-nikon-nef") ||
                        dataPath.endsWith(".nef", ignoreCase = true) ||
                        dataPath.endsWith(".cr2", ignoreCase = true) ||
                        dataPath.endsWith(".cr3", ignoreCase = true) ||
                        dataPath.endsWith(".arw", ignoreCase = true) ||
                        dataPath.endsWith(".dng", ignoreCase = true)

                val dateStr = extractDateFromPath(dataPath, realDateTaken, dateModified, dateAdded)

                items.add(
                    LocalPhotoItem(
                        uri = photoUri,
                        filePath = dataPath.ifBlank { photoUri.toString() },
                        fileType = if (isRaw) "RAW" else "JPEG",
                        dateTaken = realDateTaken,
                        dateModified = dateModified,
                        dateAdded = dateAdded,
                        dateStr = dateStr,
                        cameraMake = null // MediaStoreからの取得は不安定なため一旦nullに
                    )
                )
            } catch (e: Exception) { }
        }
    }
    return@withContext items.distinctBy { it.uri.toString() }
}

suspend fun loadPhotosWithMimeStreaming(
    context: Context,
    onBatch: suspend (List<LocalPhotoItem>) -> Unit
) = withContext(Dispatchers.IO) {
    val baseProjection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_MODIFIED,
        MediaStore.Images.Media.DATA
    )

    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    context.contentResolver.query(uri, baseProjection, null, null, sortOrder)?.use { c ->
        val idCol = c.getColumnIndex(MediaStore.Images.Media._ID)
        val mimeCol = c.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
        val takenCol = c.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
        val addedCol = c.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
        val modCol = c.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
        val dataCol = c.getColumnIndex(MediaStore.Images.Media.DATA)

        val batch = mutableListOf<LocalPhotoItem>()
        while (c.moveToNext()) {
            try {
                val id = if (idCol != -1) c.getLong(idCol) else continue
                val mimeType = if (mimeCol != -1) (c.getString(mimeCol) ?: "").lowercase() else ""
                val dataPath = if (dataCol != -1) (c.getString(dataCol) ?: "") else ""

                val realDateTaken = if (takenCol != -1) c.getLong(takenCol) else 0L
                val dateAdded = if (addedCol != -1) c.getLong(addedCol) * 1000L else 0L
                val dateModified = if (modCol != -1) c.getLong(modCol) else 0L
                val photoUri = Uri.withAppendedPath(uri, id.toString())

                val isRaw = mimeType.contains("raw") ||
                        mimeType.contains("x-adobe-dng") ||
                        mimeType.contains("image/x-nikon-nef") ||
                        dataPath.endsWith(".nef", ignoreCase = true) ||
                        dataPath.endsWith(".cr2", ignoreCase = true) ||
                        dataPath.endsWith(".cr3", ignoreCase = true) ||
                        dataPath.endsWith(".arw", ignoreCase = true) ||
                        dataPath.endsWith(".dng", ignoreCase = true)

                val dateStr = extractDateFromPath(dataPath, realDateTaken, dateModified, dateAdded)

                batch.add(
                    LocalPhotoItem(
                        uri = photoUri,
                        filePath = dataPath.ifBlank { photoUri.toString() },
                        fileType = if (isRaw) "RAW" else "JPEG",
                        dateTaken = realDateTaken,
                        dateModified = dateModified,
                        dateAdded = dateAdded,
                        dateStr = dateStr,
                        cameraMake = null
                    )
                )

                if (batch.size >= 100) {
                    val snapshot = batch.toList()
                    withContext(Dispatchers.Main) { onBatch(snapshot) }
                    batch.clear()
                }
            } catch (e: Exception) { }
        }
        if (batch.isNotEmpty()) {
            val snapshot = batch.toList()
            withContext(Dispatchers.Main) { onBatch(snapshot) }
        }
    }
}

// 日付抽出 (撮影日時 > ファイル名 > 編集日時 > 追加日時)
fun extractDateFromPath(path: String, dateTaken: Long, dateModified: Long, dateAdded: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    // 1. 撮影日時 (DATE_TAKEN) があれば最優先
    if (dateTaken > 0) {
        return sdf.format(Date(dateTaken))
    }
    
    // 2. ファイル名から日付らしき数字を探す
    val fileNameDate = extractDateOnlyFromFilename(path)
    if (fileNameDate.isNotEmpty()) {
        return fileNameDate
    }
    
    // 3. 編集日時 (DATE_MODIFIED)
    if (dateModified > 0) {
        return sdf.format(Date(dateModified * 1000L))
    }

    // 4. 追加日時 (DATE_ADDED)
    if (dateAdded > 0) {
        return sdf.format(Date(dateAdded))
    }
    
    return ""
}

fun extractDateOnlyFromFilename(path: String): String {
    val fileName = path.substringAfterLast("/")
    val dateRegex = Regex("""(20\d{2})(\d{2})(\d{2})""")
    val match = dateRegex.find(fileName)
    return if (match != null) {
        "${match.groupValues[1]}-${match.groupValues[2]}-${match.groupValues[3]}"
    } else ""
}

// ImageLoaderのシングルトン保持
private var _lightweightImageLoader: ImageLoader? = null

fun getLightweightImageLoader(context: Context): ImageLoader {
    return _lightweightImageLoader ?: ImageLoader.Builder(context.applicationContext)
        .memoryCache {
            MemoryCache.Builder(context.applicationContext)
                .maxSizePercent(0.20) // メモリキャッシュを少し多めに確保
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.applicationContext.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.1) // ディスクキャッシュを10%に設定
                .build()
        }
        .crossfade(false) // フェードをオフ
        .build().also { _lightweightImageLoader = it }
}

/**
 * URI準備関数
 * Android 11以降の権限制限や、他アプリの権限委譲不可問題を回避するため、
 * アプリのキャッシュ領域に複製してから共有します。
 */
suspend fun prepareShareUri(context: Context, filePath: String, mediaStoreUri: Uri?): Uri = withContext(Dispatchers.IO) {
    // 古い一時ファイルのクリーンアップ（1時間以上前のもの）
    try {
        val cacheDir = File(context.cacheDir, "shared_proxy")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        cacheDir.listFiles()?.forEach { 
            if (System.currentTimeMillis() - it.lastModified() > 3600_000) it.delete() 
        }
    } catch (e: Exception) {}

    val sourceUri = mediaStoreUri ?: if (filePath.startsWith("content://")) {
        Uri.parse(filePath)
    } else {
        Uri.fromFile(File(filePath))
    }

    try {
        // MIMEタイプと拡張子の特定
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(sourceUri) ?: "image/jpeg"
        val extension = when (mimeType) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/gif" -> "gif"
            "image/heic", "image/heif" -> "heic"
            else -> "jpg"
        }

        // アプリ専用のキャッシュ領域にコピー
        val proxyDir = File(context.cacheDir, "shared_proxy")
        if (!proxyDir.exists()) proxyDir.mkdirs()
        
        val tempFile = File(proxyDir, "share_${System.currentTimeMillis()}_${(0..1000).random()}.$extension")
        
        contentResolver.openInputStream(sourceUri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        if (tempFile.exists() && tempFile.length() > 0) {
            return@withContext FileProvider.getUriForFile(context, context.packageName + ".fileprovider", tempFile)
        }
    } catch (e: Exception) {
        Log.e("KamekoPad", "Failed to proxy URI to cache: $sourceUri", e)
    }

    // 失敗時のフォールバック
    return@withContext try {
        val file = File(filePath)
        if (file.exists()) FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        else sourceUri
    } catch (e: Exception) {
        sourceUri
    }
}

/**
 * 特定のパッケージに直接共有、または通常の共有ダイアログを表示する
 */
fun shareToPackage(
    context: Context,
    packageId: String?,
    uris: List<Uri>,
    memo: String?,
    onStart: () -> Unit
) {
    if (uris.isEmpty()) return

    val shareIntent = if (uris.size == 1) {
        Intent(Intent.ACTION_SEND).apply {
            type = context.contentResolver.getType(uris[0]) ?: "image/*"
            putExtra(Intent.EXTRA_STREAM, uris[0])
            memo?.let { if (it.isNotBlank()) putExtra(Intent.EXTRA_TEXT, it) }
        }
    } else {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            // 複数枚の場合はメモの扱いがアプリによって異なるため、一旦なし or 検討
        }
    }

    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    
    // ClipDataを構成（全URIに対して明示的に権限付与）
    val cd = ClipData.newUri(context.contentResolver, "Photos", uris[0])
    for (i in 1 until uris.size) {
        cd.addItem(ClipData.Item(uris[i]))
    }
    shareIntent.clipData = cd

    if (packageId != null) {
        shareIntent.setPackage(packageId)
        try {
            context.startActivity(shareIntent)
            onStart()
        } catch (e: Exception) {
            // アプリが入っていない場合はトースト
            android.widget.Toast.makeText(context, "アプリが見つかりません", android.widget.Toast.LENGTH_SHORT).show()
        }
    } else {
        // 通常の共有ダイアログ
        val chooser = Intent.createChooser(shareIntent, "写真を共有")
        chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(chooser)
        onStart()
    }
}
