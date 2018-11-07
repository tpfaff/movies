package com.example.tyler.movies.detail.model

import com.example.tyler.movies.network.Api
import io.reactivex.Observable

/**
 * Copyright (c) 2018 Pandora Media, Inc.
 */
class DetailFragmentRepo {
    private val api = Api()
    fun getMovieDetail(id: String): Observable<MovieDetailModel> {
        return api.getMovieDetails(id)
    }
}