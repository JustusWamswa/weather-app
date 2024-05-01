package com.example.chemirmir_justus_s2038641.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chemirmir_justus_s2038641.R;
import com.example.chemirmir_justus_s2038641.databinding.FragmentHomeBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<City> cities;
    private List<Forecast> forecast = new ArrayList<>();
    private List<Latest> latest = new ArrayList<>();
    private View root;
    private int selectedForecastDay = 1;
    private int selectedCityIndex = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        ImageButton prevButton = root.findViewById(R.id.prev_button);
        ImageButton nextButton = root.findViewById(R.id.next_button);
        ImageView prevForecast = root.findViewById(R.id.prev_button_forecast);
        ImageView nextForecast = root.findViewById(R.id.next_button_forecast);

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
                if (selectedForecastDay > 1) {
                    selectedForecastDay--;
                    setViewValues();
                    nextForecast.setVisibility(View.VISIBLE);
                } else {
                    prevForecast.setVisibility(View.INVISIBLE);
                }
            }
        });
        nextForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                if (selectedForecastDay < 3) {
                    selectedForecastDay++;
                    setViewValues();
                    prevForecast.setVisibility(View.VISIBLE);
                } else {
                    nextForecast.setVisibility(View.INVISIBLE);
                }
            }
        });


        return root;
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
        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_anim);
        v.startAnimation(scaleAnimation);

        // Apply alpha animation
        Animation alphaAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_anim);
        v.startAnimation(alphaAnimation);
    }

    private void fetchForecastData(int position, int id, String name, String country) {
        showLoader();
        forecast.clear();
        selectedForecastDay = 1;

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // display the fetched data
                            if (data.length() > 0) {
                                TextView featuredLocationNumber = root.findViewById(R.id.text_location_count);
                                TextView featuredLocation = root.findViewById(R.id.text_location);

                                featuredLocationNumber.setText(String.format("Featured location %d of 6", position));
                                featuredLocation.setText(String.format("%s, %s", name, country));

                                setViewValues();
                                fetchLatestData(id);

                            } else {
                                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_LONG).show();
                                hideLoader();
                            }

                            // Log the values of the forecast list
                            for (Forecast f : forecast) {
                                Log.d("FORECAST", "Day: " + f.getDay());
                                Log.d("FORECAST", "Title: " + f.getTitle());
                                Log.d("FORECAST", "Description: " + f.getDescription());
                                Log.d("FORECAST", "PubDate: " + f.getPubDate());
                                Log.d("FORECAST", "GeoLocation: " + f.getGeoLocation());
                                Log.d("FORECAST", "-------------------");
                            }

                            // Log the first element of the forecast list if it's not empty
                            if (!forecast.isEmpty()) {
                                Forecast firstForecast = forecast.get(0);
                                Log.d("FIRST_FORECAST", "Day: " + firstForecast.getDay());
                                Log.d("FIRST_FORECAST", "Title: " + firstForecast.getTitle());
                                Log.d("FIRST_FORECAST", "Description: " + firstForecast.getDescription());
                                Log.d("FIRST_FORECAST", "PubDate: " + firstForecast.getPubDate());
                                Log.d("FIRST_FORECAST", "GeoLocation: " + firstForecast.getGeoLocation());
                            } else {
                                Log.d("FIRST_FORECAST", "Forecast list is empty.");
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

                TextView weatherDesc = root.findViewById(R.id.text_weather_desc);
                TextView date = root.findViewById(R.id.text_day_time);
                TextView update = root.findViewById(R.id.text_update);
                TextView maxTemp = root.findViewById(R.id.text_temperature_max_value);
                TextView minTemp = root.findViewById(R.id.text_temperature_min_value);
                TextView windSpeedValue = root.findViewById(R.id.text_wind_speed_value);
                TextView windDirectionValue = root.findViewById(R.id.text_wind_direction_value);
                TextView uvRiskValue = root.findViewById(R.id.text_uv_risk_value);
                TextView pollutionValue = root.findViewById(R.id.text_pollution_value);
                TextView sunriseValue = root.findViewById(R.id.text_sunrise_value);
                TextView sunsetValue = root.findViewById(R.id.text_sunset_value);
                TextView humidityValue = root.findViewById(R.id.text_humidity_value);
                TextView pressureValue = root.findViewById(R.id.text_pressure_value);
                TextView visibilityValue = root.findViewById(R.id.text_visibility_value);
                TextView coordinatesValue = root.findViewById(R.id.text_coordinates_value);

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

                date.setText(nextDayString);
                weatherDesc.setText(weatherCondition);
                maxTemp.setText(maxTemperature);
                minTemp.setText(minTemperature);
                windDirectionValue.setText(windDirection);
                windSpeedValue.setText(windSpeed);
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display the fetched data in the TextView
                            // Replace rawDataDisplay with your appropriate TextView in the layout
                            if (data.length() > 0) {
                                TextView textTemperature = root.findViewById(R.id.text_temperature);
                                String temperature = extractTemperatureFromData(data.toString());
                                textTemperature.setText(temperature);
                            } else {
                                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_LONG).show();
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

    // method to get image bitmap and remove background
    private void getImageSrc(String imageUrl) {
        ImageView imageView = root.findViewById(R.id.image_weather);

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
                        getActivity().runOnUiThread(new Runnable() {
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

    // method to extract temperature value from the data string
    private String extractTemperatureFromData(String data) {
        // find the index of "°C"
        int endIndex = data.indexOf("°C");
        if (endIndex != -1) {
            // find the index of the last space before "°C"
            int startIndex = data.lastIndexOf(" ", endIndex);
            if (startIndex != -1 && startIndex < endIndex) {
                // extract the substring containing temperature
                return data.substring(startIndex + 1, endIndex + 2);
            }
        }
        // return "N/A" if temperature is not found
        return "N/A";
    }

    // method to extract weather condition from the data string
    private String extractWeatherCondition(String data) {
        // find the index of the first colon
        int colonIndex = data.indexOf(":");
        // if colonIndex is -1, return an empty string
        if (colonIndex == -1) {
            return "";
        }
        // extract the substring after the colon
        String condition = data.substring(colonIndex + 1).trim();
        // split the string by comma and take the first part
        String[] parts = condition.split(",");
        if (parts.length > 0) {
            // return the extracted weather condition
            return parts[0].trim();
        } else {
            // return an empty string if the string cannot be split
            return "";
        }
    }

    // method to extract date and time from the data string
    private String[] extractDateTime(String data) {
        // split the string by spaces
        String[] parts = data.split(" ");
        if (parts.length >= 5) {
            // combine the first four parts to get the date
            String date = parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3];
            // combine the fifth and sixth parts to get the time
            String time = parts[4] + " " + parts[5];
            // return date and time as an array
            return new String[]{date, time};
        } else {
            // return empty strings if the string cannot be split
            return new String[]{"", ""};
        }
    }

    // method to extract weather element value from the data string
    private String extractValueByKey(String data, String key) {
        // split the string by commas
        String[] parts = data.split(",");
        // loop through the parts to find the key-value pair
        for (String part : parts) {
            // split the part by ":" to separate key and value
            String[] keyValue = part.split(":");
            // sunrise and sunset have different formatting
            if (keyValue.length == 3 && keyValue[0].trim().equals(key)) {
                return keyValue[1].trim() + ":" + keyValue[2].trim();
            }
            // if the key matches, return the value (trimmed)
            if (keyValue.length == 2 && keyValue[0].trim().equals(key)) {
                return keyValue[1].trim();
            }
        }
        // if the key is not found, return "N/A"
        return "N/A";
    }

    // method to extract coordinates from the data string
    private String extractCoordinates(String coordinates) {
        // split the string by space to separate latitude and longitude
        String[] parts = coordinates.split(" ");
        if (parts.length == 2) {
            // extract latitude and longitude
            String latitude = parts[0];
            String longitude = parts[1];

            // append degree symbol and direction to latitude and longitude
            String formattedCoordinates = Math.abs(Double.parseDouble(latitude)) + "°" + (Double.parseDouble(latitude) < 0 ? "S, " : "N, ") +
                    Math.abs(Double.parseDouble(longitude)) + "°" + (Double.parseDouble(longitude) < 0 ? "W" : "E");

            return formattedCoordinates;
        } else {
            return "N/A";
        }
    }


    // Method to show loader
    private void showLoader() {
        ProgressBar progressBar = root.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    // Method to hide loader
    private void hideLoader() {
        ProgressBar progressBar = root.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // class representing a city

    private static class City {
        private int position;
        private int id;
        private String name;
        private String country;

        public City(int position, int id, String name, String country) {
            this.position = position;
            this.id = id;
            this.name = name;
            this.country = country;
        }

        public int getPosition() {
            return position;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }
    }

    // class representing weather forecast
    private static class Forecast {
        private int day;
        private String title;
        private String description;
        private String pubDate;
        private String geoLocation;

        public Forecast(int day, String title, String description, String pubDate, String geoLocation) {
            this.day = day;
            this.title = title;
            this.description = description;
            this.pubDate = pubDate;
            this.geoLocation = geoLocation;
        }

        public int getDay() {
            return day;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getGeoLocation() {
            return geoLocation;
        }

    }

    // class representing latest weather
    private static class Latest {
        private String description;

        public Latest(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}