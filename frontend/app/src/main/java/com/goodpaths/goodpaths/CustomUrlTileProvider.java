package com.goodpaths.goodpaths;

import com.goodpaths.goodpaths.networking.ServerAccess;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class CustomUrlTileProvider extends UrlTileProvider {

    private static final String URL = ServerAccess.BASE_URL + "/tileRequest?x=%d&y=%d&zoom=%d";

    public CustomUrlTileProvider(int i1, int i2) {
        super(i1, i2);
    }

    @Override
    public URL getTileUrl(int x, int y, int zoom) {
        try {
            return new URL(String.format(URL, x, y, zoom));
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
