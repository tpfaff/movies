package com.example.tyler.movies.overview

import com.example.tyler.movies.Api
import com.example.tyler.movies.NowPlayingMoviesResponse
import io.reactivex.Observable
import retrofit2.Call

class OverviewFragmentRepo {
    val api = Api()
    fun getMovies(): Observable<NowPlayingMoviesResponse> {
        return api.getNowPlayingMovies()
    }
}