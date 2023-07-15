package com.example.javaappassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient client = new OkHttpClient();
    public String image_url1 = "";
    public String image_author1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exec("https://picsum.photos/v2/list?limit=10");
            }
        });
    }

    private void exec(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.display);
                        textView.setText("Failed to retrieve data");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String strResponse = response.body().string();
                if (strResponse != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(strResponse);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                image_author1 = jsonObject.getString("author");
                                image_url1 = jsonObject.getString("download_url");

                                TextView textView = findViewById(R.id.display);
                                textView.setText("Author Name: \n" + image_author1);

                                Glide.with(MainActivity.this)
                                        .load(image_url1)
                                        .into((ImageView) findViewById(R.id.author_image));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    onFailure(call, new IOException("Empty response body"));
                }
            }
        });
        client.dispatcher().executorService().shutdown();
    }
}
