package com.deepzub.istanbulcafe.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.deepzub.istanbulcafe.model.Cafe
import com.deepzub.istanbulcafe.service.CafeAPIService
import com.deepzub.istanbulcafe.service.CafeDatabase
import com.deepzub.istanbulcafe.util.CustomSharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class CafeFeedViewModel(application: Application) : BaseViewModel(application){

    private val cafeAPIService = CafeAPIService()
    private val disposable = CompositeDisposable()

    private var customSharedPreferences = CustomSharedPreferences(getApplication())
    private var refreshTime = 10 * 60 * 1000 * 1000 * 1000L

    val cafes = MutableLiveData<List<Cafe>>()
    val filteredCafes = MutableLiveData<List<Cafe>>()
    val idCafe = MutableLiveData<List<Int>>()
    val cafeError = MutableLiveData<Boolean>()
    val cafeLoading = MutableLiveData<Boolean>()

    fun refreshData(){
        val updateTime = customSharedPreferences.getTime()
        if(updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime){ // 10 dakikayı geçmediyse
            getDataFromSQLite()
        }else{
            getDataFromAPI()
        }
    }

    fun refreshFromAPI(){
        getDataFromAPI()
    }

    private fun getDataFromSQLite(){
        cafeLoading.value = true
        launch {
            val cafes = CafeDatabase(getApplication()).cafeDao().getAllCafes()
            showCafes(cafes)
        }

    }

    private fun getDataFromAPI(){
        cafeLoading.value = true

        disposable.add(
            cafeAPIService.getData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Cafe>>(){
                    override fun onSuccess(t: List<Cafe>) {
                        storeInSQLite(t)
                    }
                    override fun onError(e: Throwable) {
                        cafeLoading.value = false
                        cafeError.value = true
                        e.printStackTrace()
                    }
                })
        )
    }

    private fun showCafes(cafesList: List<Cafe>){
        cafes.value = cafesList
        cafeError.value = false
        cafeLoading.value = false
    }

    private fun storeInSQLite(list: List<Cafe>){
        launch {
            val dao = CafeDatabase(getApplication()).cafeDao()
            dao.deleteAllCafes()
            dao.insertAll(*list.toTypedArray()) // listeyi teker teker ekliyor
            showCafes(list)
        }
        customSharedPreferences.saveTime(System.nanoTime())
    }

    fun byNameFilter(newText: String?){

        launch {
            if (!newText.equals("")) {
                val dao = CafeDatabase(getApplication()).cafeDao()
                filteredCafes.value = dao.byNameGetFilteredCafes(newText)
            }
        }
    }

    fun byFeaturesFilter(newText: String?){

        launch {
            if (!newText.equals("")) {
                val dao = CafeDatabase(getApplication()).cafeDao()
                filteredCafes.value = dao.byFeaturesGetFilteredCafes(newText)
            }
        }
    }

    @SuppressLint("CheckResult")
    fun getIdInSQLite(){
            val dao = CafeDatabase(getApplication()).cafeDao()
            dao.getIdFavoriteCafes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Consumer<List<Int>> {
                    override fun accept(t: List<Int>?) {
                        t?.let {
                            idCafe.value = it
                        }
                    }
                })
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}