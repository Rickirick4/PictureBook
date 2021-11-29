package com.rickirick.picturebook


import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rickirick.picturebook.databinding.RecyclerRowBinding

class PictureAdapter(val pictureList : ArrayList<Picture> ): RecyclerView.Adapter<PictureAdapter.PictureHolder>() {

    class PictureHolder(val binding: RecyclerRowBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return PictureHolder(binding)
    }

    override fun onBindViewHolder(holder: PictureHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = pictureList.get(position).name
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,DetailsActivity::class.java)
            intent.putExtra("info", "old")
            intent.putExtra("id", pictureList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return pictureList.size
    }
}