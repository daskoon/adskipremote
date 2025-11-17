package com.whatdoyouwantwired.adskipremote.wear

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.EventBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.wear.tiles.CoroutinesTileService

private const val RESOURCES_VERSION = "1"
private const val SKIP_AD_PATH = "/skip_ad"
private const val CLICK_ID = "skip_ad_click"

class SkipAdTileService : CoroutinesTileService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources {
        return ResourceBuilders.Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .build()
    }

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): TileBuilders.Tile {
        return TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTimeline(timeline())
            .build()
    }

    private fun timeline(): TimelineBuilders.Timeline {
        return TimelineBuilders.Timeline.fromLayoutElement(layout())
    }

    private fun layout(): LayoutElementBuilders.LayoutElement {
        return PrimaryLayout.Builder(this)
            .setPrimaryLabelTextContent(
                Text.Builder(this, "Ad Skip")
                    .build()
            )
            .setContent(
                CompactChip.Builder(this, "SKIP",
                    ActionBuilders.Clickable.Builder()
                        .setId(CLICK_ID)
                        .setOnClick(ActionBuilders.LoadAction.Builder().build())
                        .build()
                )
                    .setChipColors(ChipColors.primaryChipColors(this))
                    .build()
            )
            .build()
    }

    override suspend fun onTileClick(requestParams: EventBuilders.TileClickEvent) {
        if (requestParams.id == CLICK_ID) {
            serviceScope.launch {
                try {
                    val nodes = Wearable.getNodeClient(this@SkipAdTileService).connectedNodes.await()
                    nodes.forEach { node ->
                        Wearable.getMessageClient(this@SkipAdTileService).sendMessage(
                            node.id,
                            SKIP_AD_PATH,
                            null
                        ).await()
                    }
                } catch (e: Exception) {
                    // Log error
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}