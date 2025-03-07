package com.example.doggos.di

import com.example.doggos.dane.api.DogApi
import com.example.doggos.dane.api.UzytkownikApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModul {

    @Provides
    @Singleton
    @Named("dogApiRetrofit")
    fun zapewnijDogRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("userApiRetrofit")
    fun zapewnijUserRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun zapewnijDogApi(@Named("dogApiRetrofit") retrofit: Retrofit): DogApi {
        return retrofit.create(DogApi::class.java)
    }

    @Provides
    @Singleton
    fun zapewnijUzytkownikApi(@Named("userApiRetrofit") retrofit: Retrofit): UzytkownikApi {
        return retrofit.create(UzytkownikApi::class.java)
    }
}