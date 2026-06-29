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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.electricitymeterhelper.domain.ReadingEntry
import com.davanok.electricitymeterhelper.ui.components.SnackbarMessageHandler
import electricitymeterhelper.sharedui.generated.resources.Res
import electricitymeterhelper.sharedui.generated.resources.all_done
import electricitymeterhelper.sharedui.generated.resources.back
import electricitymeterhelper.sharedui.generated.resources.confirm
import electricitymeterhelper.sharedui.generated.resources.enter_kwh
import electricitymeterhelper.sharedui.generated.resources.finish_text
import electricitymeterhelper.sharedui.generated.resources.go_home_button_text
import electricitymeterhelper.sharedui.generated.resources.go_info_button_text
import electricitymeterhelper.sharedui.generated.resources.ic_back
import electricitymeterhelper.sharedui.generated.resources.ic_check
import electricitymeterhelper.sharedui.generated.resources.lower_than_previous_warning
import electricitymeterhelper.sharedui.generated.resources.new_reading
import electricitymeterhelper.sharedui.generated.resources.previous_value
import electricitymeterhelper.sharedui.generated.resources.reading_data
import electricitymeterhelper.sharedui.generated.resources.save_anyway
import electricitymeterhelper.sharedui.generated.resources.skip
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

// ---------------------------------------------------------------------------
// Entry point
// ---------------------------------------------------------------------------

@Composable
fun ReadingScreen(
    navigateBack: () -> Unit,
    navigateToInfo: (entryId: Uuid) -> Unit,
    viewModel: ReadingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SnackbarMessageHandler(uiState.errorMessage)

    DisposableEffect(Unit) {
        onDispose { viewModel.saveData() }
    }

    Content(
        uiState = uiState,
        moveToItem = viewModel::moveToItem,
        setValue = { index, value ->
            viewModel.setReadingValue(index, value)
            viewModel.moveToItem(index + 1)
        },
        goToInfo = { viewModel.saveData { navigateToInfo(it) } },
        goBack = { viewModel.saveData { navigateBack() } },
        modifier = Modifier.fillMaxSize()
    )
}

// ---------------------------------------------------------------------------
// Root scaffold
// ---------------------------------------------------------------------------

