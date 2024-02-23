package com.deepzub.istanbulcafe.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepzub.istanbulcafe.R
import com.deepzub.istanbulcafe.adapter.CafeAdapter
import com.deepzub.istanbulcafe.databinding.FragmentCafeFeedBinding
import com.deepzub.istanbulcafe.viewmodel.CafeFeedViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import kotlinx.android.synthetic.main.fragment_cafe_feed.*

class CafeFeedFragment : Fragment() {

    private var _binding : FragmentCafeFeedBinding? = null
    private val binding get() = _binding!!  // Bu özellik yalnızca onCreateView ve onDestroyView arasında geçerlidir.
    private lateinit var viewModel: CafeFeedViewModel
    private val cafeAdapter = CafeAdapter(arrayListOf())
    private var bySearchCriteria = 0
    private var byWorkingHour = 0

    lateinit var mAdView : AdView
    private val TAG = "Ads"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCafeFeedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CafeFeedViewModel::class.java)

        arguments?.let {

            bySearchCriteria = CafeFeedFragmentArgs.fromBundle(it).bySearchCriteria
            byWorkingHour = CafeFeedFragmentArgs.fromBundle(it).byWorkingHour

        }
        Log.i("byWorkingHour",byWorkingHour.toString())
        viewModel.refreshData(byWorkingHour)
        viewModel.getIdInSQLite()

        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = cafeAdapter

        swipeRefreshLayout.setOnRefreshListener {
            rv.visibility = View.GONE
            cafeError.visibility = View.GONE
            cafeLoading.visibility = View.VISIBLE
            viewModel.refreshFromAPI()
            swipeRefreshLayout.isRefreshing = false
        }

        observeLiveData()

        mAdView = requireActivity().findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        setAdListener()
    }

    private fun setAdListener() {
        mAdView.adListener = object : AdListener() {
            override fun onAdClicked() {
                Log.i(TAG, "onAdClicked")
            }

            override fun onAdClosed() {
                Log.i(TAG, "onAdClosed")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.i(TAG, "onAdFailedToLoad")
                Log.i(TAG, adError.message)
                Log.i(TAG, adError.domain)
                Log.i(TAG, adError.responseInfo!!.mediationAdapterClassName.toString())
            }

            override fun onAdImpression() {
                Log.i(TAG, "onAdImpression")
            }

            override fun onAdLoaded() {
                Log.i(TAG, "onAdLoaded")
            }

            override fun onAdOpened() {
                Log.i(TAG, "onAdOpened")
            }
        }
    }


    private fun observeLiveData(){
        viewModel.cafes.observe(viewLifecycleOwner, Observer { cafes ->
            cafes?.let {
                rv.visibility = View.VISIBLE
                cafeAdapter.updateCafeList(cafes)
            }
        })

        viewModel.cafeError.observe(viewLifecycleOwner, Observer { error ->
            error.let {
                if(it){
                    cafeError.visibility = View.VISIBLE
                    rv.visibility = View.GONE
                }else{
                    cafeError.visibility = View.GONE
                }
            }
        })

        viewModel.cafeLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading.let {
                if(it){
                    cafeLoading.visibility = View.VISIBLE
                    rv.visibility = View.GONE
                    cafeError.visibility = View.GONE
                }else{
                    cafeLoading.visibility = View.GONE
                }
            }
        })

        viewModel.idCafe.observe(viewLifecycleOwner, Observer { idCafe ->
            idCafe.let {
                cafeAdapter.updateIdFavorite(idCafe)
            }
        })
    }

    fun observeFilter(){
        viewModel.filteredCafes.observe(this, Observer { filtercafes ->
            filtercafes?.let {
                cafeAdapter.filteredList(filtercafes)
                if(filtercafes.isEmpty()){
                    rv.visibility = View.GONE
                    noResult.visibility = View.VISIBLE
                    errorMessage.visibility = View.VISIBLE
                }else{
                    rv.visibility = View.VISIBLE
                    noResult.visibility = View.GONE
                    errorMessage.visibility = View.GONE
                }

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cafe_search,menu)
        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                bySearchCriteria.let { criteria ->
                    if (criteria == 0 || criteria == R.id.byName){
                        viewModel.byNameFilter(query)
                        observeFilter()
                    } else{
                        viewModel.byFeaturesFilter(query)
                        observeFilter()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}