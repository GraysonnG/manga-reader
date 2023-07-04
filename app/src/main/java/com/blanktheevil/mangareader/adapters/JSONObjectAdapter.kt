package com.blanktheevil.mangareader.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject

internal class JSONObjectAdapter: JsonAdapter<JSONObject>() {

    @FromJson
    override fun fromJson(reader: JsonReader): JSONObject? {
        // Here we're expecting the JSON object, it is processed as Map<String, Any> by Moshi
        return (reader.readJsonValue() as? Map<String, Any>)?.let { data ->
            try {
                JSONObject(data)
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            }
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: JSONObject?) {
        value?.let { writer.value(Buffer().writeUtf8(value.toString())) }
    }
}