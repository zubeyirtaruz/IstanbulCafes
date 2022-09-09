package com.deepzub.istanbulcafe.service

import com.deepzub.istanbulcafe.model.Cafe
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CafeAPIService {

    private val BASE_URL = "https://www.netdata.com/"
    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(CafeAPI::class.java)

    fun getData(): Single<List<Cafe>> {
        return api.getCafes()

    }
}