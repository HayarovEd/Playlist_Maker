package com.example.playlistmakerfirstproject.audioplayer.presentation.ui.settings.impl

import com.example.playlistmakerfirstproject.audioplayer.data.setting.settings.SettingsRepository
import com.example.playlistmakerfirstproject.audioplayer.presentation.ui.settings.SettingsInteractor
import com.example.playlistmakerfirstproject.audioplayer.presentation.ui.settings.model.ThemeSettings

class SettingsInteractorImpl(
    private val settingRepository: SettingsRepository
) : SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return settingRepository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingRepository.updateThemeSetting(settings)
    }
}