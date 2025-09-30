package com.watertracker.app;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "WaterTrackerPrefs";
    private static final String KEY_DAILY_GOAL = "daily_goal";
    private static final String KEY_CUP_CAPACITY = "cup_capacity";
    private static final String KEY_CURRENT_AMOUNT = "current_amount";
    private static final String KEY_LAST_DATE = "last_date";
    private static final String KEY_RECORDS = "records";

    private TextView currentAmountText;
    private TextView totalGoalText;
    private TextView remainingText;
    private TextView dateText;
    private TextView cupText;
    private View drinkButtonView;
    private CircularProgressView progressView;
    private RecyclerView recordsRecyclerView;
    private LinearLayout emptyStateLayout;

    private RecordsAdapter recordsAdapter;
    private List<WaterRecord> records;

    private int dailyGoal = 2000;
    private int cupCapacity = 250;
    private int currentAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        loadData();
        updateDisplay();
        updateDate();
    }

    private void initViews() {
        currentAmountText = findViewById(R.id.currentAmountText);
        totalGoalText = findViewById(R.id.totalGoalText);
        remainingText = findViewById(R.id.remainingText);
        dateText = findViewById(R.id.dateText);
        cupText = findViewById(R.id.cupText);
        drinkButtonView = findViewById(R.id.drinkButton);
        progressView = findViewById(R.id.progressView);
        recordsRecyclerView = findViewById(R.id.recordsRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        drinkButtonView.setOnClickListener(v -> drinkWater());
    }

    private void setupRecyclerView() {
        records = new ArrayList<>();
        recordsAdapter = new RecordsAdapter(records);
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordsRecyclerView.setAdapter(recordsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("daily_goal", dailyGoal);
            intent.putExtra("cup_capacity", cupCapacity);
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            dailyGoal = data.getIntExtra("daily_goal", 2000);
            cupCapacity = data.getIntExtra("cup_capacity", 250);
            saveData();
            updateDisplay();
        }
    }

    private void loadData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String today = getCurrentDateString();
        String lastDate = prefs.getString(KEY_LAST_DATE, "");

        dailyGoal = prefs.getInt(KEY_DAILY_GOAL, 2000);
        cupCapacity = prefs.getInt(KEY_CUP_CAPACITY, 250);

        if (today.equals(lastDate)) {
            currentAmount = prefs.getInt(KEY_CURRENT_AMOUNT, 0);
            String recordsJson = prefs.getString(KEY_RECORDS, "");
            records.clear();
            records.addAll(WaterRecord.fromJsonArray(recordsJson));
        } else {
            currentAmount = 0;
            records.clear();
        }
    }

    private void saveData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_DAILY_GOAL, dailyGoal);
        editor.putInt(KEY_CUP_CAPACITY, cupCapacity);
        editor.putInt(KEY_CURRENT_AMOUNT, currentAmount);
        editor.putString(KEY_LAST_DATE, getCurrentDateString());
        editor.putString(KEY_RECORDS, WaterRecord.toJsonArray(records));

        editor.apply();
    }

    private void updateDisplay() {
        currentAmountText.setText(String.valueOf(currentAmount));
        totalGoalText.setText(String.valueOf(dailyGoal));
        cupText.setText(cupCapacity + "ml");

        int remaining = Math.max(0, dailyGoal - currentAmount);
        remainingText.setText("今日剩余目标: " + remaining + "ml");

        float progress = Math.min((float) currentAmount / dailyGoal, 1.0f);
        progressView.setProgress(progress);

        updateRecordsList();
    }

    private void updateRecordsList() {
        if (records.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recordsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recordsRecyclerView.setVisibility(View.VISIBLE);
            recordsAdapter.notifyDataSetChanged();
        }
    }

    private void drinkWater() {
        currentAmount += cupCapacity;

        // 添加记录
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        WaterRecord record = new WaterRecord(currentTime, cupCapacity);
        records.add(0, record);

        saveData();
        updateDisplay();
        showSuccessAnimation();

        // 检查是否完成目标
        if (currentAmount >= dailyGoal) {
            showGoalCompletedDialog();
        }
    }

    private void showSuccessAnimation() {
        // 创建缩放动画
        ValueAnimator scaleDown = ValueAnimator.ofFloat(1.0f, 0.9f);
        scaleDown.setDuration(100);
        scaleDown.setInterpolator(new AccelerateDecelerateInterpolator());

        ValueAnimator scaleUp = ValueAnimator.ofFloat(0.9f, 1.0f);
        scaleUp.setDuration(100);
        scaleUp.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleDown.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            drinkButtonView.setScaleX(scale);
            drinkButtonView.setScaleY(scale);
        });

        scaleUp.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            drinkButtonView.setScaleX(scale);
            drinkButtonView.setScaleY(scale);
        });

        scaleDown.start();
        scaleUp.setStartDelay(100);
        scaleUp.start();

        Toast.makeText(this, "打卡成功！+" + cupCapacity + "ml", Toast.LENGTH_SHORT).show();
    }

    private void showGoalCompletedDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("🎉 恭喜！")
                .setMessage("今日喝水目标已完成！")
                .setPositiveButton("好的", null)
                .show();
    }

    private void updateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日 EEEE", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        dateText.setText(currentDate);
    }

    private String getCurrentDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}