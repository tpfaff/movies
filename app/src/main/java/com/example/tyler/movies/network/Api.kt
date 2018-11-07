package com.example.tyler.movies.network

import com.example.tyler.movies.detail.model.MovieDetailModel
import com.example.tyler.movies.overview.model.NowPlayingMoviesResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Api() {

    val API_KEY = "58817998765aa2e66d9a930487c77b7b"
    val service: ApiService

    init {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor { original ->
                val url = original.request().url().newBuilder().addQueryParameter("api_key", API_KEY).build()
                val authenticatedRequest = original.request().newBuilder().url(url).build()
                original.proceed(authenticatedRequest)
            }
            .addInterceptor(loggingInterceptor)
            .build()


        val retrofit = Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/")
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build()

        service = retrofit.create(ApiService::class.java)

    }


    fun getNowPlayingMovies(): Observable<NowPlayingMoviesResponse> {
        return service.getNowPlayingMovies()
    }
    
    fun getMovieDetails(movieId: String): Observable<MovieDetailModel> {
        return service.getMovieDetails(movieId)
    }
}