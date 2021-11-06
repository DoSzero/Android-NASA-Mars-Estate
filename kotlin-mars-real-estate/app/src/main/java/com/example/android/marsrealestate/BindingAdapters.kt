
package com.example.android.marsrealestate

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.overview.MarsApiStatus
import com.example.android.marsrealestate.overview.PhotoGridAdapter

@BindingAdapter("listData")
    fun bindRecyclerView(recyclerView: RecyclerView, data: List<MarsProperty>?) {
        val adapter = recyclerView.adapter as PhotoGridAdapter
        adapter.submitList(data)
}

// Use Glide lib to load an image by URL into ImageView
@BindingAdapter("imageUrl")
    fun bindImage(imgView:ImageView,imgUrl:String?){
        imgUrl?.let {
            val imgUrl = it.toUri().buildUpon().scheme("https").build()
            // server pull the images from requires HTTPS and we load images by Glide
            Glide.with(imgView.context)
                .load(imgUrl)
                .apply(RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
                .into(imgView)
        }
    }

@BindingAdapter("marsApiStatus")
    fun bindStatus(statusImageView: ImageView, status: MarsApiStatus?) {
        when (status) {
            MarsApiStatus.LOADING -> {
                statusImageView.visibility = View.VISIBLE
                statusImageView.setImageResource(R.drawable.loading_animation)
            }
             MarsApiStatus.ERROR -> {
                 statusImageView.visibility = View.VISIBLE
                 statusImageView.setImageResource(R.drawable.ic_connection_error)
             }
            MarsApiStatus.DONE -> {
                statusImageView.visibility = View.GONE
            }
    }
}