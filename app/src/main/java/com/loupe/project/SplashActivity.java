package com.loupe.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.zackratos.ultimatebar.UltimateBar;
import com.loupe.project.camare.TimerUtils;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initStatusBar();

        //倒计时1秒
        TimerUtils.getInstance().startTimer(1111, 1, 0, 1000);

        TimerUtils.getInstance().setOnSplashFinishInterface(new TimerUtils.OnSplashFinishInterface() {
            @Override
            public void onSplashTimerBack() {
                //1秒后回调
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initStatusBar() {
        //取消状态栏
        if (CommonLoupeSetUtils.canLightLoupeStatusBar()) {
            //判断是否支持
            UltimateBar.newTransparentBuilder()
                    // 状态栏颜色
                    .statusColor(Color.TRANSPARENT)
                    // 状态栏透明度
                    .statusAlpha(50).build(this).apply();
        }

        //状态内容颜色
        CommonLoupeSetUtils.onlyLoupeLightStatusbarTextDark(this.getWindow(), false);
    }
}