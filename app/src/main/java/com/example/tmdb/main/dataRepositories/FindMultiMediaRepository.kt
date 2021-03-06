package com.example.tmdb.main.dataRepositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tmdb.dataClasses.HybridResponse
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.networking.MultiMediaAPI
import com.example.tmdb.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FindMultiMediaRepository {

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"
    private val multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient()
        .create(MultiMediaAPI::class.java)

    private var currentPage = 1
    private var foundMediaTotalPages = 0
    private var foundMedia = mutableListOf<MultiMedia>()
    private val foundMediaResponseLiveData: MutableLiveData<List<MultiMedia>> =
        MutableLiveData()
    private var lastSearchText = ""

    fun findMediaByName(name: String, searchTextChanged: Boolean): LiveData<List<MultiMedia>> {
        if(searchTextChanged) {
            currentPage = 1
            sendCachedOrNetworkData(name)
        }

        else{
            getNextPageContentIfExists(name)
        }
        return foundMediaResponseLiveData
    }

    /**
     * If the user entered new text in the editText, the app should make a new request and get the
     * new result page by page. When making a new request, the page will be reset to 1, but we don't
     * want the repository to pass the old contents again, it should remake the api request.
     */
    private fun sendCachedOrNetworkData(name: String){
        if (foundMedia.isEmpty() || name != lastSearchText) {
            lastSearchText = name
            returnNetworkData(name, 1)
        }
        else
            returnCachedData()
    }

    private fun returnCachedData(){
        foundMediaResponseLiveData.value = foundMedia
    }

    private fun getNextPageContentIfExists(name: String){
        if(currentPage < foundMediaTotalPages) {
            returnNetworkData(name, currentPage + 1)
        }
    }

    private fun returnNetworkData(name: String, page: Int){
            multiMediaAPI.findMediaByName(key, page, name)
                .apply { enqueueCallback(this) }
    }

    private fun enqueueCallback(call: Call<HybridResponse>) {
        call.enqueue(object: Callback<HybridResponse> {

            override fun onResponse(call: Call<HybridResponse>,
                                    response: Response<HybridResponse>) {

                response.body()?.let {
                    val listWithoutPeopleEntries = it.filterPeopleEntriesFromResponse()
                    foundMediaResponseLiveData.postValue(listWithoutPeopleEntries)
                    updateRepository(it)
                }
            }

            override fun onFailure(call: Call<HybridResponse>, t: Throwable) {
                Log.e("Find shows error", "Couldn't get found shows")
                Log.e("find shows error", "${t.message}")
            }
        })
    }

    private fun updateRepository(response: HybridResponse){
        updateCurrentPage(response.page)
        appendResultItemsToList(response.filterPeopleEntriesFromResponse())
        saveTotalNumberOfPages(response.totalPages)
    }

    private fun updateCurrentPage(page: Int){
        currentPage = page
    }


    private fun appendResultItemsToList(results: List<MultiMedia>) {
        // If the current page = 1, then foundMedia list will either be empty
        // or already have entries but we want to overwrite them with the new search results
        // Either way, we will overwrite the foundMedia list contents
        if(currentPage == 1){
            clearPreviousListContent()
        }
            foundMedia.addAll(results)
    }

    private fun clearPreviousListContent(){
        foundMedia.removeAll {true}
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        foundMediaTotalPages = totalPages
    }
}