package org.vaadin.lightvaadin.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceRemoteData {

    private static Logger log = Logger.getLogger(ServiceRemoteData.class.getName());

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String forecastWeatherApiUrl = "http://api.apixu.com/v1/forecast.json?key=28fccda6476946a285d103203180706&q=";
    private static final String exchangeRates = "https://www.cbr-xml-daily.ru/daily_json.js";

    private ServiceRemoteData(){
    }

    public static String[] getTemperature(String city){
        String json = sendGet(forecastWeatherApiUrl+city);
        if(json==null){
            return null;
        }
        return convertTemperatureFromJson(json);
    }

    private static String sendGet(String url){
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent",USER_AGENT);
            log.log(Level.INFO,"send get request, get weather data");
            return readBody(con.getInputStream());
        } catch (IOException e) {
            log.log(Level.SEVERE,"Exception: ",e);
        }
        return null;
    }

    public static Map<String,String> getExchangeRates(){
        String json = sendGet(exchangeRates);
        if(json==null){
            return null;
        }
        return convertRatesFromJson(json);
    }


    private static String readBody(InputStream inputStream)  {
        try( ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1){
                result.write(buffer,0,length);
            }
            return result.toString("UTF-8");
        } catch (IOException e) {
            log.log(Level.SEVERE,"Exception: " ,e);
        }
        return null;
    }

    private static String[] convertTemperatureFromJson(String json){
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();
        String[] result= new String[2];
        result[0]=object.get("current").getAsJsonObject().get("temp_c").toString();
        result[1]=object.get("forecast").getAsJsonObject()
                .get("forecastday").getAsJsonArray().get(0).getAsJsonObject()
                .get("day").getAsJsonObject()
                .get("avgtemp_c").toString();
        return result;
    }

    private static Map<String,String> convertRatesFromJson(String json){
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();
        Map<String,String> result = new HashMap<>();
        JsonObject data = object.get("Valute").getAsJsonObject();
        result.put("USD",data.get("USD").getAsJsonObject().get("Value").toString());
        result.put("EUR",data.get("EUR").getAsJsonObject().get("Value").toString());
        return result;
    }
}
