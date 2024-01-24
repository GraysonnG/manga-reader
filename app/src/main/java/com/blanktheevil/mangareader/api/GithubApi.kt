package com.blanktheevil.mangareader.api

import com.blanktheevil.mangareader.data.dto.GetSeasonalDataResponse
import retrofit2.http.GET

interface GithubApi {
    @GET("mangadex-seasonal/seasonal-list.json")
    suspend fun getSeasonalData(): GetSeasonalDataResponse
}