package com.watertracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText dailyGoalEditText;
    private EditText cupCapacityEditText;
    private Button saveButton;

    private int dailyGoal;
    private int cupCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        loadCurrentValues();
        setupListeners();
    }

    private void initViews() {
        dailyGoalEditText = findViewById(R.id.dailyGoalEditText);
        cupCapacityEditText = findViewById(R.id.cupCapacityEditText);
        saveButton = findViewById(R.id.saveButton);
    }

    private void loadCurrentValues() {
        Intent intent = getIntent();
        dailyGoal = intent.getIntExtra("daily_goal", 2000);
        cupCapacity = intent.getIntExtra("cup_capacity", 250);

        dailyGoalEditText.setText(String.valueOf(dailyGoal));
        cupCapacityEditText.setText(String.valueOf(cupCapacity));
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        String dailyGoalStr = dailyGoalEditText.getText().toString().trim();
        String cupCapacityStr = cupCapacityEditText.getText().toString().trim();

        if (dailyGoalStr.isEmpty() || cupCapacityStr.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int newDailyGoal = Integer.parseInt(dailyGoalStr);
            int newCupCapacity = Integer.parseInt(cupCapacityStr);

            if (newDailyGoal <= 0 || newCupCapacity <= 0) {
                Toast.makeText(this, "请输入有效的数值！", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newDailyGoal > 10000 || newCupCapacity > 2000) {
                Toast.makeText(this, "数值过大，请输入合理的数值", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("daily_goal", newDailyGoal);
            resultIntent.putExtra("cup_capacity", newCupCapacity);
            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}