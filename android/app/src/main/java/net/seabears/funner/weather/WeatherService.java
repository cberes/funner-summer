package net.seabears.funner.weather;

import android.content.Context;
import android.content.SharedPreferences;

public class WeatherService {
    private static final String KEY_CONDITION = "condition";
    private static final String KEY_TEMPERATURE = "temperature";

    public Weather getWeather(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        String condition = sharedPref.getString(KEY_CONDITION, "clouds");
        int temperature = sharedPref.getInt(KEY_TEMPERATURE, 70);
        return new Weather(condition, temperature);
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
}
