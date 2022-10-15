package com.example.weathertest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //TextView[] mainViews = new TextView[]{findViewById(R.id.main1), findViewById(R.id.main2), findViewById(R.id.main3), findViewById(R.id.main4)};
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String ICONS_URL = "https://openweathermap.org/img/wn/";
    private static final String APP_ID = "348b4aa02eb4246162995089567cda2e";
    SharedPreferences settings;
    public static final String APP_PREFERENCES = "favSettings";
    private static final List<String> cities = List.of("Krasnogorsk", "Moscow", "Yaroslavl", "Saint Petersburg");
    List<TextView> mainViews = new ArrayList<>();
    List<TextView> cityViews = new ArrayList<>();
    List<TextView> temperatureViews = new ArrayList<>();
    List<ImageView> imageViews = new ArrayList<>();
    List<ImageView> favoriteViews = new ArrayList<>();
    ArrayList<CellModel> cellModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViews = List.of(findViewById(R.id.main1), findViewById(R.id.main2), findViewById(R.id.main3), findViewById(R.id.main4));
        imageViews = List.of(findViewById(R.id.weatherIcon1), findViewById(R.id.weatherIcon2), findViewById(R.id.weatherIcon3), findViewById(R.id.weatherIcon4));
        cityViews = List.of(findViewById(R.id.city1), findViewById(R.id.city2), findViewById(R.id.city3), findViewById(R.id.city4));
        temperatureViews = List.of(findViewById(R.id.temperature1), findViewById(R.id.temperature2), findViewById(R.id.temperature3), findViewById(R.id.temperature4));
        favoriteViews = List.of(findViewById(R.id.star1), findViewById(R.id.star2), findViewById(R.id.star3), findViewById(R.id.star4));
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int i = 0;
        for (String c : cities) {
            getWeatherForCity(c, i);
            i++;
        }
    }

    @Override
    protected void onPause() {
        saveFavorites();
        cellModels.clear();
        super.onPause();
    }

    private void getWeatherForCity(String city, int cellId) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        getJson(params, cellId);
    }

    private void getJson(RequestParams params, int cellId) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    CellModel cell = cellModelFactory(response);
                    updateUI(cell, cellId);
                    cellModels.add(cell);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private CellModel cellModelFactory(JSONObject jsonObject) throws JSONException {
        CellModel cellModel = new CellModel();
        cellModel.setCity(jsonObject.getString("name"));
        cellModel.setWeather(jsonObject.getJSONArray("weather").getJSONObject(0).getString("main"));
        cellModel.setDescription(jsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));
        cellModel.setIcon(jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png");
        cellModel.setTemperature(jsonObject.getJSONObject("main").getDouble("temp") - 273.15);
        cellModel.setWind(jsonObject.getJSONObject("wind").getDouble("speed"));
        loadFavorites(cellModel);
        return cellModel;
    }

    private void saveFavorites() {
        SharedPreferences.Editor prefEditor = settings.edit();
        for (CellModel c : cellModels) {
            prefEditor.putBoolean(c.getCity(), c.isFavorite());
            prefEditor.apply();
        }
    }

    private void loadFavorites(CellModel cellModel) {
        cellModel.setFavorite(settings.getBoolean(cellModel.getCity(), false));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI(CellModel cell, int cellId) {
        mainViews.get(cellId).setText(cell.getWeather());
        cityViews.get(cellId).setText(cell.getCity());
        temperatureViews.get(cellId).setText(String.format("%,.2f", cell.getTemperature()) + "℃");
        Glide.with(this).asDrawable().load(ICONS_URL + cell.getIcon()).into(imageViews.get(cellId));
        ImageView starView = favoriteViews.get(cellId);
        if (cell.isFavorite()) {
            starView.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            starView.setImageResource(android.R.drawable.btn_star_big_off);
        }
        starView.setOnClickListener(v -> {
            cell.changeFavorite();
            if (cell.isFavorite()) {
                starView.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                starView.setImageResource(android.R.drawable.btn_star_big_off);
            }
        });
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    cellModels = (ArrayList<CellModel>) intent.getSerializableExtra("CellModelsAnswer");
                    saveFavorites();
                    cellModels.clear();
                }
            });

    public void openFavorites(View view) {
        Intent intent = new Intent(this, FavoriteCities.class);
        intent.putExtra("CellModels", cellModels);
        mStartForResult.launch(intent);
        cellModels.clear();
    }


}