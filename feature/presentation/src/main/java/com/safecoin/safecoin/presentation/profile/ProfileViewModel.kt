package com.safecoin.safecoin.presentation.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.safecoin.safecoin.domain.model.AppLanguage
import com.safecoin.safecoin.domain.model.AppSettings
import com.safecoin.safecoin.domain.model.ThemeMode
import com.safecoin.safecoin.domain.model.UserProfile
import com.safecoin.safecoin.domain.repository.SettingsRepository
import com.safecoin.safecoin.presentation.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val settings: AppSettings = AppSettings(ThemeMode.SYSTEM, AppLanguage.ENGLISH, true),
    val profile: UserProfile = UserProfile("", "", ""),
    val editName: String = "",
    val editEmail: String = "",
    val editPhone: String = "",
    val isEditing: Boolean = false,
    @StringRes val savedMessageRes: Int? = null,
)

class ProfileViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {

    private val editing = MutableStateFlow(false)
    private val draft = MutableStateFlow(Triple("", "", ""))
    private val message = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.observeSettings(),
        repository.observeProfile(),
        editing,
        draft,
        message,
    ) { settings, profile, isEditing, draftValues, savedMsg ->
        ProfileUiState(
            settings = settings,
            profile = profile,
            editName = if (isEditing) draftValues.first else profile.name,
            editEmail = if (isEditing) draftValues.second else profile.email,
            editPhone = if (isEditing) draftValues.third else profile.phone,
            isEditing = isEditing,
            savedMessageRes = savedMsg,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun startEditing() {
        val profile = uiState.value.profile
        draft.value = Triple(profile.name, profile.email, profile.phone)
        editing.value = true
    }

    fun cancelEditing() {
        editing.value = false
    }

    fun onNameChange(value: String) {
        draft.update { Triple(value, it.second, it.third) }
    }

    fun onEmailChange(value: String) {
        draft.update { Triple(it.first, value, it.third) }
    }

    fun onPhoneChange(value: String) {
        draft.update { Triple(it.first, it.second, value) }
    }

    fun saveProfile() {
        val draftValues = draft.value
        viewModelScope.launch {
            repository.updateProfile(
                UserProfile(draftValues.first, draftValues.second, draftValues.third),
            )
            editing.value = false
            message.value = R.string.profile_saved
        }
    }

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch { repository.updateTheme(theme) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { repository.updateLanguage(language) }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { repository.updateNotifications(enabled) }
    }

    fun clearMessage() {
        message.value = null
    }

    companion object {
        fun factory(repository: SettingsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ProfileViewModel(repository) as T
            }
    }
}
