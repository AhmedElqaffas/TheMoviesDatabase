package com.example.moviesretrofit.dataClasses

import androidx.room.Entity
import androidx.room.Index
import com.example.moviesretrofit.database.AppDatabase
import com.example.moviesretrofit.mediaDetails.credits.CreditsDatabaseHandler
import com.example.moviesretrofit.mediaDetails.credits.CreditsRetrofitRequester
import com.example.moviesretrofit.networking.MultiMediaAPI
import retrofit2.Call

@Entity(tableName = "movies", primaryKeys = ["id"], indices = [Index("id")])
class Movie(): MultiMedia("",0,0,"","",0f, "movie",
    "", "",0f, listOf()) {

    var budget: Int? = 0
    var revenue: Long? = 0

    constructor(title: String, id: Int, totalVotes: Int, poster: String?, cover: String?,
                rating: Float, releaseDate: String, mediaType: String,
                overview: String?, popularity: Float, budget: Int?, revenue: Long?,
                genres: List<Genre>?, isFavorite: Boolean):
            this() {
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
        this.revenue =revenue
        this.genres = genres
        this.isFavorite = isFavorite
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

    override fun copyObtainedDetails(receivedMedia: MultiMedia){
        this.budget = (receivedMedia as Movie).budget
        this.revenue = receivedMedia.revenue
        this.genres = receivedMedia.genres

    }

    override suspend fun getFromDatabase(database: AppDatabase): MultiMedia {
        return database.getMultimediaDao().getSingleMovie(this.id)
    }

    override suspend fun updateFavoriteField(database: AppDatabase) {
        database.getMultimediaDao().updateMovieFavoriteField(this.id, this.isFavorite)
    }

    override suspend fun getExistingMovieIsFavorite(database: AppDatabase) {
        // If the database returns null because there is no entry for this movie, set isFavorite to false
        try{
            this.isFavorite = database.getMultimediaDao().getMovieIsFavorite(id)
        }catch(e: Exception){
            this.isFavorite = false
        }
    }

    override fun makeSimilarShowsRequest(key: String, multiMediaAPI: MultiMediaAPI): Call<MultiMediaResponse>? {
        return multiMediaAPI.getSimilarMovies(id, key) as Call<MultiMediaResponse>
    }
}