package com.davanok.electricitymeterhelper.ui.screens.reading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.electricitymeterhelper.domain.ReadingEntry
import com.davanok.electricitymeterhelper.ui.components.SnackbarMessageHandler
import electricitymeterhelper.sharedui.generated.resources.Res
import electricitymeterhelper.sharedui.generated.resources.back
import electricitymeterhelper.sharedui.generated.resources.confirm
import electricitymeterhelper.sharedui.generated.resources.finish_text
import electricitymeterhelper.sharedui.generated.resources.go_home_button_text
import electricitymeterhelper.sharedui.generated.resources.go_info_button_text
import electricitymeterhelper.sharedui.generated.resources.ic_back
import electricitymeterhelper.sharedui.generated.resources.ic_check
import electricitymeterhelper.sharedui.generated.resources.previous_value
import electricitymeterhelper.sharedui.generated.resources.reading_data
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
fun ReadingScreen(
    navigateToHome: () -> Unit,
    navigateToInfo: (entryId: Uuid) -> Unit,
    viewModel: ReadingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SnackbarMessageHandler(uiState.errorMessage)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveData()
        }
    }

    Content(
        uiState = uiState,
        moveToItem = viewModel::moveToItem,
        setValue = { index, value ->
            viewModel.setReadingValue(index, value)
            viewModel.moveToItem(index + 1)
        },
        goToInfo = { viewModel.saveData { navigateToInfo(it) } },
        goToHome = { viewModel.saveData { navigateToHome() } },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun Content(
    uiState: ReadingScreenUiState,
    moveToItem: (Int) -> Unit,
    setValue: (Int, Int) -> Unit,
    goToHome: () -> Unit,
    goToInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.reading_data)) },
                navigationIcon = {
                    IconButton(onClick = goToHome) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
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

            uiState.currentReadingEntryIndex > uiState.entries.lastIndex -> FinishContent(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                goToHome = goToHome,
                goToInfo = goToInfo
            )

            else -> Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                ApartmentIndicator(
                    apartments = uiState.entries,
                    currentIndex = uiState.currentReadingEntryIndex,
                    moveToItem = moveToItem,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.padding(12.dp))

                ApartmentEditable(
                    info = uiState.entries[uiState.currentReadingEntryIndex],
                    onSetValue = { setValue(uiState.currentReadingEntryIndex, it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ApartmentIndicator(
    apartments: List<ReadingEntry>,
    currentIndex: Int,
    moveToItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyRowState = rememberLazyListState()
    LaunchedEffect(currentIndex) {
        lazyRowState.requestScrollToItem(currentIndex)
    }
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        itemsIndexed(
            items = apartments,
            key = { _, a -> a.apartment.name }
        ) { i, a ->
            AssistChip(
                onClick = { moveToItem(i) },
                label = { Text(a.apartment.name) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = when {
                        a.currentValue == 0 -> Color.Unspecified
                        a.currentValue < a.previousValue -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                )
            )
        }
    }
}

@Composable
private fun ApartmentEditable(
    info: ReadingEntry,
    onSetValue: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var value by remember { mutableStateOf(info.currentValue) }

    LaunchedEffect(info.currentValue) {
        value = info.currentValue
    }

    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = info.apartment.name,
                style = MaterialTheme.typography.labelMedium
            )
            if (info.apartment.owner.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(text = info.apartment.owner)
            }
        }

        if (info.previousValue > 0) {
            Spacer(Modifier.height(8.dp))
            Text(text = stringResource(Res.string.previous_value, info.previousValue))
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value.toString(),
            onValueChange = { v -> v.toIntOrNull()?.takeIf { it > 0 }?.let { value = it } },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions { onSetValue(value) },
            trailingIcon = {
                IconButton(onClick = { onSetValue(value) }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_check),
                        contentDescription = stringResource(Res.string.confirm)
                    )
                }
            }
        )
    }
}

@Composable
private fun FinishContent(
    goToHome: () -> Unit,
    goToInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_check),
            contentDescription = null
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.finish_text),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = goToHome) {
                Text(text = stringResource(Res.string.go_home_button_text))
            }
            TextButton(onClick = goToInfo) {
                Text(text = stringResource(Res.string.go_info_button_text))
            }
        }
    }
}