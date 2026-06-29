package com.davanok.electricitymeterhelper.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.electricitymeterhelper.domain.ReadingObjectMin
import com.davanok.electricitymeterhelper.ui.components.SnackbarMessageHandler
import com.davanok.electricitymeterhelper.utils.DateFormat
import electricitymeterhelper.sharedui.generated.resources.Res
import electricitymeterhelper.sharedui.generated.resources.add_first_reading
import electricitymeterhelper.sharedui.generated.resources.add_reading
import electricitymeterhelper.sharedui.generated.resources.app_name
import electricitymeterhelper.sharedui.generated.resources.ic_add
import electricitymeterhelper.sharedui.generated.resources.ic_arrow_right
import electricitymeterhelper.sharedui.generated.resources.ic_electric_bolt
import electricitymeterhelper.sharedui.generated.resources.ic_refresh
import electricitymeterhelper.sharedui.generated.resources.no_reading_entries
import electricitymeterhelper.sharedui.generated.resources.readings_count
import electricitymeterhelper.sharedui.generated.resources.reload_items
import electricitymeterhelper.sharedui.generated.resources.tap_to_view_details
import kotlinx.datetime.format
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
fun HomeScreen(
    navigateToInfo: (entryId: Uuid) -> Unit,
    navigateToReading: () -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SnackbarMessageHandler(uiState.errorMessage)

    Content(
        uiState = uiState,
        navigateToReading = navigateToReading,
        navigateToInfo = navigateToInfo,
        refreshItems = viewModel::loadReadingObjects,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun Content(
    uiState: HomeScreenUiState,
    navigateToReading: () -> Unit,
    navigateToInfo: (entryId: Uuid) -> Unit,
    refreshItems: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(Res.string.app_name))
                        Text(
                            text = stringResource(Res.string.readings_count, uiState.entries.size),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = refreshItems) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_refresh),
                            contentDescription = stringResource(Res.string.reload_items)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToReading
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = stringResource(Res.string.add_reading)
                )
            }
        }
    ) { padding ->

        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = uiState.isLoading,
            onRefresh = refreshItems
        ) {

            if (uiState.entries.isEmpty() && !uiState.isLoading) {
                EmptyState(
                    modifier = Modifier.fillMaxSize(),
                    onAddClick = navigateToReading
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        uiState.entries,
                        key = { it.id }
                    ) { item ->
                        ReadingCard(
                            item = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .clickable {
                                    navigateToInfo(item.id)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingCard(
    item: ReadingObjectMin,
    modifier: Modifier = Modifier
) {
    val date = remember(item.date) {
        item.date.format(DateFormat)
    }

    ElevatedCard(
        modifier = modifier
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(Res.string.tap_to_view_details),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            trailingContent = {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun EmptyState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                painter = painterResource(Res.drawable.ic_electric_bolt),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.no_reading_entries),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.add_first_reading),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onAddClick
            ) {
                Text(text = stringResource(Res.string.add_reading))
            }
        }
    }
}