package com.pebbles.Utils



//
//import android.R
//import android.animation.Animator
//import android.animation.AnimatorListenerAdapter
//import android.animation.ValueAnimator
//import android.graphics.Rect
//import android.view.View
//import android.widget.ImageView
//import kotlinx.android.synthetic.main.activity_home_page.*
//
//private fun animateTo(view: ImageView) {
//    val currentRect: Rect = itemSelectedView.boundingBox
//    val newRect = view.boundingBox
//
//    when {
//        currentRect.centerX() > newRect.centerX() -> { //leftward animation
//            animateLeftValues(currentRect.left, newRect.left) {
//                animateRightValues(currentRect.right, newRect.right) {
//
//                }
//            }
//        }
//        currentRect.centerX() < newRect.centerX() -> { //rightward animation
//            animateRightValues(currentRect.right, newRect.right) {
//                animateLeftValues(currentRect.left, newRect.left) {
//
//                }
//            }
//        }
//        else -> {
//            //equal do nothing
//        }
//    }
//
//
////         view?.x?.let {
////            itemSelectedView.x = it - (view.width / 2)
////        }
////        view?.y?.let {
////            itemSelectedView.y = it - (view.height / 2)
////        }
//
//}
//
//private fun animateLeftValues(from: Int, to: Int, function: () -> Unit) {
//    ValueAnimator.ofInt(from, to).apply {
//        addUpdateListener { updatedAnimation ->
//            itemSelectedView.left = updatedAnimation.animatedValue as Int
//        }
//        addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                itemSelectedView.left = to
//                function.invoke()
//            }
//        })
//        duration = 1000
//        start()
//    }
//}
//
//private fun animateRightValues(from: Int, to: Int, function: () -> Unit) {
//    ValueAnimator.ofInt(from, to).apply {
//        addUpdateListener { updatedAnimation ->
//            itemSelectedView.right = updatedAnimation.animatedValue as Int
//        }
//        addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                itemSelectedView.right = to
//                function.invoke()
//            }
//        })
//        R.attr.fillAfter
//        duration = 1000
//        start()
//    }
//
//}
//
//val View.screenLocation
//    get(): IntArray {
//        val point = IntArray(2)
//        getLocationOnScreen(point)
//        return point
//    }
//
//val View.boundingBox
//    get(): Rect {
//        val (x, y) = screenLocation
//        return Rect(x, y, x + width, y + height)
//    }
