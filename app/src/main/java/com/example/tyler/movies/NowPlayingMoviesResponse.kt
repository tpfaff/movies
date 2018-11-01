package com.example.tyler.movies

data class NowPlayingMoviesResponse(
    val dates: Dates,
    val page: Int,
    val results: List<MovieModel>,
    val total_pages: Int,
    val total_results: Int
)