package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.GetSeasonalDataResponse
import retrofit2.http.GET

interface GithubApi {
    @GET("mangadex-seasonal/seasonal-list.json")
    suspend fun getSeasonalData(): GetSeasonalDataResponse
}