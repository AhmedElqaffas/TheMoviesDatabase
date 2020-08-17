package com.example.moviesretrofit.dataClasses

import androidx.room.Entity
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.mediaDetails.credits.CreditsDatabaseHandler
import com.example.moviesretrofit.mediaDetails.credits.CreditsRetrofitRequester
import com.example.moviesretrofit.networking.MultiMediaAPI
import com.google.gson.annotations.SerializedName
import retrofit2.Call

@Entity(tableName = "series", primaryKeys = ["id"])
class Series() : MultiMedia("",0,0,"","",0f, "tv",
    "", "",0f, listOf()){

    @SerializedName("number_of_seasons")
    var numberOfSeasons: Int? = 0
    @SerializedName("last_air_date")
    var lastAirDate: String = ""
    @SerializedName("in_production")
    var inProduction: Boolean = false

    constructor(title: String, id: Int, totalVotes: Int, poster: String?, cover: String?,
                rating: Float, releaseDate: String, mediaType: String,
                overview: String?, popularity: Float, numberOfSeasons: Int?,
                lastAirDate: String, inProduction: Boolean, genres: List<Genre>?, isFavorite: Boolean): this(){
        this.title = title
        this.id = id
        this.totalVotes = totalVotes
        this.poster = poster
        this.cover = cover
        this.rating = rating
        this.mediaType = mediaType
        this.releaseDate = releaseDate
        this.overview = overview
        this.popularity = popularity
        this.numberOfSeasons = numberOfSeasons
        this.lastAirDate = lastAirDate
        this.inProduction = inProduction
        this.genres = genres
        this.isFavorite = isFavorite
    }


    override fun makeCreditsRequest(): Call<CreditsResponse> {
        return CreditsRetrofitRequester.makeSeriesCreditsRequest(this) as Call<CreditsResponse>
    }

    override suspend fun saveCreditsInDatabase(database: AppDatabase, creditsList: List<Person>) {
        saveInCreditsTable(database, creditsList)
        saveSeriesAndCreditsForeignKeys(database, creditsList)
    }

    private suspend fun saveInCreditsTable(database: AppDatabase, creditsList: List<Person>){
        database.getCreditsDao().insertCredits(creditsList)
    }

    private suspend fun saveSeriesAndCreditsForeignKeys(database: AppDatabase, creditsList: List<Person>) {
        val creditsDao = database.getCreditsDao()
        for(person in creditsList)
            creditsDao.linkSeriesAndCredits(CreditsAndSeriesForeignKeyTable(id, person.name))
    }

    override suspend fun getCreditsFromDatabase(database: AppDatabase): List<Person> {
        return CreditsDatabaseHandler.getSeriesCredits(database, this.id)
    }

    override fun makeDetailsRequest(key: String, multiMediaAPI: MultiMediaAPI): Call<MultiMedia> {
        return multiMediaAPI.makeSeriesDetailsRequest(this.id, key) as Call<MultiMedia>
    }

    override fun copyObtainedDetails(receivedMedia: MultiMedia) {
        this.numberOfSeasons = (receivedMedia as Series).numberOfSeasons
        this.inProduction = receivedMedia.inProduction
        this.lastAirDate = receivedMedia.lastAirDate
        this.genres = receivedMedia.genres
    }

    override suspend fun saveInDatabase(database: AppDatabase){
        database.getMultimediaDao().insertSingleSeries(this)
    }

    override suspend fun getFromDatabase(database: AppDatabase): MultiMedia {
        return database.getMultimediaDao().getSingleSeries(this.id)
    }
}