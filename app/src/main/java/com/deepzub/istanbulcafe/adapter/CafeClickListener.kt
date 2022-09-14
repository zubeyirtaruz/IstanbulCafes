package com.deepzub.istanbulcafe.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ToggleButton
import java.text.FieldPosition

interface CafeClickListener {

    fun onCafeClicked(v: View)
    fun clickedStar(v: ToggleButton, position: Int)
    fun showFavorite(v: ToggleButton, position: Int)

}