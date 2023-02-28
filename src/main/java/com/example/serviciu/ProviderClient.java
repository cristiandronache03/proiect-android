package com.example.serviciu;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class ProviderClient {
    private Uri uri_ = Uri.parse("content://com.myProvider.DATA");
    private static ContentProviderClient provider;
    public ProviderClient(Context context){
        if(provider == null){
            provider = context.getContentResolver().acquireUnstableContentProviderClient(uri_);
            Log.d("Test", "ProviderCLient" + provider);
        }
    }
    public String getValue(String key){
        Cursor cursor; String value = "";
        try {
            if(provider != null){
                cursor = provider.query(Uri.withAppendedPath(uri_, key), null, "", null, "");
                if(cursor!=null){
                    cursor.moveToNext();value = cursor.getColumnName(0);
                    Log.d("Test","Receive from Provider: " + value);
                }
            }
        } catch (RemoteException e) {
            provider = null;
        }
        return value;
    }
}

