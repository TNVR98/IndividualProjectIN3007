package com.tanvir.tractivity.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.tanvir.tractivity.Constants
import com.tanvir.tractivity.R
import com.tanvir.tractivity.model.ActivityClass
import com.tanvir.tractivity.model.ActivityRecordClass
import com.tanvir.tractivity.model.FireStoreClass
import kotlinx.android.synthetic.main.activity_charts.*
import kotlinx.android.synthetic.main.activity_user_profile.*

class ChartsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()
        showChart()
        showBarChart()
    }

    private fun showBarChart(){
        FireStoreClass().fireStore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
            .collection(Constants.ACTIVITIES)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("DB", "All documents received")

                val activityBarEntries = ArrayList<BarEntry>()
                val activityBarLabel = ArrayList<String>()
                var index = 0f
                for (document in documents) {
                    var totalProgress: Long = 0

                    val activity = document.toObject(ActivityClass::class.java)
                    FireStoreClass().fireStore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
                        .collection(Constants.ACTIVITIES).document(activity.name).collection(Constants.RECORDS)
                        .get().addOnSuccessListener { results ->

                            for (result in results){

                                val record : ActivityRecordClass = result.toObject(ActivityRecordClass::class.java)
                                totalProgress += record.progress
                            }

                            if (totalProgress > 0){

                                activityBarEntries.add(BarEntry(index++,totalProgress.toFloat()))
                                activityBarLabel.add(activity.name)
                            }
                            displayBarChart(activityBarEntries,activityBarLabel)
                        }.addOnFailureListener { exception ->
                            Log.d("DB", "Error getting records: ", exception)
                        }

                }

            }.addOnFailureListener { exception ->
                Log.d("DB", "Error getting activities: ", exception)
            }
    }

    private fun showChart(){

        FireStoreClass().fireStore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
            .collection(Constants.ACTIVITIES)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("DB", "All documents received")

                val activityPieData = ArrayList<PieEntry>()

                for (document in documents) {
                    var totalProgress: Long = 0
                    val activity = document.toObject(ActivityClass::class.java)
                    FireStoreClass().fireStore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
                        .collection(Constants.ACTIVITIES).document(activity.name).collection(Constants.RECORDS)
                        .get().addOnSuccessListener { results ->
                            for (result in results){

                                val record : ActivityRecordClass = result.toObject(ActivityRecordClass::class.java)
                                totalProgress += record.progress
                            }

                            if (totalProgress > 0){
                                activityPieData.add(PieEntry(totalProgress.toFloat(), activity.name))

                            }
                            displayPieChart(activityPieData)
                        }.addOnFailureListener { exception ->
                            Log.d("DB", "Error getting records: ", exception)
                        }

                }

            }.addOnFailureListener { exception ->
                Log.d("DB", "Error getting activities: ", exception)
            }
    }


    fun displayBarChart(activityBarEntries: ArrayList<BarEntry>,activityBarLabels : ArrayList<String>){
        var colors :ArrayList<Int> = ArrayList<Int>()
        for (color in ColorTemplate.JOYFUL_COLORS){
            colors.add(color)
        }
        for (color in ColorTemplate.COLORFUL_COLORS){
            colors.add(color)
        }
        val barDataSet = BarDataSet(activityBarEntries,"Bar data")
        barDataSet.valueTextSize=10f
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.colors=colors
        barDataSet.valueFormatter= ProgressValueFormatter()

        val barData = BarData(barDataSet)
        crt_barChart.setDrawGridBackground(false)
        crt_barChart.setFitBars(true)
        crt_barChart.xAxis.valueFormatter= IndexAxisValueFormatter(activityBarLabels)
        crt_barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        crt_barChart.description.isEnabled= false
        crt_barChart.data = barData

        crt_barChart.axisRight.isEnabled = false
        crt_barChart.invalidate()

    }

    fun displayPieChart(activityData: ArrayList<PieEntry>){

        var colors :ArrayList<Int> = ArrayList<Int>()
        for (color in ColorTemplate.JOYFUL_COLORS){
            colors.add(color)
        }
        for (color in ColorTemplate.COLORFUL_COLORS){
            colors.add(color)
        }
        val pieDataSet = PieDataSet(activityData,"Progress")
        pieDataSet.valueFormatter= ProgressValueFormatter()
        pieDataSet.colors = colors
        pieDataSet.valueTextSize = 10f
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.sliceSpace= 2f
        pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE


        val pieData = PieData(pieDataSet)
        crt_pieChart.data = pieData
        crt_pieChart.setEntryLabelColor(Color.BLACK)
        crt_pieChart.legend.isWordWrapEnabled = true
        crt_pieChart.animateXY(50, 50)
        crt_pieChart.description.isEnabled = false
        crt_pieChart.centerText="Time Spent"
        crt_pieChart.invalidate()

    }
    //CUSTOM ACTION BAR
    private fun setupActionBar() {
        setSupportActionBar(toolbar_charts)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        actionBar.title = "Charts"

        toolbar_charts.setNavigationOnClickListener{
            onBackPressed() }
    }

    private class ProgressValueFormatter : ValueFormatter(){
        override fun getFormattedValue(value:Float):String {
            return formatProgress(value.toLong())
        }
        private fun formatProgress(progressInSec : Long) : String{
            val hours = progressInSec / 3600
            val minutes = (progressInSec % 3600) / 60
            val seconds = progressInSec % 60
            return when {
                hours >0 -> {
                    String.format("%02dh%02dm%02ds", hours, minutes, seconds)
                }
                minutes>0 -> {
                    String.format("%02dm%02ds",minutes, seconds)
                }
                else -> {
                    String.format("%02ds", seconds)
                }
            }
        }
    }

}

