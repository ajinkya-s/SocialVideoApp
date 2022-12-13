package com.example.socialvideoapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialvideoapp.R
import com.example.socialvideoapp.data.localData.Video

class VideoListAdapter(
    private val videoListItemClickListener: (Video) -> Unit
) : ListAdapter<Video, VideoListAdapter.ViewHolder>(VideoDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.video_list_item, parent, false)
        return ViewHolder(itemView, videoListItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val videoListItemClickListener: (Video) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        // ImageView for the video thumbnail
        private val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)

        fun bind(video: Video) {
            // Set the video thumbnail
            Glide.with(thumbnail.context).load(video.image).into(thumbnail)

            // Set the click listener for the video list item
            itemView.setOnClickListener {
                videoListItemClickListener(video)
            }
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }
}

