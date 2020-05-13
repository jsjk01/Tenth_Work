package com.example.tenth_work;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class EatTogether extends AppCompatActivity {

    ListView listView;
    ContentResolver resolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_together);
        //主动申请权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EatTogether.this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }

        listView=(ListView)findViewById(R.id.listview);

        resolver=getContentResolver();
        //1.to get the data from db of any app, data is stored in cursor
        //to get contact list and to get the sorted contact list add 5th parmeter as given instead of null
        Cursor cursor=resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

//        //URI to read the call log info and ass READ_CALL_LOG permission
//        Cursor cursor=resolver.query(CallLog.Calls.CONTENT_URI, null,null,null, null);
//        String[] from=new String[]{CallLog.Calls.CACHED_NAME,CallLog.Calls.NUMBER};



        //3.just need name and number from contact list
        String[] from=new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        //4. id's for UI componenets
        int[] to=new int[]{R.id.tv1, R.id.tv2};

        //2.adapter with 5 params so define above 2params
        SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.cursor, cursor, from, to);
        listView.setAdapter(adapter);

    }
}
