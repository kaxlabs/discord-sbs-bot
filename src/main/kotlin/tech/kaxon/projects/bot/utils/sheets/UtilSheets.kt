package tech.kaxon.projects.bot.utils.sheets

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import tech.kaxon.projects.bot.main.Main
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.CompletableFuture


object UtilSheets {
    const val APPLICATION_NAME = "SBS Balance"
    val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private val TOKENS_DIRECTORY_PATH = Main.tokensDir.canonicalPath

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)
    private val CREDENTIALS_FILE_PATH = Main.credentialsFile.canonicalPath

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        // Load client secrets.
        val `in` = FileInputStream(CREDENTIALS_FILE_PATH)
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH))).setAccessType("offline").build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    private val spreadsheetId = Main.config.spreadsheetURL

    fun getSheetIDByCheck(check: Triple<String, String, String>): Pair<Int, Int> {
        if (check.second == "alliance") return Pair(Main.config.allianceSheetGID, Main.config.allianceSheetAdGID)
        if (check.second == "horde") return Pair(Main.config.hordeSheetGID, Main.config.hordeSheetAdGID)
        return Pair(Int.MAX_VALUE, Int.MAX_VALUE)
    }

    fun getChannelTypeByID(channel: Long): Triple<String, String, String> {
        for (ch in Main.normalChannels.allianceChannels) {
            if (ch == channel) return Triple("normal", "alliance", "")
        }
        for (ch in Main.normalChannels.hordeChannels) {
            if (ch == channel) return Triple("normal", "horde", "")
        }
        for (ch in Main.mythicChannels.allianceChannels) {
            if (ch == channel) return Triple("mythic", "alliance", "")
        }
        for (ch in Main.mythicChannels.hordeChannels) {
            if (ch == channel) return Triple("mythic", "horde", "")
        }
        for (ch in Main.heroicChannels.allianceBoosterChannels) {
            if (ch == channel) return Triple("heroic", "alliance", "booster")
        }
        for (ch in Main.heroicChannels.allianceAdvertiserChannels) {
            if (ch == channel) return Triple("heroic", "alliance", "advertiser")
        }
        for (ch in Main.heroicChannels.hordeBoosterChannels) {
            if (ch == channel) return Triple("heroic", "horde", "booster")
        }
        for (ch in Main.heroicChannels.hordeAdvertiserChannels) {
            if (ch == channel) return Triple("heroic", "horde", "advertiser")
        }
        return Triple("", "", "")
    }

    fun updateSheetByID(id: Int, map: HashMap<String, Double>, cellRange: String, startRowIndex: Int, endRowIndex: Int, startColumnIndex: Int, subtract: Boolean = false, type: String = String()) {
        // Build a new authorized API client service.
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val service = Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME).build()
        val valueRange = getValueRange(service, id, cellRange)
        getUserDataByNames(valueRange, map.keys.toList()).thenAccept { userData ->
            val requestList = LinkedList<Request>()
            val requestList2 = LinkedList<Request>()
            loop@ for ((key, value) in map) {
                for (item in userData) {
                    if (key == item.name) {
                        when (type) {
                            "paid" -> {
                                if (subtract) requestList.addAll(updateUserDataByName(id, valueRange, key, UserData(item.name, item.earned, (item.paid ?: 0.0) - value, item.penalty), startRowIndex, startColumnIndex))
                                else requestList.addAll(updateUserDataByName(id, valueRange, key, UserData(item.name, item.earned, (item.paid ?: 0.0) + value, item.penalty), startRowIndex, startColumnIndex))
                            }
                            "penalty" -> {
                                if (subtract) requestList.addAll(updateUserDataByName(id, valueRange, key, UserData(item.name, item.earned, item.paid, (item.penalty ?: 0.0) - value), startRowIndex, startColumnIndex))
                                else requestList.addAll(updateUserDataByName(id, valueRange, key, UserData(item.name, item.earned, item.paid, (item.penalty ?: 0.0) + value), startRowIndex, startColumnIndex))
                            }
                            else -> {
                                if (subtract) requestList.addAll(updateUserDataByName(id, valueRange, key, UserData(item.name, (item.earned ?: 0.0) - value), startRowIndex, startColumnIndex))
                                else requestList.addAll(updateUserDataByName(id, valueRange, key, UserData(item.name, (item.earned ?: 0.0) + value), startRowIndex, startColumnIndex))
                            }
                        }
                        continue@loop
                    }
                }
                when (type) {
                    "paid" -> requestList2.addAll(addUserData(id, UserData(key, paid = value), startRowIndex, endRowIndex, startColumnIndex))
                    "penalty" -> requestList2.addAll(addUserData(id, UserData(key, penalty = value), startRowIndex, endRowIndex, startColumnIndex))
                    else -> requestList2.addAll(addUserData(id, UserData(key, earned = value), startRowIndex, endRowIndex, startColumnIndex))
                }
            }
            requestList.addAll(requestList2)
            requestList.add(sort(id, startRowIndex, startColumnIndex))

            val r: BatchUpdateSpreadsheetRequest = BatchUpdateSpreadsheetRequest().setRequests(requestList)
            try {
                service.spreadsheets().batchUpdate(Main.config.spreadsheetURL, r).execute()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun sort(id: Int, startRowIndex: Int, startColumnIndex: Int): Request {
        val busReq = BatchUpdateSpreadsheetRequest()
        val ss = SortSpec()
        ss.sortOrder = "ASCENDING"
        ss.dimensionIndex = startColumnIndex
        val srr = SortRangeRequest()
        srr.range = GridRange().setStartRowIndex(startRowIndex).setStartColumnIndex(startColumnIndex).setEndColumnIndex(startColumnIndex + 5).setSheetId(id)
        srr.sortSpecs = listOf(ss)
        val req = Request()
        req.sortRange = srr
        busReq.requests = listOf(req)
        // Service is a instance of com.google.api.services.sheets.v4.Sheets
        return req
    }

    private fun updateUserDataByName(id: Int, valueRange: ValueRange?, name: String, user: UserData, startRowIndex: Int, startColumnIndex: Int): MutableList<Request> {
        val values = valueRange!!.getValues()
        // find row name is on
        var x = -1
        if (values != null) {
            for (row in values) {
                x++
                if (row[0] == name) {
                    break
                }
            }
        }
        if (x == -1) return mutableListOf()
        val requests: MutableList<Request> = ArrayList()
        requests.add(Request().setPasteData(PasteDataRequest().setData(user.name).setDelimiter("\t").setCoordinate(GridCoordinate().setColumnIndex(startColumnIndex).setRowIndex(startRowIndex + x).setSheetId(id))))
        if (user.earned != null)
            requests.add(Request().setPasteData(
                    PasteDataRequest().setData(DecimalFormat("#.##").format(user.earned) + "").setDelimiter("\t").setCoordinate(GridCoordinate().setColumnIndex(startColumnIndex + 1).setRowIndex(startRowIndex + x).setSheetId(id))))
        if (user.paid != null) requests.add(
                Request().setPasteData(PasteDataRequest().setData(user.paid.toString()).setDelimiter("\t").setCoordinate(GridCoordinate().setColumnIndex(startColumnIndex + 2).setRowIndex(startRowIndex + x).setSheetId(id))))
        if (user.penalty != null) requests.add(
                Request().setPasteData(PasteDataRequest().setData(user.penalty.toString()).setDelimiter("\t").setCoordinate(GridCoordinate().setColumnIndex(startColumnIndex + 3).setRowIndex(startRowIndex + x).setSheetId(id))))
        return requests
    }

    private val map = hashMapOf<Int, String>()
    private var long = 0L

    fun getValueRange(service: Sheets, id: Int, cellRange: String): ValueRange? {
        val time = System.currentTimeMillis()
        if (time - long > 10000) {
            long = time
            map.clear()
            for (sheet in service.spreadsheets().get(spreadsheetId).execute().sheets) {
                map[sheet.properties.sheetId] = sheet.properties.title
            }
        }

        val x = map[id] ?: throw RuntimeException("Sheet id for range is null!")
        try {
            return service.spreadsheets().values()[spreadsheetId, "'$x'!$cellRange"].execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getUserDataByName(valueRange: ValueRange?, name: String): Optional<UserData>? {
        val values = valueRange?.getValues()
        val data = MutableList<Any>(4) { String() }
        if (values != null) {
            for (row in values) {
                if (row[0] == name) {
                    for (i in 0 until row.size)
                        data[i] = row[i]
                    break
                }
            }
        }
        if (data.isEmpty()) return null
        val earned = if (data[1].toString().isNotBlank()) data[1].toString().toDouble() else null
        val paid = if (data[2].toString().isNotBlank()) data[2].toString().toDouble() else null
        val penalty = if (data[3].toString().isNotBlank()) data[3].toString().toDouble() else null
        val userData = UserData(data[0].toString(), earned, paid, penalty)
        return Optional.of(userData)
    }

    private fun getUserDataByNames(valueRange: ValueRange?, names: List<String>): CompletableFuture<List<UserData>> {
        return CompletableFuture.supplyAsync {
            val dataList: MutableList<UserData> = ArrayList()
            val values = valueRange?.getValues()
            for (name in names) {
                val data = MutableList<Any>(4) { String() }
                if (values != null) {
                    for (row in values) {
                        if (row[0] == name) {
                            for (i in 0 until row.size)
                                data[i] = row[i]
                            break
                        }
                    }
                }
                val earned = if (data[1].toString().isNotBlank()) data[1].toString().toDouble() else null
                val paid = if (data[2].toString().isNotBlank()) data[2].toString().toDouble() else null
                val penalty = if (data[3].toString().isNotBlank()) data[3].toString().toDouble() else null
                val userData = UserData(data[0].toString(), earned, paid, penalty)
                dataList.add(userData)
            }
            dataList
        }
    }

    private fun addUserData(id: Int, user: UserData, startRowIndex: Int, endRowIndex: Int, startColumnIndex: Int): MutableList<Request> {
        val valueList: MutableList<List<Any>> = ArrayList()
        val column: MutableList<Any> = ArrayList()
        column.add(user.name!!)
        column.add(user.earned.toString() + "")
        valueList.add(column)
        val valueRange = ValueRange()
        valueRange.majorDimension = "ROWS"
        valueRange.setValues(valueList)
        val insertRow = InsertDimensionRequest()
        insertRow.range = DimensionRange().setDimension("ROWS").setStartIndex(startRowIndex).setEndIndex(endRowIndex).setSheetId(id)
        val requests: MutableList<Request> = ArrayList()
        requests.add(Request().setInsertDimension(insertRow))
        requests.add(Request().setPasteData(PasteDataRequest().setData(user.name).setDelimiter("\t").setCoordinate(GridCoordinate().setColumnIndex(startColumnIndex).setRowIndex(startRowIndex).setSheetId(id))))
        if (user.earned != null)
            requests.add(Request().setPasteData(
                    PasteDataRequest().setData(DecimalFormat("#.##").format(user.earned) + "").setDelimiter("\t").setCoordinate(GridCoordinate().setColumnIndex(startColumnIndex + 1).setRowIndex(startRowIndex).setSheetId(id))))
        if (user.paid != null) requests.add(
                Request().setPasteData(PasteDataRequest().setData(user.paid.toString()).setDelimiter("\t").setCoordinate(GridCoordinate().setColumnIndex(startColumnIndex + 2).setRowIndex(startRowIndex).setSheetId(id))))
        if (user.penalty != null) requests.add(
                Request().setPasteData(PasteDataRequest().setData(user.penalty.toString()).setDelimiter("\t").setCoordinate(GridCoordinate().setColumnIndex(startColumnIndex + 3).setRowIndex(startRowIndex).setSheetId(id))))
        return requests
    }
}