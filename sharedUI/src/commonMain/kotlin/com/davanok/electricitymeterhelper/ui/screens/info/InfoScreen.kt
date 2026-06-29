package com.davanok.electricitymeterhelper.ui.screens.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.electricitymeterhelper.domain.ReadingEntry
import com.davanok.electricitymeterhelper.ui.components.SnackbarMessageHandler
import com.davanok.electricitymeterhelper.utils.DateFormat
import electricitymeterhelper.sharedui.generated.resources.Res
import electricitymeterhelper.sharedui.generated.resources.back
import electricitymeterhelper.sharedui.generated.resources.edit
import electricitymeterhelper.sharedui.generated.resources.export
import electricitymeterhelper.sharedui.generated.resources.ic_back
import electricitymeterhelper.sharedui.generated.resources.ic_edit
import electricitymeterhelper.sharedui.generated.resources.ic_export
import electricitymeterhelper.sharedui.generated.resources.info_screen_title
import electricitymeterhelper.sharedui.generated.resources.no_reading_entries
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun InfoScreen(
    navigateBack: () -> Unit,
    navigateToReading: () -> Unit,
    viewModel: InfoViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SnackbarMessageHandler(uiState.errorMessage)

    Content(
        uiState = uiState,
        onBack = navigateBack,
        onEdit = navigateToReading,
        onExport = { TODO() },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun Content(
    uiState: InfoScreenUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            InfoTopBar(
                date = uiState.data?.date,
                onBack = onBack,
                onEdit = onEdit,
                onExport = onExport,
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                LoadingIndicator(Modifier.align(Alignment.Center))
            }

            uiState.data?.entries.isNullOrEmpty() -> Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(Res.string.no_reading_entries),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) {
                items(
                    items = uiState.data.entries,
                    key = { it.apartment }
                ) { entry ->
                    InfoListItem(
                        item = entry,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoTopBar(
    date: LocalDate?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onExport: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    val titleText = remember(date) { date?.format(DateFormat) }
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {
            Text(text = titleText ?: stringResource(Res.string.info_screen_title))
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(Res.drawable.ic_back),
                    contentDescription = stringResource(Res.string.back)
                )
            }
        },
        actions = {
            IconButton(
                onClick = onEdit,
                enabled = date != null
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_edit),
                    contentDescription = stringResource(Res.string.edit)
                )
            }
            IconButton(
                onClick = onExport,
                enabled = date != null
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_export),
                    contentDescription = stringResource(Res.string.export)
                )
            }
        }
    )
}

@Composable
private fun InfoListItem(
    item: ReadingEntry,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Surface(
            color = if (item.currentValue > item.previousValue) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.errorContainer
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = item.apartment.name)

                Text(text = item.previousValue.toString())

                Text(text = item.currentValue.toString())
            }
        }
        HorizontalDivider()
    }
}