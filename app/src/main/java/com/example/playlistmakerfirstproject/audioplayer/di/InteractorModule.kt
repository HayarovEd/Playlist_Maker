package com.example.playlistmakerfirstproject.audioplayer.di

import com.example.playlistmakerfirstproject.audioplayer.domain.api.TrackInteractor
import com.example.playlistmakerfirstproject.audioplayer.domain.db.FavouriteInteractor
import com.example.playlistmakerfirstproject.audioplayer.domain.history.HistoryInteractor
import com.example.playlistmakerfirstproject.audioplayer.domain.history.impl.HistoryInteractorImpl
import com.example.playlistmakerfirstproject.audioplayer.domain.impl.FavouriteInteractorImpl
import com.example.playlistmakerfirstproject.audioplayer.domain.impl.TrackInteractorImpl
import com.example.playlistmakerfirstproject.audioplayer.domain.m_navigation.InternalNavigationInteractor
import com.example.playlistmakerfirstproject.audioplayer.domain.m_navigation.impl.InternalNavigationInteractorImpl
import com.example.playlistmakerfirstproject.audioplayer.domain.player.AudioPlayerInteractor
import com.example.playlistmakerfirstproject.audioplayer.domain.player.impl.AudioPlayerInteractorImpl
import com.example.playlistmakerfirstproject.audioplayer.presentation.ui.settings.SettingsInteractor
import com.example.playlistmakerfirstproject.audioplayer.presentation.ui.settings.impl.SettingsInteractorImpl
import com.example.playlistmakerfirstproject.audioplayer.domain.setting.sharing.SharingInteractor
import com.example.playlistmakerfirstproject.audioplayer.domain.setting.sharing.impl.SharingInteractorImpl
import com.google.gson.Gson
import org.koin.dsl.module

val interactorModule = module {
    single<FavouriteInteractor.FavouriteInteractor> {
        FavouriteInteractorImpl(get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(get(), get())
    }

    single<InternalNavigationInteractor> {
        InternalNavigationInteractorImpl(get())
    }

    single<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory { Gson() }


}