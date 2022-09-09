package com.deepzub.istanbulcafe.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.deepzub.istanbulcafe.model.Cafe
import com.deepzub.istanbulcafe.service.CafeAPIService
import com.deepzub.istanbulcafe.service.CafeDatabase
import com.deepzub.istanbulcafe.util.CustomSharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
            //Toast.makeText(getApplication(),"SQL",Toast.LENGTH_SHORT).show()
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
                        //Toast.makeText(getApplication(),"API",Toast.LENGTH_SHORT).show()

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


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}