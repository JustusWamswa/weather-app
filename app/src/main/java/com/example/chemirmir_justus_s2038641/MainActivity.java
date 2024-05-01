//
// Name                 ________ Justus Wamswa Chemirmir
// Student ID           ________ S2038641
// Programme of Study   ________ BSc (Hons) Computing
//

package com.example.chemirmir_justus_s2038641;

import static com.example.chemirmir_justus_s2038641.ExtractorUtil.extractCoordinates;
import static com.example.chemirmir_justus_s2038641.ExtractorUtil.extractDateTime;
import static com.example.chemirmir_justus_s2038641.ExtractorUtil.extractTemperatureFromData;
import static com.example.chemirmir_justus_s2038641.ExtractorUtil.extractValueByKey;
import static com.example.chemirmir_justus_s2038641.ExtractorUtil.extractWeatherCondition;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private List<City> cities;
    private List<Forecast> forecast = new ArrayList<>();
    private List<Latest> latest = new ArrayList<>();
    private int selectedForecastDay = 1;
    private int selectedCityIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //remove actionbar
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!NetworkUtil.isNetworkAvailable(this)) {
            // show a Toast message indicating no internet connection
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }

        ImageButton prevButton = findViewById(R.id.prev_button);
        ImageButton nextButton = findViewById(R.id.next_button);
        ImageView prevForecast = findViewById(R.id.prev_button_forecast);
        ImageView nextForecast = findViewById(R.id.next_button_forecast);
        LinearLayout search = findViewById(R.id.search_city);
        ScrollView searchScrollView = findViewById(R.id.scrollview_2);
        ImageButton backButton = findViewById(R.id.back_button);
        LinearLayout glasgow = findViewById(R.id.glasgow);
        LinearLayout london = findViewById(R.id.london);
        LinearLayout nyc = findViewById(R.id.nyc);
        LinearLayout muscat = findViewById(R.id.muscat);
        LinearLayout pl = findViewById(R.id.pl);
        LinearLayout dhaka = findViewById(R.id.dhaka);

        // creating a list of cities
        cities = new ArrayList<>();
        cities.add(new City(1, 2648579, "Glasgow", "Scotland"));
        cities.add(new City(2, 2643743, "London", "England"));
        cities.add(new City(3, 5128581, "New York", "USA"));
        cities.add(new City(4, 287286, "Muscat", "Oman"));
        cities.add(new City(5, 934154, "Port Louis", "Mauritius"));
        cities.add(new City(6, 1185241, "Dhaka", "Bangladesh"));

        // trigger network request to get the first city
        if (!cities.isEmpty()) {
            updateCityData();
        }

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);

                selectedCityIndex--;
                if (selectedCityIndex < 0) {
                    selectedCityIndex = cities.size() - 1;
                }
                updateCityData();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);

                selectedCityIndex++;
                if (selectedCityIndex >= cities.size()) {
                    selectedCityIndex = 0;
                }
                updateCityData();
            }
        });
        prevForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                nextForecast.setVisibility(View.VISIBLE);
                if (selectedForecastDay > 1) {
                    selectedForecastDay--;
                    setViewValues();
                    if (selectedForecastDay == 1) {
                        prevForecast.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        nextForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                prevForecast.setVisibility(View.VISIBLE);
                if (selectedForecastDay < 3) {
                    selectedForecastDay++;
                    setViewValues();
                    if (selectedForecastDay == 3) {
                        nextForecast.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                searchScrollView.setVisibility(View.VISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                searchScrollView.setVisibility(View.GONE);
            }
        });

        glasgow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                searchScrollView.setVisibility(View.GONE);
                selectedCityIndex = 0;
                updateCityData();
            }
        });
        london.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                searchScrollView.setVisibility(View.GONE);
                selectedCityIndex = 1;
                updateCityData();
            }
        });
        nyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                searchScrollView.setVisibility(View.GONE);
                selectedCityIndex = 2;
                updateCityData();
            }
        });
        muscat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                searchScrollView.setVisibility(View.GONE);
                selectedCityIndex = 3;
                updateCityData();
            }
        });
        pl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                searchScrollView.setVisibility(View.GONE);
                selectedCityIndex = 4;
                updateCityData();
            }
        });
        dhaka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                searchScrollView.setVisibility(View.GONE);
                selectedCityIndex = 5;
                updateCityData();
            }
        });

    }

    private void fetchForecastData(int position, int id, String name, String country) {
        showLoader();
        forecast.clear();
        selectedForecastDay = 1;
        ImageView prevForecast = findViewById(R.id.prev_button_forecast);
        ImageView nextForecast = findViewById(R.id.next_button_forecast);
        prevForecast.setVisibility(View.INVISIBLE);
        nextForecast.setVisibility(View.VISIBLE);

        // set the url to have the given id
        String urlString = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + id;

        // run network access on separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(url.openStream(), null);

                    int eventType = parser.getEventType();
                    StringBuilder data = new StringBuilder();
                    int day = 1;
                    String imageUrl = "";
                    String title = "";
                    String description = "";
                    String pubDate = "";
                    String geoLocation = "";

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String tagName = parser.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                if (tagName.equalsIgnoreCase(("image"))) {

                                    while (!(eventType == XmlPullParser.END_TAG && tagName.equalsIgnoreCase("image"))) {
                                        if (eventType == XmlPullParser.START_TAG) {
                                            if (tagName.equalsIgnoreCase("url")) {
                                                imageUrl = parser.nextText().trim();
                                                Log.d("IMAGE", "Url: " + imageUrl);
                                                getImageSrc(imageUrl);
                                            }
                                        }
                                        eventType = parser.next();
                                        tagName = parser.getName();
                                    }
                                }
                                if (tagName.equalsIgnoreCase(("item"))) {

                                    while (!(eventType == XmlPullParser.END_TAG && tagName.equalsIgnoreCase("item"))) {
                                        if (eventType == XmlPullParser.START_TAG) {
                                            if (tagName.equalsIgnoreCase("title")) {
                                                title = parser.nextText().trim();
                                            } else if (tagName.equalsIgnoreCase("description")) {
                                                description = parser.nextText().trim();
                                            } else if (tagName.equalsIgnoreCase("pubDate")) {
                                                pubDate = parser.nextText().trim();
                                            } else if (tagName.equalsIgnoreCase("georss:point")) {
                                                geoLocation = parser.nextText().trim();
                                            }
                                        }
                                        eventType = parser.next();
                                        tagName = parser.getName();
                                    }

                                    data.append(title).append("\n")
                                            .append(description).append("\n")
                                            .append(pubDate).append("\n")
                                            .append(geoLocation).append("\n\n");

                                    forecast.add(new Forecast(day, title, description, pubDate, geoLocation));

                                    day += 1;

                                }
                                break;
                        }
                        eventType = parser.next();
                    }

                    // update ui with fetched data
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // display the fetched data
                            if (data.length() > 0) {
                                TextView featuredLocationNumber = findViewById(R.id.text_location_count);
                                TextView featuredLocation = findViewById(R.id.text_location);

                                featuredLocationNumber.setText(String.format("Featured location %d of 6", position));
                                featuredLocation.setText(String.format("%s, %s", name, country));

                                setViewValues();
                                fetchLatestData(id);

                            } else {
                                Toast.makeText(MainActivity.this, "Failed to fetch data!", Toast.LENGTH_LONG).show();
                                hideLoader();
                            }
                        }
                    });
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void setViewValues() {
        for (Forecast f : forecast) {
            if (f.getDay() == selectedForecastDay) {

                TextView weatherDesc = findViewById(R.id.text_weather_desc);
                TextView date = findViewById(R.id.text_day_time);
                TextView update = findViewById(R.id.text_update);
                TextView maxTemp = findViewById(R.id.text_temperature_max_value);
                TextView minTemp = findViewById(R.id.text_temperature_min_value);
                TextView windSpeedValue = findViewById(R.id.text_wind_speed_value);
                TextView windDirectionValue = findViewById(R.id.text_wind_direction_value);
                TextView uvRiskValue = findViewById(R.id.text_uv_risk_value);
                TextView pollutionValue = findViewById(R.id.text_pollution_value);
                TextView sunriseValue = findViewById(R.id.text_sunrise_value);
                TextView sunsetValue = findViewById(R.id.text_sunset_value);
                TextView humidityValue = findViewById(R.id.text_humidity_value);
                TextView pressureValue = findViewById(R.id.text_pressure_value);
                TextView visibilityValue = findViewById(R.id.text_visibility_value);
                TextView coordinatesValue = findViewById(R.id.text_coordinates_value);

                String weatherCondition = extractWeatherCondition(f.getTitle());
                String[] dateTime = extractDateTime(f.getPubDate());
                String maxTemperature = extractValueByKey(f.getDescription(), "Maximum Temperature");
                String minTemperature = extractValueByKey(f.getDescription(), "Minimum Temperature");
                String windDirection = extractValueByKey(f.getDescription(), "Wind Direction");
                String windSpeed = extractValueByKey(f.getDescription(), "Wind Speed");
                String visibility = extractValueByKey(f.getDescription(), "Visibility");
                String pressure = extractValueByKey(f.getDescription(), "Pressure");
                String humidity = extractValueByKey(f.getDescription(), "Humidity");
                String uvRisk = extractValueByKey(f.getDescription(), "UV Risk");
                String pollution = extractValueByKey(f.getDescription(), "Pollution");
                String sunrise = extractValueByKey(f.getDescription(), "Sunrise");
                String sunset = extractValueByKey(f.getDescription(), "Sunset");
                String coordinates = extractCoordinates(f.getGeoLocation());

                update.setText(String.format("Last update: %s", dateTime[1]));

                SimpleDateFormat inputFormatter = new SimpleDateFormat("EEE, dd MMMM yyyy", Locale.ENGLISH);
                Date initialDate = null;
                try {
                    initialDate = inputFormatter.parse(dateTime[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
                long initialTime = initialDate.getTime();
                long oneDayMillis = 24 * 60 * 60 * 1000;
                long nextDayTime = initialTime + oneDayMillis * (selectedForecastDay - 1);
                Date nextDayDate = new Date(nextDayTime);
                String nextDayString = inputFormatter.format(nextDayDate);

                // extract numeric value from the string
                String numericValue = windSpeed.replaceAll("[^\\d.]", "");
                double speedInMph = Double.parseDouble(numericValue);
                // convert speed from mph to km/h
                double speedInKmh = speedInMph * 1.60934;
                // round off to two decimal places
                DecimalFormat df = new DecimalFormat("#.##");
                double roundedSpeedInKmh = Double.parseDouble(df.format(speedInKmh));
                String overallWindSpeed = windSpeed + "/" + roundedSpeedInKmh + "km/h";

                date.setText(nextDayString);
                weatherDesc.setText(weatherCondition);
                maxTemp.setText(maxTemperature);
                minTemp.setText(minTemperature);
                windDirectionValue.setText(windDirection);
                windSpeedValue.setText(overallWindSpeed);
                visibilityValue.setText(visibility);
                pressureValue.setText(pressure);
                humidityValue.setText(humidity);
                uvRiskValue.setText(uvRisk);
                pollutionValue.setText(pollution);
                sunriseValue.setText(sunrise);
                sunsetValue.setText(sunset);
                coordinatesValue.setText(coordinates);

            }

        }
    }

    private void fetchLatestData(int id) {
        // set the url to have the given id
        String urlString = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/" + id;

        // run network access on separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(url.openStream(), null);

                    int eventType = parser.getEventType();
                    StringBuilder data = new StringBuilder();

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String tagName = parser.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                if (tagName.equalsIgnoreCase(("item"))) {
                                    String description = "";

                                    while (!(eventType == XmlPullParser.END_TAG && tagName.equalsIgnoreCase("item"))) {
                                        if (eventType == XmlPullParser.START_TAG) {
                                            if (tagName.equalsIgnoreCase("description")) {
                                                description = parser.nextText().trim();
                                            }
                                        }
                                        eventType = parser.next();
                                        tagName = parser.getName();
                                    }

                                    data.append(description).append("\n");

                                    latest.add(new Latest(description));

                                }
                                break;
                        }
                        eventType = parser.next();
                    }

                    // update ui with fetched data
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display the fetched data in the TextView
                            // Replace rawDataDisplay with your appropriate TextView in the layout
                            if (data.length() > 0) {
                                TextView textTemperature = findViewById(R.id.text_temperature);
                                String temperature = extractTemperatureFromData(data.toString());
                                textTemperature.setText(temperature);
                                // remove the °C from the string
                                String temperatureString = temperature.replaceAll("[^0-9]", "");
                                int temperatureInteger = Integer.parseInt(temperatureString);

                                ScrollView scrollView = findViewById(R.id.scrollview_1);

                                if (temperatureInteger <= 10) {
                                    scrollView.setBackgroundResource(R.drawable.cold_background);
                                    Window window = getWindow();
                                    window.setStatusBarColor(getResources().getColor(R.color.cold));
                                } else if (temperatureInteger > 25) {
                                    scrollView.setBackgroundResource(R.drawable.hot_background);
                                    Window window = getWindow();
                                    window.setStatusBarColor(getResources().getColor(R.color.hot));
                                } else {
                                    scrollView.setBackgroundResource(R.drawable.warm_background);
                                    Window window = getWindow();
                                    window.setStatusBarColor(getResources().getColor(R.color.warm));
                                }
                                int fahrenheit = (int) Math.ceil((double) (temperatureInteger * 9) / 5) + 32;
                                String fahrenheitString = "/" + fahrenheit + "°F";
                                TextView textFahrenheit = findViewById(R.id.text_fahrenheit);
                                textFahrenheit.setText(fahrenheitString);


                            } else {
                                Toast.makeText(MainActivity.this, "Failed to fetch data!", Toast.LENGTH_LONG).show();
                            }
                            hideLoader();
                        }
                    });
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    // method to update data of the selected city
    private void updateCityData() {
        City city = cities.get(selectedCityIndex);
        int position = city.getPosition();
        int id = city.getId();
        String name = city.getName();
        String country = city.getCountry();
        fetchForecastData(position, id, name, country);
    }

    // method to apply animation to the button
    private void animateButton(View v) {
        // Apply scale animation
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
        v.startAnimation(scaleAnimation);

        // Apply alpha animation
        Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        v.startAnimation(alphaAnimation);
    }


    // method to get image bitmap and remove background
    private void getImageSrc(String imageUrl) {
        ImageView imageView = findViewById(R.id.image_weather);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // create URL object
                    URL url = new URL(imageUrl);

                    // open connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // check if connection is successful
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        // get input stream
                        InputStream inputStream = connection.getInputStream();

                        // decode the input stream into a Bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        // Update UI on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Set the Bitmap as the image source of the ImageView
                                imageView.setImageBitmap(bitmap);
                            }
                        });

                        // Close input stream
                        inputStream.close();
                    } else {
                        Log.e("ImageLoader", "Failed to load image: " + connection.getResponseCode());
                    }

                    // Disconnect connection
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    // Method to show loader
    private void showLoader() {
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    // Method to hide loader
    private void hideLoader() {
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }
}