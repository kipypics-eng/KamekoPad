package com.example.kameko

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * アーティストのチップ編集部品。
 * - 選択中のアーティストはチップで表示し、× で外す
 * - 入力欄で候補を絞り込み、候補チップのタップまたは「＋」で追加（未登録の名前は新規登録）
 *
 * @param suggestions 候補名（表示したい順）。選択中のものは除外して表示する
 * @param prioritySuggestions イベント出演者など、アイコンを付けて目立たせる候補
 * @param onSelectedClick 選択中チップ本体をタップしたときの処理（既定は外す）
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArtistChipEditor(
    selected: List<String>,
    suggestions: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedLabel: (String) -> String = { it },
    onSelectedClick: (String) -> Unit = onRemove,
    prioritySuggestions: Set<String> = emptySet(),
    maxSuggestions: Int = 20
) {
    var input by remember { mutableStateOf("") }
    val query = input.trim()
    val selectedKeys = selected.map { it.lowercase() }.toSet()
    val visibleSuggestions = suggestions.distinct()
        .filter { it.lowercase() !in selectedKeys && (query.isEmpty() || it.contains(query, ignoreCase = true)) }
        .take(maxSuggestions)

    val submit: () -> Unit = {
        if (query.isNotEmpty()) {
            // 大文字小文字違いの既存名があればそちらに寄せる
            onAdd(suggestions.firstOrNull { it.equals(query, ignoreCase = true) } ?: query)
            input = ""
        }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (selected.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                selected.forEach { name ->
                    InputChip(
                        selected = true,
                        onClick = { onSelectedClick(name) },
                        label = { Text(selectedLabel(name), fontSize = 12.sp) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "外す",
                                modifier = Modifier.size(16.dp).clickable { onRemove(name) }
                            )
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("アーティスト名") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            trailingIcon = {
                IconButton(onClick = submit, enabled = query.isNotEmpty()) {
                    Icon(Icons.Default.Add, contentDescription = "追加")
                }
            }
        )

        if (visibleSuggestions.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                visibleSuggestions.forEach { name ->
                    SuggestionChip(
                        onClick = { onAdd(name); input = "" },
                        label = { Text(name, fontSize = 12.sp) },
                        icon = if (name in prioritySuggestions) {
                            { Icon(Icons.Default.Person, contentDescription = "出演者", modifier = Modifier.size(14.dp)) }
                        } else null
                    )
                }
            }
        }
    }
}

/** 出演者を先頭にした候補名リスト */
private fun buildSuggestions(allArtists: List<ArtistEntity>, lineupNames: List<String>): List<String> =
    lineupNames + allArtists.map { it.name }.filter { it !in lineupNames }

private fun lineupNamesFor(eventIds: Collection<Long>, eventArtists: Map<Long, List<Long>>, byId: Map<Long, ArtistEntity>): List<String> =
    eventIds.flatMap { eventArtists[it].orEmpty() }.distinct()
        .mapNotNull { byId[it]?.name }
        .sortedBy { it.lowercase() }

/** 写真詳細のアーティスト欄 */
@Composable
fun PhotoArtistSection(viewModel: PhotoViewModel, photo: PhotoEntity, eventId: Long?) {
    val artists by remember(photo.id) { viewModel.photoArtists(photo.id) }.collectAsState(initial = emptyList())
    val allArtists by viewModel.allArtists.collectAsState()
    val eventArtists by viewModel.eventArtists.collectAsState()

    val byId = allArtists.associateBy { it.id }
    val lineupNames = lineupNamesFor(listOfNotNull(eventId), eventArtists, byId)
    val byName = allArtists.associateBy { it.name }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Text(text = "ARTISTS", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            ArtistChipEditor(
                selected = artists.map { it.name },
                suggestions = buildSuggestions(allArtists, lineupNames),
                prioritySuggestions = lineupNames.toSet(),
                onAdd = { viewModel.addArtistToPhotos(listOf(photo.id), it) },
                onRemove = { name -> byName[name]?.let { viewModel.removeArtistFromPhotos(listOf(photo.id), it.id) } }
            )
        }
    }
}

