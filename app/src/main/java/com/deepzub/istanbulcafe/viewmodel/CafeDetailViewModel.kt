package com.deepzub.istanbulcafe.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.deepzub.istanbulcafe.model.Cafe
import com.deepzub.istanbulcafe.service.CafeDatabase
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch

class CafeDetailViewModel(application: Application): BaseViewModel(application) {

    private val disposable = CompositeDisposable()

    val cafeLiveData = MutableLiveData<Cafe>()

    fun getDataFromRoom(uuid: Int){
        launch {
            val dao = CafeDatabase(getApplication()).cafeDao()
            val cafe = dao.getcafe(uuid)
            cafeLiveData.value = cafe
        }
    }

    fun getCafeName() : String {
        return cafeLiveData.value?.cafeName.toString()
    }

    fun getCafeAdress() : String {
        return cafeLiveData.value?.cafeAddress.toString()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}