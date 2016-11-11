package com.cmpe277.android.sjsumap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*Called by building activity classes to get the time taken and distance to reach the destination from Google Distance Matrix API in background.
  Geo calls setValues to set the values in the layout*/
public class GeoTask extends AsyncTask<String, Void, String> {

    Context mContext;

    Geo geo1;
    //constructor is used to get the context
    public GeoTask(Context mContext) {
        this.mContext = mContext;
        geo1= (Geo) mContext;
    }

    @Override
    protected void onPostExecute(String aDouble) {
        super.onPostExecute(aDouble);
        if(aDouble!=null)
        {
            geo1.setValues(aDouble);
        }
        else
            Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
    }

    //Json Parsing such that it gets the values from "value" in distance and duration (Standard units meters and seconds)
    @Override
    protected String doInBackground(String... params) {
        try {
            URL url=new URL(params[0]);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statuscode=con.getResponseCode();
            if(statuscode==HttpURLConnection.HTTP_OK)
            {
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String line=br.readLine();
                while(line!=null)
                {
                    sb.append(line);
                    line=br.readLine();
                }
                String json=sb.toString();
                Log.d("JSON",json);
                JSONObject root=new JSONObject(json);
                JSONArray array_rows=root.getJSONArray("rows");
                Log.d("JSON","array_rows:"+array_rows);
                JSONObject object_rows=array_rows.getJSONObject(0);
                Log.d("JSON","object_rows:"+object_rows);
                JSONArray array_elements=object_rows.getJSONArray("elements");
                Log.d("JSON","array_elements:"+array_elements);
                JSONObject  object_elements=array_elements.getJSONObject(0);
                Log.d("JSON","object_elements:"+object_elements);
                JSONObject object_duration=object_elements.getJSONObject("duration");
                JSONObject object_distance=object_elements.getJSONObject("distance");

                Log.d("JSON","object_duration:"+object_duration);
                return object_duration.getString("value")+","+object_distance.getString("value");

            }
        } catch (MalformedURLException e) {
            Log.d("error", "error1");
        } catch (IOException e) {
            Log.d("error", "error2");
        } catch (JSONException e) {
            Log.d("error","error3");
        }


        return null;
    }
    interface Geo{
        public void setValues(String min);
    }

}


