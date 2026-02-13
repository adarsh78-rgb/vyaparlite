package com.vyaparlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.google.android.gms.ads.MobileAds
import com.vyaparlite.data.AppContainer
import com.vyaparlite.ui.navigation.AppNavHost
import com.vyaparlite.ui.theme.VyaparLiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        val appContainer = AppContainer(applicationContext)
        setContent {
            VyaparLiteTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(appContainer = appContainer)
                }
            }
        }
    }
}
