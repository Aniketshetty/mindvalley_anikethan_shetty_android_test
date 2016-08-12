package com.example.anikethan.mindvalley_anikethan_android_test;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.anikethan.mindvalley_anikethan_android_test.Custom.SpacesItemDecoration;
import com.example.anikethan.mindvalley_anikethan_android_test.Model.Items;
import com.example.anikethan.mindvalley_anikethan_android_test.Utils.NetworkChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private boolean isInternetPresent = false;
    CoordinatorLayout coordinatorLayoutNewActivity;


    // Connection detector class
    NetworkChecker networkChecker = new NetworkChecker(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Make call to AsyncTask


        coordinatorLayoutNewActivity = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);


        isInternetPresent = networkChecker.isConnectingToInternet(this);
        Log.d("APP", "onCreate: " + isInternetPresent);

        if (isInternetPresent) {

//            Snackbar.make(coordinatorLayoutNewActivity, "Hi ALL", Snackbar.LENGTH_LONG)
//                    .show();
            new AsyncLogin().execute();

        } else {

            Snackbar.make(coordinatorLayoutNewActivity, "Please Check Network Connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRestart();
                        }
                    })
                    .show();
        }


    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading Please Wait...........");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {


            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder builder = null;
            try {
                url = new URL("http://pastebin.com/raw/wgkJgazE");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                builder = new StringBuilder();
                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();

            return (builder.toString());

        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            pdLoading.dismiss();
            List<Items> data = new ArrayList<>();
            pdLoading.dismiss();
            try {
                JSONArray jArray = new JSONArray(result);
                // Extract data from json and store into ArrayList as class objects
                for (int i = 0; i < jArray.length(); i++) {
                    Items items = new Items();
                    JSONObject json_data = jArray.getJSONObject(i);
                    JSONObject user = json_data.getJSONObject("user");
                    JSONObject profileImage = user.getJSONObject("profile_image");
                    JSONObject urls = json_data.getJSONObject("urls");
                    items.name = user.getString("name");
                    items.profile_image = profileImage.getString("large");
                    items.urls = urls.getString("raw");
                    data.add(items);
                }

                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                mAdapter = new MyAdapter(data);
                recyclerView.setAdapter(mAdapter);
                SpacesItemDecoration decoration = new SpacesItemDecoration(10);
                recyclerView.addItemDecoration(decoration);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyView> {
        List<Items> data;

        public MyAdapter(List<Items> data) {
            this.data = data;
        }

        @Override
        public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main, parent, false);
            return new MyView(view);
        }

        public void onBindViewHolder(final MyView myView, int position) {
            Items currentData = data.get(position);
            myView.textView.setText(currentData.name);
            Glide.with(MainActivity.this).load(currentData.urls)
                    .placeholder(R.drawable.five)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(myView.imageView);
            Glide.with(MainActivity.this).load(currentData.profile_image).asBitmap().centerCrop()
                    .into(new BitmapImageViewTarget(myView.imgAuthorPic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(MainActivity.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            myView.imgAuthorPic.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyView extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private ImageView imgAuthorPic;
            private TextView textView;

            public MyView(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                imgAuthorPic = (ImageView) itemView.findViewById(R.id.imgAuthorPic);
                textView = (TextView) itemView.findViewById(R.id.userName);
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        isInternetPresent = networkChecker.isConnectingToInternet(this);
        if (isInternetPresent) {
            new AsyncLogin().execute();
        } else {
            Snackbar.make(coordinatorLayoutNewActivity, "Please Check Network Connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRestart();
                        }
                    })
                    .show();
        }
    }
}
