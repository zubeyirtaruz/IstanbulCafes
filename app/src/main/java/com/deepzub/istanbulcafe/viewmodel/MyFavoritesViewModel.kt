package com.deepzub.istanbulcafe.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.deepzub.istanbulcafe.model.MyFavorite
import com.deepzub.istanbulcafe.service.CafeDatabase
import io.reactivex.android.schedulers.AndroidSchedulers

class MyFavoritesViewModel(application: Application) : BaseViewModel(application) {

    val myFavoriteCafes = MutableLiveData<List<MyFavorite>>()
    val infoMessage = MutableLiveData<Boolean>()

    @SuppressLint("CheckResult")
    fun getDataFromRoom(){
            val dao = CafeDatabase(getApplication()).cafeDao()
            dao.getAllMyFavoriteCafes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { t ->
                    t?.let {
                        showCafes(it)
                    }
                }
    }

    private fun showCafes(cafesList: List<MyFavorite>){
        myFavoriteCafes.value = cafesList
        infoMessage.value = cafesList.isEmpty()
    }

}