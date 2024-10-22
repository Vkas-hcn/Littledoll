package com.dear.littledoll.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dear.littledoll.utils.DataManager
import com.dear.littledoll.R
import com.dear.littledoll.bean.CountryBean
import com.dear.littledoll.databinding.ItemServiceBinding
import com.dear.littledoll.utils.ConnectUtils

class ServiceAdapter(val block: (CountryBean) -> Unit) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {


    private var openSelect = if (DataManager.selectItem.ldHost.isEmpty()) 0 else -1

    private val list = DataManager.getGrouping()


    class ViewHolder(val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            if (position == 0) {
                icon.setImageResource(R.mipmap.default_icon)
                name.text = "Smart Server"
            } else {
                val item = list[position][0]
                icon.setImageResource(item.getIcon())
                name.text = item.getFistName()
            }
            rvLayout.adapter = ItemServiceAdapter(list[position]) { block(it) }
            rvLayout.visibility = if (position == openSelect) View.VISIBLE else View.GONE
            select.setImageResource(if (position == openSelect) R.mipmap.select_k else R.mipmap.un_select_k)
            content.setBackgroundResource(if (position == openSelect) R.drawable.item_open_bg else R.drawable.item_bg)
            content.setOnClickListener {
                if (position != 0) {
                    openSelect = if (openSelect == position) -1 else position
                    notifyDataSetChanged()
                } else {
                    block(CountryBean())
                }
            }


        }
    }


}