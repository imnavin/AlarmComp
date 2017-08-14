package weathercomp.model;

/**
 * Created by navin on 8/14/2017.
 */

public class BadWeather {

    public boolean isRaining(String data){

        //Log.v("Data : ",weather.place.getCity());
        //Log.v("Data : ",tempWeather.currentCondition.getDescription());

        data.toLowerCase();

        if((data.indexOf("rain"))!= -1){
            return true;
        }
        else if((data.indexOf("rainy"))!= -1){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isCloudy(String data){

        data.toLowerCase();

        if((data.indexOf("clouds"))!= -1){
            return true;
        }
        else if ((data.indexOf("cloudy"))!= -1){
            return true;
        }
        else{
            return false;
        }

        /*//data hold the whole StringBuffer that we returned from WeatherHttpClient class
        String data = ((new WeatherHttpClient()).getWeatherData(parms[0]));
        tempWeather = JSONWeatherParser.getWeather(data);

        //Log.v("Data : ",weather.place.getCity());
        //Log.v("Data : ",tempWeather.currentCondition.getDescription());

        data = ""+tempWeather.currentCondition.getDescription();

        data.toLowerCase();

        if((data.indexOf("clouds"))!= -1){
            return true;
        }
        else{
            return false;
        }*/
    }

    /*public boolean isBadWeather(boolean x, boolean y){
        if (x || y){
            return true;
        }
        else {
            return false;
        }
    }*/
}
