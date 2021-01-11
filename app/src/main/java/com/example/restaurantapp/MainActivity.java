package com.example.restaurantapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // defining all the required Variable like ->  Button ,ListView , EditText and also define the OKHttpClient
    // giving the name similar to it's data type for simplicity
    private Button button;
    private ListView listView;
    private OkHttpClient client; // To use OkHttpClient Library, must include this library 👉 'com.squareup.okhttp3:okhttp:4.1.0' into Build.gradle file .
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=(Button) findViewById(R.id.button_id); // associate the button data variable with it's corresponding I'd defined In XML layout
        listView=(ListView) findViewById(R.id.list_id); // associate the listView data variable with it's corresponding I'd defined In XML layout
        editText=(EditText)findViewById(R.id.text_id); // associate the editText data variable with it's corresponding I'd defined In XML layout

        // defining the base URL because this below base URL will always be same in each Query only City I'd will be changed by User
        final String baseurl="https://developers.zomato.com/api/v2.1/cuisines?city_id=";


        // the following code will start executing when User Search Button is clicked
        // I have used setOnClickListener for Button response after getting clicked

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userData=editText.getText().toString(); // Convert data (provided by user in EditText as City ID ) into string

                if(userData.equals("")) // if User don't enter the data and click the search button then Pop this Toast message
                {
                    Toast.makeText(MainActivity.this,"Please enter city",Toast.LENGTH_SHORT).show();
                    return;
                }
                else // If User enter City Id into editText then following code will execute and show the result in LIstView
                {
                    String url=baseurl+userData;
                    url=url+"&lat=60&lon=56";
                    getWebresult(url,userData); // Calling the getWebresult() for getting data from Zomato-API , LOOK at method for more details
                }
            }
        });
        client=new OkHttpClient(); // initialize  OkHttpClient object

    }

    // This method will fetch data from Zomato-API in JSON format and I have parsed it show that USER can see the appropriate result
    private void getWebresult(String url, final String user_data)
    {
        //Creating request objects for make network calls
        // making request with API call and provide the Authenticated API key and it's value and URL
        Request request = new Request.Builder()
                .header("user-key", "4f13bf3bff0c6caea78e2b50c33b8637")
                .url(url)
                .build();

        // making the request call and Use the enqueue method which will run in background thread because network thread prefer to use background thread for better responsive
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)  // Handle the failure case like error during fetching the JSON data and show Toast message in case of failure
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"error, Check your Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException // after getting Json response successfully do the following
            {
                if(response.isSuccessful()){
                    final String myresponse=response.body().string();
                    System.out.println("Received data-> "+myresponse); // used for debugging purpose in case you get any error then see using Received data -> in LOGCAT
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // initialize the ArrayList which will store the list of cuisine
                            ArrayList<String> list_Of_cuisine = new ArrayList<String>();

                            // Parse the JSON response handle the exception with try and catch block
                            try {
                                JSONObject baseJson_Object=new JSONObject(myresponse); //initialize the JSON object of received response
                                JSONArray feature_Array=baseJson_Object.getJSONArray("cuisines"); // initialize the JsonArray object
                                // fetch the data of from Json Array and store into ArrayList which is of string data type
                                for(int i=0;i<feature_Array.length();i++)
                                {
                                    JSONObject different_cuisine=feature_Array.getJSONObject(i);
                                    JSONObject cuisine=different_cuisine.getJSONObject("cuisine");
                                    String cuisine_name=cuisine.getString("cuisine_name");
                                    list_Of_cuisine.add(cuisine_name);

                                }
                                if(feature_Array.length()<1) // If JSON response is successful but there is No data for the entered city id then display this Toast message
                                {
                                    Toast.makeText(MainActivity.this,"No data found for City id "+ user_data,Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else
                                {
                                    System.out.println("Added successfully"); // for debugging purpose keep this line if any error then in Logcat search Added Successfully
                                    ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,list_Of_cuisine);
                                    listView.setAdapter(arrayAdapter); // load all the data from arrayAdapter to listView that will be displayed to User
                                }
                            } catch (JSONException e) { // handle the case when there is any error during API-call and fetching data here in catch block.
                                System.out.println("Here is the ERROR ");
                                e.printStackTrace();
                            }

                        }
                    });
                }

            }
        });

    }




}