@Composable
private fun Content(
    uiState: ReadingScreenUiState,
    moveToItem: (Int) -> Unit,
    setValue: (Int, Int) -> Unit,
    goBack: () -> Unit,
    goToInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.reading_data)) },
                navigationIcon = {
                    IconButton(onClick = goBack) {
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
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }

            uiState.currentReadingEntryIndex > uiState.entries.lastIndex -> FinishContent(
                totalCount = uiState.entries.size,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                goToHome = goBack,
                goToInfo = goToInfo
            )

            else -> ReadingContent(
                uiState = uiState,
                moveToItem = moveToItem,
                setValue = setValue,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Main reading content (chips + progress + editable card)
// ---------------------------------------------------------------------------

@Composable
private fun ReadingContent(
    uiState: ReadingScreenUiState,
    moveToItem: (Int) -> Unit,
    setValue: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentIndex = uiState.currentReadingEntryIndex
    val totalCount = uiState.entries.size
    val currentEntry = uiState.entries[currentIndex]
    val isValueSuspect = currentEntry.currentValue > 0 &&
            currentEntry.currentValue < currentEntry.previousValue

    Column(modifier = modifier) {

        // Chip strip
        ApartmentIndicator(
            apartments = uiState.entries,
            currentIndex = currentIndex,
            moveToItem = moveToItem,
            modifier = Modifier.fillMaxWidth()
        )

        // Progress row
        ProgressRow(
            current = currentIndex + 1,
            total = totalCount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Editable card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            ApartmentEditable(
                info = currentEntry,
                isValueSuspect = isValueSuspect,
                onSetValue = { setValue(currentIndex, it) },
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Bottom action row
        BottomActions(
            isValueSuspect = isValueSuspect,
            onSkip = { moveToItem(currentIndex + 1) },
            onConfirm = {
                // Trigger confirm via the editable card's latest value;
                // the actual value is read from shared state in the ViewModel.
                moveToItem(currentIndex + 1)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(16.dp))
    }
}

// ---------------------------------------------------------------------------
// Progress indicator
// ---------------------------------------------------------------------------

@Composable
private fun ProgressRow(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LinearProgressIndicator(
            progress = { current.toFloat() / total.toFloat() },
            modifier = Modifier
                .weight(1f)
                .height(6.dp),
            strokeCap = StrokeCap.Round
        )
        Text(
            text = "$current / $total",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Chip row
// ---------------------------------------------------------------------------

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
        modifier = modifier.padding(horizontal = 8.dp),
        state = lazyRowState,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        itemsIndexed(
            items = apartments,
            key = { _, a -> a.apartment.name }
        ) { i, a ->
            val isSelected = i == currentIndex
            val isSuspect = a.currentValue > 0 && a.currentValue < a.previousValue
            val isDone = a.currentValue > 0 && !isSuspect

            val chipColors = when {
                isSuspect -> FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                )

                isDone -> FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )

                else -> FilterChipDefaults.filterChipColors()
            }

            FilterChip(
                selected = isSelected || isDone || isSuspect,
                onClick = { moveToItem(i) },
                label = { Text(a.apartment.name) },
                colors = chipColors,
                border = if (isSelected)
                    FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = true,
                        borderColor = MaterialTheme.colorScheme.primary,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        selectedBorderWidth = 2.dp
                    )
                else FilterChipDefaults.filterChipBorder(enabled = true, selected = false)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Apartment editable card
// ---------------------------------------------------------------------------

@Composable
private fun ApartmentEditable(
    info: ReadingEntry,
    isValueSuspect: Boolean,
    onSetValue: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var rawValue by remember(info.apartment.name) {
        mutableStateOf(if (info.currentValue > 0) info.currentValue.toString() else "")
    }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(info.currentValue) {
        if (!isFocused) {
            rawValue = if (info.currentValue > 0) info.currentValue.toString() else ""
        }
    }

    val parsedValue = rawValue.toIntOrNull()

    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header: apartment name + owner
            Text(
                text = info.apartment.name,
                style = MaterialTheme.typography.titleMedium
            )
            if (info.apartment.owner.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = info.apartment.owner,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            // Previous reading row
            if (info.previousValue > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.previous_value),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = info.previousValue.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // New reading input
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        // Select-all on focus: done via TextFieldValue if needed;
                        // here we clear to let the user type fresh
                    },
                value = rawValue,
                onValueChange = { input ->
                    // Allow digits only; accept empty (cleared field)
                    if (input.isEmpty() || input.toIntOrNull() != null) {
                        rawValue = input
                    }
                },
                label = { Text(text = stringResource(Res.string.new_reading)) },
                placeholder = { Text(stringResource(Res.string.enter_kwh)) },
                isError = isValueSuspect,
                supportingText = if (isValueSuspect) {
                    {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_check), // replace with warning icon
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = stringResource(Res.string.lower_than_previous_warning),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else null,
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                    parsedValue?.let { onSetValue(it) }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { parsedValue?.let { onSetValue(it) } },
                        enabled = parsedValue != null
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_check),
                            contentDescription = stringResource(Res.string.confirm)
                        )
                    }
                },
                singleLine = true
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Bottom action row
// ---------------------------------------------------------------------------

@Composable
private fun BottomActions(
    isValueSuspect: Boolean,
    onSkip: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onSkip,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(Res.string.skip))
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(2f),
            colors = if (isValueSuspect)
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            else ButtonDefaults.buttonColors()
        ) {
            Text(if (isValueSuspect) stringResource(Res.string.save_anyway) else stringResource(Res.string.confirm))
        }
    }
}

// ---------------------------------------------------------------------------
// Finish screen
// ---------------------------------------------------------------------------

@Composable
private fun FinishContent(
    totalCount: Int,
    goToHome: () -> Unit,
    goToInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success icon in a tinted circle
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(Res.drawable.ic_check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(Res.string.all_done),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.finish_text, totalCount),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = goToInfo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.go_info_button_text))
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = goToHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.go_home_button_text))
        }
    }
}