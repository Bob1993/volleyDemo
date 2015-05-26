package com.bob.volleydemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.*;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText editText;

    private RequestQueue mQueue;//创建一个请求队列
    private StringRequest stringRequest;
    private JsonObjectRequest jsonObjectRequest;
    private ImageRequest imageRequest;
    private ImageLoader imageLoader;
    private NetworkImageView networkImageView;


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
              //  imageReques(editText.getText().toString());
                imageLoade(editText.getText().toString());//内部也是ImageRequest实现方式
                //mQueue.add(imageRequest);
            }
        });

        stringReques();
        jsonObjectReques();
        networkImage();

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
                imageView.setImageResource(R.mipmap.failed_image);
            }//两个0参数表示允许图片的最大宽高，0表示不压缩
        });
    }

    public void imageLoade(String url){//缓存图片，过滤掉重复url链接
        imageLoader= new ImageLoader(mQueue, new ImageLoader.ImageCache() {//匿名内部类实现ImageCache接口
            int maxSize = 10 * 1024 * 1024;//10mb
            LruCache<String, Bitmap> mCache= new LruCache<String,Bitmap>(maxSize){//缓存总大小
                @Override//匿名内部类继承LruCache类，即缓存池
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes()*value.getHeight();//返回图片大小
                }
            };

            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);//将图片存入缓存
            }
        });

        ImageLoader.ImageListener listener= imageLoader.getImageListener(imageView,R.mipmap.default_imaging,R.mipmap.failed_image);
        imageLoader.get(url,listener);
    }

    public void networkImage(){
        imageLoader= new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String s) {
                return null;
            }

            @Override
            public void putBitmap(String s, Bitmap bitmap) {

            }
        });
        networkImageView= (NetworkImageView) findViewById(R.id.img_network);
        networkImageView.setDefaultImageResId(R.mipmap.default_imaging);
        networkImageView.setErrorImageResId(R.mipmap.failed_image);
        networkImageView.setImageUrl("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",
                imageLoader);
    }


}
