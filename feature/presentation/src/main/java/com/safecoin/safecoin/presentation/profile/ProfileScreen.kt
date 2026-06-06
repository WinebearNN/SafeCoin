package com.safecoin.safecoin.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.safecoin.safecoin.design.components.SectionHeader
import com.safecoin.safecoin.domain.model.AppLanguage
import com.safecoin.safecoin.domain.model.ThemeMode
import com.safecoin.safecoin.presentation.R
import com.safecoin.safecoin.presentation.util.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.savedMessageRes) {
        uiState.savedMessageRes?.let { resId ->
            snackbarHostState.showSnackbar(context.getString(resId))
            viewModel.clearMessage()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionHeader(title = stringResource(R.string.profile))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                Text(
                    text = stringResource(R.string.personal_data),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                if (uiState.isEditing) {
                    OutlinedTextField(
                        value = uiState.editName,
                        onValueChange = viewModel::onNameChange,
                        label = { Text(stringResource(R.string.name)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = uiState.editEmail,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text(stringResource(R.string.email)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = uiState.editPhone,
                        onValueChange = viewModel::onPhoneChange,
                        label = { Text(stringResource(R.string.phone)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = viewModel::saveProfile) {
                            Text(stringResource(R.string.save))
                        }
                        TextButton(onClick = viewModel::cancelEditing) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                } else {
                    ProfileField(stringResource(R.string.name), uiState.profile.name)
                    ProfileField(stringResource(R.string.email), uiState.profile.email)
                    ProfileField(stringResource(R.string.phone), uiState.profile.phone)
                    TextButton(onClick = viewModel::startEditing) {
                        Text(stringResource(R.string.edit_profile))
                    }
                }

                HorizontalDivider()

                Text(
                    text = stringResource(R.string.appearance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                ThemeDropdown(
                    selected = uiState.settings.darkTheme,
                    onSelected = viewModel::setTheme,
                )

                LanguageDropdown(
                    selected = uiState.settings.language,
                    onSelected = viewModel::setLanguage,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.notifications))
                    Switch(
                        checked = uiState.settings.notificationsEnabled,
                        onCheckedChange = viewModel::setNotifications,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeDropdown(
    selected: ThemeMode,
    onSelected: (ThemeMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val options = ThemeMode.entries

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.label(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.theme)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label()) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageDropdown(
    selected: AppLanguage,
    onSelected: (AppLanguage) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.label(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.language)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AppLanguage.entries.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.label()) },
                    onClick = {
                        onSelected(language)
                        expanded = false
                    },
                )
            }
        }
    }
}
