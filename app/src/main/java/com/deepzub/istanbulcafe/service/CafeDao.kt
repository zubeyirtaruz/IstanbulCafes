package com.deepzub.istanbulcafe.service

import androidx.room.*
import com.deepzub.istanbulcafe.model.Cafe
import com.deepzub.istanbulcafe.model.MyFavorite
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single


@Dao
interface CafeDao {

    @Insert
    suspend fun insertAll(vararg  cafes: Cafe)   // vararg : Sayısı belli olmayan bir tekil objeyi farklı sayılarla vericeğimiz keyword

    @Query("SELECT * FROM cafe")
    suspend fun getAllCafes(): List<Cafe>

    @Query("SELECT * FROM cafe WHERE ID = :cafeId")
    suspend fun getcafe(cafeId : Int) : Cafe

    @Query("DELETE FROM cafe")
    suspend fun deleteAllCafes()

    @Query("SELECT * FROM cafe WHERE dc_Mekan_Adi LIKE '%' || :text || '%'")
    suspend fun byNameGetFilteredCafes(text: String?): List<Cafe>

    @Query("SELECT * FROM cafe WHERE dc_Meshur_Ozellikleri LIKE '%' || :text || '%'")
    suspend fun byFeaturesGetFilteredCafes(text: String?): List<Cafe>

    @Insert(entity = MyFavorite::class)
    suspend fun insertFavoriteCafe(cafe : MyFavorite)

    @Query("DELETE FROM myFavoriteCafes WHERE uuid = :cafeId")
    suspend fun deleteFavoriteCafe(cafeId : Int)

    @Query("SELECT * FROM myFavoriteCafes")
    fun getAllMyFavoriteCafes(): Flowable<List<MyFavorite>>



}