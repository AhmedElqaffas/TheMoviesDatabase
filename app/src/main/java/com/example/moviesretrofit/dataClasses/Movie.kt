package com.example.moviesretrofit.dataClasses

import androidx.room.Entity
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.mediaDetails.credits.CreditsDatabaseHandler
import com.example.moviesretrofit.mediaDetails.credits.CreditsRetrofitRequester
import com.example.moviesretrofit.networking.MultiMediaAPI
import retrofit2.Call

@Entity(tableName = "movies", primaryKeys = ["id"])
class Movie(): MultiMedia("",0,0,"","",0f, "movie",
    "", "",0f, 0, 0) {


    constructor(title: String, id: Int, totalVotes: Int, poster: String?, cover: String?,
                rating: Float, releaseDate: String, mediaType: String,
                overview: String?, popularity: Float, budget: Int?, revenue: Int?): this(){
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
        this.budget = budget
        this.revenue = revenue
    }


    override fun makeCreditsRequest(): Call<CreditsResponse> {
        return CreditsRetrofitRequester.makeMovieCreditsRequest(this) as Call<CreditsResponse>
    }

    override suspend fun saveCreditsInDatabase(database: AppDatabase, creditsList: List<Person>) {
        saveInCreditsTable(database, creditsList)
        saveMovieAndCreditsForeignKeys(database, creditsList)
    }

    private suspend fun saveInCreditsTable(database: AppDatabase, creditsList: List<Person>){
        database.getCreditsDao().insertCredits(creditsList)
    }

    override suspend fun saveInDatabase(database: AppDatabase){
        database.getMultimediaDao().insertSingleMovie(this)
    }

    private suspend fun saveMovieAndCreditsForeignKeys(database: AppDatabase, creditsList: List<Person>) {
        val creditsDao = database.getCreditsDao()
        for(person in creditsList)
            creditsDao.linkMovieAndCredits(CreditsAndMoviesForeignKeyTable(id, person.name))
    }

    override suspend fun getCreditsFromDatabase(database: AppDatabase): List<Person> {
        return CreditsDatabaseHandler.getMovieCredits(database, this.id)
    }

    override fun makeDetailsRequest(key: String, multiMediaAPI: MultiMediaAPI): Call<MultiMedia> {
        return multiMediaAPI.makeMovieDetailsRequest(this.id, key) as Call<MultiMedia>
    }

    override fun copyObtainedDetails(receivedMedia: MultiMedia) {
        this.budget = receivedMedia.budget
        this.revenue = receivedMedia.revenue
    }

    override suspend fun getFromDatabase(database: AppDatabase): MultiMedia {
        return database.getMultimediaDao().getSingleMovie(this.id)
    }
}