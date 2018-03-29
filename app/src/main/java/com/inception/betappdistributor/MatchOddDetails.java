package com.inception.betappdistributor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchOddDetails extends AppCompatActivity {

    private TextView team_1 , back_1 , lay_1 , team_2 , back_2 , lay_2 , team_1_m , team_2_m , all_total_team_1 , all_total_team_2;

    private RequestQueue requestQueue ;

    private Boolean FETCH_MARKET_STATUS = true;

    private JSONArray market_status_array , bets_data_array ;

    private RecyclerView market_status_recycler , bets_recycler ;

    private int total_team_1 , total_team_2;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_odd_details);

        requestQueue = Volley.newRequestQueue(MatchOddDetails.this);

        getSupportActionBar().setTitle("Match Odds");

        market_status_recycler = findViewById(R.id.match_market_recycler);
        market_status_recycler.setLayoutManager(new LinearLayoutManager(MatchOddDetails.this , LinearLayoutManager.VERTICAL , false));

        market_status_recycler.setNestedScrollingEnabled(false);


        bets_recycler = findViewById(R.id.bets_recycler);

        bets_recycler.setLayoutManager(new LinearLayoutManager(MatchOddDetails.this , LinearLayoutManager.VERTICAL , false));

        bets_recycler.setNestedScrollingEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getSupportActionBar().setHomeButtonEnabled(true);

        all_total_team_1 = findViewById(R.id.all_total_team_1);

        all_total_team_2 = findViewById(R.id.all_total_team_2);


        team_1 = findViewById(R.id.team_1);
        team_2 = findViewById(R.id.team_2);

        team_1_m = findViewById(R.id.team_1_m);
        team_2_m = findViewById(R.id.team_2_m);

        back_1 = findViewById(R.id.back_1);
        back_2 = findViewById(R.id.back_2);

        lay_1 = findViewById(R.id.lay_1);
        lay_2 = findViewById(R.id.lay_2);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                getdata();
                handler.postDelayed(this, 5000);
            }
        }, 0);



        get_bet_data();

    }

    public void getdata()
    {
        String event_id = getIntent().getStringExtra("event_id");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://www.lotusbook.com/api/exchange/eventType/"+event_id, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);

                try {
                    JSONArray jsonArray = response.getJSONArray("result");

                    JSONArray jsonArray1 =   jsonArray.getJSONObject(0).getJSONArray("runners");

                    JSONObject jsonObject = jsonArray1.getJSONObject(0);

                    String team1 = jsonObject.getString("name");

                    String back1 = jsonObject.getJSONArray("back").getJSONObject(0).getString("price");

                    String lay1 = jsonObject.getJSONArray("lay").getJSONObject(0).getString("price");


                    JSONObject jsonObject2 = jsonArray1.getJSONObject(1);

                    String team2 = jsonObject2.getString("name");

                    String back2 = jsonObject2.getJSONArray("back").getJSONObject(0).getString("price");

                    String lay2 =  jsonObject2.getJSONArray("lay").getJSONObject(0).getString("price");

                    team_1.setText(team1);

                    team_2.setText(team2);

                    team_1_m.setText(team1);

                    team_2_m.setText(team2);

                    back_1.setText(back1);

                    lay_1.setText(lay1);

                    back_2.setText(back2);

                    lay_2.setText(lay2);

                    if(FETCH_MARKET_STATUS)
                    {
                        get_market_status();
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20000 ,2 ,2 ));

        requestQueue.add(jsonObjectRequest);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == android.R.id.home)
        {
           finish();
        }

        return true;
    }

    private void get_market_status()
    {
        SharedPreferences sp = getSharedPreferences("user_info" , MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();

        String event_id = getIntent().getStringExtra("event_id")+getIntent().getStringExtra("open_date").toLowerCase().replace(" " , "");

        try {
            jsonObject.put("event_id" , event_id);
            jsonObject.put("dis_id" , sp.getString("id" , ""));
            jsonObject.put("module" , "market status");
            jsonObject.put("team1" , team_1.getText().toString());
            jsonObject.put("team2" , team_2.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url.ip, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                FETCH_MARKET_STATUS = false;

                System.out.println(response);

                try {
                    market_status_array = response.getJSONArray("result");

                    total_team_1 = response.getInt("total_team_1");

                    total_team_2 = response.getInt("total_team_2");

                    Adapter adapter = new Adapter();




                    market_status_recycler.setAdapter(adapter);

                    all_total_team_1.setText(String.valueOf(total_team_1));
                    all_total_team_2.setText(String.valueOf(total_team_2));

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

    private class view_holder extends RecyclerView.ViewHolder
    {

        private TextView username , team_1_total , team_2_total;

        public view_holder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);

            team_1_total = itemView.findViewById(R.id.team_1_total);

            team_2_total = itemView.findViewById(R.id.team_2_total);

        }
    }


    private  class Adapter extends RecyclerView.Adapter<view_holder>
    {

        @Override
        public view_holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new view_holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.market_status_cell , parent , false));
        }

        @Override
        public void onBindViewHolder(view_holder holder, int position) {

            try {
                JSONObject jsonObject = market_status_array.getJSONObject(position);

                holder.username.setText(jsonObject.getString("username"));
                holder.team_1_total.setText(jsonObject.getString(team_1.getText().toString()));
                holder.team_2_total.setText(jsonObject.getString(team_2.getText().toString()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return market_status_array.length();
        }
    }



    public void get_bet_data() {

        SharedPreferences sp = getSharedPreferences("user_info" , MODE_PRIVATE);

        final JSONObject jsonObject = new JSONObject();

        String event_id = getIntent().getStringExtra("event_id")+getIntent().getStringExtra("open_date").toLowerCase().replace(" " , "");


        try {
            jsonObject.put("module", "show_bet");
            jsonObject.put("dis_id", sp.getString("id" , ""));
            jsonObject.put("event_id", event_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url.ip, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);


                try {

                    bets_data_array = response.getJSONArray("result");
                    Log.i("adapter to recycler --", "");
                    Adapter_bets adapter = new Adapter_bets();
                    bets_recycler.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.print(error);

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 2, 2));


        requestQueue.add(jsonObjectRequest);


    }

    private class Adapter_bets extends RecyclerView.Adapter<view_holder_bets> {
        Context context;



        @Override
        public view_holder_bets onCreateViewHolder(ViewGroup parent, int viewType) {
            return new view_holder_bets(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_bets_cell, parent, false));
        }

        @Override
        public void onBindViewHolder(final view_holder_bets holder, int position) {

            try {
                JSONObject data = bets_data_array.getJSONObject(position);
                holder.team_name.setText(data.getString("team"));
                holder.rate.setText(data.getString("rate"));

                holder.username.setText(data.getString("username"));
                holder.bet_value.setText(data.getString("stake"));
                holder.bet.setText(data.getString("bet"));
                if (data.getString("bet").toLowerCase().equals("lay")) {
                    holder.bet_layout.setBackgroundColor(getResources().getColor( R.color.Lay));
                } else {
                    holder.bet_layout.setBackgroundColor(getResources().getColor(R.color.Back));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return bets_data_array.length();
        }
    }

    private  class view_holder_bets extends RecyclerView.ViewHolder {
        TextView team_name,  rate, bet, bet_value , username;
        LinearLayout bet_layout;

        public view_holder_bets(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            team_name = itemView.findViewById(R.id.team_name);
            rate = itemView.findViewById(R.id.rate);

            bet_value = itemView.findViewById(R.id.stake);
            bet = itemView.findViewById(R.id.bet);
            bet_layout = itemView.findViewById(R.id.bet_layout);
        }
    }

}
