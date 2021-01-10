package com.nrgbrainn.adolescentes.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.google.firebase.FirebaseApp
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.helpers.DatabaseHelper

class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isColorTransitionsEnabled = true

        addSlide(
            AppIntroFragment.newInstance(
                title = getString(R.string.welcome),
                description = getString(R.string.welcome_description),
                imageDrawable = R.drawable.ic_nrg,
                backgroundColor = Color.rgb(38, 198, 218),
                titleColor = resources.getColor(R.color.black),
                descriptionColor = resources.getColor(R.color.black)
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                title = getString(R.string.team),
                description = getString(R.string.team_description),
                imageDrawable = R.drawable.ic_unicamp_white,
                backgroundColor = Color.rgb(38, 198, 218),
                titleColor = resources.getColor(R.color.black),
                descriptionColor = resources.getColor(R.color.black)
            )
        )
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }
}