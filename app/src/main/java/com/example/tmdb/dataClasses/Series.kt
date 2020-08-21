package com.example.tmdb.dataClasses

import android.util.Log
import androidx.room.Entity
import com.example.tmdb.database.AppDatabase
import com.example.tmdb.helpers.MultiMediaCaster
import com.example.tmdb.mediaDetails.credits.CreditsDatabaseHandler
import com.example.tmdb.mediaDetails.credits.CreditsRetrofitRequester
import com.example.tmdb.networking.MultiMediaAPI
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
                lastAirDate: String, inProduction: Boolean, genres: List<Genre>?, isFavorite: Boolean,
                extraDetailsObtained: Boolean, userId: String): this(){
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
        this.extraDetailsObtained = extraDetailsObtained
        this.userId = userId
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
        this.extraDetailsObtained = receivedMedia.extraDetailsObtained
        this.userId = receivedMedia.userId
    }

    override suspend fun saveInDatabase(database: AppDatabase){
        database.getMultimediaDao().insertSingleSeries(this)
    }

    override suspend fun getFromDatabase(database: AppDatabase): MultiMedia {
        return database.getMultimediaDao().getSingleSeries(this.id)
    }

    override suspend fun updateFavoriteInDatabase(database: AppDatabase) {
        database.getMultimediaDao().updateSeriesFavorite(this.id, this.isFavorite, this.userId)
    }

    override suspend fun getExistingShowFields(database: AppDatabase) {
        // If the database returns null because there is no entry for this series, set isFavorite to false
        try{
            val existingSeries = database.getMultimediaDao().getSingleSeries(id)
            this.isFavorite = existingSeries.isFavorite
            this.userId = existingSeries.userId
        }catch(e: Exception){
            this.isFavorite = false
        }
    }

    override fun makeSimilarShowsRequest(key: String, multiMediaAPI: MultiMediaAPI): Call<MultiMediaResponse>? {
        return multiMediaAPI.getSimilarSeries(id, key) as Call<MultiMediaResponse>
    }

    override suspend fun createOrRemoveFirestoreRecord(firestore: FirebaseFirestore,
                                                       userId: String,
                                                       firebaseCallback: FirebaseCallback){

        val documentReference = firestore.collection("series_linker")
            .document(userId)

        val documentSnapshot = documentReference.get()
        documentSnapshot.addOnSuccessListener {
            if(seriesAlreadyInFavorites(it)){
                deleteSeriesRecord(documentReference)
            }

            else{
                updateLinkerDocument(documentReference, it)
                insertSeriesDetailsInNewCollection(firestore)
            }
            firebaseCallback.onFirebaseRequestEnded(true, this)

        }.addOnFailureListener {
            firebaseCallback.onFirebaseRequestEnded(false, this)
            Log.i("Series", "${it.message}")

        }
    }

    private fun seriesAlreadyInFavorites(ds: DocumentSnapshot): Boolean{
        return ds.data?.get(this.id.toString()) != null
    }

    private fun deleteSeriesRecord(documentReference: DocumentReference){
        deleteLinkerDocumentEntry(documentReference)
    }

    private fun deleteLinkerDocumentEntry(documentReference: DocumentReference){
        documentReference.update(this.id.toString(), FieldValue.delete())
    }

    private fun updateLinkerDocument(documentReference: DocumentReference,
                                     documentSnapshot: DocumentSnapshot
    ){
        val multimediaMap: HashMap<String, Any> = hashMapOf()
        multimediaMap[id.toString()] = mediaType
        if(documentSnapshot.exists()){
            documentReference.update(multimediaMap)
        }
        else{
            documentReference.set(multimediaMap)
        }

    }

    private fun insertSeriesDetailsInNewCollection(firestore: FirebaseFirestore){
        val documentReference = firestore.collection("series")
            .document(this.id.toString())
        // add the movie details
        documentReference.set(MultiMediaCaster.createMultimediaMap(this))
    }
}