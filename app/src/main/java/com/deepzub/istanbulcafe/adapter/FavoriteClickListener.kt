package com.deepzub.istanbulcafe.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.ToggleButton
import com.deepzub.istanbulcafe.viewmodel.MyFavoritesViewModel

interface FavoriteClickListener {
    fun onCafeClicked(v: View)
    fun clickedGarbage(v: ImageButton, position: Int)

}