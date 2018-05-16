package com.lamti.simplenotes.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.EditText
import com.lamti.simplenotes.R
import com.lamti.simplenotes.utils.Utils


/**
 * Created by Timoleon on 2/10/2018.
 */

class SetTitleDialog : DialogFragment() {

    private var dialogTitle: String = "Title"

    interface InputTitleDialogListener {
        fun onFinishInputDialog(inputText: String)
    }

    fun setDialogTitle(title: String) {
        dialogTitle = title
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {

        val mUtils = Utils()
        val view = inflater.inflate(R.layout.set_title_dialog, container)

        val txtname = view.findViewById(R.id.txtName) as EditText
        val btnDone = view.findViewById(R.id.btnDone) as Button


        btnDone.setOnClickListener {

            mUtils.hideKeyboard( activity!!.applicationContext, txtname )
            val activity = activity as InputTitleDialogListener?
            activity!!.onFinishInputDialog(txtname.text.toString())
            dismiss()
        }

        txtname.requestFocus()
        dialog.window!!.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setCancelable(true)
        dialog.setTitle(dialogTitle)

        return view
    }
}
/*

class SetTitleDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {

        val args = arguments
        val title = args!!.getString(ARG_TITLE)
        val message = args.getString(ARG_MESSAGE)

        return AlertDialog.Builder(activity!!)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                })
                .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                    targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, null)
                })
                .create()
    }

    companion object {
        val ARG_TITLE = "YesNoDialog.Title"
        val ARG_MESSAGE = "YesNoDialog.Message"
    }
}*/

/* // custom dialog
			final Dialog dialog = new Dialog(activity);
			dialog.setContentView(R.layout.custom);
			dialog.setTitle("Title...");

			// set the custom dialog components - text, image and button
			TextView text = (TextView) dialog.findViewById(R.id.text);
			text.setText("Android custom dialog example!");
			ImageView image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(R.drawable.ic_launcher);

			Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();*/
