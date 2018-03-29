package com.inception.betappdistributor;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class Settings extends AppCompatActivity {


    private RequestQueue requestQueue;

    private TextView current_fee ;

    EditText change_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);

        SharedPreferences sp = getSharedPreferences("user_info" , MODE_PRIVATE);


        TextView name_txt = findViewById(R.id.name_txt);

        name_txt.setText(sp.getString("username" , ""));

        change_password = findViewById(R.id.chnge_pass);

        requestQueue = Volley.newRequestQueue(Settings.this);

        current_fee = findViewById(R.id.current_fee);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        get_distributor_details();


    }

    public void zero_fee(View view) {

        set_match_fee("0");

    }


    public void set_match_fee(final String fee)
    {

        SharedPreferences sp = getSharedPreferences("user_info" , MODE_PRIVATE);



        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("username" , sp.getString("username" , ""));
            jsonObject.put("match_fee" , fee);
            jsonObject.put("module" , "set_match_fee");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url.ip, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);


                try {
                    if(response.getString("result").equals("done"))
                    {
                        current_fee.setText(fee +" per event");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        requestQueue.add(jsonObjectRequest);


    }

    public void fifty_fee(View view) {

        set_match_fee("50");

    }


    public void get_distributor_details()
    {
        SharedPreferences sp = getSharedPreferences("user_info" , MODE_PRIVATE);



        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("username" , sp.getString("username" , ""));
            jsonObject.put("module" , "distributor_details");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url.ip, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);


                try {
                    if(response.getString("result").equals("done"))
                    {
                        current_fee.setText(response.getJSONObject("data").getString("match_fee") +" per event");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        requestQueue.add(jsonObjectRequest);


    }

    public void change_pass(View view) {

        final SharedPreferences sp = getSharedPreferences("user_info" , MODE_PRIVATE);

        final String pass = change_password.getText().toString();
        if (pass.equals(""))
        {
            Toast.makeText(Settings.this, "enter password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            DialogInterface.OnClickListener dialogClickListener9 = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            JSONObject jsonObject = new JSONObject();

                            try {
                                jsonObject.put("module", "change_distributor2_password");
                                jsonObject.put("username", sp.getString("username" , ""));
                                jsonObject.put("password", pass);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url.ip, jsonObject, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    System.out.println(response);

                                    try {

                                        if (response.getString("result").equals("done")) {


                                            Toast.makeText(Settings.this, "password changed successfully", Toast.LENGTH_SHORT).show();


                                        } else {

                                            Toast.makeText(Settings.this, "error try again", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    System.out.println(error);


                                }
                            });

                            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 2, 2));

                            requestQueue.add(jsonObjectRequest);

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }

                }
            };
            AlertDialog.Builder ab9 = new AlertDialog.Builder(this);
            ab9.setMessage("Do you want to change password?").setPositiveButton("Yes", dialogClickListener9)
                    .setNegativeButton("No", dialogClickListener9).show();
        }
    }

    public void three_hundered_fee(View view) {

        set_match_fee("300");
    }

    public void two_fifty_fee(View view) {

        set_match_fee("250");
    }

    public void two_hundered_fee(View view) {

        set_match_fee("200");
    }

    public void one_fifty_fee(View view) {

        set_match_fee("150");
    }

    public void hundered_fee(View view) {

        set_match_fee("100");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return true;
    }
}
