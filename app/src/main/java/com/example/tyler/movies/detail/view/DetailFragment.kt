package com.example.tyler.movies.detail.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.example.tyler.movies.Constants
import com.example.tyler.movies.R
import com.example.tyler.movies.detail.viewmodel.DetailFragmentViewModel
import com.example.tyler.movies.overview.model.UiState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment() {

    private val viewModel: DetailFragmentViewModel by lazy {
        ViewModelProviders.of(this).get(DetailFragmentViewModel::class.java)
    }
    private val allSubscriptions = CompositeDisposable()

    companion object {
        val TAG = DetailFragment::class.java.simpleName
        val EXTRA_ID = "extra_id"
        fun newInstance(args: Bundle): Fragment {
            val fragment = DetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.details_fragment_title)
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        //listen for ui state changes from the viewmodel
        allSubscriptions.add(
            viewModel.uiStateChanged
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uiState ->
                    when (uiState) {
                        is UiState.Loading -> showLoadingView()
                        is UiState.DetailsReady -> showDetails(uiState)
                        is UiState.Error -> showErrorView()
                    }
                }, { error -> Log.e(TAG, "Couldn't display movie details", error) })
        )

         // If just rotating, lets rely on the cached data instead of hitting the network again
        // The BehaviorSubject in the OverviewFragmentViewModel will replay it's last emission ( the list to display )
        if (savedInstanceState == null) {
            viewModel.getDetails(arguments!!.getString(EXTRA_ID)!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        allSubscriptions.clear()
    }

    private fun showLoadingView() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun showErrorView() {
        progress_bar.visibility = View.GONE
        Toast.makeText(requireContext(), "Couldn't load details", Toast.LENGTH_LONG).show()
    }

    private fun showDetails(uiState: UiState.DetailsReady) {
        progress_bar.visibility = View.GONE
        Glide.with(requireContext())
            .load("${Constants.poster_url}${uiState.details.backdrop_path}")
            .thumbnail(0.3f)
            .into(header_imageview)

        title_textview.text = uiState.details.title
        description_textview.text = uiState.details.overview
    }
}