package com.example.tmdb.dataClasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverters
import com.example.tmdb.database.AppDatabase
import com.example.tmdb.database.GenreDatabaseConverter
import com.example.tmdb.networking.MultiMediaAPI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import java.io.Serializable

open class MultiMedia(@SerializedName("name", alternate = ["title"]) var title: String,
                      var id: Int,
                      @SerializedName("vote_count") open var totalVotes: Int,
                      @SerializedName("poster_path") open var poster: String?,
                      @SerializedName("backdrop_path") open var cover: String?,
                      @SerializedName("vote_average") open var rating: Float,
                      @SerializedName("media_type") open var mediaType: String,
                      @SerializedName("release_date", alternate = ["first_air_date"]) open var releaseDate: String,
                      open var overview: String?,
                      open var popularity: Float,
                      @TypeConverters(GenreDatabaseConverter::class) open var genres: List<Genre>?): Serializable{

    var isFavorite: Boolean = false
    // Indicates whether show details (from /3/movie/{movie_id} and /3/tv/{tv_id}) were obtained from API or not
    var extraDetailsObtained: Boolean = false
    // The user that put this multimedia in favorites
    var userId: String? = "-"

    open fun makeCreditsRequest(): Call<CreditsResponse>? {return null}
    open fun makeDetailsRequest(key: String, multiMediaAPI: MultiMediaAPI): Call<MultiMedia>? {return null}
    open suspend fun getCreditsFromDatabase(database: AppDatabase): List<Person> {return listOf()}
    open suspend fun saveCreditsInDatabase(database: AppDatabase, creditsList: List<Person>){}
    open fun copyObtainedDetails(receivedMedia: MultiMedia){}
    open suspend fun saveInDatabase(database: AppDatabase){}
    open suspend fun getFromDatabase(database: AppDatabase): MultiMedia? {return null}
    open suspend fun updateFavoriteInDatabase(database: AppDatabase){}
    open suspend fun getExistingShowFields(database: AppDatabase){}
    open fun makeSimilarShowsRequest(key: String, multiMediaAPI: MultiMediaAPI): Call<MultiMediaResponse>? {return null}
    open suspend fun createOrRemoveFirestoreRecord(firestore: FirebaseFirestore,
                                                   userId: String,
                                                   firebaseCallback: FirebaseCallback){}


    interface FirebaseCallback{
        fun onFirebaseRequestEnded(success: Boolean, multiMedia: MultiMedia)
    }

}

