package com.example.tyler.movies.overview.model

import androidx.recyclerview.widget.DiffUtil
import com.example.tyler.movies.detail.model.MovieDetailModel

sealed class UiState() {
    class Loading : UiState()
    class ListReady(val movies: List<MovieOverviewModel>, val diffResult: DiffUtil.DiffResult, val currentSearch: String?) : UiState()
    class Error : UiState()
    class DetailsReady(val details : MovieDetailModel) : UiState()
}