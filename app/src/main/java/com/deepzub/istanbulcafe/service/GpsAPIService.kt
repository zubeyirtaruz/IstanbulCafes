package com.deepzub.istanbulcafe.service

import com.deepzub.istanbulcafe.model.Cafe
import com.deepzub.istanbulcafe.model.GpsCafe
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class GpsAPIService {

    private val BASE_URL = "https://www.gps-coordinates.net/api/"
    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(GPSAPI::class.java)

    fun getDataGps(url : String): Single<GpsCafe> {
        return api.getCafeGps(url)

    }
}