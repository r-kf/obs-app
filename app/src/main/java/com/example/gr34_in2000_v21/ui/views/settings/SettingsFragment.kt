package com.example.gr34_in2000_v21.ui.views.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gr34_in2000_v21.R
import com.example.gr34_in2000_v21.databinding.SettingsFragmentBinding
import com.example.gr34_in2000_v21.ui.MainActivity
import com.example.gr34_in2000_v21.ui.intro.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        //Used to store information about the switches
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val button1: Button? = getView()?.findViewById(R.id.bakgrunn)
        val button2: Button? = getView()?.findViewById(R.id.om)
        val button3: Button? = getView()?.findViewById(R.id.personvern)
        val button4: Button? = getView()?.findViewById(R.id.tutorialButton)
        button1?.background?.alpha = 100
        button2?.background?.alpha = 100
        button3?.background?.alpha = 100
        button4?.background?.alpha = 100
        button1?.elevation = 0F
        button2?.elevation = 0F
        button3?.elevation = 0F
        button4?.elevation = 0F

        //If the night mode switch is on
        if (sharedPref.getBoolean("switchValue2", false)) {
            binding.nattmodus.isChecked = true
        }

        binding.tutorialButton.setOnClickListener {
            val intent = Intent(context, WelcomeActivity::class.java)
            startActivity(intent)
        }

        binding.nattmodus.setOnCheckedChangeListener { _, isChecked ->
            val msg: String
            if (isChecked) {
                editor.putBoolean("switchValue2", true).apply()

                msg = "For at endringene skal tre i effekt må du lukke og åpne appen igjen. " +
                        "Vennligst bytt til en nattmodusbakgrunn, disse fungerer best med " +
                        "nattmodus."

                (activity as MainActivity).endrer.putBoolean("isDarkModeOn", true).apply()
            } else {
                editor.putBoolean("switchValue2", false).commit()

                msg = "For at endringene skal tre i effekt må du lukke og åpne appen igjen. " +
                        "Vennligst bytt til en dagmodusbakgrunn, disse fungerer best med " +
                        "dagmodus."

                (activity as MainActivity).endrer.putBoolean("isDarkModeOn", false).apply()
            }

            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(activity)

            // set message of alert dialog
            dialogBuilder.setMessage(msg)
                // if the dialog is cancelable
                .setCancelable(true)
                // negative button text and action
                .setNegativeButton("Lukk") { dialog, _ ->
                    dialog.cancel()
                }

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Nattmodus")
            // show alert dialog
            alert.show()
        }

        binding.bakgrunn.setOnClickListener {
            val imageView = (activity as MainActivity).findViewById<ImageView>(R.id.image)
            val image = ImageView(context)

            //Build alert dialog
            val dialogBuilder = AlertDialog.Builder(activity)

            // add array list
            val options: Array<String?> =
                arrayOf(
                    "Dagmodus - 1",
                    "Dagmodus - 2",
                    "Dagmodus - 3",
                    "Dagmodus - 4",
                    "Dagmodus - 5",
                    "Dagmodus - 6",
                    "Dagmodus - 7",
                    "Nattmodus - 1",
                    "Nattmodus - 2",
                    "Nattmodus - 3",
                    "Nattmodus - 4",
                    "Nattmodus - 5",
                    "Nattmodus - 6",
                    "Nattmodus - 7",
                    "Ingen"
                )

            var big = R.drawable.bakgrunn1
            var small = R.drawable.liten1
            var number = 0

            //Pass the array list in Alert dialog
            dialogBuilder.setItems(
                options
            ) { _, which ->
                when (which) {
                    0 -> big = R.drawable.bakgrunn1
                    1 -> big = R.drawable.bakgrunn2
                    2 -> big = R.drawable.bakgrunn3
                    3 -> big = R.drawable.bakgrunn4
                    4 -> big = R.drawable.bakgrunn5
                    5 -> big = R.drawable.bakgrunn6
                    6 -> big = R.drawable.bakgrunn7
                    7 -> big = R.drawable.night1
                    8 -> big = R.drawable.night2
                    9 -> big = R.drawable.night3
                    10 -> big = R.drawable.night4
                    11 -> big = R.drawable.night5
                    12 -> big = R.drawable.night6
                    13 -> big = R.drawable.night7
                    14 -> Glide.with(activity as MainActivity).load(R.drawable.bakgrunn_ingen)
                        .into(imageView)
                }

                when (which) {
                    0 -> small = R.drawable.liten1
                    1 -> small = R.drawable.liten2
                    2 -> small = R.drawable.liten3
                    3 -> small = R.drawable.liten4
                    4 -> small = R.drawable.liten5
                    5 -> small = R.drawable.liten6
                    6 -> small = R.drawable.liten7
                    7 -> small = R.drawable.night_small1
                    8 -> small = R.drawable.night_small2
                    9 -> small = R.drawable.night_small3
                    10 -> small = R.drawable.night_small4
                    11 -> small = R.drawable.night_small5
                    12 -> small = R.drawable.night_small6
                    13 -> small = R.drawable.night_small7
                    14 -> (activity as MainActivity).endrer.putString(
                        "bakgrunn",
                        "0"
                    ).apply()
                }

                when(which){
                    0 -> number = 1
                    1 -> number = 2
                    2 -> number = 3
                    3 -> number = 4
                    4 -> number = 5
                    5 -> number = 6
                    6 -> number = 7
                    7 -> number = 8
                    8 -> number = 9
                    9 -> number = 10
                    10 -> number = 11
                    11 -> number = 12
                    12 -> number = 13
                    13 -> number = 14
                }

                if(number != 0) {

                    // build alert dialog
                    val dialog2 = AlertDialog.Builder(activity)

                    image.setImageResource(small)

                    // set message of alert dialog
                    dialog2.setMessage("Ønsker du å bytte til denne bakgrunnen?")
                        // if the dialog is cancelable
                        .setView(image)
                        .setCancelable(true)
                        // positive button text and action
                        .setPositiveButton("Ja") { dialog, _ ->
                            Glide.with(activity as MainActivity).load(big)
                                .into(imageView)
                            (activity as MainActivity).endrer.putString(
                                "bakgrunn",
                                number.toString()
                            ).apply()
                            dialog.cancel()
                        }
                        // negative button text and action
                        .setNegativeButton("Nei") { dialog, _ ->
                            dialog.cancel()
                        }

                    // create dialog box
                    val alert1 = dialog2.create()
                    // set title for alert dialog box
                    alert1.setTitle("Om appen")
                    // show alert dialog
                    alert1.show()
                }
            }

            // set message of alert dialog
            dialogBuilder
                // if the dialog is cancelable
                .setCancelable(true)
                // negative button text and action
                .setNegativeButton("Lukk") { dialog, _ ->
                    dialog.cancel()
                }

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Bakgrunnsbilder")
            // show alert dialog
            alert.show()
        }

        binding.om.setOnClickListener {
            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(activity)

            // set message of alert dialog
            dialogBuilder.setMessage(R.string.om_appen)
                // if the dialog is cancelable
                .setCancelable(true)
                // negative button text and action
                .setNegativeButton("Lukk") { dialog, _ ->
                    dialog.cancel()
                }

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Om appen")
            // show alert dialog
            alert.show()
        }

        binding.personvern.setOnClickListener {
            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(activity)

            // set message of alert dialog
            dialogBuilder.setMessage(R.string.personvern)
                // if the dialog is cancelable
                .setCancelable(true)
                // negative button text and action
                .setNegativeButton("Lukk") { dialog, _ ->
                    dialog.cancel()
                }

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Personvern")
            // show alert dialog
            alert.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

