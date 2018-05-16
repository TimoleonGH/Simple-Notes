package com.lamti.simplenotes.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lamti.simplenotes.R
import com.lamti.simplenotes.firebase.NoteItem
import java.util.*

/**
 * Created by Timoleon on 2/11/2018.
 */
class CategoryDialog : DialogFragment() {

    private var oneTimeFlag = true
    private var dialogTitle: String = "Title"
    private var mCategories: ArrayList<NoteItem>? = null
    private var mCategoriesAdapter: CategoriesAdapter? = null

    interface CategoriesDialogListener {
        fun onFinishCategoryDialog(categoryTitle: String, categoryId: Int)
    }

    fun setDialogTitle(title: String) {
        dialogTitle = title
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.category_dialog, container)
        val categoryCancelTV = view.findViewById(R.id.categoryItemCancelTV) as TextView
        val categoriesRV = view.findViewById(R.id.categoriesRV) as RecyclerView
        val categoryOkTV = view.findViewById(R.id.categoryItemOkTV) as TextView

        if (oneTimeFlag) initCategories( categoriesRV )

        categoryOkTV.setOnClickListener {

            val activity = activity as CategoriesDialogListener?
            activity!!.onFinishCategoryDialog(catTitle, catID)

            dialog.dismiss()
        }

        categoryCancelTV.setOnClickListener { dialog.dismiss() }

        dialog.setCancelable(true)
        dialog.setTitle(dialogTitle)

        return view
    }

    private fun initCategories(categoriesRV: RecyclerView) {

        oneTimeFlag = false
        mCategories = ArrayList()
        val intsList = listOf(0, 1, 2, 3, 4, 5, 6, 7)

        for ( index in intsList ) {

            var noteItem = NoteItem()
            noteItem.categoryID = index
            noteItem.categoryTitle = "category $index"
            mCategories!!.add(noteItem)
        }

        mCategoriesAdapter = CategoriesAdapter( activity!!, mCategories!! )

        categoriesRV.setHasFixedSize(true)
        categoriesRV.layoutManager = LinearLayoutManager( activity )
        categoriesRV.itemAnimator = DefaultItemAnimator()
        categoriesRV.adapter = mCategoriesAdapter
    }

    companion object {

        private lateinit var catTitle: String
        private var catID: Int = 0

        fun funCatTitle(categoryTitle: String ) {
            catTitle = categoryTitle
            catID = categoryTitle.toInt()
            Log.d("TAGARA", "$categoryTitle, This title: $catTitle")
        }
    }

}