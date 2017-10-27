package com.dennis.loopview.loopview

import android.content.Context
import android.view.LayoutInflater
import android.view.View

/**
 * Created by dennis on 26/10/2017.
 * so.....
 */

abstract class LoopViewAdapter<in T>( val context: Context, private val data: List<T>) {

    private val list: ArrayList<View> = ArrayList()

    var adapterListener: AdapterNotifyChangedListener? = null

    private fun initializeView() {
        for ((index, value) in data.withIndex()) {
            val view = getView(createItemView())
            view?.let {
                bindView(it, data[index], index)
                view.tag = value
                list.add(it)
            }
        }
    }

    fun getViews(): ArrayList<View> {
        return list
    }

    private fun getView(res: Int): View? {
        return LayoutInflater.from(context).inflate(res, null, false)
    }

    val listener: LoopView.ItemOnClickListener = object : LoopView.ItemOnClickListener {
        override fun onItemClick(position: Int) {
            onItemClick(data[position], position)
        }
    }

    public fun notifyChanged() {
        initializeView()
        adapterListener?.adapterNotify()
    }

    abstract fun createItemView(): Int
    abstract fun bindView(view: View, t: T, position: Int)
    abstract fun onItemClick(data: T, position: Int)

    interface AdapterNotifyChangedListener {
        fun adapterNotify()
    }
}
