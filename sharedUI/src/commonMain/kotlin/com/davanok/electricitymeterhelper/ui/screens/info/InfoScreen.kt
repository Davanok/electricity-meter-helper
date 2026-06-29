package com.davanok.electricitymeterhelper.ui.screens.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.electricitymeterhelper.domain.ReadingEntry
import com.davanok.electricitymeterhelper.ui.components.SnackbarMessageHandler
import com.davanok.electricitymeterhelper.utils.DateFormat
import electricitymeterhelper.sharedui.generated.resources.Res
import electricitymeterhelper.sharedui.generated.resources.back
import electricitymeterhelper.sharedui.generated.resources.current
import electricitymeterhelper.sharedui.generated.resources.delta_down
import electricitymeterhelper.sharedui.generated.resources.delta_up
import electricitymeterhelper.sharedui.generated.resources.delta_zero
import electricitymeterhelper.sharedui.generated.resources.edit
import electricitymeterhelper.sharedui.generated.resources.export
import electricitymeterhelper.sharedui.generated.resources.ic_back
import electricitymeterhelper.sharedui.generated.resources.ic_edit
import electricitymeterhelper.sharedui.generated.resources.ic_export
import electricitymeterhelper.sharedui.generated.resources.info_screen_title
import electricitymeterhelper.sharedui.generated.resources.no_reading_entries
import electricitymeterhelper.sharedui.generated.resources.previous_value
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
        onExport = viewModel::saveFile,
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            InfoTopBar(
                date = uiState.data?.date,
                onBack = onBack,
                onEdit = onEdit,
                onExport = onExport,
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->

        when {
            uiState.isLoading -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            uiState.data?.entries.isNullOrEmpty() -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_reading_entries),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = uiState.data.entries,
                        key = { it.apartment }
                    ) { entry ->
                        InfoCard(item = entry)
                    }
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
private fun InfoCard(
    item: ReadingEntry,
    modifier: Modifier = Modifier
) {
    val delta = item.currentValue - item.previousValue

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.apartment.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                DeltaIndicator(delta = delta)
            }

            // Values row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ValueBlock(
                    label = stringResource(Res.string.previous_value),
                    value = item.previousValue.toString()
                )

                ValueBlock(
                    label = stringResource(Res.string.current),
                    value = item.currentValue.toString()
                )
            }
        }
    }
}
@Composable
private fun DeltaIndicator(delta: Int) {
    val (text, color) = when {
        delta > 0 -> stringResource(Res.string.delta_up, delta) to MaterialTheme.colorScheme.primary
        delta < 0 -> stringResource(Res.string.delta_down, delta) to MaterialTheme.colorScheme.error
        else -> stringResource(Res.string.delta_zero) to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun ValueBlock(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge
        )
    }
}