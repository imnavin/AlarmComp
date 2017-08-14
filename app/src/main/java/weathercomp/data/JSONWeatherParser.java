package weathercomp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weathercomp.Util.Utils;
import weathercomp.model.Place;
import weathercomp.model.Weather;

/**
 * Created by navin on 8/14/2017.
 */

public class JSONWeatherParser {

    public static Weather getWeather(String data){
        Weather weather = new Weather();

        //Create JSON object from data
        try {
            JSONObject jsonObject = new JSONObject(data);
            Place place = new Place();
            JSONObject coordObj = Utils.getObject("coord",jsonObject);
            place.setLat(Utils.getFloat("lat", coordObj));
            place.setLon(Utils.getFloat("lon", coordObj));

            //Get the sys object
            JSONObject sysObj = Utils.getObject("sys",jsonObject);
            place.setCountry(Utils.getString("country",sysObj));
            place.setCity(Utils.getString("name",jsonObject));
            weather.place = place;

            //Get the weather info
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherId(Utils.getInt("id",jsonWeather));
            weather.currentCondition.setDescription(Utils.getString("description",jsonWeather));
            weather.currentCondition.setCondition(Utils.getString("main",jsonWeather));

            //Get the cloud object
            JSONObject cloudObj = Utils.getObject("clouds",jsonObject);
            weather.clouds.setPrecipitation(Utils.getInt("all",cloudObj));

            return weather;


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }
}
