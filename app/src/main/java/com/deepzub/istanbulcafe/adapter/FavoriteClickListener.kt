package com.deepzub.istanbulcafe.adapter

import android.view.View
import android.widget.ImageButton

interface FavoriteClickListener {

    fun onCafeClicked(v: View)
    fun clickedGarbage(v: ImageButton, position: Int)

}