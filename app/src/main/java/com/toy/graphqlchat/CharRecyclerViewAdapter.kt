package com.toy.graphqlchat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Provide views to RecyclerView with data from dataSet.
 *
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
class ChatRecyclerViewAdapter internal constructor(
  private var dataSet: ArrayList<String>
) : RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.textView1.text = dataSet[position]
  }

  override fun getItemCount() = dataSet.size

  inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    override fun onClick(v: View?) = Unit

    internal var textView1: TextView = itemView.findViewById(android.R.id.text1)
  }

  fun addMessage(message: String) {
    dataSet.add(message)
    notifyItemInserted(dataSet.size - 1)
  }
}