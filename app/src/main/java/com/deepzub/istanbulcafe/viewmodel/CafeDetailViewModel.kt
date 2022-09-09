package com.deepzub.istanbulcafe.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.deepzub.istanbulcafe.model.Cafe
import com.deepzub.istanbulcafe.model.GpsCafe
import com.deepzub.istanbulcafe.service.CafeDatabase
import com.deepzub.istanbulcafe.service.GpsAPIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class CafeDetailViewModel(application: Application): BaseViewModel(application) {

    private val gpsAPIService = GpsAPIService()
    private val disposable = CompositeDisposable()

    val cafeGps = MutableLiveData<GpsCafe>()
    val cafeLiveData = MutableLiveData<Cafe>()

    fun getDataFromRoom(uuid: Int){
        launch {
            val dao = CafeDatabase(getApplication()).cafeDao()
            val cafe = dao.getcafe(uuid)
            cafeLiveData.value = cafe
        }



    }

    fun getGpsFromAPI(cafeUuid : Int){
        disposable.add(
            gpsAPIService.getDataGps("cafe"+cafeUuid.toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GpsCafe>(){
                    override fun onSuccess(t: GpsCafe) {
                        cafeGps.value = t
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        )


    }



    fun getCafeName() : String {
        return cafeLiveData.value?.cafeName.toString()
    }

    fun getLat() : Float? {
        return cafeGps.value?.latitude
    }

    fun getLng() : Float? {
        return cafeGps.value?.longitude
    }







    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}