package it.vfsfitvnm.vimusic.ui.views

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.models.PlaylistPreview
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.utils.color
import it.vfsfitvnm.vimusic.utils.semiBold
import it.vfsfitvnm.vimusic.utils.thumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun PlaylistPreviewItem(
    playlistPreview: PlaylistPreview,
    modifier: Modifier = Modifier,
    thumbnailSize: Dp = Dimensions.thumbnails.song,
    onClick: () -> Unit
) {
    val density = LocalDensity.current
    val (_, _, thumbnailShape) = LocalAppearance.current

    val thumbnailSizePx = with(density) {
        thumbnailSize.roundToPx()
    }

    val thumbnails by remember(playlistPreview.playlist.id) {
        Database.playlistThumbnailUrls(playlistPreview.playlist.id).distinctUntilChanged().map {
            it.map { url ->
                url.thumbnail(thumbnailSizePx)
            }
        }
    }.collectAsState(initial = emptyList(), context = Dispatchers.IO)

    PlaylistItem(
        name = playlistPreview.playlist.name,
        textColor = Color.White,
        imageContent = {
            if (thumbnails.toSet().size == 1) {
                AsyncImage(
                    model = thumbnails.first().thumbnail(thumbnailSizePx * 2),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(thumbnailShape)
                        .fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    listOf(
                        Alignment.TopStart,
                        Alignment.TopEnd,
                        Alignment.BottomStart,
                        Alignment.BottomEnd
                    ).forEachIndexed { index, alignment ->
                        AsyncImage(
                            model = thumbnails.getOrNull(index),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize(0.5f)
                                .clip(thumbnailShape)
                                .align(alignment)
                        )
                    }
                }
            }
        },
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun BuiltInPlaylistItem(
    @DrawableRes icon: Int,
    colorTint: Color,
    name: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (_, _, thumbnailShape) = LocalAppearance.current

    PlaylistItem(
        name = name,
        withGradient = false,
        imageContent = {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorTint),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp)
            )
        },
        modifier = modifier
            .clip(thumbnailShape),
        onClick = onClick
    )
}

@Composable
fun PlaylistItem(
    name: String,
    modifier: Modifier = Modifier,
    textColor: Color? = null,
    withGradient: Boolean = true,
    imageContent: @Composable BoxScope.() -> Unit,
    onClick: () -> Unit
) {
    val (colorPalette, typography, thumbnailShape) = LocalAppearance.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(thumbnailShape)
            .background(colorPalette.background1)
            .aspectRatio(1f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            content = imageContent
        )

        BasicText(
            text = name,
            style = typography.xxs.semiBold.color(textColor ?: colorPalette.text),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .run {
                    if (withGradient) {
                        background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                    } else {
                        this
                    }
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
