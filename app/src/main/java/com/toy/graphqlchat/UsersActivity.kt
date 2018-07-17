package com.toy.graphqlchat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_users.usersRecyclerView
import timber.log.Timber

class UsersActivity : AppCompatActivity() {

  private val disposables = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_users)

    usersRecyclerView.layoutManager = LinearLayoutManager(this)

    fetchUsers()
  }

  private fun fetchUsers() {
    val usersQuery = (application as ChatApplication).apolloClient().query(
        UsersQuery())

    disposables.add(Rx2Apollo.from(usersQuery)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ setUsersData(it.data()) }, { setError(it) }))
  }

  private fun setUsersData(data: UsersQuery.Data?) {
    data?.users?.let {
      val adapter = UsersRecyclerViewAdapter(it)

      adapter.setClickListener(object : UsersRecyclerViewAdapter.ItemClickListener {
        override fun onItemClick(view: View, position: Int) {
          val intent = ChatActivity.newIntent(view.context, adapter.getItem(position))
          startActivity(intent)
        }
      })

      usersRecyclerView.adapter = adapter
    }
  }

  private fun setError(throwable: Throwable?) {
    throwable?.let {
      Timber.e(throwable)
      Toast.makeText(this, throwable.toString(), LENGTH_SHORT).show()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    disposables.dispose()
  }

}
