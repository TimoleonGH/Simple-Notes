package com.lamti.simplenotes.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast


/**
 * Created by Timoleon on 2/5/2018.
 */
class Utils {

    init {

    }

    fun toast ( context: Context, text: String) {
        Toast.makeText( context, text, Toast.LENGTH_SHORT).show()
    }

    // Snackbar
    fun snackbar ( view: View, snackbarText: String, timeLength: Int ) {
        Snackbar.make( view, snackbarText, timeLength).show()
    }

    fun showKeyboard ( context: Context ) {

        //setWindowResize()
        /*constraintSet2.clone(this@CleanCodeActivity, R.layout.clean_code_animate_layout)
        constraintSet3.center(card_view.id, PARENT_ID, TOP, 0, PARENT_ID, BOTTOM, 0, 0.00000000001f)
        constraintSet3.applyTo(cleanCodeCL)*/

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard ( context: Context, editText: EditText ) {


        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun setWindowResize ( activity: Activity, editText: EditText, flag: Boolean ) {

        if ( flag ) activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        else activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        editText.imeOptions = EditorInfo.IME_ACTION_DONE
    }

    fun setColorAnimation( animateView: View, propertyName: String, startColor: Int, endColor: Int ): ObjectAnimator {

        val valueAnimator = ObjectAnimator.ofInt(
                animateView, // Target object
                propertyName, // Property name
                startColor, // Value
                endColor // Value
        )

        // Set value animator evaluator
        valueAnimator.setEvaluator(ArgbEvaluator())
        // Set animation duration in milliseconds
        valueAnimator.duration = 1000
        // Animation repeat count and mode
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.REVERSE

        // Finally, start the animation
        valueAnimator.start()

        return valueAnimator
    }
}