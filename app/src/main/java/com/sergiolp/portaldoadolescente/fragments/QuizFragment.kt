package com.sergiolp.portaldoadolescente.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.activities.QuizActivity
import com.sergiolp.portaldoadolescente.helpers.ALTERNATIVES
import com.sergiolp.portaldoadolescente.helpers.CORRECT
import com.sergiolp.portaldoadolescente.helpers.MULTIPLE_CHOICE
import com.sergiolp.portaldoadolescente.helpers.QUESTION
import com.sergiolp.portaldoadolescente.models.CustomViewPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class QuizFragment : Fragment(), FragmentLifecycle {
    private var selectedAlternative: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_quiz, container, false)
        val root = v.findViewById<ViewGroup>(R.id.root)

        val correctPlayer = MediaPlayer.create(context!!, R.raw.correct)
        val wrongPlayer = MediaPlayer.create(context!!, R.raw.wrong)

        //Getting arguments
        val question = arguments?.getString(QUESTION)
        val alternatives = arguments?.getStringArrayList(ALTERNATIVES)!!
        val correctAlternative = arguments?.getInt(CORRECT)

        v.findViewById<TextView>(R.id.quiz_question).text = question

        val altButtons = arrayListOf<TextView>()

        for (i in 0 until alternatives.size) {
            val btn =
                ((context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    R.layout.view_button_alt,
                    root, false
                ) as LinearLayout).findViewById(R.id.btn_alt) as TextView

            altButtons.add(btn)
    }

    for (i in 0 until alternatives.size)
    {
        altButtons[i].text = alternatives[i]

        (altButtons[i].parent as LinearLayout).setOnClickListener { tView ->
            for (b in altButtons) {
                b.setOnClickListener { }
            }

            this.selectedAlternative = i

            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.9f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.9f)
            val animator = ObjectAnimator.ofPropertyValuesHolder(tView, scaleX, scaleY)
            animator.duration = 550
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.repeatCount = 1

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    GlobalScope.launch(Dispatchers.Main) {
                        chooseAlternative(correctAlternative!!)
                    }.start()
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {
                    setBtnColor(correctAlternative!!, altButtons, correctPlayer, wrongPlayer)
                }

            })

            animator.apply {
                interpolator = OvershootInterpolator()
            }.start()

        }

        if (altButtons[i].text != null && altButtons[i].text != "") {
            root.addView(altButtons[i].parent as LinearLayout)
        }
    }


    when
    {
        savedInstanceState != null -> {
        this.selectedAlternative = savedInstanceState.getInt("SELECTED")
        if (selectedAlternative != -1) {
            for (i in 0..3) {
                altButtons[i].isClickable = false
            }
        }
    }
    }

    setBtnColor(correctAlternative!!, altButtons, correctPlayer, wrongPlayer)

    return v
}

override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt("SELECTED", this.selectedAlternative)
}

private fun chooseAlternative(correctAlt: Int): Boolean {
    if (activity == null) {
        return false
    }
    var right = false
    val act = activity as QuizActivity
    val pager = act.findViewById<CustomViewPager>(R.id.quiz_pager)


    if (this.selectedAlternative + 1 == correctAlt) {
        act.currentScore++
        right = true
    }

    pager.setCurrentItem(pager.currentItem + 1, true)


    return right
}

private fun setBtnColor(
    correctAlt: Int,
    buttons: ArrayList<TextView>,
    correctPlayer: MediaPlayer,
    wrongPlayer: MediaPlayer
) {

    for (btn in buttons) {
        if ((btn.parent as LinearLayout).background == null) return
        (((btn.parent as LinearLayout).background as RippleDrawable).findDrawableByLayerId(
            android.R.id.background
        ) as GradientDrawable).setColor(
            ContextCompat.getColor(
                context!!, R.color.btn_color
            )
        )
    }

    if (selectedAlternative != -1) {
        if (selectedAlternative + 1 == correctAlt) {
            (((buttons[selectedAlternative].parent as LinearLayout).background as RippleDrawable).findDrawableByLayerId(
                android.R.id.background
            ) as GradientDrawable).setColor(
                ContextCompat.getColor(
                    context!!, R.color.correct_color
                )
            )

            correctPlayer.start()
        } else {
            (((buttons[selectedAlternative].parent as LinearLayout).background as RippleDrawable).findDrawableByLayerId(
                android.R.id.background
            ) as GradientDrawable).setColor(
                ContextCompat.getColor(
                    context!!, R.color.wrong_color
                )
            )

            wrongPlayer.start()
        }
    }

}
}