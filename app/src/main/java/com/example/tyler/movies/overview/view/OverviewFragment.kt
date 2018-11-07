package com.example.tyler.movies.overview.view


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tyler.movies.Constants
import com.example.tyler.movies.R
import com.example.tyler.movies.detail.view.DetailFragment
import com.example.tyler.movies.overview.model.MovieOverviewModel
import com.example.tyler.movies.overview.model.UiState
import com.example.tyler.movies.overview.viewmodel.OverviewFragmentViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.movie_item.view.*


class OverviewFragment : Fragment() {


    private val viewModel: OverviewFragmentViewModel by lazy {
        ViewModelProviders.of(this).get(OverviewFragmentViewModel::class.java)
    }
    private val allSubscriptions = CompositeDisposable()
    val adapter = OverviewAdapter()
    var menu: Menu? = null
    var searchMenuItem: MenuItem? = null

    companion object {
        val TAG = OverviewFragment::class.java.simpleName
        fun newInstance(): OverviewFragment {
            return OverviewFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater?.inflate(R.menu.search_menu, menu)
        searchMenuItem = menu?.findItem(R.id.search_view)
        (searchMenuItem?.actionView as SearchView)
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d(TAG, "onQueryTextSubmit $query")
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "onQueryTextChange $newText")
                    viewModel.filterMovies(newText.toString())
                    return true
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = GridLayoutManagerNoPredictiveAnimation(requireContext(), 2)

        //listen for ui state changes from the viewmodel
        allSubscriptions.add(
            viewModel.uiStateChanged
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uiState ->
                    when (uiState) {
                        is UiState.Loading -> showLoadingView()
                        is UiState.ListReady -> {
                            showList(uiState)
                            populateSearchText(uiState)
                        }

                        is UiState.Error -> showErrorView()
                    }
                }, { error ->
                    Log.e(TAG, "Couldn't get UiState", error)
                })
        )

        // If just rotating, lets rely on the cached data instead of hitting the network again
        // The BehaviorSubject in the OverviewFragmentViewModel will replay it's last emission ( the list to display )
        if (savedInstanceState == null) {
            viewModel.loadMovies()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        allSubscriptions.clear()
    }

    private fun showLoadingView() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun showList(uiState: UiState.ListReady) {
        progress_bar.visibility = View.GONE
        adapter.movies = uiState.movies
        if (recycler_view.adapter == null) {
            recycler_view.adapter = adapter
        }
        uiState.diffResult.dispatchUpdatesTo(adapter)
    }

    // Upon rotation, need to repopulate the search view text
    private fun populateSearchText(uiState: UiState.ListReady) {
        if(uiState.currentSearch.isNullOrEmpty()) return
        searchMenuItem?.let {
            //If this is already expanded, there was no rotation, and there is no need to repopulate it.
            if (!it.isActionViewExpanded) {
                val searchView = it.actionView as SearchView
                    it.expandActionView()
                    //set the text but do not submit the query
                    searchView.setQuery(uiState.currentSearch, false)
            }
        }
    }

    private fun showErrorView() {
        progress_bar.visibility = View.GONE
        Toast.makeText(context, "Couldn't load movies", Toast.LENGTH_LONG).show()
    }

    inner class OverviewAdapter() :
        RecyclerView.Adapter<OverviewAdapter.MovieViewHolder>() {

        lateinit var movies: List<MovieOverviewModel>

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
            //Using this as a cheap "viewmodel"
            private var _movieModel: MovieOverviewModel? = null
            var movieModel: MovieOverviewModel
                get() = _movieModel!!
                set(value) {
                    _movieModel = value

                    Glide.with(this@OverviewFragment.requireContext())
                        .load("${Constants.poster_url}${movieModel.poster_path}")
                        .thumbnail(0.3f)
                        .into(view.movie_imageview)

                    view.setOnClickListener {
                        val extras = Bundle()
                        extras.putString(DetailFragment.EXTRA_ID, movieModel.id.toString())
                        fragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_container, DetailFragment.newInstance(extras), DetailFragment.TAG)
                            ?.addToBackStack(DetailFragment.TAG)
                            ?.commit()
                    }
                }
        }
    }


}
