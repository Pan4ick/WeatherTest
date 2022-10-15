package com.example.weathertest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FavoriteCities extends AppCompatActivity {

    ArrayList<CellModel> cellModels;
    TableLayout tableLayout;
    private static final String ICONS_URL = "https://openweathermap.org/img/wn/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_cities);
        tableLayout = findViewById(R.id.table);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FavoriteCities.class);
        intent.putExtra("CellModelsAnswer", cellModels);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cellModels = (ArrayList<CellModel>) getIntent().getSerializableExtra("CellModels");
        init(cellModels);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void init(ArrayList<CellModel> cellModels) {
        for (CellModel c : cellModels) {
            if (c.isFavorite()) {
                TableRow tableRow = new TableRow(this);
                TextView main = new TextView(this);
                TextView city = new TextView(this);
                TextView wind = new TextView(this);
                TextView temperature = new TextView(this);
                ImageView starView = new ImageView(this);
                main.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                main.setText(c.getWeather());
                main.setTextSize(12);
                tableRow.addView(main);
                city.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                city.setText(c.getCity());
                city.setTextSize(12);
                tableRow.addView(city);
                wind.setText(String.format("%,.2f", c.getWind()) + "m/s");
                wind.setTextSize(12);
                wind.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tableRow.addView(wind);
                temperature.setText(String.format("%,.2f", c.getTemperature()) + "℃");
                temperature.setTextSize(12);
                main.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tableRow.addView(temperature);
                starView.setImageResource(android.R.drawable.btn_star_big_on);
                main.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tableRow.addView(starView);
                starView.setOnClickListener(v -> {
                    c.changeFavorite();
                    if (c.isFavorite()) {
                        starView.setImageResource(android.R.drawable.btn_star_big_on);
                    } else {
                        starView.setImageResource(android.R.drawable.btn_star_big_off);
                    }
                });
                tableLayout.addView(tableRow);
            }
        }
    }

    private void clearTable(TableLayout tableLayout) {

    }
}