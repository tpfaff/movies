package com.example.tyler.movies.overview.model

data class NowPlayingMoviesResponse(
    val dates: Dates,
    val page: Int,
    val results: List<MovieOverviewModel>,
    val total_pages: Int,
    val total_results: Int
)