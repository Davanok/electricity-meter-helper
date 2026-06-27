package com.davanok.electricitymeterhelper.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.electricitymeterhelper.domain.ReadingObjectMin
import com.davanok.electricitymeterhelper.ui.components.SnackbarMessageHandler
import com.davanok.electricitymeterhelper.utils.DateFormat
import electricitymeterhelper.sharedui.generated.resources.Res
import electricitymeterhelper.sharedui.generated.resources.add_reading
import electricitymeterhelper.sharedui.generated.resources.app_name
import electricitymeterhelper.sharedui.generated.resources.ic_add
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
                title = { Text(text = stringResource(Res.string.app_name)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToReading) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = stringResource(Res.string.add_reading)
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = refreshItems,
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn {
                items(
                    items = uiState.entries,
                    key = { it.id }
                ) { item ->
                    ReadingListItem(
                        item = item,
                        modifier = Modifier.clickable { navigateToInfo(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadingListItem(
    item: ReadingObjectMin,
    modifier: Modifier = Modifier
) {
    val datetime = remember(item.date) { item.date.format(DateFormat) }

    Column(modifier = modifier) {
        ListItem(
            headlineContent = { Text(text = datetime) }
        )
        HorizontalDivider()
    }
}