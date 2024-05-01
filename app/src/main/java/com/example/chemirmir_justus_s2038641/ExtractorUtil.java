package com.example.chemirmir_justus_s2038641;

//
// Name                 ________ Justus Wamswa Chemirmir
// Student ID           ________ S2038641
// Programme of Study   ________ BSc (Hons) Computing
//
public class ExtractorUtil {
    // method to extract coordinates from the data string
    public static String extractCoordinates(String coordinates) {
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

    // method to extract temperature value from the data string
    public static String extractTemperatureFromData(String data) {
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
    public static String extractWeatherCondition(String data) {
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
    public static String[] extractDateTime(String data) {
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
    public static String extractValueByKey(String data, String key) {
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


}
