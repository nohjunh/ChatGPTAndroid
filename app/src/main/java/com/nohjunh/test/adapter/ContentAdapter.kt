package com.nohjunh.test.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.nohjunh.test.R
import com.nohjunh.test.database.entity.ContentEntity
import timber.log.Timber

class ContentAdapter : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

    private val dataSet: MutableList<ContentEntity> = mutableListOf()

    companion object {
        const val Gpt = 1
        const val User = 2
        private const val STEAM_PAYLOAD = "steam_payload"
    }

    interface DelChatLayoutClick {
        fun onLongClick(view: View, position: Int)
    }
    var delChatLayoutClick : DelChatLayoutClick? = null

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val contentTV : TextView = view.findViewById(R.id.rvItemTV)
        val delChatLayout : ConstraintLayout = view.findViewById(R.id.chatLayout)
        val idHolder : TextView = view.findViewById(R.id.holdingId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == Gpt) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.gpt_content_item, parent, false)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_content_item, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.contentTV.text = dataSet[position].content
        holder.idHolder.text = dataSet[position].id.toString()

        holder.delChatLayout.setOnLongClickListener { view ->
            delChatLayoutClick?.onLongClick(view, position)
            return@setOnLongClickListener true
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            if (payloads[0].toString() == STEAM_PAYLOAD) {
                holder.contentTV.text = dataSet[position].content
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].gptOrUser == 1) { // Gpt
            Gpt
        } else {
            User
        }
    }

    private val steamBuilder = StringBuilder()

    fun addChatGpt(data: String) {
        Timber.d("addLast: $data")
        steamBuilder.clear()
        dataSet.add(ContentEntity(0, data, Gpt))
        notifyItemInserted(dataSet.size - 1)
    }

    fun appendLast(data: String) {
        steamBuilder.append(data)
        val last = dataSet.size - 1
        dataSet[last].content = steamBuilder.toString()
        notifyItemChanged(last, STEAM_PAYLOAD)
    }

    fun addMsg(msg: ContentEntity) {
        dataSet.add(msg)
        notifyItemInserted(dataSet.size - 1)
    }

    fun submitList(it: List<ContentEntity>) {
        dataSet.clear()
        dataSet.addAll(it)
        notifyDataSetChanged()
    }

    fun getLastContent(): String {
        return steamBuilder.toString()
    }


}