package com.deepzub.istanbulcafe.service

import com.deepzub.istanbulcafe.model.Cafe
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET

interface CafeAPI {

    @GET("JSON/fdd52097")
    fun getCafes():Single<List<Cafe>>

}