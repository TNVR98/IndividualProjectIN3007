package com.tanvir.tractivity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tanvir.tractivity.R
import com.tanvir.tractivity.model.ActivityRecordClass
import kotlinx.android.synthetic.main.item_activity.view.tv_date
import kotlinx.android.synthetic.main.item_details.view.*

/**
 * the Adapter class of Record list RecycleView
 * source followed: https://developer.android.com/guide/topics/ui/layout/recyclerview
 *                  https://www.youtube.com/watch?v=6Gm3eMG8KqI
 *                  https://www.youtube.com/watch?v=afl_i6uvvU0
 *                  https://guides.codepath.com/android/using-the-recyclerview
 */

class RecordAdapter (private val list : ArrayList<ActivityRecordClass> ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecordViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_details, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is RecordViewHolder){
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