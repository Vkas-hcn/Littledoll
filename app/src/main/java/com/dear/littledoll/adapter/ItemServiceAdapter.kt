package com.dear.littledoll.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dear.littledoll.R
import com.dear.littledoll.bean.CountryBean
import com.dear.littledoll.databinding.ItemServiceItemBinding
import com.dear.littledoll.utils.DataManager

class ItemServiceAdapter(private val list: MutableList<CountryBean>, private val block: (CountryBean) -> Unit) :
    RecyclerView.Adapter<ItemServiceAdapter.ViewHolder>() {


    class ViewHolder(val binding: ItemServiceItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemServiceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val tagName = "${list[position].getLastName()}-${list[position].getTag()}"
            name.text = tagName
            icon.setImageResource(if (list[position].ldHost == DataManager.selectItem.ldHost) R.mipmap.select_k else R.mipmap.un_select_k)
            root.setOnClickListener { block(list[position]) }
        }
    }
}