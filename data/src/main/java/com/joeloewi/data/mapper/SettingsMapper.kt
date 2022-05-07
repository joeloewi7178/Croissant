package com.joeloewi.data.mapper

import com.joeloewi.domain.entity.Settings

class SettingsMapper {
    fun toDomain(settings: com.joeloewi.data.Settings): Settings = Settings(
        darkThemeEnabled = settings.darkThemeEnabled,
        isFirstLaunch = settings.isFirstLaunch
    )
}