package com.dennis.loopview

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dennis.loopview.loopview.LoopView
import com.dennis.loopview.loopview.LoopViewAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loopView = findViewById<LoopView>(R.id.loop_view)
        val data = ArrayList<String>()
        (0 until 5).map { data.add(it.toString()) }
        val adapter = CustomLoopViewAdapter(this, data)
        loopView.setAdapter(adapter)
        adapter.notifyChanged()
        loopView.setAnimationDuration(1000)
        loopView.startFlipping()
    }

    class CustomLoopViewAdapter(context: Context, data: List<String>) : LoopViewAdapter<String>(context, data) {
        override fun createItemView(): Int {
            return R.layout.item
        }

        override fun bindView(view: View, t: String, position: Int) {
            val textViewMain: TextView = view.findViewById(R.id.text_view_main)
            textViewMain.text = t
        }

        override fun onItemClick(data: String, position: Int) {
            Toast.makeText(context,"data is $data,position is $position",Toast.LENGTH_SHORT).show()
        }

    }
}
