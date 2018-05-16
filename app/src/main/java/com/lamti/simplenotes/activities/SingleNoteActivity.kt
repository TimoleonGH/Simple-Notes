package com.lamti.simplenotes.activities

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.design.widget.Snackbar
import android.support.transition.ChangeBounds
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.lamti.simplenotes.R
import com.lamti.simplenotes.dialogs.CategoryDialog
import com.lamti.simplenotes.dialogs.SetTitleDialog
import com.lamti.simplenotes.utils.Animations
import com.lamti.simplenotes.utils.Utils
import kotlinx.android.synthetic.main.activity_single_note.*


class SingleNoteActivity : AppCompatActivity(), SetTitleDialog.InputTitleDialogListener, CategoryDialog.CategoriesDialogListener {

    // System variables
    private val TAG = "TAGARA_:D"
    private var mUtils: Utils = Utils()
    private var mAnimations: Animations = Animations()

    // Animations variables
    private var changed = false
    private var colorAnimator: ObjectAnimator? = null
    private val transition = ChangeBounds()
    private val constraintSet1 = ConstraintSet()
    private val constraintSet2 = ConstraintSet()
    private val constraintSet3 = ConstraintSet()

    // Firestore
    private val mAuth = FirebaseAuth.getInstance()
    private val mAuthUID = FirebaseAuth.getInstance().currentUser!!.uid
    private val mFirestoreDB = FirebaseFirestore.getInstance()
    private val mNotesCol = mFirestoreDB.collection("notes")
    private var mUserNotesCol: CollectionReference? = null
    private var noteID: String? = null
    private var noteTitle: String = "untitled"
    private var categoryTitle: String = "untitled"
    private var categoryID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_note)

        initVars()
        animateNote()
        clickListeners()
        setTitleValueFromFirebase()
        setTitleValueColorAnimation()
        noteEditTextTouchListener( false )
    }

    private fun initVars() {
        mUserNotesCol = mFirestoreDB.collection("notes").document(mAuthUID).collection("userNotes")

        val receivedIntent = intent
        noteID = receivedIntent.getStringExtra("noteID")
        val noteText = receivedIntent.getStringExtra("noteText")

        if ( noteID != null ) {

            noteET.setText( noteText )
        }
    }


    // Views
    private fun clickListeners() {

        noteInstructionsTV.setOnClickListener {

            //if ( noteInstructionsTV.text == "WRITE" ) mUtils.showKeyboard(this@SingleNoteActivity)

            if ( noteInstructionsTV.text == "FINISH" ) {

                mUtils.hideKeyboard(this@SingleNoteActivity, noteET)
                noteET.ellipsize = TextUtils.TruncateAt.END
                noteET.setLines(4)
            } else {
                noteET.ellipsize = TextUtils.TruncateAt.END
                noteET.setLines(11)
            }

            applyTransition()
            changed = !changed

            changeInstructionsText( changed )
            setSaveButtonClickable( changed )
            noteEditTextTouchListener( changed )

            //mUtils.setWindowResize(this@SingleNoteActivity, noteET, !changed)
        }

        titleValueTV.setOnClickListener {

            mUtils.setWindowResize(this@SingleNoteActivity, noteET, false)
            showTitleDialog()
        }

        categoryValueTV.setOnClickListener {

            mUtils.setWindowResize(this@SingleNoteActivity, noteET, false)
            showCategoryDialog()
        }

        saveB.setOnClickListener {

            uploadNoteToFirestore()
        }
    }

    private fun noteEditTextTouchListener ( flag: Boolean ) {

        //noteET.isFocusable = !flag

        /*if ( !flag ) {
            noteET.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(noteET, InputMethodManager.SHOW_IMPLICIT)
        }*/

        noteET.isCursorVisible = !flag

        val otl = View.OnTouchListener { v, event ->
            Log.d(TAG, "onTouch")

            //noteET.isFocusableInTouchMode = !flag

            flag // the listener has consumed the event
        }

        noteET.setOnTouchListener(otl)
    }

    private fun changeInstructionsText ( changed: Boolean ) {

        if ( changed ) noteInstructionsTV.text = "WRITE" else noteInstructionsTV.text = "FINISH"
    }

    private fun setSaveButtonClickable ( changed: Boolean ) {

        saveB.isClickable = changed
    }


    // Firebase
    private fun uploadNoteToFirestore () {

        val note: HashMap<String, Any> = HashMap<String, Any>()
        note["userEmail"] = mAuth.currentUser!!.email.toString()
        note["userID"] = mAuthUID
        note["noteText"] = noteET.text.toString()
        note["timestamp"] = FieldValue.serverTimestamp()
        note["noteTitle"] = noteTitle
        note["categoryTitle"] = categoryTitle
        note["categoryID"] = categoryID

        if ( noteID == null ) {

            val ref = mUserNotesCol!!.document()
            val refID = ref.id
            note["noteID"] = refID

            ref.set(note).addOnSuccessListener({
                mUtils.toast(this@SingleNoteActivity, "Note uploaded!")
            }).addOnFailureListener { Log.d(TAG, " error: ${it.message}") }

        } else {

            note["noteID"] = noteID!!

            mUserNotesCol!!.document(noteID!!).update(note).addOnSuccessListener({
                mUtils.toast(this@SingleNoteActivity, "Note uploaded!")
            }).addOnFailureListener { Log.d(TAG, " error: ${it.message}") }
        }

        noteET.text.clear()
        finish()
    }

    private fun setTitleValueFromFirebase() {

        noteID?.let { mUserNotesCol!!.document(it).get()
                .addOnCompleteListener( { task ->

                    var b = true
                    if ( !task.isSuccessful ) b = false
                    val document = task.result
                    if ( document == null ) b = false

                    if ( b ) {
                        colorAnimator!!.cancel()
                        titleValueTV.text = task.result.getString("noteTitle")
                        titleValueTV.setTextColor(resources.getColor(R.color.colorPrimary))
                    } else {
                        titleValueTV.text = "untitled :D"
                    }
                })
        }
    }

    private fun uploadNoteCategory ( categoryID: Int, categoryTitle: String ) {

        this.categoryID = categoryID
        this.categoryTitle = categoryTitle

        /*var map: HashMap<String, Any> = HashMap()
        map["categoryId"] = categoryID
        map["categoryTitle"] = categoryTitle

        noteID?.let { mUserNotesCol!!.document(it).update(map).addOnSuccessListener {


            }

        }*/
    }


    // Animations
    private fun applyTransition() {

        TransitionManager.beginDelayedTransition(singleNoteCL, transition) //TransitionManager.go(Scene(singleNoteCL), transition)
        val constraint = if (changed) constraintSet1 else constraintSet2
        constraint.applyTo(singleNoteCL)
    }

    private fun animateNote () {

        constraintSet1.clone(singleNoteCL)
        constraintSet2.clone(this@SingleNoteActivity, R.layout.single_note_edit_layout)

        transition.interpolator = DecelerateInterpolator()
        transition.duration = 350
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {

                if ( changed ) noteCV.radius = 20f else noteCV.radius = 0f

                constraintSet3.clone(singleNoteCL)
                constraintSet3.center(noteInstructionsTV.id, ConstraintLayout.LayoutParams.PARENT_ID,
                        ConstraintLayout.LayoutParams.TOP, 0, ConstraintLayout.LayoutParams.PARENT_ID,
                        ConstraintLayout.LayoutParams.BOTTOM, 0, 0.00000000001f)
                constraintSet3.applyTo(singleNoteCL)
            }

            override fun onTransitionEnd(transition: Transition) {

                if ( noteInstructionsTV.text == "FINISH" ) {

                    mUtils.showKeyboard(this@SingleNoteActivity)

                }

                if ( noteInstructionsTV.text == "WRITE" ){
                    animateMenuViews()
                }

            }

            override fun onTransitionCancel(transition: Transition) {}

            override fun onTransitionPause(transition: Transition) {}

            override fun onTransitionResume(transition: Transition) {}
        })
    }

    private fun setTitleValueColorAnimation() {

        val primaryColor = resources.getColor(R.color.colorCyanLight)
        val accentColor = resources.getColor(R.color.colorAccent)
        colorAnimator = mUtils.setColorAnimation(titleValueTV, "textColor", primaryColor, accentColor)
    }

    private fun animateMenuViews() {

        Log.d(TAG, "screen height: ${mAnimations.getScreenHeight(this@SingleNoteActivity)}")

        val screenHeight = mAnimations.getScreenHeight(this@SingleNoteActivity)
        val navbarHeight = mAnimations.getNavbarHeight(this@SingleNoteActivity)
        val toY: Float = screenHeight - navbarHeight- navbarHeight - navbarHeight

        mAnimations.enterAnimateY(saveB, screenHeight  - navbarHeight, toY ,150, 225)
        mAnimations.enterAnimateY(noteCategoryIV, screenHeight  - navbarHeight, toY ,150, 150)
        mAnimations.enterAnimateY(categoryTV, screenHeight  - navbarHeight, toY ,150, 175)
        mAnimations.enterAnimateY(categoryValueTV, screenHeight  - navbarHeight, toY ,150, 200)
        mAnimations.enterAnimateY(alarmIV, screenHeight  - navbarHeight, toY ,150, 75)
        mAnimations.enterAnimateY(reminderTV, screenHeight  - navbarHeight, toY ,150, 100)
        mAnimations.enterAnimateY(reminderValueTV, screenHeight  - navbarHeight, toY ,150, 125)
        mAnimations.enterAnimateY(noteTitleIV, screenHeight  - navbarHeight, toY ,150, 1)
        mAnimations.enterAnimateY(titleTV, screenHeight  - navbarHeight, toY ,150, 25)
        mAnimations.enterAnimateY(titleValueTV, screenHeight  - navbarHeight, toY ,150, 50)
    }


    // Keyboard functions
    override fun onKeyShortcut(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //put your logic here
            mUtils.snackbar(singleNoteCL, "Down pressed!", Snackbar.LENGTH_SHORT)
        } else {
            mUtils.snackbar(singleNoteCL, "keyCode: $keyCode / event: $event", Snackbar.LENGTH_SHORT)
        }

        return super.onKeyShortcut(keyCode, event)
    }

    private fun keyboardAction ( changed: Boolean ) {

        Log.d(TAG, "changed show: $changed")

        if ( !changed ) {
            mUtils.showKeyboard( this@SingleNoteActivity )
        } else {
            mUtils.hideKeyboard(this@SingleNoteActivity, noteET )
        }
    }

    private fun noteEditTextKeyListener() {

        noteET.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

            Log.d(TAG, "keycode: $keyCode / event: $event")

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (event.action == KeyEvent.ACTION_UP) {

                }
                return@OnKeyListener true
            }
            false
        })

        /*private fun EditText.onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        return if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            // Do your thing.
            true  // So it is not propagated.
        } else super.dispatchKeyEvent(event)

    }*/
    }

    // Dialogs
    private fun showTitleDialog() {
        val fragmentManager = supportFragmentManager
        val inputTitleDialog = SetTitleDialog()
        inputTitleDialog.isCancelable = false
        inputTitleDialog.setDialogTitle("Set Title")
        inputTitleDialog.show(fragmentManager, "Input Dialog")
    }

    private fun showCategoryDialog() {
        val fragmentManager = supportFragmentManager
        val categoryDialog = CategoryDialog()
        categoryDialog.isCancelable = false
        categoryDialog.setDialogTitle("Select category")
        categoryDialog.show(fragmentManager, "Category Dialog")
    }

    override fun onFinishInputDialog(inputText: String) {

        noteTitle = inputText
        colorAnimator!!.cancel()
        titleValueTV.text = inputText
        titleValueTV.setTextColor(resources.getColor(R.color.colorPrimary))
        mUtils.setWindowResize(this@SingleNoteActivity, noteET, true)
    }

    override fun onFinishCategoryDialog(categoryTitle: String, categoryId: Int) {

        uploadNoteCategory( categoryId, categoryTitle )
    }


    // Activity functions
    override fun onBackPressed() {
        super.onBackPressed()

        Log.d(TAG, "onBackPressed")
    }

    override fun onStop() {
        super.onStop()

        mUtils.hideKeyboard(this@SingleNoteActivity, noteET)
        Log.d(TAG, "Activity Stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity Destroyed")
    }
}