package com.example.tyler.movies.overview


import android.graphics.Movie
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tyler.movies.MovieModel

import com.example.tyler.movies.R
import com.example.tyler.movies.overview.model.UiState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.movie_item.*
import kotlinx.android.synthetic.main.movie_item.view.*


class OverviewFragment : Fragment() {


    private lateinit var viewModel: OverviewFragmentViewModel
    val allSubscriptions = CompositeDisposable()

    companion object {
        val TAG = OverviewFragment::class.java.simpleName
        fun newInstance(): OverviewFragment {
            return OverviewFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OverviewFragmentViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allSubscriptions.add(viewModel.uiStateChanged
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ uiState ->
                when (uiState) {
                    is UiState.Loading -> showLoadingView()
                    is UiState.ListReady -> showList(uiState)
                    is UiState.Error -> showErrorView()
                }
            }, { error ->
                Log.e(TAG, error.message, error)
            })
        )
        viewModel.loadMovies()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        allSubscriptions.clear()
    }

    private fun showLoadingView() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun showList(uiState: UiState.ListReady) {
        progress_bar.visibility = View.GONE
        val adapter = OverviewAdapter(uiState.movies)
        recycler_view.layoutManager = GridLayoutManager(context, 2)
        recycler_view.adapter = adapter
    }

    private fun showErrorView() {
        Toast.makeText(context, "Couldn't load movies", Toast.LENGTH_LONG).show()
    }

    inner class OverviewAdapter(val movies: List<MovieModel>) :
        RecyclerView.Adapter<OverviewAdapter.MovieViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            return MovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false))
        }

        override fun getItemCount(): Int {
            return movies.size
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            holder.movieModel = movies[position]
        }

        inner class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            private var _movieModel: MovieModel? = null
            var movieModel: MovieModel
                get() = _movieModel!!
                set(value) {
                    _movieModel = value

                    Glide.with(this@OverviewFragment.requireContext())
                        .load("https://image.tmdb.org/t/p/w500${movieModel.poster_path}")
                        .into(view.movie_imageview)
                }
        }
    }


}
