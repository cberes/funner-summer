package net.seabears.funner.summer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.seabears.funner.weather.TemperatureUnit;
import net.seabears.funner.weather.Weather;
import net.seabears.funner.weather.WeatherService;

import java.util.HashMap;
import java.util.Map;

public class WeatherEditor extends FragmentActivity {

    private final WeatherService weatherService = new WeatherService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Weather weather = weatherService.getWeather(this);

        setupConditionRadio(weather);
        setupTemperatureControls(weather);
        setupUnitRadio(weather);
        setupDoneButton(weather);
    }

    private void setupConditionRadio(Weather weather) {
        Map<String, Integer> conditionToId = conditionToIdMap();
        Map<Integer, String> idToCondition = idToConditionMap();

        RadioGroup radio = (RadioGroup) findViewById(R.id.radio_weather);
        Integer selected = conditionToId.get(weather.getCondition());
        if (selected != null) {
            radio.check(selected);
        } else {
            radio.check(R.id.radio_clouds);
        }
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String condition = idToCondition.get(checkedId);
                weather.setCondition(condition);
                weatherService.setCondition(WeatherEditor.this, condition);
            }
        });
    }

    @NonNull
    private static Map<String, Integer> conditionToIdMap() {
        Map<String, Integer> conditionToId = new HashMap<>();
        conditionToId.put("clear", R.id.radio_clear);
        conditionToId.put("clouds", R.id.radio_clouds);
        conditionToId.put("drizzle", R.id.radio_drizzle);
        conditionToId.put("rain", R.id.radio_rain);
        conditionToId.put("thunderstorm", R.id.radio_thunderstorm);
        conditionToId.put("snow", R.id.radio_snow);
        conditionToId.put("atmosphere", R.id.radio_atmosphere);
        conditionToId.put("extreme", R.id.radio_extreme);
        return conditionToId;
    }

    @NonNull
    private static Map<Integer, String> idToConditionMap() {
        Map<Integer, String> idToCondition = new HashMap<>();
        for (Map.Entry<String, Integer> entry : conditionToIdMap().entrySet()) {
            idToCondition.put(entry.getValue(), entry.getKey());
        }
        return idToCondition;
    }

    private void setupTemperatureControls(Weather weather) {
        TextView text = (TextView) findViewById(R.id.text_temperature);
        text.setText(formatTemperature(weather.getTemperature()));

        SeekBar seek = (SeekBar) findViewById(R.id.seek_temperature);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seek.setMin(-100);
        }
        seek.setMax(150);
        seek.setProgress(weather.getTemperature());
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int temperature, boolean fromUser) {
                text.setText(formatTemperature(temperature));
                weather.setTemperature(temperature);
                weatherService.setTemperature(WeatherEditor.this, temperature);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // nothing
            }
        });
    }

    private String formatTemperature(int temperature) {
        return String.format(getResources().getConfiguration().locale, "%d°", temperature);
    }

    private void setupUnitRadio(Weather weather) {
        RadioGroup radio = (RadioGroup) findViewById(R.id.radio_temperature_unit);
        radio.check(weather.getTemperatureUnit() == TemperatureUnit.CELSIUS ? R.id.radio_temperature_c : R.id.radio_temperature_f);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TemperatureUnit unit = checkedId == R.id.radio_temperature_c ? TemperatureUnit.CELSIUS : TemperatureUnit.FAHRENHEIT;
                weather.setTemperatureUnit(unit);
                weatherService.setTemperatureUnit(WeatherEditor.this, unit);
            }
        });
    }

    private void setupDoneButton(Weather weather) {
        Button done = (Button) findViewById(R.id.button_weather_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithWeather(weather);
            }
        });
    }

    private void finishWithWeather(Weather weather) {
        Intent data = new Intent();
        data.putExtra("condition", weather.getCondition());
        data.putExtra("temperature", weather.getTemperatureAsF());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home)
        {
            finishWithWeather(weatherService.getWeather(this));
        }
        return super.onOptionsItemSelected(item);
    }
}