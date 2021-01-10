package com.nrgbrainn.adolescentes.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.activities.QuizActivity
import com.nrgbrainn.adolescentes.helpers.APPROVE_THRESHOLD
import com.nrgbrainn.adolescentes.helpers.COMPLETED_CONTENT
import com.nrgbrainn.adolescentes.helpers.DatabaseHelper
import com.nrgbrainn.adolescentes.helpers.USER_SCORE
import com.nrgbrainn.adolescentes.models.User
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment(
    private val unitId: String,
    private val contentId: String,
    private val contentTitle: String,
    private val userId: String
) :
    QuizFragment(), FragmentLifecycle {
    private var tvTitle: TextView? = null
    private var tvDescription: TextView? = null
    private var tvScore: TextView? = null
    private var btnResult: Button? = null
    private var root: ViewGroup? = null
    private var progressBar: ProgressBar? = null
    private var layoutApprovedScore: ViewGroup? = null
    private var coordinator: ViewGroup? = null
    private var resultWinPlayer: MediaPlayer? = null
    private var resultLosePlayer: MediaPlayer? = null
    private var scoreIncreasePlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_result, container, false)

        tvTitle = v.findViewById(R.id.result_title)!!
        tvDescription = v.findViewById(R.id.result_desc)!!
        tvScore = v.findViewById(R.id.result_score)!!
        btnResult = v.findViewById(R.id.result_btn)!!
        root = v.findViewById(R.id.root)!!
        progressBar = v.findViewById(R.id.progress_bar)!!
        layoutApprovedScore = v.findViewById(R.id.layout_approved_score)!!
        coordinator = v.findViewById(R.id.coordinator)!!

        resultWinPlayer = MediaPlayer.create(context, R.raw.win)
        resultLosePlayer = MediaPlayer.create(context, R.raw.lose)
        scoreIncreasePlayer = MediaPlayer.create(context, R.raw.score)

        resultWinPlayer?.setVolume(0.3f, 0.3f)
        resultLosePlayer?.setVolume(0.3f, 0.3f)


        (((btnResult as Button).background as RippleDrawable).findDrawableByLayerId(
            android.R.id.background
        ) as GradientDrawable).setColor(
            ContextCompat.getColor(
                context!!, R.color.btn_color
            )
        )

        btnResult?.isClickable = false



        return v
    }

    override fun onFragmentResume() {
        val act = activity as QuizActivity
        val score = act.currentScore * 10

        val title: String
        val description: CharSequence
        val btn: String

        //Checks if user has score to be approved at this content
        if (score >= APPROVE_THRESHOLD) {
            title = context?.getText(R.string.result_congratz).toString()
            description = Html.fromHtml(
                getString(R.string.result_congratz_desc, contentTitle, score / 10, score)
            )
            btn = context?.getText(R.string.result_congratz_btn).toString()

            val dbHelper = DatabaseHelper(FirebaseApp.getInstance())

            //Writes score and adds completed content to DB
            dbHelper.getUser(userId).get().addOnSuccessListener { response ->
                val user = response.toObject(User::class.java)
                if (user != null) {
                    if (user.completed_content == null) {
                        user.completed_content = arrayListOf()
                    }
                    user.completed_content?.add("$unitId+$contentId")
                    user.score = user.score + score

                    dbHelper.getUser(userId).update(
                        mapOf
                            (
                            COMPLETED_CONTENT to user.completed_content,
                            USER_SCORE to user.score
                        )
                    ).addOnSuccessListener {
                        dbHelper.finishDB()

                        if (resultWinPlayer != null) {
                            resultWinPlayer?.start()
                        }

                        //Adds a text view for displaying the score increase
                        val tvPlusScore = TextView(context)
                        tvPlusScore.text = String.format(getString(R.string.plus), score)
                        tvPlusScore.measure(0, 0)

                        progressBar?.visibility = View.GONE
                        root?.visibility = View.VISIBLE
                        layoutApprovedScore?.visibility = View.VISIBLE

                        //Sets total score text
                        tvScore?.text = (user.score - score).toString()

                        tvScore!!.measure(0, 0)

                        //Position for the score increase TextView animation
                        tvPlusScore.x =
                            tvScore!!.left + (tvScore!!.measuredWidth.toFloat() / 2) + (tvPlusScore.measuredWidth / 4)
                        tvPlusScore.y = tvScore!!.y + (tvScore!!.height) / 2
                        tvPlusScore.setTextAppearance(context, R.style.AppbarText)
                        tvPlusScore.alpha = 0f

                        val posY = PropertyValuesHolder.ofFloat(
                            View.Y,
                            tvPlusScore.y,
                            tvPlusScore.y - ((tvScore!!.height) / 2) + (tv_approved_total_score.height / 1.5f)
                        )
                        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
                        ObjectAnimator.ofPropertyValuesHolder(tvPlusScore, posY, alpha).apply {
                            duration = 1500
                            startDelay = 800
                            addListener(object : Animator.AnimatorListener {
                                override fun onAnimationRepeat(p0: Animator?) {

                                }

                                override fun onAnimationEnd(p0: Animator?) {
                                    ObjectAnimator.ofFloat(tvPlusScore, View.ALPHA, 1f, 0f).start()
                                    ValueAnimator.ofInt((user.score - score), user.score).apply {
                                        addUpdateListener {
                                            tvScore?.text = it.animatedValue.toString()
                                        }
                                        addListener(object : Animator.AnimatorListener {
                                            override fun onAnimationRepeat(p0: Animator?) {
                                            }

                                            override fun onAnimationEnd(p0: Animator?) {
                                                scoreIncreasePlayer?.stop()
                                                btnResult?.apply {
                                                    isClickable = true
                                                    setOnClickListener {
                                                        act.finish()
                                                    }
                                                }
                                            }

                                            override fun onAnimationCancel(p0: Animator?) {
                                            }

                                            override fun onAnimationStart(p0: Animator?) {
                                                if (scoreIncreasePlayer != null) {
                                                    scoreIncreasePlayer?.start()
                                                }
                                            }

                                        })
                                        duration = 1000
                                        startDelay = 30
                                    }.start()

                                    dbHelper.setUserPrefScore(context!!, user.score)
                                }

                                override fun onAnimationCancel(p0: Animator?) {

                                }

                                override fun onAnimationStart(p0: Animator?) {

                                }

                            })
                        }.start()

                        animateLayoutAppearance(root!!)

                        layoutApprovedScore?.addView(tvPlusScore)
                    }.addOnFailureListener { e ->
                        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                        Log.e(
                            "TAG",
                            "Login Activity - onActivityResult: " + e.message
                        )
                        dbHelper.finishDB()
                    }
                }

            }.addOnFailureListener { e ->
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                Log.e(
                    "TAG",
                    "Login Activity - onActivityResult: " + e.message
                )
                dbHelper.finishDB()
            }


        } else {
            title = context?.getText(R.string.result_failed).toString()
            description = Html.fromHtml(
                getString(R.string.result_failed_description, score / 10, contentTitle)
            )
            btn = context?.getText(R.string.ok).toString()

            resultLosePlayer?.start()

            progressBar?.visibility = View.GONE
            root?.visibility = View.VISIBLE
            btnResult?.apply {
                isClickable = true
                setOnClickListener {
                    act.finish()
                }
            }

            animateLayoutAppearance(root!!)
        }

        tvTitle?.text = title
        tvDescription?.text = description
        btnResult?.text = btn
    }

    private fun animateLayoutAppearance(layout: View) {
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f)
        ObjectAnimator.ofPropertyValuesHolder(layout, alpha, scaleX, scaleY).apply {
            interpolator = OvershootInterpolator()
        }.start()
    }
}

