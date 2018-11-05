package com.example.tyler.movies.detail.model

import com.example.tyler.movies.Api
import io.reactivex.Observable

/**
 * Copyright (c) 2018 Pandora Media, Inc.
 */
class DetailFragmentRepo {
    val api = Api()
    fun getMovieDetail(id: String): Observable<MovieDetailModel> {
        return api.getMovieDetails(id)
    }
}