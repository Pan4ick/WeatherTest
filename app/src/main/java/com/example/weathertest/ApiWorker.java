package com.example.weathertest;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiWorker {
    public CellModel cellModelFactory(JSONObject jsonObject) throws JSONException {
        CellModel cellModel = new CellModel();
        cellModel.setCity(jsonObject.getString("name"));
        cellModel.setWeather(jsonObject.getJSONArray("weather").getJSONObject(0).getString("main"));
        cellModel.setDescription(jsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));
        cellModel.setIcon(jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon"));
        cellModel.setTemperature(jsonObject.getJSONObject("main").getDouble("temp") - 273.15);
        cellModel.setWind(jsonObject.getJSONObject("wind").getDouble("speed"));
        return cellModel;
    }
}
