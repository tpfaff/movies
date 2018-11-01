package com.example.tyler.movies

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET

interface ApiService{
    @GET("movie/now_playing")
    fun getNowPlayingMovies() : Observable<NowPlayingMoviesResponse>
}