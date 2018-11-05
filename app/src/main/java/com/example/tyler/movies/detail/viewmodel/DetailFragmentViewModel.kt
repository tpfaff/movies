package com.example.tyler.movies.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tyler.movies.detail.model.DetailFragmentRepo
import com.example.tyler.movies.overview.model.UiState
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Copyright (c) 2018 Pandora Media, Inc.
 */
class DetailFragmentViewModel : ViewModel() {
    private val repo = DetailFragmentRepo()
    val uiStateChanged = BehaviorSubject.create<UiState>()

    companion object {
        val TAG = DetailFragmentViewModel::class.java.simpleName
    }

    fun getDetails(id: String) {
        uiStateChanged.onNext(UiState.Loading())
        repo.getMovieDetail(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { details -> uiStateChanged.onNext(UiState.DetailsReady(details)) },
                { error -> 
                    uiStateChanged.onNext(UiState.Error())
                    Log.e(TAG, "Couldn't get movie details", error) })
    }
}