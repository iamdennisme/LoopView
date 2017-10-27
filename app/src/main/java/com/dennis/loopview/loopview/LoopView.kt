package com.dennis.loopview.loopview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.Animation
import android.view.animation.Animation.RELATIVE_TO_PARENT
import android.view.animation.TranslateAnimation
import android.widget.*


@SuppressLint("InflateParams")
/**
 * Created by dennis on 24/10/2017.
 * so.....
 */

class LoopView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attributeSet, defStyleAttr), LoopViewMethods {


    private val DURATION: Long = 1000

    private val outAnimation: TranslateAnimation by lazy {
        val outAnimationCache = TranslateAnimation(RELATIVE_TO_PARENT, 0f, RELATIVE_TO_PARENT, 1f, 0, 0f, 0, 0f)
        outAnimationCache.duration = DURATION
        return@lazy outAnimationCache
    }
    private val inAnimation: TranslateAnimation by lazy {
        val inAnimationCache = TranslateAnimation(RELATIVE_TO_PARENT, -1f, RELATIVE_TO_PARENT, 0f, 0, 0f, 0, 0f)
        inAnimationCache.duration = DURATION
        return@lazy inAnimationCache
    }

    private var itemOnClickListener: ItemOnClickListener? = null

    private val vfLoop: ViewFlipper by lazy {
        val vfLoopCache = ViewFlipper(context)
        val lp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        vfLoopCache.layoutParams = lp
        return@lazy vfLoopCache
    }

    private val container: RelativeLayout by lazy {
        val containerCache = RelativeLayout(context)
        val lp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        containerCache.layoutParams = lp
        return@lazy containerCache
    }

    private val linearAllPoint: LinearLayout by lazy {
        val linearAllPointCache = LinearLayout(context)
        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        linearAllPointCache.layoutParams = lp
        return@lazy linearAllPointCache
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        container.addView(vfLoop)
        addView(container)
    }


    fun <T> setAdapter(loopViewAdapter: LoopViewAdapter<T>) {
        loopViewAdapter.adapterListener = object : LoopViewAdapter.AdapterNotifyChangedListener {
            override fun adapterNotify() {
                setViewFlipper(loopViewAdapter.getViews())
                itemOnClickListener = loopViewAdapter.listener
            }
        }
    }

    private fun setViewFlipper(views: MutableList<View>) {
        //add View
        views.map {
            vfLoop.addView(it)
        }
        //set animation
        vfLoop.inAnimation = inAnimation
        vfLoop.outAnimation = outAnimation
        setOnClickListener {
            itemOnClickListener?.onItemClick(vfLoop.displayedChild)
        }
        vfLoop.inAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationStart(p0: Animation?) {
                for (i in 0 until linearAllPoint.childCount) {
                    linearAllPoint.getChildAt(i).isEnabled = i == vfLoop.displayedChild
                }
            }

            override fun onAnimationEnd(p0: Animation?) {

            }

        })
        initIndicator(views.size)
    }

    private fun initIndicator(size: Int) {
        for (i in 0 until size) {
            val imageView = ImageView(context)
            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            imageView.layoutParams = lp
            val drawable = createDrawableSelector()
            imageView.isEnabled = i == 0
            imageView.background = drawable
            imageView.setPadding(dip2px(context, 5f), dip2px(context, 5f), dip2px(context, 5f), dip2px(context, 5f))
            val llp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            llp.gravity = CENTER_VERTICAL
            llp.setMargins(dip2px(context, 5f), dip2px(context, 5f), dip2px(context, 5f), dip2px(context, 5f))
            linearAllPoint.addView(imageView, llp)
        }
        val rlp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        rlp.bottomMargin = 30
        rlp.leftMargin = 30
        (0 until container.childCount)
                .filter { container.getChildAt(it).hashCode()==linearAllPoint.hashCode() }
                .forEach { return }
        container.addView(linearAllPoint, rlp)
    }

    override fun setIndicator(showPositionDrawable: GradientDrawable, noShowPositionDrawable: GradientDrawable) {
        this.showPositionDrawable = showPositionDrawable
        this.noShowPositionDrawable = noShowPositionDrawable
        if (linearAllPoint.childCount != 0)
            initIndicator(linearAllPoint.childCount)
    }

    private var showPositionDrawable: GradientDrawable = createDefaultShowPosition()
    private var noShowPositionDrawable: GradientDrawable = createDefaultNoShowPosition()


    private fun createDrawableSelector(): StateListDrawable {
        val stateList = StateListDrawable()
        val stateEnabled = android.R.attr.state_enabled
        stateList.addState(intArrayOf(stateEnabled), showPositionDrawable)
        stateList.addState(intArrayOf(-stateEnabled), noShowPositionDrawable)
        return stateList
    }

    private fun createDefaultShowPosition(): GradientDrawable {
        val gd = GradientDrawable()
        gd.shape = GradientDrawable.OVAL
        val color = 0xAAFFFFFF.toInt()
        gd.setColor(color)
        return gd
    }

    private fun createDefaultNoShowPosition(): GradientDrawable {
        val gd = GradientDrawable()
        gd.shape = GradientDrawable.OVAL
        val color = 0x55000000
        gd.setColor(color)
        return gd
    }

    private fun dip2px(context: Context, dip: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dip * scale + 0.5f).toInt()
    }


    private var isRunning: Boolean = false

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility != View.VISIBLE) {
            if (vfLoop.isFlipping) {
                isRunning = vfLoop.isFlipping
                vfLoop.stopFlipping()
            }
        } else {
            if (isRunning) {
                vfLoop.startFlipping()
            }
        }
    }

    override fun startFlipping() {
        vfLoop.startFlipping()
    }

    override fun setFlipInterval(milliseconds: Int) {
        vfLoop.setFlipInterval(milliseconds)
    }

    override fun setAnimationDuration(duration: Long) {
        inAnimation.duration = duration
        outAnimation.duration = duration
    }

    interface ItemOnClickListener {
        fun onItemClick(position: Int)
    }

}

interface LoopViewMethods {
    fun startFlipping()
    fun setFlipInterval(milliseconds: Int)
    fun setAnimationDuration(duration: Long)
    fun setIndicator(showPositionDrawable: GradientDrawable, noShowPositionDrawable: GradientDrawable)
}


