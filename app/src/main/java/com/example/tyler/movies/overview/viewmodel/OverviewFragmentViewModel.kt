package com.example.tyler.movies.overview.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.example.tyler.movies.overview.model.OverviewFragmentRepo
import com.example.tyler.movies.overview.model.UiState
import com.example.tyler.movies.overview.view.MovieListDiffUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject


class OverviewFragmentViewModel : ViewModel() {

    val uiStateChanged = BehaviorSubject.create<UiState>()
    val repo = OverviewFragmentRepo()
    val allSubscriptions = CompositeDisposable()

    companion object {
        val TAG = OverviewFragmentViewModel::class.java.simpleName
    }

    fun loadMovies() {
        uiStateChanged.onNext(UiState.Loading())

        allSubscriptions.add(repo.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                val diffResult = DiffUtil.calculateDiff(MovieListDiffUtil(repo.allMovies, response.results))
                repo.allMovies = response.results
                uiStateChanged.onNext(UiState.ListReady(response.results, diffResult, repo.currentSearchTerm))
            }, { error ->
                uiStateChanged.onNext(UiState.Error())
                Log.e(TAG, error.message, error)
            }))
    }

    fun filterMovies(title: String) {
        //save the current search term so we can restore it upon rotation
        repo.currentSearchTerm = title

        repo.allMovies?.let {
            //filter the list of all movies into a list of movies whose title starts with the search term
            repo.currentFilterList = it.filter { item ->
                item.title.startsWith(title, ignoreCase = true) || title.isEmpty()
            }

            // If there isn't a previous list of filtered movies, then the list of all movies will do.
            if(repo.oldFilteredList == null){
                repo.oldFilteredList = repo.allMovies
            }

            //diff the old list with the new list
            val diffResult = DiffUtil.calculateDiff(MovieListDiffUtil(repo.oldFilteredList, repo.currentFilterList))
            repo.oldFilteredList = repo.currentFilterList

            // Update the ui state with the newly filtered list
            // also emit the search term so we can restore it upon rotation
            // by way of the BehaviorSubject's caching mechanism
            uiStateChanged.onNext(UiState.ListReady(repo.currentFilterList, diffResult, repo.currentSearchTerm))
        }
    }


    override fun onCleared() {
        super.onCleared()
        allSubscriptions.clear()
    }
}