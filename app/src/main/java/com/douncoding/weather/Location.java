package com.douncoding.weather;

import java.util.ArrayList;

public class Locations {

    /*
    public static int GYEONGGI = ;
    public static int GANGWON = 105
    public static int North Chungcheong Province = 131
    public static int Chungcheongnam-do = 133
    public static int Jeonbuk = 146
    public static int Jeollanam-do = 156
    public static int Gyeongsangbuk-do = 143
    public static int Gyeongsangnam-do = 159
    public static int Jeju = 184
    */
    private Locations INSTANCE = null;

    private Locations() {
    }

    public Locations getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Locations();
        }
        return INSTANCE;
    }

    /*
    서울
인천
수원
파주

춘천
원주
강릉

청주
대전
서산
세종
전주
군산
광주
목포
여수
대구
안동
포항
부산
울산
창원
제주
서귀포

     */

    private class Location {
        String name;
        String termCode;
        ArrayList<City> cities = new ArrayList<>();
    }

    private class City {
        String name;
        String CourtCode;
    }
}
