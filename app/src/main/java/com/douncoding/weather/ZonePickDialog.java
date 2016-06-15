package com.douncoding.weather;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

public class ZonePickDialog {

    public static OnListener listener;

    private static ArrayList<String> mSupportedZoneName;

    public static void show(Context context) {
        final SupportedZone zone = SupportedZone.getInstance();
        mSupportedZoneName = new ArrayList<>(zone.getSupportedZoneList());

        new MaterialDialog.Builder(context)
                .title("지역을 선택하세요.")
                .items(mSupportedZoneName)
                .itemsCallback(listCallback)
                .show();
    }

    static MaterialDialog.ListCallback listCallback = new MaterialDialog.ListCallback() {
        @Override
        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
            listener.onClick(mSupportedZoneName.get(which));
        }
    };

    public interface OnListener {
        void onClick(String name);
    }
}
