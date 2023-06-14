package com.ttrommlitz.family_map_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

public class SettingsActivity extends AppCompatActivity {
    private DataCache dataCache = DataCache.getInstance();
    private SwitchCompat storylinesSwitch;
    private SwitchCompat familyTreeLinesSwitch;
    private SwitchCompat spouseLinesSwitch;
    private SwitchCompat fatherFilterSwitch;
    private SwitchCompat motherFilterSwitch;
    private SwitchCompat maleFilterSwitch;
    private SwitchCompat femaleFilterSwitch;
    private RelativeLayout logoutClick;
    private Filter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storylinesSwitch = findViewById(R.id.story_lines_switch);
        familyTreeLinesSwitch = findViewById(R.id.family_tree_lines_switch);
        spouseLinesSwitch = findViewById(R.id.spouse_lines_switch);
        fatherFilterSwitch = findViewById(R.id.father_filter_switch);
        motherFilterSwitch = findViewById(R.id.mother_filter_switch);
        maleFilterSwitch = findViewById(R.id.male_filter_switch);
        femaleFilterSwitch = findViewById(R.id.female_filter_switch);
        logoutClick = findViewById(R.id.logout_click);
        filter = new Filter();

        setInitialValues();
        setListeners();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private void setInitialValues() {
        storylinesSwitch.setChecked(dataCache.showLifeStoryLines);
        familyTreeLinesSwitch.setChecked(dataCache.showFamilyTreeLines);
        spouseLinesSwitch.setChecked(dataCache.showSpouseLines);
        fatherFilterSwitch.setChecked(dataCache.showFatherSide);
        motherFilterSwitch.setChecked(dataCache.showMotherSide);
        maleFilterSwitch.setChecked(dataCache.showMaleEvents);
        femaleFilterSwitch.setChecked(dataCache.showFemaleEvents);
    }

    private void setListeners() {
        storylinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                dataCache.showLifeStoryLines = checked;
            }
        });

        familyTreeLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                dataCache.showFamilyTreeLines = checked;
            }
        });

        spouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                dataCache.showSpouseLines = checked;
            }
        });

        fatherFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                filter.filterFatherSide(checked);
            }
        });

        motherFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                filter.filterMotherSide(checked);
            }
        });

        maleFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                filter.filterMale(checked);
            }
        });

        femaleFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                filter.filterFemale(checked);
            }
        });

        logoutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}