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
class UsersRecyclerViewAdapter internal constructor(
  private var dataSet: List<UsersQuery.User>
) : RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder>() {

  private var itemClickListener: ItemClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.textView1.text = dataSet[position].id
    holder.textView2.text = dataSet[position].name
  }

  override fun getItemCount() = dataSet.size

  inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    internal var textView1: TextView = itemView.findViewById(android.R.id.text1)
    internal var textView2: TextView = itemView.findViewById(android.R.id.text2)

    init {
      itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
      itemClickListener?.onItemClick(view, adapterPosition)
    }
  }

  /**
   * Convenience method for getting data at click position.
   */
  fun getItem(id: Int): UsersQuery.User {
    return dataSet[id]
  }

  /**
   * Set item click listener.
   */
  fun setClickListener(itemClickListener: ItemClickListener) {
    this.itemClickListener = itemClickListener
  }

  /**
   * Interface definition for receiving click events.
   */
  interface ItemClickListener {
    fun onItemClick(view: View, position: Int)
  }
}
