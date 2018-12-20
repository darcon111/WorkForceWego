package com.workforce.app.ec.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.workforce.app.ec.R;
import com.workforce.app.ec.adapter.TourFragmentPagerAdapter;
import com.workforce.app.ec.config.AppPreferences;



public class TourActivity extends FragmentActivity {
    private AppPreferences app;

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next pages.
     */
    private ViewPager pager = null;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    TourFragmentPagerAdapter pagerAdapter;

    Button btningresar;



    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        this.setContentView(R.layout.activity_tour);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Instantiate a ViewPager
        this.pager = (ViewPager) this.findViewById(R.id.pager);

        app = new AppPreferences(getApplicationContext());

        // Create an adapter with the fragments we show on the ViewPager
        TourFragmentPagerAdapter adapter = new TourFragmentPagerAdapter(
                getSupportFragmentManager());

        adapter.addFragment(ScreenSlidePageFragment.newInstance(R.drawable.ic_logo_wego,getString(R.string.tour1),1));
        adapter.addFragment(ScreenSlidePageFragment.newInstance(R.drawable.ic_logo_wego,getString(R.string.tour2),2));
        adapter.addFragment(ScreenSlidePageFragment.newInstance(R.drawable.ic_logo_wego,getString(R.string.tour3),3));


        pager.setAdapter(adapter);

        PageIndicatorView pageIndicatorView = (PageIndicatorView) this.findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(pager);

        pageIndicatorView.setInteractiveAnimation(true);
        pageIndicatorView.setAnimationType(AnimationType.THIN_WORM);

        this.btningresar = (Button) this.findViewById(R.id.btningresar);

        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                app = new AppPreferences(getApplicationContext());

                app.setTour("1");
                Intent intent = new Intent(TourActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }

    @Override
    public void onBackPressed() {

        // Return to previous page when we press back button
        if (this.pager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            this.pager.setCurrentItem(this.pager.getCurrentItem() - 1);

    }

    public void close(View v)
    {

        app.setTour("1");
        Intent mainIntent = new Intent().setClass(
                TourActivity.this, LoginActivity.class);
        startActivity(mainIntent);

        // Close the activity so the user won't able to go back this
        // activity pressing Back button
        finish();
    }



}
