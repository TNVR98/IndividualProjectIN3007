package com.tanvir.tractivity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tanvir.tractivity.R
import com.tanvir.tractivity.model.ActivityClass
import kotlinx.android.synthetic.main.item_activity.view.*

/**
 * the Adapter class of Activity list RecycleView
 * source followed: https://developer.android.com/guide/topics/ui/layout/recyclerview
 *                  https://www.youtube.com/watch?v=6Gm3eMG8KqI
 *                  https://www.youtube.com/watch?v=afl_i6uvvU0
 *                  https://guides.codepath.com/android/using-the-recyclerview
 */
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