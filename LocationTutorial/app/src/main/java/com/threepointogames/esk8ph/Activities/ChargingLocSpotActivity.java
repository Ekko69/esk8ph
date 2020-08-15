package com.threepointogames.esk8ph.Activities;

import android.os.Bundle;

import com.example.locationtutorial.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import java.util.Arrays;


public class ChargingLocSpotActivity extends AppCompatActivity  implements RatingDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_loc_spot);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rate();
            }
        });
    }

    private void Rate(){
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(2)
                .setTitle("Rate this Charging Spot")
                .setDescription("Please select some stars and give your feedback")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.ratingStar)
                .setNoteDescriptionTextColor(R.color.ratingStar)
                .setTitleTextColor(R.color.ratingTitle)
                .setDescriptionTextColor(R.color.ratingHint)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.ratingHint)
                .setCommentTextColor(R.color.ratingTextDescription)
                .setCommentBackgroundColor(R.color.ratingTextBoxDescription)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(ChargingLocSpotActivity.this)
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int rate, String comment) {
        Log.d("Ekko","Rate: "+ rate+ " Comment: "+ comment);
    }
}