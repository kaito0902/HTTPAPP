package com.sghh.httpapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText etitText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etitText = findViewById(R.id.uri);
        imageView = findViewById(R.id.img);
        Button downloadButton = findViewById(R.id.download);
        // downloadButtonを押下時にeditTextにあるリンクの画像を引数にdownloadImageを呼び出す。
        downloadButton.setOnClickListener(v -> {
            String stringUrl = etitText.getText().toString();
            downloadImage(stringUrl);
        });
    }

    private void downloadImage(String urlSt){
        // Singleの別スレッドを立ち上げる
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(urlSt);
                HttpURLConnection urlCon =  (HttpURLConnection) url.openConnection();

                // タイムアウト設定
                urlCon.setReadTimeout(10000);
                urlCon.setConnectTimeout(20000);

                // リクエストメソッド
                urlCon.setRequestMethod("GET");

                // リダイレクトを自動で許可しない設定
                urlCon.setInstanceFollowRedirects(false);

                InputStream is = urlCon.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(is);

                // 別スレッド内での処理を管理し実行する
                HandlerCompat.createAsync(getMainLooper()).post(() ->
                        // Mainスレッドに渡す
                        imageView.setImageBitmap(bmp)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}