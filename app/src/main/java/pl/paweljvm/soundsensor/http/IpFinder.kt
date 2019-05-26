package pl.paweljvm.soundsensor.http

import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * Created by pawel on 2019-05-14.
 */
object IpFinder {

    fun firstExternalIp():String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        for(ni in networkInterfaces) {
            for( ip in ni.inetAddresses) {
                val host = ip.hostAddress
                if(!ip.isLoopbackAddress && ip is Inet4Address) {
                    return host
                }
            }
        }
        return null
    }
}