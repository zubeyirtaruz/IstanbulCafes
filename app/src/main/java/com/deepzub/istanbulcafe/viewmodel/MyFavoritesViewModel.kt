package com.deepzub.istanbulcafe.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.deepzub.istanbulcafe.model.MyFavorite
import com.deepzub.istanbulcafe.service.CafeDatabase
import com.deepzub.istanbulcafe.util.CustomSharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import kotlinx.coroutines.launch


class MyFavoritesViewModel(application: Application) : BaseViewModel(application) {


    val myFavoriteCafes = MutableLiveData<List<MyFavorite>>()
    val infoMessage = MutableLiveData<Boolean>()

    fun getDataFromRoom(){
            val dao = CafeDatabase(getApplication()).cafeDao()
            dao.getAllMyFavoriteCafes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Consumer<List<MyFavorite>> {
                    override fun accept(t: List<MyFavorite>?) {
                            t?.let {
                                showCafes(it)
                            }
                    }
                })
        }


    private fun showCafes(cafesList: List<MyFavorite>){
        myFavoriteCafes.value = cafesList
        infoMessage.value = cafesList.isEmpty()
    }



}