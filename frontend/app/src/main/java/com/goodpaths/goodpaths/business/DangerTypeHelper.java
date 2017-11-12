package com.goodpaths.goodpaths.business;

import android.content.Context;
import android.content.SharedPreferences;

import com.goodpaths.common.Report;

public class DangerTypeHelper {

    // region constants

    public static final int HARASSMENT = 0;
    public static final int ACCESSIBILITY = 1;

    private static final String SHARED_PREF_NAME = "com.goodpath.longname.whatever.ok.ok.ok.prefs";
    private static final String KEY_DANGER_TYPE= "danger_type";
    private static final int DEFAULT_DANGER_TYPE = HARASSMENT;

    // endregion

    private static DangerTypeHelper instance;

    private int currentDanger = -1;

    private DangerTypeHelper(){}

    public static DangerTypeHelper getInstance(){
        if(instance == null){
            instance = new DangerTypeHelper();
        }
        return instance;
    }

    public void setDangerType(Context context, int dangerType){
        currentDanger = dangerType;
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_DANGER_TYPE, dangerType);
        editor.commit();
    }

    public int getDangerType(Context context){
        if(currentDanger == -1) {
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            currentDanger = sharedPref.getInt(KEY_DANGER_TYPE, DEFAULT_DANGER_TYPE);
        }
        return currentDanger;
    }

    public Report.Type getType(Context context) {
        int val = getDangerType(context);
        if(val == HARASSMENT) {
            return Report.Type.HARASSMENT;
        } else {
            return Report.Type.ACCESSIBILITY;
        }
    }
}

