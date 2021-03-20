package com.tanvir.tractivity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.dialog_save.*

abstract class Save_Dialog : AppCompatDialogFragment() {
  //  private val listener : Save_DialogInterface


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        val inflater = activity?.layoutInflater
//        val inflater = layoutInflater
        val dialogLayout = inflater?.inflate(R.layout.dialog_save,null)
        builder.setView(dialogLayout)
        builder.setNegativeButton("cancel",
                DialogInterface.OnClickListener { dialog, id ->
                    getDialog()?.cancel()
                })
        builder.setPositiveButton("Submit",
                DialogInterface.OnClickListener { dialog, id ->
                    var username : String = et_activityName.text.toString()

                })


        return builder.create()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)


    }

   open interface Save_DialogInterface {
        fun applyTexts(activityN :String)
    }

}