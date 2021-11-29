package com.rickirick.fragmentartbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.rickirick.fragmentartbook.ListFragmentDirections
import com.rickirick.fragmentartbook.databinding.RecyclerRowBinding
import com.rickirick.fragmentartbook.model.Model

class ArtAdapter (val artList : List<Model>) : RecyclerView.Adapter<ArtAdapter.ArtHolder>() {

    class ArtHolder(val recyclerRowBinding: RecyclerRowBinding) : RecyclerView.ViewHolder(recyclerRowBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtHolder(recyclerRowBinding)
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.recyclerRowBinding.recyclerText.text = artList.get(position).pictureName
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToDetailsFragment(artList[position].id, "old")
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return artList.size
    }
}