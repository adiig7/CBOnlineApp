package com.codingblocks.onlineapi

import com.codingblocks.onlineapi.api.OnlinePublicApi
import com.codingblocks.onlineapi.models.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jasminb.jsonapi.RelationshipResolver
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.SerializationFeature
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Clients {
    private val om = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)

    private val onlineApiResourceConverter = ResourceConverter(
            om,
            Instructor::class.java,
            Course::class.java,
            Sections::class.java,
            Contents::class.java,
            MyCourseRuns::class.java,
            MyCourse::class.java,
            MyRunAttempts::class.java,
            MyRunAttempt::class.java,
            LectureVideo::class.java,
            LectureContent::class.java,
            LectureDocument::class.java

    )
    private val relationshipResolver = RelationshipResolver {
        OkHttpClient()
                .newCall(Request.Builder().url(it).build())
                .execute()
                .body()
                ?.bytes()

    }

    init {
        onlineApiResourceConverter.setGlobalResolver(relationshipResolver)
        onlineApiResourceConverter.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES);
    }


    val onlineV2PublicClient: OnlinePublicApi
        get() = Retrofit.Builder()
                .baseUrl("https://api-online.cb.lk/api/v2/")
                .addConverterFactory(JSONAPIConverterFactory(onlineApiResourceConverter))
                .build()
                .create(OnlinePublicApi::class.java)
    val retrofit = Retrofit.Builder()
            .baseUrl("https://api-online.cb.lk/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val api = retrofit.create(OnlinePublicApi::class.java)


    val retrofitAuth = Retrofit.Builder()
            .baseUrl("https://account.codingblocks.com/apiAuth/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val apiAuth = retrofitAuth.create(OnlinePublicApi::class.java)

}