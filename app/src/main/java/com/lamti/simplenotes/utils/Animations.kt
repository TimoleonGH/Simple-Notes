package com.lamti.simplenotes.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.CardView
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewAnimationUtils
import com.lamti.simplenotes.R
import com.lamti.simplenotes.firebase.NoteItem


/**
 * Created by Timoleon on 2/5/2018.
 */
class Animations {

    init {

    }

    fun multipleSharedAnimation (activityOne: Activity, activityTwo: Class<*>, sharedView1: View, sharedView2: View) {

        val intent = Intent( activityOne, activityTwo )
        val pair1 = Pair(sharedView1, ViewCompat.getTransitionName(sharedView1))
        //val pair2 = Pair(sharedView2, ViewCompat.getTransitionName(sharedView2))

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activityOne, pair1 )
        activityOne.startActivity(intent, options.toBundle())
    }

    fun multipleSharedAnimation (activityOne: Activity, activityTwo: Class<*>, sharedView1: View, sharedView2: View, noteItem: NoteItem) {

        val intent = Intent( activityOne, activityTwo )
        intent.putExtra("noteText", noteItem.noteText)
        intent.putExtra("noteID", noteItem.noteID)
        val pair1 = Pair(sharedView1, ViewCompat.getTransitionName(sharedView1))
        //val pair2 = Pair(sharedView2, ViewCompat.getTransitionName(sharedView2))

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activityOne, pair1 )
        activityOne.startActivity(intent, options.toBundle())
    }

    fun scaleUp ( context: Context, animateView: View, duration: Long ) {

        animateView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .rotationBy(0f)
                .setDuration(duration)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {

                    }

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                })
                .start()
    }

    fun scaleDown ( context: Context, animateView: View, duration: Long ) {

        animateView.animate()
                .scaleX(0f)
                .scaleY(0f)
                .translationY(0f)
                .rotationBy(0f)
                .setDuration(duration)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                    }

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                })
                .start()
    }

    fun animateY ( animateView: View, y: Float, duration: Long, delay: Long ) {

        val valueAnimator = ValueAnimator.ofFloat(y, 0f)
        valueAnimator.interpolator = FastOutSlowInInterpolator()
        valueAnimator.startDelay = delay
        valueAnimator.duration = duration
        valueAnimator.start()

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            animateView.translationY = value
        }

        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                animateView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    fun enterAnimateY ( animateView: View, y: Float, viewY: Float, duration: Long, delay: Long ) {


        val objectAnimator = ObjectAnimator.ofFloat(animateView, "translationY", y, 0f)
        objectAnimator.interpolator = FastOutSlowInInterpolator()
        objectAnimator.startDelay = delay
        objectAnimator.duration = duration
        objectAnimator.start()

        objectAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            animateView.translationY = value
        }

        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                animateView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    fun exitAnimateY ( animateView: View, y: Float, viewY: Float, duration: Long, delay: Long ) {


        val objectAnimator = ObjectAnimator.ofFloat(animateView, "translationY",0f, y)
        objectAnimator.interpolator = FastOutSlowInInterpolator()
        objectAnimator.startDelay = delay
        objectAnimator.duration = duration
        objectAnimator.start()

        objectAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            animateView.translationY = value
        }

        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                animateView.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun reveal(revealView: View, me: MotionEvent) {

        revealView.visibility = VISIBLE

        val cx = revealView.width
        val cy = revealView.height

        val finalRadius = Math.max(cx, cy).toFloat() * 1.2f
        val animation = ViewAnimationUtils
                .createCircularReveal(revealView, me.x.toInt(), me.y.toInt(), 0f, finalRadius)

        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {

                /*baseView.setBackgroundColor(FIRST_COLOR)
                revealView.visibility = INVISIBLE*/
            }
        })

        animation.start()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun reveal(revealView: View, cardView: CardView, circleView: View, x: Float, y:Float, color: Int) {

        val cx = revealView.width
        val cy = revealView.height

        val finalRadius = Math.max(cx, cy).toFloat() * 1.2f
        val animation = ViewAnimationUtils.createCircularReveal(revealView, x.toInt(), y.toInt(), 0f, finalRadius)

        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {

                revealView.visibility = INVISIBLE
                cardView.setCardBackgroundColor(color)
            }
        })

        val reverseAnimation = ViewAnimationUtils.createCircularReveal(circleView, x.toInt(), y.toInt(), finalRadius, 0f)

        reverseAnimation.addListener(object: AnimatorListenerAdapter(){

            override fun onAnimationEnd(animationReverse: Animator?) {

                changeDrawableViewColor( revealView, color )
                revealView.visibility = VISIBLE
                animation.start()
                changeDrawableViewColor( circleView, Color.WHITE )
            }
        })

        reverseAnimation.start()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun reverseReveal(revealView: View, cardView: CardView, circleView: View, x: Float, y:Float, color: Int) {

        changeDrawableViewColor( revealView, color )
        revealView.visibility = VISIBLE

        val cx = revealView.width
        val cy = revealView.height

        val finalRadius = Math.max(cx, cy).toFloat() * 1.2f
        val animation = ViewAnimationUtils.createCircularReveal(revealView, x.toInt(), y.toInt(), finalRadius, 0f)

        val reverseAnimation = ViewAnimationUtils.createCircularReveal(circleView, x.toInt(), y.toInt(), 0f, finalRadius)

        reverseAnimation.addListener(object: AnimatorListenerAdapter(){

            override fun onAnimationStart(animation: Animator?) {

                changeDrawableViewColor( circleView, color )
            }

            override fun onAnimationEnd(animationReverse: Animator?) {

            }
        })

        animation.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator?) {

                cardView.setCardBackgroundColor(Color.WHITE)
                Log.d("COLOR_TAG", "animation start")
            }

            override fun onAnimationEnd(animator: Animator) {

                revealView.visibility = INVISIBLE
                reverseAnimation.start()
                Log.d("COLOR_TAG", "animation end")
            }
        })

        animation.start()
    }

    fun changeDrawableViewColor( colorView: View, toColor: Int ) {
        /*val gradient = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(bottomColor, topColor))
        gradient.shape = GradientDrawable.RECTANGLE
        gradient.cornerRadius = 10f*/
        //setBackgroundDrawable(gradient)

        val drawable =  colorView.background as GradientDrawable
        drawable.setColor(toColor)
    }

    fun getScreen( activity: Activity ) {

        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        val resourceId = activity.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val navBarHeight = activity.resources.getDimensionPixelSize(resourceId)

        val resizeY = height.toFloat() - navBarHeight - navBarHeight
        //saveB.y = resizeY

        /*saveB.x = 0f
        saveB.y = resizeY

        Log.d(TAG, "Screen - height: ${height.toFloat()}, width: $width, --> ${height.toFloat() - ( saveB.height * 2 )}" )
        Log.d(TAG, " navbar: ${resources.getDimensionPixelSize(resourceId)}" )
        Log.d(TAG, " resizeY: $resizeY" )*/
    }

    fun getScreenHeight( activity: Activity ): Float {

        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        return height.toFloat()
    }

    fun getNavbarHeight ( activity: Activity ): Float {

        val resourceId = activity.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val navBarHeight = activity.resources.getDimensionPixelSize(resourceId)

        return navBarHeight.toFloat()
    }

    fun getNoteColor( activity: Activity, catID: Int ): Int {

        var catColor = activity.resources.getColor(R.color.colorCyan)

        when (catID) {
            0 -> catColor = activity.resources.getColor(R.color.colorCyan)
            1 -> catColor = activity.resources.getColor(R.color.colorPrimary)
            2 -> catColor = activity.resources.getColor(R.color.colorAccent)
            3 -> catColor = activity.resources.getColor(R.color.colorGray)
            4 -> catColor = activity.resources.getColor(R.color.colorPinky)
            5 -> catColor = activity.resources.getColor(R.color.colorGrayDark)
            6 -> catColor = activity.resources.getColor(R.color.colorPrimaryDark)
            7 -> catColor = activity.resources.getColor(R.color.colorDivider)
        }
        return catColor
    }
}