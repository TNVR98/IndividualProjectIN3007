package com.tanvir.tractivity

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tanvir.tractivity.model.ActivityClass
import kotlinx.android.synthetic.main.item_activity.view.*

class ActivityAdapter (private val list : ArrayList<ActivityClass> ) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onItemClickListener : OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ActivitiesViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is ActivitiesViewHolder){
            holder.itemView.tv_activityName.text=item.name
            holder.itemView.tv_date.text = item.date
            holder.itemView.setOnClickListener{
                if(onItemClickListener!=null){
                    onItemClickListener!!.onItemClick(position,item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ActivitiesViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val textView1 : TextView = itemView.tv_activityName
        val textView2 : TextView = itemView.tv_date
    }

    fun setOnclickListener(onClickListener: OnItemClickListener){
        this.onItemClickListener = onClickListener
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int, item: ActivityClass)
    }


}