package com.goodpaths.goodpaths.business;

import android.content.Context;

import com.goodpaths.common.Report;
import com.goodpaths.goodpaths.networking.ServerAccess;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class CustomUrlTileProvider extends UrlTileProvider {

    private static final String URL = ServerAccess.BASE_URL + "/tileRequest?x=%d&y=%d&zoom=%d&problemtype=%s";

    private final Context context;

    public CustomUrlTileProvider(int i1, int i2, Context context) {
        super(i1, i2);
        this.context = context;
    }

    @Override
    public URL getTileUrl(int x, int y, int zoom) {
        try {
            Report.Type toUse = DangerTypeHelper.getInstance().getDangerType(context) == DangerTypeHelper.ACCESSIBILITY ? Report.Type.ACCESSIBILITY : Report.Type.HARASSMENT;
            return new URL(String.format(URL, x, y, zoom, toUse.toString()));
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
