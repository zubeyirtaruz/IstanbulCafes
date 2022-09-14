package com.deepzub.istanbulcafe.adapter


import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.deepzub.istanbulcafe.R
import com.deepzub.istanbulcafe.databinding.MyFavoritesItemCafeBinding
import com.deepzub.istanbulcafe.model.MyFavorite
import com.deepzub.istanbulcafe.service.CafeDatabase
import com.deepzub.istanbulcafe.view.MyFavoritesFragmentDirections
import kotlinx.android.synthetic.main.my_favorites_item_cafe.view.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FavoriteAdapter(private val cafeList: ArrayList<MyFavorite>): RecyclerView.Adapter<FavoriteAdapter.MyFavoriteViewHolder>(), FavoriteClickListener {

    class MyFavoriteViewHolder(var view: MyFavoritesItemCafeBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<MyFavoritesItemCafeBinding>(inflater,
            R.layout.my_favorites_item_cafe,parent,false)
        return MyFavoriteViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyFavoriteViewHolder, position: Int){
        if(position % 2 == 1){
            holder.view.rowFavoriteCardView.setCardBackgroundColor(Color.GRAY)
            holder.view.favoriteItemName.setTextColor(Color.WHITE)
        }else{
            holder.view.rowFavoriteCardView.setCardBackgroundColor(Color.WHITE)
            holder.view.favoriteItemName.setTextColor(Color.GRAY)
        }

        holder.view.cafe = cafeList [position]
        holder.view.listener = this
        clickedGarbage(holder.view.itemGarbage,position)




    }

    override fun getItemCount(): Int {
        return cafeList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCafeList(newCafeList: List<MyFavorite>){
        cafeList.clear()
        cafeList.addAll(newCafeList)
        notifyDataSetChanged()
    }

    override fun onCafeClicked(v: View) {
        val uuid = v.favoriteCafeUuidText.text.toString().toInt()
        val action = MyFavoritesFragmentDirections.actionMyFavoritesFragmentToCafeDetailFragment(uuid)
        Navigation.findNavController(v).navigate(action)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun clickedGarbage(v: ImageButton, position: Int) {
        v.setOnClickListener {
            val selectedCafeId = cafeList[position].uuid
            GlobalScope.launch(Dispatchers.IO) {
                val dao = CafeDatabase(v.context).cafeDao()
                selectedCafeId?.let {
                    dao.deleteFavoriteCafe(it)
                }


            }

        }

    }



}
