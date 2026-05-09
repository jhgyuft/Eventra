package com.example.eventra

import android.app.Application
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class EventraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        SdkUIFlowBuilder.init()
            .setContext(this)
            .setClientKey("SB-Mid-client-YOUR_CLIENT_KEY") // Replace with actual Client Key
            .setMerchantBaseUrl("https://your-merchant-url.com/") // Replace with actual Merchant URL
            .enableLog(true)
            .buildSDK()
    }
}
