package com.example.poibrowser.utils.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

/**
 * @author Tomislav Curis
 */

@SuppressLint("AppCompatCustomView")
class CustomTextView : TextView {


    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) :
        super(context, attrs, defStyle)


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    constructor(context: Context?) : super(context)

    var holdUrl: String = "https://foursquare.com/"
        get() = field
        set(value) { field = value }

}