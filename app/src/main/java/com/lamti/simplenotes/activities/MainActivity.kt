package com.lamti.simplenotes.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.lamti.simplenotes.R
import com.lamti.simplenotes.firebase.NoteItem
import com.lamti.simplenotes.firebase.NotesAdapter
import com.lamti.simplenotes.utils.Animations
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mNotesList: ArrayList<NoteItem>? = null
    private val mAnimations: Animations = Animations()
    private var mCheckedList: ArrayList<Int>? = null
    private var mNotesAdapter: NotesAdapter? = null
    private var mCheckedListNoteIDs: ArrayList<String>? = null

    private val mAuth = FirebaseAuth.getInstance()
    private val FIRE_TAG = "FIRE_TAG"
    private var fireAdapter: FirestoreRecyclerAdapter<NoteItem, NotesHolder>? = null
    private val mFirestoreDB = FirebaseFirestore.getInstance()
    private var mListenerRegistration: ListenerRegistration? = null

    private var mColRef: CollectionReference? = null
    private var mAuthUID: String = "not_null"
    private var oneTimeFlag: Boolean = true
    private var mUserNotesCol: CollectionReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(FIRE_TAG, "onCreate")

        initVars()
        clickListeners()
    }

    private fun initVars() {
        mAuthUID = mAuth.currentUser!!.uid
        mUserNotesCol = mFirestoreDB.collection("notes").document(mAuthUID).collection("userNotes")

    }

    private fun clickListeners() {
        addIB.setOnClickListener { mAnimations.multipleSharedAnimation(this@MainActivity,
                SingleNoteActivity::class.java, addBgV, mainCL) }

        deleteNotesFAB.setOnClickListener { deleteNotes() }

        logoutB.setOnClickListener { logout() }
    }

    private fun showDeleteFAB () {

        val screenHeight = mAnimations.getScreenHeight(this@MainActivity)
        val navbarHeight = mAnimations.getNavbarHeight(this@MainActivity)
        val toY: Float = screenHeight - navbarHeight- navbarHeight - navbarHeight

        if (deleteNotesFAB.visibility == View.INVISIBLE)
            mAnimations.enterAnimateY(deleteNotesFAB, screenHeight  - navbarHeight, toY ,450, 300)
    }

    private fun hideDeleteFAB () {

        val screenHeight = mAnimations.getScreenHeight(this@MainActivity)
        val navbarHeight = mAnimations.getNavbarHeight(this@MainActivity)
        val toY: Float = screenHeight - navbarHeight- navbarHeight - navbarHeight

        if (deleteNotesFAB.visibility == View.VISIBLE)
            mAnimations.exitAnimateY(deleteNotesFAB, screenHeight  - navbarHeight, toY ,450, 1)
    }

    // Firebase
    private fun logout() {

        mAuth.signOut()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
        return
    }

    private fun initRecyclerView() {

        // init recyclerView
        notesRV.setHasFixedSize(true)
        //mPlayersRV.setLayoutManager(new GridLayoutManager(this, 2));
        notesRV.layoutManager = LinearLayoutManager(this)
        notesRV.itemAnimator = DefaultItemAnimator()
        //notesRV.fireAdapter = mNotesAdapter
        notesRV.adapter = fireAdapter
    }

    private fun deleteNotes() {

        var boolean = false
        mCheckedListNoteIDs!!.forEachIndexed { index, value ->

            mUserNotesCol!!.document(value).delete()
                    .addOnSuccessListener( {
                        Log.d(FIRE_TAG, "DocumentSnapshot successfully deleted!")

                        mCheckedListNoteIDs!!.remove(value)

                        if ( !boolean ) hideDeleteFAB()
                        boolean = true

                        if ( mCheckedListNoteIDs!!.size == 0 ) mCheckedList!!.clear()
                        //if ( mCheckedListNoteIDs!!.size == 0 ) hideDeleteFAB()
                    })
                    .addOnFailureListener( {
                        e -> Log.w(FIRE_TAG, "Error deleting document", e) })
        }
    }


    // Firebase-ui
    private fun initFireAdapter() {

        mCheckedList = ArrayList()
        mCheckedListNoteIDs = ArrayList()

        Log.d(FIRE_TAG, "auth: $mAuthUID")
        val query = mFirestoreDB.collection("notes").document(mAuthUID).collection("userNotes").orderBy("timestamp")
        val response = FirestoreRecyclerOptions.Builder<NoteItem>()
                .setQuery(query, NoteItem::class.java)
                .build()

        fireAdapter = object : FirestoreRecyclerAdapter<NoteItem, NotesHolder>(response) {
            override fun onBindViewHolder(holder: NotesHolder, position: Int, model: NoteItem) {

                progressBar.visibility = View.GONE
                val noteColor = mAnimations.getNoteColor(this@MainActivity, model.categoryID)

                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val noteDate: String?
                try {
                    noteDate = simpleDateFormat.format(  model.timestamp )
                    holder.noteDateTV.text = "·$noteDate"
                } catch (e: Exception) { }

                holder.noteTextTV.text = model.noteText
                holder.noteTitleTV.text = model.noteTitle

                /*Glide.with(applicationContext)
                        .load(model.getImage())
                        .into(holder.imageView)*/

                holder.noteTextCV.setOnClickListener({ v ->

                    mAnimations.multipleSharedAnimation( this@MainActivity, SingleNoteActivity::class.java,
                            holder.noteTextCV, holder.itemView, model )
                })

                holder.noteCircleV.setOnClickListener {

                    val finalX = it.x + it.width / 2
                    val finalY = it.y + it.height / 2

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (holder.noteTextCV.cardBackgroundColor.defaultColor == noteColor) {

                            mCheckedList!!.remove(position)
                            mCheckedListNoteIDs!!.remove(model.noteID)
                            Log.d(FIRE_TAG, "Reverse Reveal Animation")
                            mAnimations.reverseReveal(holder.backgroundV, holder.noteTextCV, holder.noteCircleV,
                                    finalX, finalY, noteColor )
                        } else {

                            mCheckedList!!.add(position)
                            mCheckedListNoteIDs!!.add(model.noteID)
                            Log.d(FIRE_TAG, "Reveal Animation")
                            mAnimations.reveal(holder.backgroundV, holder.noteTextCV, holder.noteCircleV,
                                    finalX, finalY, noteColor )
                        }
                    }

                    if ( mCheckedList!!.size > 0) showDeleteFAB() else hideDeleteFAB()
                    Log.d(FIRE_TAG, "Checked list size: ${mCheckedList!!.size}, other: ${mCheckedListNoteIDs!!.size}")
                }

                if ( mCheckedList!!.contains(position) ) {

                    holder.noteTextCV.setCardBackgroundColor( noteColor )
                    mAnimations.changeDrawableViewColor( holder.noteCircleV, Color.WHITE )

                } else {

                    holder.noteTextCV.setCardBackgroundColor( Color.WHITE )
                    mAnimations.changeDrawableViewColor( holder.noteCircleV,  noteColor )
                }
            }

            override fun onCreateViewHolder(group: ViewGroup, i: Int): NotesHolder {
                Log.d(FIRE_TAG, "onCreateViewHolder...")

                val view = LayoutInflater.from(group.context).inflate(R.layout.note_item, group, false)
                return NotesHolder(view!!)
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.e("FIRE_error", e.message)
            }
        }

        // Scroll to bottom on new messages
        fireAdapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                notesRV.smoothScrollToPosition(fireAdapter!!.itemCount)
            }
        })
    }

    class NotesHolder(rootView: View ) : RecyclerView.ViewHolder(rootView) {

        var noteTextCV: CardView = rootView.findViewById(R.id.noteItemCV)
        var noteTextTV: TextView = rootView.findViewById(R.id.noteItemTV)
        var noteDateTV: TextView = rootView.findViewById(R.id.noteItemDateTV)
        var noteCircleV: View = rootView.findViewById(R.id.noteItemCircle)
        var backgroundV: View = rootView.findViewById(R.id.noteItemBackgroundV)
        var noteTitleTV: TextView = rootView.findViewById(R.id.noteItemTitleTV)
    }


    // Activity functions
    override fun onStart() {
        super.onStart()
        Log.d(FIRE_TAG, "onStart")

        initFireAdapter()
        initRecyclerView()
        fireAdapter!!.startListening()
    }

    override fun onResume() {
        super.onResume()
        if ( mCheckedList!!.size == 0) deleteNotesFAB.visibility = View.INVISIBLE
        Log.d(FIRE_TAG, "checkedList size: ${mCheckedList!!.size}")
    }

    override fun onPause() {
        super.onPause()
        if ( mCheckedList!!.size == 0) deleteNotesFAB.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        //mListenerRegistration?.remove()
        fireAdapter!!.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if ( item.itemId == R.id.addNote ) {
                //val intent = Intent(this, NoteActivity::class.java)
                //startActivity(intent)
            } else if ( item.itemId == R.id.logout ) {
                logout()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    // Not Used
    private fun fillFirestorePlayersList() {

        mColRef = mFirestoreDB.collection("games")

        mColRef!!.document(mAuthUID).collection("notes").addSnapshotListener( { documentSnapshots, e ->

            if ( e != null ) Log.d(FIRE_TAG, "" + e.message)

            documentSnapshots.documentChanges
                    .filter { it.type == DocumentChange.Type.ADDED }
                    .map {
                        //val name = doc.document.getString("name")
                        //val userId = doc.document.id
                        it.document.toObject<NoteItem>(NoteItem::class.java)
                    }
                    .forEach { mNotesList!!.add(it) }

            Log.d(FIRE_TAG, "notes number: ${mNotesList!!.size}")
        })
    }

    private fun initNotesList() {

        mNotesList = ArrayList()

        mListenerRegistration = mUserNotesCol!!.orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    for (doc in querySnapshot.documentChanges) {

                        val noteText = doc.document.getString("noteText")
                        val userEmail = doc.document.getString("userEmail")!!
                        val userID = doc.document.getString("userID")
                        val timestamp = doc.document.data["timestamp"]
                        var id = doc.document.id

                        var noteItem = NoteItem(noteText, timestamp, userEmail, userID, id)


                        when (doc.type) {
                            DocumentChange.Type.ADDED -> mNotesList!!.add(noteItem)
                            DocumentChange.Type.MODIFIED -> mNotesAdapter!!.notifyDataSetChanged()
                            DocumentChange.Type.REMOVED -> Log.d(FIRE_TAG, "Removed note: " + doc.document.data)
                        }
                    }

                    mNotesAdapter = NotesAdapter(this@MainActivity, mNotesList!!)
                    mNotesAdapter!!.notifyDataSetChanged()
                    //initRecyclerView()

                    if (oneTimeFlag) {
                        oneTimeFlag = false
                        notesRV.scrollToPosition(mNotesAdapter?.itemCount!! - 1)
                    }
                }
    }
}
