package com.deepzub.istanbulcafe.adapter

import android.view.View
import android.widget.ToggleButton

interface CafeClickListener {

    fun onCafeClicked(v: View)
    fun clickedStar(v: ToggleButton, position: Int)
    fun showFavorite(v: ToggleButton, position: Int)

}