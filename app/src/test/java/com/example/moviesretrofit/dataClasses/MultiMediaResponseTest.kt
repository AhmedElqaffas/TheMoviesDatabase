package com.example.moviesretrofit.dataClasses

import org.junit.Test

import org.junit.Assert.*

class MultiMediaResponseTest {

    @Test
    // Test should follow AAA rule: Arrange, Act, Assert
    fun `testFilterPeopleEntriesFromResponse RETURN list without persons entries`() {

        // Arrange
        val dummyMultimediaList = mutableListOf(
            MultiMedia("",0,0,"","",0f, "person", "", "",0f, listOf()),
            MultiMedia("",0,0,"","",0f, "movie", "", "",0f, listOf()),
            MultiMedia("",0,0,"","",0f, "movie", "", "",0f, listOf()),
            MultiMedia("",0,0,"","",0f, "series", "","",0f, listOf()),
            MultiMedia("",0,0,"","",0f, "person", "","",0f, listOf()),
            Series("",0,0,"","",0f, "","","",0f,0,"",false, listOf<Genre>(), false)
        )

        val multiMediaResponse = MultiMediaResponse(0, dummyMultimediaList, 0)
        dummyMultimediaList.apply {
            this.removeAt(4)
            this.removeAt(0)
        }

        // Act
        val actualFilteredList = multiMediaResponse.filterPeopleEntriesFromResponse()

        // Assert (only one assertion)
        assertEquals(dummyMultimediaList, actualFilteredList)

    }
}