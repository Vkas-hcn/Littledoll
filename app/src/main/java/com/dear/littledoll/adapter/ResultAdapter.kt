package com.dear.littledoll.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dear.littledoll.R
import com.dear.littledoll.bean.CountryBean
import com.dear.littledoll.databinding.ItemResultBinding
import com.dear.littledoll.databinding.ItemServiceItemBinding
import com.dear.littledoll.utils.DataManager

class ResultAdapter(private val list: MutableList<CountryBean>, private val block: (CountryBean) -> Unit) :
    RecyclerView.Adapter<ResultAdapter.ViewHolder>() {


    class ViewHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            name.text = list[position].getName()
            tag.text = list[position].getTag()
            icon.setImageResource(list[position].getIcon())
            root.setOnClickListener { block(list[position]) }
        }
    }
}