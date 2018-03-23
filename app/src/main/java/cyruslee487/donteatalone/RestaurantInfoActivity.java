package cyruslee487.donteatalone;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by cyrus on 2018-03-22.
 */

public class RestaurantInfoActivity extends AppCompatActivity {

    //Constants
    private String TAG = "DB";
    private String USER_NAME = "username";
    private String IMAGE_URL = "image_url";
    private String IMAGE_NAME = "image_name";

    //vars
    private String mImageUrl;
    private String mImageName;
    private ImageView imageView;
    private TextView textView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_info_activity);

        Intent intent = getIntent();
        if(intent.hasExtra(IMAGE_URL) && intent.hasExtra(IMAGE_NAME)) {
            mImageUrl = intent.getStringExtra(IMAGE_URL);
            mImageName = intent.getStringExtra(IMAGE_NAME);
        }

        setImage(mImageUrl, mImageName);
    }

    private void setImage(String imageUrl, String imageName){
        imageView = findViewById(R.id.image_info_activity);
        textView = findViewById(R.id.textview_info_activity);

        textView.setText(imageName);

        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .into(imageView);
    }
}
