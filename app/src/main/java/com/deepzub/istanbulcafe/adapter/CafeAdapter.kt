package com.deepzub.istanbulcafe.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.deepzub.istanbulcafe.R
import com.deepzub.istanbulcafe.databinding.ItemCafeBinding
import com.deepzub.istanbulcafe.model.Cafe
import com.deepzub.istanbulcafe.model.MyFavorite
import com.deepzub.istanbulcafe.service.CafeDatabase
import com.deepzub.istanbulcafe.view.CafeFeedFragmentDirections
import kotlinx.android.synthetic.main.item_cafe.view.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CafeAdapter(private val cafeList: ArrayList<Cafe>): RecyclerView.Adapter<CafeAdapter.CafeViewHolder>(), CafeClickListener {

    private var idCafeList: ArrayList<Int> = ArrayList()

    class CafeViewHolder(var view: ItemCafeBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemCafeBinding>(inflater,R.layout.item_cafe,parent,false)
        return CafeViewHolder(view)

    }

    override fun onBindViewHolder(holder: CafeViewHolder, position: Int){

        holder.setIsRecyclable(false)
        
        if(position % 2 == 1){
            holder.view.rowCardView.setCardBackgroundColor(Color.GRAY)
            holder.view.itemName.setTextColor(Color.WHITE)
        }else{
            holder.view.rowCardView.setCardBackgroundColor(Color.WHITE)
            holder.view.itemName.setTextColor(Color.GRAY)
        }

        showFavorite(holder.view.itemStar,position)
        holder.view.cafe = cafeList [position]
        holder.view.listener = this
        clickedStar(holder.view.itemStar,position)

    }

    override fun getItemCount(): Int {
        return cafeList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCafeList(newCafeList: List<Cafe>){
        cafeList.clear()
        cafeList.addAll(newCafeList)
        notifyDataSetChanged()
    }

    override fun onCafeClicked(v: View) {
        val uuid = v.cafeUuidText.text.toString().toInt()
        val action = CafeFeedFragmentDirections.actionCafeFeedFragmentToCafeDetailFragment(uuid)
        Navigation.findNavController(v).navigate(action)

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun clickedStar(v: ToggleButton, position: Int) {
        v.setOnCheckedChangeListener { compoundButton, b ->
            val selectedCafe = MyFavorite(cafeList[position].uuid, cafeList[position].cafeName)
            val selectedCafeId = cafeList[position].uuid
            GlobalScope.launch(Dispatchers.IO) {
                val dao = CafeDatabase(compoundButton.context).cafeDao()
                if (b) {
                    dao.insertFavoriteCafe(selectedCafe)
                } else {
                    selectedCafeId?.let {
                        dao.deleteFavoriteCafe(it)
                    }
                }
            }

        }
    }

    override fun showFavorite(v: ToggleButton, position: Int) {
        for(i in idCafeList){
            if(i == cafeList[position].uuid){
                v.isChecked = true
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun filteredList(filterList: List<Cafe>) {
        cafeList.clear()
        cafeList.addAll(filterList)
        notifyDataSetChanged()
    }

    fun updateIdFavorite(idList: List<Int>){
        idCafeList.clear()
        idCafeList.addAll(idList)
    }


}