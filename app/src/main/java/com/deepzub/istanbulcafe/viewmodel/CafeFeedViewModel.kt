package com.deepzub.istanbulcafe.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
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
import java.util.*

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

    fun refreshData(byWorkingHour : Int){
        val updateTime = customSharedPreferences.getTime()
        if(updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime){ // 10 dakikayı geçmediyse
            getDataFromSQLite()
        }else{
            getDataFromAPI(byWorkingHour)
        }
    }

    fun refreshFromAPI(){
        getDataFromAPI(0)
    }

    private fun getDataFromSQLite(){
        cafeLoading.value = true
        launch {
            val cafes = CafeDatabase(getApplication()).cafeDao().getAllCafes()
            showCafes(cafes)
        }

    }

    private fun getDataFromAPI(byWorkingHour: Int){
        cafeLoading.value = true

        disposable.add(
            cafeAPIService.getData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Cafe>>(){
                    override fun onSuccess(t: List<Cafe>) {

                        val emptyCafeList: MutableList<Cafe> = mutableListOf()

                        if(byWorkingHour != 0){ // Suanda acik cafeler gormek icin filtreleme yapmis
                            for(i in 0 until t.size){
                                val (start, end) = findSubstring(t[i].cafeWorkingHours.toString(), getDayOfWeek())
                                if(start.equals("Başlangıç Saati Bulunamadı") || end.equals("Bitiş Saati Bulunamadı")){
                                }else{
                                    val startLong = getSecondsSinceStartOfDay(start)
                                    val endLong = getSecondsSinceStartOfDay(end)
                                    if(getSecondsSinceStartOfDay()>startLong && endLong>getSecondsSinceStartOfDay()){
                                        emptyCafeList.add(t[i])
                                    }
                                }

                            }
                            showCafes(emptyCafeList)

                        }else{
                            storeInSQLite(t)
                        }
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

    private fun getDayOfWeek(): String {
        val calendar = Calendar.getInstance()

        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Pts"
            Calendar.TUESDAY -> "Sa"
            Calendar.WEDNESDAY -> "Çrş"
            Calendar.THURSDAY -> "Prş"
            Calendar.FRIDAY -> "Cum"
            Calendar.SATURDAY -> "Cts"
            Calendar.SUNDAY -> "Paz"
            else -> "Bilinmeyen"
        }
    }

    fun findSubstring(originalString: String, target: String): Pair<String, String> {
        val index = originalString.indexOf(target)
        if (index != -1) {
            val startIndex = index + target.length + 1 // Hedef alt dizenin başlangıç indeksi
            val endIndex = originalString.indexOf(',', startIndex) // Virgülün olduğu yer hedefin sonu olarak kabul edilir
            if (endIndex != -1) {
                val timeRange = originalString.substring(startIndex, endIndex)
                val splitTime = timeRange.split(" - ")
                if (splitTime.size == 2) {
                    val startTime = splitTime[0]
                    val endTime = splitTime[1]
                    return Pair(startTime, endTime)
                }
            }
        }
        return Pair("Başlangıç Saati Bulunamadı", "Bitiş Saati Bulunamadı")
    }

    private fun getSecondsSinceStartOfDay(timeString: String): Long {
        val calendar = Calendar.getInstance()

        // Verilen saat string'ini ayrıştır
        val timeParts = timeString.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        // Saat ve dakika bilgilerini ayarla
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfDay = Calendar.getInstance()
        startOfDay.set(Calendar.HOUR_OF_DAY, 0)
        startOfDay.set(Calendar.MINUTE, 0)
        startOfDay.set(Calendar.SECOND, 0)
        startOfDay.set(Calendar.MILLISECOND, 0)

        // Günün başlangıcından itibaren geçen saniyeyi hesapla
        val differenceInMillis = calendar.timeInMillis - startOfDay.timeInMillis
        return differenceInMillis / 1000
    }

    private fun getSecondsSinceStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfDay = calendar.timeInMillis

        return (now - startOfDay) / 1000
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}