package net.seabears.funner.weather;

public enum TemperatureUnit {
    FAHRENHEIT {
        @Override
        public int toCelsius(int value) {
            return (int) ((value - 32) * 5 / 9.0);
        }

        @Override
        public int toFahrenheit(int value) {
            return value;
        }
    },

    CELSIUS {
        @Override
        public int toCelsius(int value) {
            return value;
        }

        @Override
        public int toFahrenheit(int value) {
            return (int) (value * 9 / 5.0) + 32;
        }
    };

    public String code() {
        return name().substring(0, 1);
    }

    public abstract int toCelsius(int value);

    public abstract int toFahrenheit(int value);
}
