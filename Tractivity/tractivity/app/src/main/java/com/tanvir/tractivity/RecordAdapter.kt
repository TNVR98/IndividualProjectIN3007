package com.tanvir.tractivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tanvir.tractivity.model.ActivityRecordClass
import kotlinx.android.synthetic.main.item_activity.view.tv_date
import kotlinx.android.synthetic.main.item_details.view.*


class RecordAdapter (private val list : ArrayList<ActivityRecordClass> ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecordAdapter.RecordViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_details, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is RecordAdapter.RecordViewHolder){
            holder.itemView.tv_date.text = item.date
            holder.itemView.tv_timeSpent.text= formatProgress(item.progress)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    private fun formatProgress(progressInSec : Long) : String{
        val hours = progressInSec / 3600
        val minutes = (progressInSec % 3600) / 60;
        val seconds = progressInSec % 60;

         return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}