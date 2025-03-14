package com.example.playlistmakerfirstproject.audioplayer.presentation.audioPlayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmakerfirstproject.audioplayer.domain.db.FavouriteInteractor
import com.example.playlistmakerfirstproject.audioplayer.domain.models.State
import com.example.playlistmakerfirstproject.audioplayer.domain.models.Track
import com.example.playlistmakerfirstproject.audioplayer.domain.player.AudioPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    private val audioPlayerInterator: AudioPlayerInteractor,
    private val favouriteInterator: FavouriteInteractor.FavouriteInteractor,
) : ViewModel() {

    private var timerJob: Job? = null
    private var statePlayerLiveData = MutableLiveData(State.PREPARED)

    fun getStatePlayerLiveData(): LiveData<State> = statePlayerLiveData

    private var currentTimerLiveData = MutableLiveData(0)

    fun getCurrentTimerLiveData(): LiveData<Int> = currentTimerLiveData
    private var isFavourite = MutableLiveData<Boolean>()
    fun getIsFavourite(): LiveData<Boolean> = isFavourite


    fun preparePlayer(url: String) {
        audioPlayerInterator.preparePlayer(url) { state ->
            when (state) {
                State.PREPARED, State.DEFAULT -> {
                    statePlayerLiveData.postValue(State.PREPARED)
                    timerJob?.cancel()
                    currentTimerLiveData.postValue(TIMER_START)
                }

                else -> Unit
            }
        }
    }
    suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean {
        val favIndicatorsDeferred = viewModelScope.async {
            favouriteInterator.getIdOfFavouriteTracks()
        }
        val favIndicators: Flow<List<Int>> = favouriteInterator.getIdOfFavouriteTracks()

        val favIndicatorsList: MutableList<Int> = mutableListOf()

        favIndicators.collect { list ->
            favIndicatorsList.addAll(list)
        }

        return favIndicatorsList.contains(trackId)
    }

    private fun startTimer(state: State) {
        timerJob = viewModelScope.launch {
            while (state == State.PLAYING) {
                delay(DELAY_UPDATE_TIMER_MC)
                currentTimerLiveData.postValue(audioPlayerInterator.currentPosition())
            }
        }
        if (state == State.PREPARED) {
            timerJob?.cancel()
            currentTimerLiveData.postValue(TIMER_START)
        }
    }
    suspend fun onFavoriteClicked(track: Track) {
        if (track.isFavorite) {
            audioPlayerInterator.deleteTrackFromFav(track)
            isFavourite.postValue(false)
            track.isFavorite=false

        }
        else {
            audioPlayerInterator.addTrackToFav(track)
            isFavourite.postValue(true)
            track.isFavorite=true
        }
    }

    fun changePlayerState() {
        audioPlayerInterator.switchPlayer { state ->
            when (state) {
                State.PLAYING -> {
                    startTimer(State.PLAYING)
                    currentTimerLiveData.postValue(audioPlayerInterator.currentPosition())
                    statePlayerLiveData.postValue(State.PLAYING)
                }

                State.PAUSED -> {
                    statePlayerLiveData.postValue(State.PAUSED)
                }

                State.PREPARED -> {
                    timerJob?.cancel()
                    startTimer(State.PREPARED)
                    statePlayerLiveData.postValue(State.PREPARED)
                    currentTimerLiveData.postValue(TIMER_START)

                }

                State.DEFAULT -> {
                    timerJob?.cancel()
                    statePlayerLiveData.postValue(State.DEFAULT)
                }
            }
        }
    }

    fun onPause() {
        statePlayerLiveData.postValue(State.PAUSED)
        audioPlayerInterator.pausePlayer()

    }

    fun onResume() {
        statePlayerLiveData.postValue(State.PAUSED)

    }

    companion object {
        const val TIMER_START = 0
        const val DELAY_UPDATE_TIMER_MC = 300L

    }
}