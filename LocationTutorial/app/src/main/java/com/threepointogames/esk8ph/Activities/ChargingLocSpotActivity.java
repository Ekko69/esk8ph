package com.threepointogames.esk8ph.Activities;

import android.graphics.Color;
import android.os.Bundle;

import com.example.locationtutorial.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.threepointogames.esk8ph.Models.SliderItem;
import com.threepointogames.esk8ph.SliderAdapter;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.smarteist.autoimageslider.IndicatorAnimations.WORM;


public class ChargingLocSpotActivity extends AppCompatActivity  implements RatingDialogListener {

    SliderView sliderView;
    private SliderAdapter adapter;
    private  ScaleRatingBar scaleRatingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_loc_spot);
        Toolbar toolbar = findViewById(R.id.toolbar);
        scaleRatingBar = findViewById(R.id.mainRatingBar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rate();
            }
        });



        sliderView = findViewById(R.id.imageSlider);


        adapter = new SliderAdapter(this);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

         SetAverageStar(4); // Set star
         renewItems();


        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                Log.i("GGG", "onIndicatorClicked: " + sliderView.getCurrentPagePosition());
            }
        });





          






  












    }
    public void SetAverageStar(int starRating){
            // TODO: Calculate Average rating
           scaleRatingBar.setNumStars(5);
           scaleRatingBar.setMinimumStars(1);
           scaleRatingBar.setRating(starRating); // Set Star Here
    }
    public void renewItems() {
        List<SliderItem> sliderItemList = new ArrayList<>();
        //dummy data
        for (int i = 0; i < 5; i++) {
            SliderItem sliderItem = new SliderItem();
            //sliderItem.setDescription("Slider Item " + i);
            if (i % 2 == 0) {
                sliderItem.setImageUrl("https://images.squarespace-cdn.com/content/v1/55ca787ae4b07d9498906d9e/1551490529737-C7C2LZQ8MSFLT8L632H4/ke17ZwdGBToddI8pDm48kPHgPSpJs3pqpkUZU93_mvpZw-zPPgdn4jUwVcJE1ZvWQUxwkmyExglNqGp0IvTJZamWLI2zvYWH8K3-s_4yszcp2ryTI0HqTOaaUohrI8PInndiFr6ALsCf3uCDBRk4eJzsF3rQ925VZ6DZqxvBZkQKMshLAGzx4R3EDFOm1kBS/coming-soon-neon-sign-coming-soon-badge-in-vector-21133321.jpg");
            } else {
                sliderItem.setImageUrl("https://images.squarespace-cdn.com/content/v1/55ca787ae4b07d9498906d9e/1551490529737-C7C2LZQ8MSFLT8L632H4/ke17ZwdGBToddI8pDm48kPHgPSpJs3pqpkUZU93_mvpZw-zPPgdn4jUwVcJE1ZvWQUxwkmyExglNqGp0IvTJZamWLI2zvYWH8K3-s_4yszcp2ryTI0HqTOaaUohrI8PInndiFr6ALsCf3uCDBRk4eJzsF3rQ925VZ6DZqxvBZkQKMshLAGzx4R3EDFOm1kBS/coming-soon-neon-sign-coming-soon-badge-in-vector-21133321.jpg");
            }
            sliderItemList.add(sliderItem);
        }
        adapter.renewItems(sliderItemList);

    }

    public void removeLastItem(View view) {
        if (adapter.getCount() - 1 >= 0)
            adapter.deleteItem(adapter.getCount() - 1);
    }

    public void addNewItem(View view) {
        SliderItem sliderItem = new SliderItem();
        sliderItem.setDescription("Slider Item Added Manually");
        sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels--929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
        adapter.addItem(sliderItem);
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