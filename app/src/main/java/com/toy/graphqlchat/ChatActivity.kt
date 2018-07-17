package com.toy.graphqlchat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat.chatRecyclerView
import kotlinx.android.synthetic.main.activity_chat.messageEditText
import kotlinx.android.synthetic.main.activity_chat.sendButton
import timber.log.Timber

class ChatActivity : AppCompatActivity() {

  private lateinit var application: ChatApplication
  private val disposables = CompositeDisposable()

  private val chatRecyclerViewAdapter = ChatRecyclerViewAdapter(ArrayList())

  private val userId: String
    get() = intent.getStringExtra(INTENT_USER_ID)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_chat)


    application = getApplication() as ChatApplication

    chatRecyclerView.layoutManager = LinearLayoutManager(this)
    chatRecyclerView.adapter = chatRecyclerViewAdapter

    sendButton.setOnClickListener { sendMessage(userId, messageEditText.text.toString()) }
  }

  override fun onStart() {
    super.onStart()
    subscribeToNewMessage(userId, application.currentUserId)
    subscribeToNewMessage(application.currentUserId, userId)
  }

  private fun subscribeToNewMessage(fromUserId: String, toUserId: String) {

    val subscriptionCall = application.apolloClient()
        .subscribe(NewMessageSubscription(fromUserId, toUserId))

    disposables.add(Rx2Apollo.from(subscriptionCall)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ updateChat(it.data()) }, { setError(it) }))
  }

  private fun updateChat(data: NewMessageSubscription.Data?) {
    data?.newMessage?.let {
      chatRecyclerViewAdapter.addMessage(String.format("%s: %s", it.from.id, it.text))
    }
  }

  private fun sendMessage(toUserId: String, text: String) {
    text.isBlank().not().let {
      val addMessageMutation = application.apolloClient()
          .mutate(AddMessageMutation(application.currentUserId, toUserId, text))

      disposables.add(Rx2Apollo.from(addMessageMutation)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({ setSuccess(it.data()) }, { setError(it) }))
    }
  }

  private fun setSuccess(data: AddMessageMutation.Data?) {
    data?.let {
      messageEditText.text.clear()
      Toast.makeText(this, "Added id: " + it.addMessage?.id, LENGTH_SHORT).show()
    }
  }

  private fun setError(throwable: Throwable?) {
    throwable?.let {
      Timber.e(throwable)
      Toast.makeText(this, throwable.toString(), LENGTH_SHORT).show()
    }
  }

  override fun onStop() {
    disposables.dispose()
    super.onStop()
  }

  companion object {

    private const val INTENT_USER_ID = "USER_ID"

    fun newIntent(context: Context, user: UsersQuery.User): Intent {
      val intent = Intent(context, ChatActivity::class.java)
      intent.putExtra(INTENT_USER_ID, user.id)
      return intent
    }
  }
}
