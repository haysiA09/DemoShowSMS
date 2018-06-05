package com.example.a16022877.demoshowsms;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView tvSMS;
    Button btnRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvSMS=(TextView)findViewById(R.id.tv);
        btnRetrieve=(Button)findViewById(R.id.btnRetrieve);
        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck= PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);

                if(permissionCheck!=PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_SMS},0);
                    return;
                }
                //Create all message URI
                Uri uri= Uri.parse("content://sms");
                //The Columns we want
                //date is when the message took place
                //address is the number of the other party
                //body is the message content
                //type 1 is receiver , type 2 send
                String[]reqCols=new String[]{"date","address","body","type"};
                //Get Content Resolver object fro, whcih to
                //query the content provider
                ContentResolver cr=getContentResolver();
                String filter="body LIKE? AND body LIKE?";
                String[]filterArgs={"%late%","%min%"};
                Cursor cursor=cr.query(uri,reqCols,null,null,null);
                String smsBody="";
                if (cursor.moveToFirst()){
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox";
                        } else {
                            type = "Sent";
                        }
                        smsBody += type + "" + address + "\n at" + date + "\n\"" + body + "\"\n\n";
                    }while (cursor.moveToNext()) ;

                    }tvSMS.setText(smsBody);

            }
        });
    }

    public void onRequestPersmissionResult(int requestCode,String permissions[],int[]grantResults){
        switch(requestCode){
            case 0:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    btnRetrieve.performClick();
                }else{
                    Toast.makeText(MainActivity.this,"Permission not grnated",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
