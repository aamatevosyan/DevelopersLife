package edu.fintech.developerslife;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;

import edu.fintech.developerslife.core.MemeBase;

public class MainActivity extends AppCompatActivity {
    private final HashMap<String, MemeBase> memeBaseHashMap = new HashMap<>();
    private MemeBase currentMemeBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memeBaseHashMap.put(getString(R.string.tab_1_text), new MemeBase("latest"));
        memeBaseHashMap.put(getString(R.string.tab_2_text), new MemeBase("top"));
        memeBaseHashMap.put(getString(R.string.tab_3_text), new MemeBase("hot"));

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentMemeBase = memeBaseHashMap.get(tab.getText().toString());

                if (currentMemeBase.isPreviousAvailable())
                    currentMemeBase.currentIndex--;
                currentMemeBase.loadNextMeme(MainActivity.this);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        currentMemeBase = memeBaseHashMap.get(getString(R.string.tab_1_text));
        currentMemeBase.loadNextMeme(this);

        final Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMemeBase.loadPreviousMeme(MainActivity.this);
                if (!currentMemeBase.isPreviousAvailable())
                    backButton.setEnabled(false);
            }
        });
        backButton.setEnabled(false);

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMemeBase.loadNextMeme(MainActivity.this);
                backButton.setEnabled(currentMemeBase.isPreviousAvailable());
            }
        });

        Button retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMemeBase.loadNextMeme(MainActivity.this);
                backButton.setEnabled(currentMemeBase.isPreviousAvailable());
            }
        });
    }
}