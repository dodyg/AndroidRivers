/*
Android Rivers is an app to read and discover news using RiverJs, RSS and OPML format.
Copyright (C) 2012 Dody Gunawinata (dodyg@silverkeytech.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.silverkeytech.android_rivers

import android.view.View.OnTouchListener
import android.view.View
import android.view.MotionEvent

/*
This is a utility class to handle user scroll/swipe movement and to differentiate a click with a swipe motion
 */
public class ScrollMotionDetector (scrollTreshold : Float = 10f){
    companion object {
        public val TAG: String = javaClass<ScrollMotionDetector>().getSimpleName()
    }

    var movementHorizontal: Float = 0f
    var movementVertical: Float = 0f
    val SCROLL_TRESHOLD = scrollTreshold
    var onClick = false
    var deltaHorizontal: Float = 0f
    var deltaVertical: Float = 0f

    fun attach(onClickEvent : (() -> Unit)?, onMoveEvent : ((x : Float, y : Float) -> Unit)?) : OnTouchListener{
        val listener = object : OnTouchListener {
            public override fun onTouch(p0: View?, p1: MotionEvent): Boolean {
                val mov = p1.getAction()
                when (mov){
                    MotionEvent.ACTION_DOWN -> {
                        movementHorizontal = p1.getX()
                        movementVertical = p1.getY()
                        onClick = true
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP ->{
                        if (onClick){
                            if (onClickEvent != null)
                                onClickEvent()
                        }
                        else{
                            if (onMoveEvent != null)
                                onMoveEvent(deltaHorizontal, deltaVertical)
                        }

                    }
                    MotionEvent.ACTION_MOVE -> {
                        deltaHorizontal = Math.abs(movementHorizontal - p1.getX())
                        deltaVertical = Math.abs(movementVertical - p1.getY())
                        val sideway =  deltaHorizontal > SCROLL_TRESHOLD
                        val upDown =  deltaVertical > SCROLL_TRESHOLD
                        if (onClick && sideway || upDown){
                            onClick = false
                        }
                    }
                    else -> {}
                }
                return false
            }
        }

        return listener
    }

}
