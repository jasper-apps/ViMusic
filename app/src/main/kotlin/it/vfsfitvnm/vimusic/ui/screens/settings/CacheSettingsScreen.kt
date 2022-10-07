package it.vfsfitvnm.vimusic.ui.screens.settings

import android.text.format.Formatter
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import it.vfsfitvnm.route.RouteHandler
import it.vfsfitvnm.vimusic.LocalPlayerAwarePaddingValues
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.enums.CoilDiskCacheMaxSize
import it.vfsfitvnm.vimusic.ui.components.TopAppBar
import it.vfsfitvnm.vimusic.ui.screens.EnumValueSelectorSettingsEntry
import it.vfsfitvnm.vimusic.ui.screens.SettingsDescription
import it.vfsfitvnm.vimusic.ui.screens.SettingsEntryGroupText
import it.vfsfitvnm.vimusic.ui.screens.SettingsGroupDescription
import it.vfsfitvnm.vimusic.ui.screens.SettingsTitle
import it.vfsfitvnm.vimusic.ui.screens.globalRoutes
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.utils.coilDiskCacheMaxSizeKey
import it.vfsfitvnm.vimusic.utils.rememberPreference

@OptIn(ExperimentalCoilApi::class)
@ExperimentalAnimationApi
@Composable
fun CacheSettingsScreen() {

    val scrollState = rememberScrollState()

    RouteHandler(listenToGlobalEmitter = true) {
        globalRoutes()

        host {
            val context = LocalContext.current
            val (colorPalette, _) = LocalAppearance.current

            var coilDiskCacheMaxSize by rememberPreference(
                coilDiskCacheMaxSizeKey,
                CoilDiskCacheMaxSize.`128MB`
            )

            Column(
                modifier = Modifier
                    .background(colorPalette.background0)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(LocalPlayerAwarePaddingValues.current)
            ) {
                TopAppBar(
                    modifier = Modifier
                        .height(52.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.chevron_back),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette.text),
                        modifier = Modifier
                            .clickable(onClick = pop)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .size(24.dp)
                    )
                }

                SettingsTitle(text = "Cache")

                SettingsDescription(text = "When the cache runs out of space, the resources that haven't been accessed for the longest time are cleared.")

                Coil.imageLoader(context).diskCache?.let { diskCache ->
                    val diskCacheSize = remember(diskCache) {
                        diskCache.size
                    }

                    SettingsEntryGroupText(title = "IMAGE CACHE")

                    SettingsGroupDescription(
                        text = "${
                            Formatter.formatShortFileSize(
                                context,
                                diskCacheSize
                            )
                        } used (${diskCacheSize * 100 / coilDiskCacheMaxSize.bytes.coerceAtLeast(1)}%)"
                    )

                    EnumValueSelectorSettingsEntry(
                        title = "Max size",
                        selectedValue = coilDiskCacheMaxSize,
                        onValueSelected = {
                            coilDiskCacheMaxSize = it
                        }
                    )
                }
            }
        }
    }
}
