package com.deepzub.istanbulcafe.service

import com.deepzub.istanbulcafe.model.GpsCafe
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface GpsAPI {

    @GET
    fun getCafeGps(@Url url: String): Single<GpsCafe> // cafe+uuid
}