/** ギャラリーの複数選択からのアーティスト付与ダイアログ */
@Composable
fun MultiArtistAssignDialog(viewModel: PhotoViewModel, photos: List<PhotoEntity>, onDismiss: () -> Unit) {
    val ids = remember(photos) { photos.map { it.id } }
    val refs by remember(ids) { viewModel.photoArtistRefs(ids) }.collectAsState(initial = emptyList())
    val allArtists by viewModel.allArtists.collectAsState()
    val eventArtists by viewModel.eventArtists.collectAsState()

    val byId = allArtists.associateBy { it.id }
    val byName = allArtists.associateBy { it.name }
    val counts = refs.groupingBy { it.artistId }.eachCount()
    val selectedNames = counts.keys.mapNotNull { byId[it]?.name }.sortedBy { it.lowercase() }
    val lineupNames = lineupNamesFor(photos.mapNotNull { it.eventId }.distinct(), eventArtists, byId)
    val total = photos.size

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("選択した写真のアーティスト", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "${total} 枚の写真にまとめて付けます。\n(2/5) のように一部の写真だけに付いているものは、タップで全部に付けられます。× で全部から外します。",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
                ArtistChipEditor(
                    selected = selectedNames,
                    suggestions = buildSuggestions(allArtists, lineupNames),
                    prioritySuggestions = lineupNames.toSet(),
                    selectedLabel = { name ->
                        val c = byName[name]?.let { counts[it.id] } ?: 0
                        if (c < total) "$name ($c/$total)" else name
                    },
                    onAdd = { viewModel.addArtistToPhotos(ids, it) },
                    onSelectedClick = { viewModel.addArtistToPhotos(ids, it) },
                    onRemove = { name -> byName[name]?.let { viewModel.removeArtistFromPhotos(ids, it.id) } }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("閉じる") }
        }
    )
}

/** ギャラリーのアーティスト絞り込み（1組選択） */
@Composable
fun ArtistFilterDropdown(viewModel: PhotoViewModel, modifier: Modifier = Modifier) {
    val options by viewModel.artistFilterOptions.collectAsState()
    val allArtists by viewModel.allArtists.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val selectedId = viewModel.selectedArtistFilterId

    val label = when (selectedId) {
        null -> "ARTISTS"
        ARTIST_FILTER_UNASSIGNED -> "未設定"
        else -> allArtists.find { it.id == selectedId }?.name ?: "ARTISTS"
    }

    Box(modifier = modifier) {
        FilterChip(
            selected = selectedId != null,
            onClick = { expanded = true },
            label = {
                Text(
                    text = label.uppercase(),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp)) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(32.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            DropdownMenuItem(
                text = { Text("ALL ARTISTS", fontSize = 12.sp, color = Color.Gray) },
                onClick = { viewModel.selectedArtistFilterId = null; expanded = false }
            )
            DropdownMenuItem(
                text = { Text("未設定", fontSize = 13.sp) },
                onClick = { viewModel.selectedArtistFilterId = ARTIST_FILTER_UNASSIGNED; expanded = false }
            )
            HorizontalDivider()
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            if (viewModel.selectedEventFilterId != null) "この現場の出演者は未登録です" else "アーティストは未登録です",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    },
                    onClick = {},
                    enabled = false
                )
            }
            options.forEach { artist ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                artist.name,
                                fontSize = 13.sp,
                                fontWeight = if (artist.id == selectedId) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("${artist.photoCount}", fontSize = 11.sp, color = Color.Gray)
                        }
                    },
                    onClick = { viewModel.selectedArtistFilterId = artist.id; expanded = false }
                )
            }
        }
    }
}

/** 設定画面のアーティスト管理（名前の変更・削除） */
@Composable
fun ArtistManageDialog(viewModel: PhotoViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val artists by remember { viewModel.artistDao.getArtistsWithPhotoCount(null) }.collectAsState(initial = emptyList())
    var renaming by remember { mutableStateOf<ArtistWithPhotoCount?>(null) }
    var renameText by remember { mutableStateOf("") }
    var deleting by remember { mutableStateOf<ArtistWithPhotoCount?>(null) }

    renaming?.let { target ->
        AlertDialog(
            onDismissRequest = { renaming = null },
            title = { Text("名前を変更") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    label = { Text("アーティスト名") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    enabled = renameText.isNotBlank() && renameText.trim() != target.name,
                    onClick = {
                        viewModel.renameArtist(target.id, renameText) { ok ->
                            if (!ok) Toast.makeText(context, "同じ名前のアーティストが既にいます", Toast.LENGTH_SHORT).show()
                        }
                        renaming = null
                    }
                ) { Text("変更") }
            },
            dismissButton = { TextButton(onClick = { renaming = null }) { Text("キャンセル") } }
        )
    }

    deleting?.let { target ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("アーティストの削除") },
            text = { Text("「${target.name}」を削除しますか？\n写真 ${target.photoCount} 枚と、現場の出演者からも外れます。写真そのものは削除されません。") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteArtist(target.id); deleting = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("削除する") }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("キャンセル") } }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("アーティスト管理", fontWeight = FontWeight.Bold) },
        text = {
            if (artists.isEmpty()) {
                Text(
                    "アーティストはまだ登録されていません。\n現場の出演者や、写真のアーティスト欄から登録できます。",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(artists, key = { it.id }) { artist ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(artist.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("${artist.photoCount} 枚", fontSize = 11.sp, color = Color.Gray)
                            }
                            IconButton(onClick = { renameText = artist.name; renaming = artist }) {
                                Icon(Icons.Default.Edit, contentDescription = "名前を変更", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { deleting = artist }) {
                                Icon(Icons.Default.Delete, contentDescription = "削除", tint = Color.Red)
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("閉じる") } }
    )
}
