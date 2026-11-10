# Funner Summer

> Have more fun this summer! Funner Summer suggests fun activities for you based on a number of factors: weather, activity history, time, day, and whether you are alone or with a group. Great for winter and snowy weather, too!
>
> Your activity history stays completely private and never leaves your phone.

Funner Summer is an Android app that recommends fun activities.

## Project moved

The project has moved to [Codeberg](https://codeberg.org/cberes/funner-summer).

## Get the app

<a href="https://codeberg.org/cberes/funner-summer/releases">
    <img alt="Get it on Codeberg" src="https://get-it-on.codeberg.org/get-it-on-white-on-black.png" height="60">
</a>

## Icon

Thank you [Molly Beres](https://mollyillustration.com/) for the beautiful icon!

## History

- 2014: I created this app back in the times of Android KitKat. Originally the weather was obtained via a web service that proxied requests to [OpenWeatherMap](https://openweathermap.org/api). This was a Java web service using Jersey, Spring, and Hystrix. It was way too complicated, but that's the technology I was using at the time.
- 2020: I updated the app for the latest Android version. I re-made the weather service as a serverless web app using AWS Lambda.
- 2024: Again I updated the app for the latest Android version. I removed all ads and the weather service. Instead weather can be input manually. It's extra work for the user, but the app needs fewer permissions: there's no need to access the location or the internet.
