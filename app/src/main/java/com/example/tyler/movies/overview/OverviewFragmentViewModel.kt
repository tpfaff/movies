package com.example.tyler.movies.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tyler.movies.overview.model.UiState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class OverviewFragmentViewModel : ViewModel(){

    val uiStateChanged = PublishSubject.create<UiState>()
    val model = OverviewFragmentRepo()

    companion object {
        val TAG = OverviewFragmentViewModel::class.java.simpleName
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun loadMovies(){
        uiStateChanged.onNext(UiState.Loading())
        model.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response ->
                uiStateChanged.onNext(UiState.ListReady(response.results))
            }, { error ->
                uiStateChanged.onNext(UiState.Error())
                Log.e(TAG, error.message, error)
            })
    }
}