package com.deepzub.istanbulcafe.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "cafe")
data class Cafe(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "ID")
    @SerializedName("ID")
    val uuid: Int?,

    @ColumnInfo(name = "dc_Mekan_Adi")
    @SerializedName("dc_Mekan_Adi")
    val cafeName: String?,

    @ColumnInfo(name = "dc_Adres")
    @SerializedName("dc_Adres")
    val cafeAddress: String?,

    @ColumnInfo(name = "dc_Telefon")
    @SerializedName("dc_Telefon")
    val cafePhone: String?,

    @ColumnInfo(name = "dc_Meshur_Ozellikleri")
    @SerializedName("dc_Meshur_Ozellikleri")
    val cafeFamousFeatures: String?,

    @ColumnInfo(name = "dc_Calisma_Saatleri")
    @SerializedName("dc_Calisma_Saatleri")
    val cafeWorkingHours: String?,

    @ColumnInfo(name = "dc_Restoran_Ozellikleri")
    @SerializedName("dc_Restoran_Ozellikleri")
    val cafeFeatures: String?)


@Entity(tableName = "myFavoriteCafes")
data class MyFavorite(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "uuid")
    var uuid: Int?,

    @ColumnInfo(name = "cafeName")
    val cafeName: String?)

data class GpsCafe(
    @SerializedName("responseCode") // 200 ise başarılı 400 ise başarısız
    val responseCode: Int?,

    @SerializedName("identifier")
    val identifier: String?,

    @SerializedName("latitude")
    val latitude: Float?,

    @SerializedName("longitude")
    val longitude: Float?)

