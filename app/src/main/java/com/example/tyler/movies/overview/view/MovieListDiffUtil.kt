package com.example.tyler.movies.overview.view

import androidx.recyclerview.widget.DiffUtil
import com.example.tyler.movies.overview.model.MovieOverviewModel

class MovieListDiffUtil(val old: List<MovieOverviewModel>?, val new: List<MovieOverviewModel>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (old == null) return false
        return old[oldItemPosition] == new[newItemPosition]
    }

    override fun getOldListSize(): Int {
        if(old == null) return 0
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (old == null) return false
        return old[oldItemPosition].equals(new[newItemPosition])
    }
}