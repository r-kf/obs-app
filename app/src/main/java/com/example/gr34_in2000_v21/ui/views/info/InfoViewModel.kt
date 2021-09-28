package com.example.gr34_in2000_v21.ui.views.info

import androidx.lifecycle.ViewModel
import com.example.gr34_in2000_v21.ui.views.info.model.InfoCardModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor() : ViewModel() {

    val infoList: List<InfoCardModel> = listOf(
        InfoCardModel(
            "Farger på farevarslene",
            "https://i.ibb.co/Cb7Fy97/Farger-Info.png",
            """
            Du har kanskje lagt merke til at det i nettaviser og på TV snakkes om skogbrannvarsel på oransje nivå, eller at det nå sendes ut gule kulingvarsler?
            
            Farevarslene som vises på Obs! kommer fra Meteorologisk institutt (MET) og Norges vassdrags- og energidirektorat (NVE). 
            Både MET og NVE følger interasjonale standarder på utstedelse av farevarsler og har dermed tatt i bruk farenivåene gult, oransje og rødt.
            
            OBS: Snøskredvarsler følger en internasjonal standard som er noe anderledes, men det vises likt som andre farevarsler på Yr. 
            
            ### Gult = moderat fare
            ![](https://hjelp.yr.no/hc/article_attachments/360012144314/Gult.png)
            #### Gult er nok det farenivået du kommer til å se mest. Dette brukes om en moderat farlig situasjon, som kan forårsake skader lokalt.
            Gule farevarsler sendes ut når det forventes mindre konsekvenser. De fleste vil kunne fortsette sine daglige gjøremål, 
            men de som f.eks. planlegger utendørsaktiviteter, bør være oppmerksomme eller eventuelt holde seg inne.
            
            - Det kan bli skader lokalt
            - Strømbrudd kan forekomme
            - Det er sannsynlig med forsinkelser i trafikken
            - Vind kan gjøre det farlig å være i fjellet
            
            Det er alltid viktig at du leser innholdet i farevarselet for å kunne vurdere hvilke konsekvenser det kan ha for deg.
            
            ### Oransje = stor fare
            ![](https://hjelp.yr.no/hc/article_attachments/360012184593/Oransje.png)
            #### Dette er en alvorlig situasjon som forekommer sjelden. Sannsynligvis må du forberede deg, og været kan føre til alvorlige skader.
            Oransje farevarsel sendes ut når konsekvensene vil bli omfattende for mange mennesker.

            - Været kan føre til alvorlige skader
            - Reell fare for at liv og verdier kan gå tapt
            - Veier kan bli stengt
            - Avganger med båt, fly og annen transport blir kansellert
            
            Du må være forberedt på alvorlige konsekvenser, og bør vurdere om det er trygt å gjøre det du hadde planlagt i områdene som er berørt av oransje farevarsel.
            
            ### Rødt = ekstrem fare og ekstremvær
            ![](https://hjelp.yr.no/hc/article_attachments/360012144474/R_dt.png)
            #### Rødt farevarsel betyr at situasjonen er ekstrem. Dette farenivået forekommer svært sjelden, og kan føre til store skader.
            Røde farevarsler ser du bare når det er forventet at været kan gi ekstreme konsekvenser.

            - Fare for at liv går tapt
            - Store ødeleggelser på eiendom og infrastruktur
            
            Myndigheter og sivile i berørte områder oppfordres til å utføre tiltak som bidrar til å sikre liv og begrense skadeomfanget. Det frarådes å oppholde seg i berørte områder.
            
            ---
            
            [Kilde](https://hjelp.yr.no/hc/no/articles/360008876673-Farger-p%C3%A5-farevarslene)
            """.trimIndent()
        ),
        InfoCardModel(
            "Farevarselikon",
            "https://i.ibb.co/ccdhL05/Farevarselikon-Info.png",
            """      
            Farevarslene på Yr har ulike ikoner avhengig av hvilken fare som meldes. I tillegg endres fargen på ikonet avhengig av hvor alvorlig situasjonen kan komme til å bli.

            Noen av varslene vil ikke nå opp på oransje eller rødt nivå, fordi konsekvensene av den typen fare ikke vil bli store nok for samfunnet.
            
            ### Ikonene som brukes i dag
            #### Vindkast / Vind
            ![](https://www.yr.no/assets/images/svg/icon-warning-wind-yellow.svg) = Kraftige vindkast / Kuling (inkludert storm)
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-wind-orange.svg) = Svært kraftige vindkast
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-wind-red.svg) = Ekstremt kraftige vindkast
            
            #### Regn
            ![](https://www.yr.no/assets/images/svg/icon-warning-rain-yellow.svg) = Mye regn
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-rain-orange.svg) = Svært mye regn
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-rain-red.svg) = Ekstremt mye regn
            
            #### Styrtregn
            ![](https://raw.githubusercontent.com/nrkno/yr-warning-icons/eacda2fe29e2e9785e684632c098273282268677/design/svg/icon-warning-rainflood-yellow.svg) = Styrtregn
            
            ![](https://raw.githubusercontent.com/nrkno/yr-warning-icons/eacda2fe29e2e9785e684632c098273282268677/design/svg/icon-warning-rainflood-orange.svg) = Svært kraftig styrtregn
            
            #### Snø
            ![](https://www.yr.no/assets/images/svg/icon-warning-snow-yellow.svg) = Snø
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-snow-orange.svg) = Svært mye snø
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-snow-red.svg) = Ekstremt mye snø
            
            #### Kjøreforhold
            ![](https://www.yr.no/assets/images/svg/icon-warning-drivingconditions-yellow.svg) = Is (på veien)
            
            #### Vannstand langs kysten
            ![](https://www.yr.no/assets/images/svg/icon-warning-stormsurge-yellow.svg) = Høy vannstand
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-stormsurge-orange.svg) = Svært høy vannstand
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-stormsurge-red.svg) = Ekstremt høy vannstand 
            
            #### Skogsbrann
            ![](https://www.yr.no/assets/images/svg/icon-warning-forestfire-yellow.svg) = Moderat skogbrannfare
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-forestfire-orange.svg) = Betydelig skogbrannfare
            
            #### Flom
            ![](https://www.yr.no/assets/images/svg/icon-warning-flood-yellow.svg) = Flomfare
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-flood-orange.svg) = (Betydelig) Flomfare
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-flood-red.svg) = (Ekstrem) Flomfare
            
            #### Jordskred
            ![](https://www.yr.no/assets/images/svg/icon-warning-landslide-yellow.svg) = Jordskredfare
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-landslide-orange.svg) = (Betydelig) Jordskredfare
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-landslide-red.svg) = (Ekstrem) Jordskredfare
            
            #### Snøskred
            ![](https://www.yr.no/assets/images/svg/icon-warning-avalanches-yellow.svg) = Moderat snøskredfare
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-avalanches-orange.svg) = Betydelig snøskredfare
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-avalanches-red.svg) = Stor / Meget stor snøskredfare
            
            #### Polart lavtrykk
            ![](https://www.yr.no/assets/images/svg/icon-warning-polarlow-yellow.svg) = Polart lavtrykk
            
            ![](https://www.yr.no/assets/images/svg/icon-warning-polarlow-orange.svg) = Svært kraftig polart lavtrykk
            
            #### Ising
            ![](https://hjelp.yr.no/hc/article_attachments/360012604399/icon-warning-generic-yellow.png) = Moderat ising på skip
            
            ![](https://hjelp.yr.no/hc/article_attachments/360012610420/icon_warning_generic_orange.png) = Sterk ising på skip
            
            ---
            
            [Kilde](https://hjelp.yr.no/hc/no/articles/360014052634-Farevarselikonene-p%C3%A5-Yr)
            """.trimIndent()
        ),
        InfoCardModel(
            "Om ekstremvarsler",
            "https://i.ibb.co/NYdXv1H/Om-Ekstremvarsler-Info.png",
            """
            En sjelden gang kan været bli til stor fare for liv og verdier, dersom samfunnet ikke er forberedt. Et ekstremværvarsel er en vêrhending på rødt nivå som har fått navn. 
            Sendinger Meteorologisk institutt ut et slikt varsel, bør du sette i gang tiltak som sikrer verdiene og reduserer ødeleggelsene.
            
            ### Værfenomen som kan bevirke et navngitt ekstremværvarsel:
            - Sterk vind
            - Mye regn
            - Snø
            - Vannstand, eventuelt med høye bølger
            - Kombinasjon av vêrelement ovenfor som til sammen utgjør en fare, men som hvor for seg ikke oppfyller varslingkriteriene
            
            Overordnet regel for å sende ut navngitt varsel om ekstreme værforhold er at det er sannsynlig at været vil bevirke svært store skader eller ekstraordinær fare for liv og verdier i et stort nok landområde (landsdel/fylke/vesentlig del av fylke).
            
            ---
            
            [Kilde](https://www.met.no/vaer-og-klima/ekstremvaervarsler-og-andre-farevarsler/hva-er-et-ekstremvaervarsel)
            """.trimIndent()
        )
    )
}