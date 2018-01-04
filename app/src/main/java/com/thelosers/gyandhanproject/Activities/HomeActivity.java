package com.thelosers.gyandhanproject.Activities;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.thelosers.gyandhanproject.Fragments.CategoriesFragment;
import com.thelosers.gyandhanproject.R;
import com.thelosers.gyandhanproject.Utils.CustomPicasso;
import com.thelosers.gyandhanproject.Utils.Urls;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    private ImageView mQuestion1Imageview;
    private static final int MAX_WIDTH = 1300;
    private static final int MAX_HEIGHT = 1065;

    int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mQuestion1Imageview = (ImageView) findViewById(R.id.home_imageview);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.home_fragment);


        /*
        Methods to download image from server
         */
        downloadImageFromServer();
        downloadImageFromServerUsingPicasso();
        downloadImageFromServerUsingCustomPicasso();
        new DownloadImageViaDefaultHttpClientTask().execute(Urls.imageUrl, mQuestion1Imageview);
        new DownloadImageViaHttpURLConnectionTask().execute(Urls.imageUrl, mQuestion1Imageview);

        /*
         End
         */


        if (fragment == null) {
            fragment = new CategoriesFragment();
            manager.beginTransaction()
                    .add(R.id.home_fragment, fragment)
                    .commit();
        }
    }

    private void downloadImageFromServerUsingPicasso() {
        Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
                Log.i("ERROR", exception.getLocalizedMessage() + "");
            }
        });
        builder.downloader(new OkHttpDownloader(getApplicationContext()));
        builder.build().load(Urls.imageUrl).resize(200, 200)
                .skipMemoryCache().into(mQuestion1Imageview);

    }

    private void downloadImageFromServerUsingCustomPicasso() {
        CustomPicasso.with(getApplicationContext())
                .load(Urls.imageUrl)
                .into(mQuestion1Imageview);
    }

    private void downloadImageFromServer() {
        Glide.with(this).load(Urls.imageUrl).into(mQuestion1Imageview);
    }


    class DownloadImageViaHttpURLConnectionTask extends AsyncTask<Object, Void, Drawable> {
        private ImageView imgView;
        private ProgressDialog dialog;
        private HttpURLConnection con = null;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(HomeActivity.this);
            dialog.setMessage("Please wait...");
            dialog.show();
        }


        @Override
        protected Drawable doInBackground(Object... params) {

            imgView = (ImageView) params[1];

            try {
                con = (HttpURLConnection) (new URL((String) params[0]).openConnection());
                con.disconnect();
                con.setInstanceFollowRedirects(false);
                con.connect();
                final int responseCode = con.getResponseCode();

                if (responseCode != HttpStatus.SC_OK) {
                    String newUrl = con.getHeaderField("Location");
                    Log.i("TAG", "newUrl=>" + newUrl);
                    return downloadImage(newUrl);
                }
            } catch (Exception ex) {
                Log.e("TAG", "DownloadImageTask doInBackground() Exception=>" + ex);
            } finally {
                if (con != null) {
                    con.disconnect();
                    con = null;
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(Drawable drawable) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            mQuestion1Imageview.setImageDrawable(drawable);
            if (drawable != null) {

            }
        }
    }

    class DownloadImageViaDefaultHttpClientTask extends AsyncTask<Object, Void, Drawable> {
        private ImageView imgView;
        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(HomeActivity.this);
            dialog.setMessage("Please wait...");
            dialog.show();
            Log.e("TAG", "DownloadImageTask doInBackground() Exception=>");
        }


        @Override
        protected Drawable doInBackground(Object... params) {

            imgView = (ImageView) params[1];

            try {

                if (client == null) {
                    createClient();
                }

                HttpParams httpParams = client.getParams();
                httpParams.setIntParameter("http.connection.timeout", 30000); // 30 SECONDS
                httpParams.setIntParameter("http.socket.timeout", 30000); // 30 SECONDS

                HttpGet httpGet = new HttpGet((String) params[0]);
                HttpResponse httpResponse = client.execute(httpGet);

                InputStream is = httpResponse.getEntity().getContent();
                Drawable d = Drawable.createFromStream(is, "src name");
                is.close();

                return d;
            } catch (Exception ex) {
                Log.e("TAG", "DownloadImageTask doInBackground() Exception=>" + ex);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Drawable drawable) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (drawable != null) {
                imgView.setImageDrawable(drawable);
            }
        }
    }

    private static Drawable downloadImage(String stringUrl) {
        URL url = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            url = new URL(stringUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(true);
            inputStream = connection.getInputStream();


            Drawable d = Drawable.createFromStream(inputStream, "src name");
            inputStream.close();

            return d;
        } catch (Exception e) {

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    private DefaultHttpClient client = null;

    public void createClient() {
        BasicHttpParams params = new BasicHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, schemeRegistry);
        client = new DefaultHttpClient(ccm, params);
    }
}
