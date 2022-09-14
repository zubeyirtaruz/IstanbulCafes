package com.deepzub.istanbulcafe.view

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.deepzub.istanbulcafe.R
import com.deepzub.istanbulcafe.databinding.FragmentCafeDetailBinding
import com.deepzub.istanbulcafe.viewmodel.CafeDetailViewModel
import kotlinx.android.synthetic.main.fragment_cafe_detail.*

class CafeDetailFragment : Fragment() {

    private var _binding: FragmentCafeDetailBinding? = null
    private val binding get() = _binding!!  // Bu özellik yalnızca onCreateView ve onDestroyView arasında geçerlidir.
    private lateinit var dataBinding : FragmentCafeDetailBinding
    private lateinit var viewModel: CafeDetailViewModel


    private var cafeUuid = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_cafe_detail,container,false)
        _binding = FragmentCafeDetailBinding.inflate(inflater,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            cafeUuid = CafeDetailFragmentArgs.fromBundle(it).cafeUuid
        }

        viewModel = ViewModelProviders.of(this).get(CafeDetailViewModel::class.java)
        viewModel.getDataFromRoom(cafeUuid)
        viewModel.getGpsFromAPI(cafeUuid)
        observeLiveData()

        buttonGo.setOnClickListener {
            var lng = 0.0f
            var lat = 0.0f
            viewModel.getLng()?.let {
                lng = it
            }
            viewModel.getLat()?.let {
                lat = it
            }

            val action = CafeDetailFragmentDirections.actionCafeDetailFragmentToGpsFragment(viewModel.getCafeName(),lat,lng)
            Navigation.findNavController(it).navigate(action)
        }


    }

    private fun observeLiveData(){

        viewModel.cafeLiveData.observe(viewLifecycleOwner, Observer { cafe ->
            cafe?.let {
                dataBinding.selectedCafe = it
            }
        })



    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}