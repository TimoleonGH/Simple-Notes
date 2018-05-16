package com.lamti.simplenotes.firebase

/**
 * Created by Timoleon on 2/6/2018.
 */
data class NoteItem (val noteText: String = " ", val timestamp: Any? = " ", val userEmail: String = " ",
                     val userID: String = " ", val noteID: String = " ", val noteTitle: String = " ",
                     var categoryTitle: String = " ", var categoryID: Int = 0)