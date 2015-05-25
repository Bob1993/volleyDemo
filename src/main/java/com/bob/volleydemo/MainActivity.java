package com.bob.volleydemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText editText;

    private RequestQueue mQueue;//创建一个请求队列
    private StringRequest stringRequest;
    private JsonObjectRequest jsonObjectRequest;
    private ImageRequest imageRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView= (ImageView) findViewById(R.id.img);
        editText= (EditText) findViewById(R.id.et_input);

        mQueue= Volley.newRequestQueue(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageReques(editText.getText().toString());
                mQueue.add(imageRequest);
            }
        });

        stringReques();
        jsonObjectReques();


        mQueue.add(stringRequest);
        mQueue.add(jsonObjectRequest);

    }

    public void stringReques(){
        stringRequest= new StringRequest("http://www.baidu.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.i("Tag",s);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Tag", volleyError.getMessage(), volleyError);
            }
        });//默认的是Get
    }

    public void jsonObjectReques(){
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,"http://m.weather.com.cn/data/101010100.html", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
    }

    public void imageReques(String url){
        imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                imageView.setImageResource(R.mipmap.default_image);
            }
        });
    }
}
