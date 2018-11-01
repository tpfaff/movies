package com.example.tyler.movies.overview.model

import com.example.tyler.movies.MovieModel

sealed class UiState() {
    class Loading : UiState()
    class ListReady(val movies: List<MovieModel>) : UiState()
    class Error : UiState()
}