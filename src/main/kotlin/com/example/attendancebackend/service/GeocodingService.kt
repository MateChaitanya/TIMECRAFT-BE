package com.example.attendancebackend.service

import org.json.JSONObject
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Service
class GeocodingService {

    fun getAddressFromLatLng(lat: Double, lon: Double): String {
        return try {
            val urlStr =
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lon&zoom=18&addressdetails=1"

            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection

            // VERY IMPORTANT HEADERS (without this OpenStreetMap blocks request)
            conn.setRequestProperty("User-Agent", "AttendanceApp/1.0 (chaitanya)")
            conn.setRequestProperty("Accept-Language", "en")

            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            reader.close()

            val json = JSONObject(response.toString())

            if (json.has("display_name")) {
                json.getString("display_name")
            } else {
                "Unknown Location"
            }

        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown Location"
        }
    }
}
