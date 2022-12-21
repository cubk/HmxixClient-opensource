package io.space.alt.microsoft

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpServer
import org.apache.logging.log4j.LogManager
import org.lwjgl.Sys
import java.io.Closeable
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URL
import java.nio.charset.StandardCharsets

class MicrosoftAuth : Closeable {
    companion object {
        private const val CLIENT_ID = "67b74668-ef33-49c3-a75c-18cbb2481e0c"
        private const val REDIRECT_URI = "http://localhost:9643/sad"
        private const val SCOPE = "XboxLive.signin%20offline_access"

        private val URL = "https://login.live.com/oauth20_authorize.srf?client_id=<client_id>&redirect_uri=<redirect_uri>&response_type=code&display=touch&scope=<scope>&prompt=select_account"
            .replace("<client_id>", CLIENT_ID)
            .replace("<redirect_uri>", REDIRECT_URI)
            .replace("<scope>", SCOPE)

        private val gson = GsonBuilder().create()
        private val parser = JsonParser()
        private val logger = LogManager.getLogger("Microsoft Auth")
    }

    private val statusCallback : Callback<String>
    private val closeRunnable : Runnable?

    private var closed = false

    @Suppress("SpellCheckingInspection")
    constructor(callback : Callback<Data>,statusCallback : Callback<String>) {
        this.statusCallback = statusCallback

        val redirectServer = HttpServer.create(InetSocketAddress("localhost",9643),0)

        closeRunnable = Runnable {
            redirectServer.stop(0)
        }

        redirectServer.createContext("/sad") { exchange ->
            val query = exchange.requestURI.query

            if (query.contains("code")) {
                val code = query.split("code=")[1]

                val microsoftTokenAndRefreshToken = getMicrosoftTokenAndRefreshToken(code)
                val xBoxLiveToken = getXBoxLiveToken(microsoftTokenAndRefreshToken[0])
                val xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken)
                val accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1])

                val jsonObject = parser.parse(
                    get("https://api.minecraftservices.com/minecraft/profile",
                        mapOf(Pair("Authorization", "Bearer $accessToken"))
                    )
                ).asJsonObject

                callback.call(Data(
                    jsonObject.get("name").asString,
                    jsonObject.get("id").asString,
                    accessToken,
                    microsoftTokenAndRefreshToken[1]
                ))

                setStatus("登录完成")

                exchange.sendResponseHeaders(200, 0)
                val os = exchange.responseBody
                os.write("login successful".encodeToByteArray())
                os.close()

