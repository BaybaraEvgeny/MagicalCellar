package com.magicalcellar

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList
import android.content.Intent
import android.util.Log
import android.widget.*
import kotlinx.android.synthetic.main.wine_list.*


class WineList : Fragment() {
    private var listNotes = ArrayList<Wine>()

    override fun onResume() {
        loadQueryAll()
        super.onResume()
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.wine_list, container,
                false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getActivity().setTitle("My Collection")

        loadQueryAll()

        lvNotes.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            Toast.makeText(activity, "Click on " + listNotes[position].title, Toast.LENGTH_SHORT).show()
        }
    }

    fun loadQueryAll() {

        val dbManager = DBManager(activity)
        val cursor = dbManager.queryAll()

        listNotes.clear()
        if (cursor.moveToFirst()) {

            do {
                val id = cursor.getInt(cursor.getColumnIndex("Id"))
                val title = cursor.getString(cursor.getColumnIndex("Title"))
                val content = cursor.getString(cursor.getColumnIndex("Content"))

                listNotes.add(Wine(id, title, content))

            } while (cursor.moveToNext())
        }

        val notesAdapter = NotesAdapter(activity, listNotes)
        lvNotes.adapter = notesAdapter
    }

    inner class NotesAdapter(context: Context, private var notesList: ArrayList<Wine>) : BaseAdapter() {

        private var context: Context? = context

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.wine, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                Log.i("JSA", "set Tag for ViewHolder, position: " + position)
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            val mNote = notesList[position]

            vh.tvTitle.text = mNote.title
            vh.tvContent.text = mNote.content

            vh.ivEdit.setOnClickListener {
                updateNote(mNote)
            }

            vh.ivDelete.setOnClickListener {
                val dbManager = DBManager(this.context!!)
                val selectionArgs = arrayOf(mNote.id.toString())
                dbManager.delete("Id=?", selectionArgs)
                loadQueryAll()
            }

            return view
        }

        override fun getItem(position: Int): Any {
            return notesList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return notesList.size
        }
    }

    private fun updateNote(wine: Wine) {
        val intent = Intent(activity, AddWineActivity::class.java)
        intent.putExtra("MainActId", wine.id)
        intent.putExtra("MainActTitle", wine.title)
        intent.putExtra("MainActContent", wine.content)
        startActivity(intent)
    }

    private class ViewHolder(view: View?) {
        val tvTitle: TextView
        val tvContent: TextView
        val ivEdit: ImageView
        val ivDelete: ImageView

        init {
            this.tvTitle = view?.findViewById<TextView>(R.id.tvTitle) as TextView
            this.tvContent = view.findViewById<TextView>(R.id.tvContent) as TextView
            this.ivEdit = view.findViewById<ImageView>(R.id.ivEdit) as ImageView
            this.ivDelete = view.findViewById<ImageView>(R.id.ivDelete) as ImageView
        }
    }
}