package pl.robertsreberski.ketchup.reusables

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.widget.LinearLayout
import pl.robertsreberski.ketchup.R
import pl.robertsreberski.ketchup.pojos.Project

/**
 * Author: Robert Sreberski
 * Creation time: 16:21 12/05/2018
 * Package name: pl.robertsreberski.ketchup.reusables
 */
class PomodoroItem @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : CardView(context, attrs, defStyle) {

    var project: Project? = null
    var _active: Boolean? = null

    constructor(context: Context, project: Project?, active: Boolean) : this(context) {
        this.project = project
        this._active = active
    }

    init {
        if (project != null) {
            this.setCardBackgroundColor(Color.parseColor(project!!.color))
        } else if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.PomodoroItem)
            val n = array.indexCount

            for (i in 0 until n) {
                val attr = array.getIndex(i)
                when (attr) {
                    R.styleable.PomodoroItem_color -> {
                        val color = array.getColor(attr, 0)
                        this.setCardBackgroundColor(color)
                    }
                    R.styleable.PomodoroItem_isActive -> {
                        _active = array.getBoolean(attr, false)
                    }
                }
            }

            array.recycle()
        }
        this.radius = 8.px.toFloat()
        this.elevation = 2.px.toFloat()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val params = this.layoutParams as LinearLayout.LayoutParams
        params.setMargins(2.px, 2.px, 2.px, 2.px)
        params.width = (if (_active ?: false) 64 else 16).px
        params.height = 16.px
        this.layoutParams = params

    }

    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}