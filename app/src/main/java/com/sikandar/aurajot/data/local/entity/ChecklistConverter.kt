package com.sikandar.aurajot.data.local.entity

import com.sikandar.aurajot.domain.model.ChecklistItem
import org.json.JSONArray
import org.json.JSONObject

object ChecklistConverter {
    fun fromList(items: List<ChecklistItem>): String {
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("text", item.text)
            obj.put("isDone", item.isDone)
            array.put(obj)
        }
        return array.toString()
    }

    fun toList(raw: String?): List<ChecklistItem> {
        if (raw.isNullOrBlank()) return emptyList()
        val list = mutableListOf<ChecklistItem>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ChecklistItem(
                        id = obj.optString("id", i.toString()),
                        text = obj.optString("text", ""),
                        isDone = obj.optBoolean("isDone", false)
                    )
                )
            }
        } catch (_: Exception) {
            // graceful fallback
        }
        return list
    }
}
