package com.toy.graphqlchat

import android.app.Application

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport

import okhttp3.OkHttpClient

class ChatApplication : Application() {

  private lateinit var apolloClient: ApolloClient

  // This is the "logged in" user, hardcoded for simplicity
  val currentUserId: String = "user1"

  override fun onCreate() {
    super.onCreate()

    val okHttpClient = OkHttpClient.Builder()
        .build()

    apolloClient = ApolloClient.builder()
        .serverUrl(BASE_URL)
        .okHttpClient(okHttpClient)
        .subscriptionTransportFactory(WebSocketSubscriptionTransport.Factory(
            SUBSCRIPTION_BASE_URL, okHttpClient))
        .build()
  }

  fun apolloClient(): ApolloClient {
    return apolloClient
  }

  companion object {

    // Provide IP for server is running on localhost
    private const val ip = ""

    private val BASE_URL = String.format("http://%s:3000/graphql", ip)
    private val SUBSCRIPTION_BASE_URL = String.format("ws://%s:3000/subscriptions", ip)
  }
}