                close()
            } else {
                exchange.sendResponseHeaders(404, 0)
            }

            exchange.close()
        }

        redirectServer.start()

        Sys.openURL(URL)
    }

    @Suppress("SpellCheckingInspection")
    constructor(callback : Callback<Data>, statusCallback : Callback<String>, refreshToken : String) {
        this.statusCallback = statusCallback
        closeRunnable = null

        val microsoftToken = getMicrosoftTokenFromRefreshToken(refreshToken)
        val xBoxLiveToken = getXBoxLiveToken(microsoftToken)
        val xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken)
        val accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1])

        val jsonObject = parser.parse(
            get(
                "https://api.minecraftservices.com/minecraft/profile",
                mapOf(Pair("Authorization", "Bearer $accessToken"))
            )
        ).asJsonObject

        callback.call(Data(
            jsonObject.get("name").asString,
            jsonObject.get("id").asString,
            accessToken,
            refreshToken
        ))

        setStatus("登录完成")
    }

    override fun close() {
        if (closed) {
            return
        }

        println("Close")

        closeRunnable?.run()
        closed = true
    }

    private fun setStatus(status : String) {
        logger.info(status)

        statusCallback.call(status)
    }

    private fun getMicrosoftTokenFromRefreshToken(refreshToken : String) : String {
        setStatus("Get microsoft token from refreshToken")

        val jsonObject = parser.parse(
            post(
                "https://login.live.com/oauth20_token.srf",
                "client_id=$CLIENT_ID&refresh_token=$refreshToken&grant_type=refresh_token",
                mapOf(Pair("Content-Type", "application/x-www-form-urlencoded"))
            )
        ).asJsonObject

        return jsonObject.get("access_token").asString
    }

    private fun getMicrosoftTokenAndRefreshToken(code : String) : Array<String> {
        setStatus("Get microsoft token and refreshToken")

        val jsonObject = parser.parse(
            post(
                "https://login.live.com/oauth20_token.srf",
                "client_id=$CLIENT_ID&code=$code&grant_type=authorization_code&redirect_uri=$REDIRECT_URI&scope=$SCOPE",
                mapOf(Pair("Content-Type", "application/x-www-form-urlencoded"))
            )
        ).asJsonObject

        return arrayOf(
            jsonObject.get("access_token").asString,
            jsonObject.get("refresh_token").asString
        )
    }

    @Suppress("HttpUrlsUsage")
    private fun getXBoxLiveToken(microsoftToken : String) : String {
        setStatus("Get XBox live token")

        val paramObj = JsonObject()
        val propertiesObj = JsonObject()

        propertiesObj.addProperty("AuthMethod", "RPS")
        propertiesObj.addProperty("SiteName", "user.auth.xboxlive.com")
        propertiesObj.addProperty("RpsTicket", "d=$microsoftToken")
        paramObj.add("Properties", propertiesObj)
        paramObj.addProperty("RelyingParty", "http://auth.xboxlive.com")
        paramObj.addProperty("TokenType", "JWT")

        val jsonObject = parser.parse(
            post(
                "https://user.auth.xboxlive.com/user/authenticate",
                gson.toJson(paramObj),
                mapOf(
                    Pair("Content-Type", "application/json"),
                    Pair("Accept", "application/json")
                )
            )
        ).asJsonObject

        return jsonObject.get("Token").asString
    }

    private fun getXSTSTokenAndUserHash(xboxLiveToken : String) : Array<String> {
        setStatus("Get xsts token and user hash")

        val paramObj = JsonObject()
        val propertiesObj = JsonObject()

        propertiesObj.addProperty("SandboxId", "RETAIL")
        propertiesObj.add("UserTokens", parser.parse(gson.toJson(arrayListOf(xboxLiveToken))))
        paramObj.add("Properties", propertiesObj)
        paramObj.addProperty("RelyingParty", "rp://api.minecraftservices.com/")
        paramObj.addProperty("TokenType", "JWT")

        val jsonObject = parser.parse(
            post("https://xsts.auth.xboxlive.com/xsts/authorize",
                gson.toJson(paramObj),
                mapOf(Pair("Content-Type", "application/json")))
        ).asJsonObject

        return arrayOf(jsonObject.get("Token").asString, jsonObject.get("DisplayClaims").asJsonObject.get("xui").asJsonArray.get(0).asJsonObject.get("uhs").asString)
    }

    private fun getAccessToken(xstsToken : String, uhs : String) : String {
        setStatus("Get access token")

        val paramObj = JsonObject()
        paramObj.addProperty("identityToken", "XBL3.0 x=$uhs;$xstsToken")

        val jsonObject = parser.parse(
            post("https://api.minecraftservices.com/authentication/login_with_xbox",
                gson.toJson(paramObj),
                mapOf(Pair("Content-Type", "application/json"), Pair("Accept", "application/json")))
        ).asJsonObject

        return jsonObject.get("access_token").asString
    }

    private fun post(urlString : String,param : String,requestProperty : Map<String,String>) : String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        connection.doInput = true
        connection.doOutput = true

        connection.connectTimeout = 20000
        connection.readTimeout = 20000

        connection.requestMethod = "POST"

        requestProperty.forEach { (k, v) ->
            connection.setRequestProperty(k,v)
        }

        connection.connect()

        connection.outputStream.bufferedWriter(StandardCharsets.UTF_8).use { writer ->
            writer.write(param)
        }

        val readText : String

        connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
            readText = reader.readText()
        }

        connection.disconnect()

        return readText
    }

    @Suppress("SameParameterValue")
    private fun get(urlString : String, requestProperty : Map<String,String>) : String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        connection.doInput = true
        connection.requestMethod = "GET"

        connection.connectTimeout = 20000
        connection.readTimeout = 20000

        requestProperty.forEach { (k, v) ->
            connection.setRequestProperty(k,v)
        }

        connection.connect()

        val readText : String

        connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
            readText = reader.readText()
        }

        connection.disconnect()

        return readText
    }

    interface Callback<T> {
        fun call(obj : T)
    }

    data class Data(val userName : String,val uuid : String,val accessToken : String,val refreshToken : String)
}