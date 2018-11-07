package com.example.tyler.movies.overview.model

import com.example.tyler.movies.network.Api
import io.reactivex.Observable

class OverviewFragmentRepo {
    val api = Api()
    var allMovies : List<MovieOverviewModel>? = null
    lateinit var currentFilterList : List<MovieOverviewModel>
    var oldFilteredList: List<MovieOverviewModel>? = null
    var currentSearchTerm: String? = null

    fun getMovies(): Observable<NowPlayingMoviesResponse> {
        return api.getNowPlayingMovies()
    }
}