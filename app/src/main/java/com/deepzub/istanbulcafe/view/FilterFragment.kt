package com.deepzub.istanbulcafe.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.deepzub.istanbulcafe.R
import kotlinx.android.synthetic.main.fragment_filter.*


class FilterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClear.setOnClickListener {
            byName.isChecked = true
            tumu.isChecked = true
        }

        buttonApply.setOnClickListener {
            val action = FilterFragmentDirections
                .actionFilterFragmentToCafeFeedFragment(bySearchCriteria.checkedRadioButtonId,byWorkingHour.checkedRadioButtonId)
            Navigation.findNavController(it).navigate(action)

        }
    }
}