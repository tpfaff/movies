package com.example.tyler.movies.overview.view

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

/**
 * Copyright (c) 2018 Pandora Media, Inc.
 */
class GridLayoutManagerFixed(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount){
    /**
     * Disable predictive animations. There is a bug in RecyclerView which causes views that
     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
     * adapter size has decreased since the ViewHolder was recycled.
     */
    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}