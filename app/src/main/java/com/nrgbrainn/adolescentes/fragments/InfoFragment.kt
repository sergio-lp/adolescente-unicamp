package com.nrgbrainn.adolescentes.fragments

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nrgbrainn.adolescentes.R


class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_info, container, false)

        v.findViewById<TextView>(R.id.tv_site).setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.fcm.unicamp.br/adolescentes/")
            )
            startActivity(browserIntent)
        }

        v.findViewById<TextView>(R.id.tv_contact).setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(
                    Html.fromHtml(
                        "Encontro algum erro, tem alguma dúvida ou sugestão? Entre em contato pelo e-mail <br/><br/><a href=\"mailto:adolesce@unicamp.br\">adolesce@unicamp.br</a><br/><br/>Ou nas redes sociais: @nrgbrainn"
                    )
                )
                .setTitle(getString(R.string.credits))
                .setCancelable(true)
                .setPositiveButton(
                    getString(R.string.mail),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            val intentMail = Intent(Intent.ACTION_SEND)
                            intentMail.type = "message/rfc822"
                            intentMail.putExtra(
                                Intent.EXTRA_EMAIL, arrayOf(
                                    "adolesce@unicamp.br"
                                )
                            )

                            intentMail.putExtra(Intent.EXTRA_SUBJECT, "App Adolescentes - Feedback")

                            try {
                                startActivity(
                                    Intent.createChooser(
                                        intentMail,
                                        "Escreva seu e-mail."
                                    )
                                )
                            } catch (ex: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    "Não há nenhum app de e-mail instalado.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
                .show()
        }


        v.findViewById<TextView>(R.id.tv_creditos).setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setMessage(
                    Html.fromHtml(
                        "Photos by <a href=\"https://www.unsplash.com/\" title=\"Unsplash\">Unsplash</a> from <a href=\"https://www.unsplash.com/\" title=\"Unsplash\">www.unsplash.com</a><<br/><br/>" +
                        "Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a><<br/><br/>" +
                                "Icons by <a href=\"https://github.com/google/material-design-icons\">Google Material Design Icons<a><br/><br/>" +
                                "<b><a href=\"https://github.com/bumptech/glide\">Glide</a></b><br/>Copyright 2014 Google, Inc. All rights reserved.<br/>BSD-3-Clause | MIT License | Apache License 2.0<br/><br/>" +
                                "<b><a href=\"https://github.com/google/flexbox-layout\">Flexbox Layout</a></b><br/>Copyright 2018 Google LCC<br/>Apache License 2.0<br/><br/>" +
                                "<b><a href=\"https://github.com/lopspower/CircularImageView\">CircularImageView</a></b><br/>CircularImageView by Lopez Mikhael is licensed under a Apache License 2.0.<br/><br/>" +
                                "<a href=\"https://github.com/romandanylyk/PageIndicatorView\"><b>PageIndicadorView</b></a><br/>Copyright 2017 Roman Danylyk<br/>Apache License 2.0<br/><br/>" +
                                "<b><a href=\"https://github.com/AppIntro/AppIntro\">AppIntro</b></a><br/>Copyright (C) 2015-2020 AppIntro Developers<br/>Apache License 2.0<br/><br/>" +
                                "<a href=\"http://www.apache.org/licenses/LICENSE-2.0\">Apache License 2.0</a>"
                    )
                )
                .setTitle(getString(R.string.credits))
                .setCancelable(true)
                .show()

            dialog.findViewById<TextView>(android.R.id.message).typeface = Typeface.MONOSPACE
            dialog.findViewById<TextView>(android.R.id.message).movementMethod =
                LinkMovementMethod.getInstance()
        }

        return v
    }
}