package com.example.tyler.movies.overview.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tyler.movies.overview.model.MovieOverviewModel
import com.example.tyler.movies.overview.model.OverviewFragmentRepo
import com.example.tyler.movies.overview.model.UiState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.UnicastSubject
import androidx.recyclerview.widget.DiffUtil
import com.example.tyler.movies.overview.view.MovieListDiffUtil


class OverviewFragmentViewModel : ViewModel() {

    val uiStateChanged = BehaviorSubject.create<UiState>()
    val repo = OverviewFragmentRepo()

    companion object {
        val TAG = OverviewFragmentViewModel::class.java.simpleName
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun loadMovies() {
        uiStateChanged.onNext(UiState.Loading())

        repo.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                val diffResult = DiffUtil.calculateDiff(MovieListDiffUtil(repo.allMovies, response.results))
                repo.allMovies = response.results
                uiStateChanged.onNext(UiState.ListReady(response.results, diffResult, repo.currentSearchTerm))
            }, { error ->
                uiStateChanged.onNext(UiState.Error())
                Log.e(TAG, error.message, error)
            })
    }

    fun filterMovies(title: String) {
        //save the current search term so we can restore it upon rotation
        repo.currentSearchTerm = title
        repo.allMovies?.let {
            repo.currentFilterList = it.filter { item ->
                item.title.startsWith(title, ignoreCase = true) || title.isEmpty()
            }
            if(repo.oldFilteredList == null){
                repo.oldFilteredList = repo.allMovies
            }
            val diffResult = DiffUtil.calculateDiff(MovieListDiffUtil(repo.oldFilteredList, repo.currentFilterList!!))
            repo.oldFilteredList = repo.currentFilterList
            uiStateChanged.onNext(UiState.ListReady(repo.currentFilterList!!, diffResult, repo.currentSearchTerm))
        }

    }
}