package com.douncoding.weather;

import android.app.Application;

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 기상예보 지원가능 지역 정보 초기화
        SupportedZone supportedZone = SupportedZone.getInstance();
        supportedZone.setup(getResources());
    }
}
