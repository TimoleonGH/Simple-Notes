package com.lamti.simplenotes.dialogs

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.lamti.simplenotes.R
import com.lamti.simplenotes.firebase.NoteItem
import com.lamti.simplenotes.utils.Animations

/**
 * Created by Timoleon on 2/11/2018.
 */
class CategoriesAdapter ( private val activity: Activity, private val categoriesList: ArrayList<NoteItem> ) :
        RecyclerView.Adapter<CategoriesAdapter.CategoriesHolder>() {

    private val mAnimations: Animations = Animations()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CategoriesHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.category_item, parent, false)
        return CategoriesHolder(view)
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: CategoriesHolder?, position: Int) {

        var categoryTitle = categoriesList[position].categoryTitle
        var categoryID = categoriesList[position].categoryID

        holder?.categoryTitle?.text = categoryTitle
        mAnimations.changeDrawableViewColor(holder!!.categoryCircleV, mAnimations.getNoteColor(activity, categoryID))

        Log.d("FIRE", " ----->  $categoryTitle")

        holder.itemView?.setOnClickListener {

            CategoryDialog.funCatTitle("$position")
            Toast.makeText(activity, "Adapter title: $position", Toast.LENGTH_SHORT).show()
        }
    }



    class CategoriesHolder( rootView: View ) : RecyclerView.ViewHolder(rootView) {

        var categoryTitle: TextView = rootView.findViewById(R.id.categoryItemTitleTV)
        var categoryCircleV: View = rootView.findViewById(R.id.categoryItemCircle)
    }
}