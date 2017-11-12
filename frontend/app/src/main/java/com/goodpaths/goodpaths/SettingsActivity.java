package com.goodpaths.goodpaths;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import static com.goodpaths.goodpaths.DangerTypeHelper.ACCESSIBILITY;
import static com.goodpaths.goodpaths.DangerTypeHelper.HARASSMENT;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton backButton;

    private RadioGroup radioGroupDangerType;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.settingsToolbar));
        findViews();
        setViews();
    }

    private void findViews(){
        backButton = findViewById(R.id.back);
        radioGroupDangerType = findViewById(R.id.typeGroup);
    }

    private void setViews(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        int currentDangerInt = DangerTypeHelper.getInstance().getDangerType(this);
        radioGroupDangerType.check(currentDangerInt == ACCESSIBILITY? R.id.radioAccessibility : R.id.radioHarassment);
        radioGroupDangerType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                int newDangerInt = id == R.id.radioAccessibility ? ACCESSIBILITY : HARASSMENT;
                DangerTypeHelper.getInstance().setDangerType(SettingsActivity.this, newDangerInt);
            }
        });
    }
}
