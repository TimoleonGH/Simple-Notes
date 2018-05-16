package com.lamti.simplenotes.firebase

import android.app.Activity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lamti.simplenotes.R
import com.lamti.simplenotes.activities.SingleNoteActivity
import com.lamti.simplenotes.utils.Animations

/**
 * Created by Timoleon on 2/6/2018.
 */
class NotesAdapter ( val context: Activity, val noteList: ArrayList<NoteItem> ) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    /*private var mNotesList: List<NoteItem>? = null
    private var mContext: Context? = null*/

    /*constructor ( activity: Context, list: List<NoteItem> ) : this() {

        this.mContext = activity
        this.mNotesList = list
    }*/

    private val mAnimations: Animations = Animations()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.note_item, parent, false)
        return NotesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder?, position: Int) {

        var noteText = noteList[position].noteText
        var noteDate = noteList[position].timestamp

        holder?.noteTextTV?.text = noteText
        holder?.noteTextInstructionsTV?.text = noteDate.toString()
        /*holder?.noteTextTV?.setTextColor(activity.resources.getColor(R.color.colorCyan))
        holder?.noteTextTV?.setBackgroundColor(activity.resources.getColor(R.color.colorPinky))*/
        Log.d("FIRE", " ----->  $noteText")

        holder?.noteTextCV?.setOnClickListener {

            mAnimations.multipleSharedAnimation( context, SingleNoteActivity::class.java,
                    holder.noteTextCV, holder.itemView, noteList[position] )
        }
    }


    class NotesViewHolder( rootView: View ) : RecyclerView.ViewHolder(rootView) {

        var noteTextCV: CardView = rootView.findViewById(R.id.noteItemCV)
        var noteTextTV: TextView = rootView.findViewById(R.id.noteItemTV)
        var noteTextInstructionsTV: TextView = rootView.findViewById(R.id.noteItemTitleTV)

    }
}