package com.deepzub.istanbulcafe.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepzub.istanbulcafe.R
import com.deepzub.istanbulcafe.adapter.FavoriteAdapter
import com.deepzub.istanbulcafe.viewmodel.MyFavoritesViewModel
import kotlinx.android.synthetic.main.fragment_my_favorites.*

class MyFavoritesFragment : Fragment() {

    private lateinit var viewModel: MyFavoritesViewModel
    private val cafeAdapter = FavoriteAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_my_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this)[MyFavoritesViewModel::class.java]

        viewModel.getDataFromRoom()

        frv.layoutManager = LinearLayoutManager(context)
        frv.adapter = cafeAdapter

        observeLiveData()

        infoMessage.setOnClickListener {
            val action = MyFavoritesFragmentDirections.actionMyFavoritesFragmentToCafeFeedFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun observeLiveData(){
        viewModel.myFavoriteCafes.observe(viewLifecycleOwner, Observer { cafes ->
            cafes?.let {
                frv.visibility = View.VISIBLE
                cafeAdapter.updateCafeList(cafes)
            }
        })

        viewModel.infoMessage.observe(viewLifecycleOwner, Observer { info ->
            info?.let {
                if(it){
                    frv.visibility = View.GONE
                    infoMessage.visibility = View.VISIBLE
                }else{
                    frv.visibility = View.VISIBLE
                    infoMessage.visibility = View.GONE
                }
            }

        })


    }

}