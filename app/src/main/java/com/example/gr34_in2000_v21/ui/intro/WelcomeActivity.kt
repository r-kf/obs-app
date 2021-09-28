package com.example.gr34_in2000_v21.ui.intro

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.gr34_in2000_v21.R
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType

/*
    User-guide explaining where certain functionalities lie.
    This activity is shown the first time a user opens the app and can also be shown
    in the settings fragment.
 */
class WelcomeActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Adding slides with images and descriptions
        addSlide(
            AppIntroFragment.newInstance(
                title = "Velkommen til Obs!™",
                description = "Appen som varsler deg om farer på land og til havs",
                imageDrawable = R.drawable.obs_logo,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.parseColor("#b5f5f5"),
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = "Se lokale farevarsler på hjemmesiden",
                description = "Trykk på farevarselen for mer informasjon",
                imageDrawable = R.drawable.welcome_home,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.parseColor("#4ACBBD"),
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = "Eller swipe til høyre for å se lagrede steder",
                description = "Du kan legge til et nytt område ved å søke det opp og trykke på ❤️-ikonet",
                imageDrawable = R.drawable.welcome_fav,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.parseColor("#4CC6D1"),
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = "Hold deg oppdatert på siste nytt innen naturfarer på nyhetssiden",
                description = "Her kan du se twitter-innlegg fra @Meterologene, @StormGeo og @Varsom_no",
                imageDrawable = R.drawable.welcome_news,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.parseColor("#4ACBBD"),
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = "På søkesiden kan du søke etter farer i hele Norge",
                description = "Her kan du filtrere etter faretyper og swipe opp for mer informasjon om de gjeldende farene",
                imageDrawable = R.drawable.welcome_search,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.parseColor("#4CC6D1"),
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = "Endre appens utseende i innstillinger",
                description = "Her kan du velge mellom en rekke bakgrunner og aktivere nattmodus",
                imageDrawable = R.drawable.welcome_settings,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.parseColor("#4ACBBD"),
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = "Sjekk ut informasjonssiden for å lære mer om farevarsler",
                description = "Her kan du blant annet lære hva de ulike fargene på fareikonene betyr",
                imageDrawable = R.drawable.welcome_info,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.parseColor("#4CC6D1"),
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = "Skru på stedstjenester for å motta lokale farevarsler",
                description = "Infomasjon om posisjon dersom du har godtatt å dele dette lagres\n" +
                        "         lokalt på din telefon og ikke av oss i Obs!\n" +
                        "         Derimot lagrer Meteorologisk Institutt din IP-adresse i deres logger i tillegg til andre\n" +
                        "         mulig geografiske koordinater, noe Obs! ikke har kontroll over eller tilgang til.",
                imageDrawable = R.drawable.ic_baseline_share_location_24,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.parseColor("#4ACBBD"),
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = "Nå er du klar til å bruke Obs!",
                description = "Om du trenger en oppfriskning finner du disse velkomstsidene under innstillinger",
                imageDrawable = R.drawable.obs_logo,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.parseColor("#b5f5f5"),
            )
        )

        //Slide transformer
        setTransformer(AppIntroPageTransformerType.Fade)

        //Color transition
        isColorTransitionsEnabled = true

        //Permissions
        askForPermissions(
            permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            slideNumber = 8,
            required = false)
    }

    /*
        Function for when the user wishes to skip the slides and
        exit this Activity
     */
    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        val sharedPref = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean("FIRST_TIME", false)
            apply()
        }
        finish()
    }

    /*
        Function for when the user is finished looking through the slides
        and wants to exit this Activity
     */
    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val sharedPref = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean("FIRST_TIME", false)
            apply()
        }
        finish()
    }
}