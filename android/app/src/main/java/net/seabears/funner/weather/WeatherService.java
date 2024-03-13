package net.seabears.funner.weather;

import android.content.Context;
import android.content.SharedPreferences;

public class WeatherService {
    private static final String KEY_CONDITION = "condition";
    private static final String KEY_TEMPERATURE = "temperature";
    private static final String KEY_TEMPERATURE_UNIT = "temperature_unit";

    public Weather getWeather(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        String condition = sharedPref.getString(KEY_CONDITION, "clouds");
        int temperature = sharedPref.getInt(KEY_TEMPERATURE, 70);
        String unit = sharedPref.getString(KEY_TEMPERATURE_UNIT, TemperatureUnit.FAHRENHEIT.name());
        return new Weather(condition, temperature, TemperatureUnit.valueOf(unit));
    }

    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("com.cberes.funner.summer.weather", Context.MODE_PRIVATE);
    }

    public void setCondition(Context context, String condition) {
        SharedPreferences sharedPref = getPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_CONDITION, condition);
        editor.apply();
    }

    public void setTemperature(Context context, int temperature) {
        SharedPreferences sharedPref = getPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_TEMPERATURE, temperature);
        editor.apply();
    }

    public void setTemperatureUnit(Context context, TemperatureUnit unit) {
        SharedPreferences sharedPref = getPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_TEMPERATURE_UNIT, unit.name());
        editor.apply();
    }
}
