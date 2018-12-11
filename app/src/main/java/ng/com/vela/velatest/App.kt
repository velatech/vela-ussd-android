package ng.com.vela.velatest

import android.app.Application
import velasolutions.velabank.velaoffline.VelaOffline
import velasolutions.velabank.velaoffline.VelaOfflineConfig

/**
 * @author jerry on 24/11/2018
 * @project VelaTest
 **/
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = VelaOfflineConfig.Builder()
            .baseServiceCode("*931*77*0*")
            .encryptionKey("sdsndlsjdjsjdljslk")
            .build()
        VelaOffline.initWithDefaultConfig(this, config)
    }
}