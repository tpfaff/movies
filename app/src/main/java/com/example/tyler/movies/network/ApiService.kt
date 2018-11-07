package com.example.tyler.movies.network

import com.example.tyler.movies.detail.model.MovieDetailModel
import com.example.tyler.movies.overview.model.NowPlayingMoviesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService{
    @GET("movie/now_playing")
    fun getNowPlayingMovies() : Observable<NowPlayingMoviesResponse>
    
    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId : String) : Observable<MovieDetailModel>
}