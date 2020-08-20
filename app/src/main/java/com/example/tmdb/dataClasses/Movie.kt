package com.example.tmdb.dataClasses

import android.util.Log
import androidx.room.Entity
import androidx.room.Index
import com.example.tmdb.database.AppDatabase
import com.example.tmdb.helpers.MultiMediaCaster
import com.example.tmdb.mediaDetails.MultimediaDetailsRepository
import com.example.tmdb.mediaDetails.credits.CreditsDatabaseHandler
import com.example.tmdb.mediaDetails.credits.CreditsRetrofitRequester
import com.example.tmdb.networking.MultiMediaAPI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call

@Entity(tableName = "movies", primaryKeys = ["id"], indices = [Index("id")])
class Movie(): MultiMedia("",0,0,"","",0f, "movie",
    "", "",0f, listOf()) {

    var budget: Int? = 0
    var revenue: Long? = 0

    constructor(title: String, id: Int, totalVotes: Int, poster: String?, cover: String?,
                rating: Float, releaseDate: String, mediaType: String,
                overview: String?, popularity: Float, budget: Int?, revenue: Long?,
                genres: List<Genre>?, isFavorite: Boolean, extraDetailsObtained: Boolean, userId: String):
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
        this.extraDetailsObtained = extraDetailsObtained
        this.userId = userId
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
        this.extraDetailsObtained = receivedMedia.extraDetailsObtained
        this.userId = receivedMedia.userId

    }

    override suspend fun getFromDatabase(database: AppDatabase): MultiMedia {
        return database.getMultimediaDao().getSingleMovie(this.id)
    }

    override suspend fun updateFavoriteInDatabase(database: AppDatabase) {
        database.getMultimediaDao().updateMovieFavorite(this.id, this.isFavorite, this.userId)
    }

    override suspend fun getExistingShowFields(database: AppDatabase) {
        // If the database returns null because there is no entry for this movie, set isFavorite to false
        try{
            val existingMovie = database.getMultimediaDao().getSingleMovie(id)
            this.isFavorite = existingMovie.isFavorite
            this.userId = existingMovie.userId
        }catch(e: Exception){
            this.isFavorite = false
        }
    }

    override fun makeSimilarShowsRequest(key: String, multiMediaAPI: MultiMediaAPI): Call<MultiMediaResponse>? {
        return multiMediaAPI.getSimilarMovies(id, key) as Call<MultiMediaResponse>
    }

    override suspend fun createOrRemoveFirestoreRecord(firestore: FirebaseFirestore,
                                                       firebaseAuth: FirebaseAuth,
                                                       firebaseCallback: FirebaseCallback){
        
        val documentReference = firestore.collection("movies_linker")
            .document(firebaseAuth.currentUser!!.uid)

        val documentSnapshot = documentReference.get()
        documentSnapshot.addOnSuccessListener {
            if(movieAlreadyInFavorites(it)){
                deleteMovieRecord(documentReference)
            }

            else{
                updateLinkerDocument(documentReference)
                insertMovieDetailsInNewCollection(firestore)
            }
            firebaseCallback.onFirebaseRequestEnded(true, this)

        }.addOnFailureListener {
            firebaseCallback.onFirebaseRequestEnded(false, this)
            Log.i("MultimediaDetails", it.message)

        }
    }

    private fun movieAlreadyInFavorites(ds: DocumentSnapshot): Boolean{
        return ds.data?.get(this.id.toString()) != null
    }

    private fun deleteMovieRecord(documentReference: DocumentReference){
        deleteLinkerDocumentEntry(documentReference)
    }

    private fun deleteLinkerDocumentEntry(documentReference: DocumentReference){
        documentReference.update(this.id.toString(), FieldValue.delete())
    }

    private fun updateLinkerDocument(documentReference: DocumentReference){
        val multimediaMap: HashMap<String, Any> = hashMapOf()
        multimediaMap[id.toString()] = mediaType
        documentReference.update(multimediaMap)
    }

    private fun insertMovieDetailsInNewCollection(firestore: FirebaseFirestore){
        val documentReference = firestore.collection("movies")
            .document(this.id.toString())
        // add the movie details
        documentReference.set(MultiMediaCaster.createMultimediaMap(this))
    }


}