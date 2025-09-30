package com.watertracker.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WaterRecord {
    private String time;
    private int amount;

    public WaterRecord(String time, int amount) {
        this.time = time;
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public int getAmount() {
        return amount;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("time", time);
            json.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static WaterRecord fromJson(JSONObject json) {
        try {
            String time = json.getString("time");
            int amount = json.getInt("amount");
            return new WaterRecord(time, amount);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toJsonArray(List<WaterRecord> records) {
        JSONArray jsonArray = new JSONArray();
        for (WaterRecord record : records) {
            jsonArray.put(record.toJson());
        }
        return jsonArray.toString();
    }

    public static List<WaterRecord> fromJsonArray(String jsonString) {
        List<WaterRecord> records = new ArrayList<>();
        if (jsonString == null || jsonString.isEmpty()) {
            return records;
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                WaterRecord record = fromJson(json);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return records;
    }
}