package edu.fintech.developerslife.core;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import edu.fintech.developerslife.R;

public class MemeBase {
    public final String category;
    public final ArrayList<Meme> memes;
    public int currentIndex = -1;
    public int currentPage = -1;

    public MemeBase(String category) {
        this.category = category;
        memes = new ArrayList<>();
    }

    public void loadNextMeme(final Activity activity) {
        if (currentIndex == -1 || currentIndex == memes.size() - 1) {
            (new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    String result = null;
                    String urlPath = params[0];
                    try {
                        URL url = new URL(urlPath);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.connect();

                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                            BufferedReader reader = new BufferedReader(inputStreamReader);
                            StringBuilder stringBuilder = new StringBuilder();
                            String temp;

                            while ((temp = reader.readLine()) != null) {
                                stringBuilder.append(temp);
                            }
                            result = stringBuilder.toString();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(String s) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(s);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject el = jsonArray.getJSONObject(i);
                            Meme meme = new Meme(el.getString("id"), el.getString("description"), el.getString("gifURL"));
                            memes.add(meme);
                        }
                        loadMeme(activity, memes.get(++currentIndex));
                    } catch (Exception e) {
                        showError(activity);
                        e.printStackTrace();
                    }
                }
            }).execute(String.format("https://developerslife.ru/%s/%d?json=true", category, ++currentPage));
        } else {
            try {
                loadMeme(activity, memes.get(++currentIndex));
            } catch (Exception e) {
                showError(activity);
                e.printStackTrace();
            }
        }
    }

    public void loadMeme(final Activity activity, Meme meme) {
        hideError(activity);
        ImageView imageView = activity.findViewById(R.id.memeImageView);

        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(activity);
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(30);
        circularProgressDrawable.start();

        try {
            Glide.with(activity).load(new CustomGlideUrl(new URL(meme.gifURL), meme.id)).
                    diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(circularProgressDrawable).
                    apply(RequestOptions.bitmapTransform(new RoundedCorners(16))).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    circularProgressDrawable.stop();
                    showError(activity);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(imageView);
            TextView textView = activity.findViewById(R.id.descriptionView);
            textView.setText(meme.description);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPreviousAvailable() {
        return currentIndex >= 1;
    }

    public void loadPreviousMeme(Activity activity) {
        if (isPreviousAvailable())
            loadMeme(activity, memes.get(--currentIndex));
    }

    public void showError(Activity activity) {
        ImageView imageView = activity.findViewById(R.id.memeImageView);
        imageView.setImageDrawable(null);

        TextView textView = activity.findViewById(R.id.descriptionView);
        textView.setText("");

        textView = activity.findViewById(R.id.errorTextView);
        textView.setVisibility(View.VISIBLE);

        Button button = activity.findViewById(R.id.retryButton);
        button.setVisibility(View.VISIBLE);
    }

    public void hideError(Activity activity) {
        ImageView imageView = activity.findViewById(R.id.memeImageView);
        imageView.setImageDrawable(null);

        TextView textView = activity.findViewById(R.id.descriptionView);
        textView.setText("");

        textView = activity.findViewById(R.id.errorTextView);
        textView.setVisibility(View.INVISIBLE);

        Button button = activity.findViewById(R.id.retryButton);
        button.setVisibility(View.INVISIBLE);
    }
}
