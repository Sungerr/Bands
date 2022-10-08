package com.example.bands;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Placeholder;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends AppCompatActivity {

    TextView textViewInfo;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        textViewInfo = findViewById(R.id.info);

        nfcAdapter = NfcAdapter.getDefaultAdapter(ScanActivity.this);
        if(nfcAdapter == null){
            Toast.makeText(ScanActivity.this, "NFC NOT supported on this devices!", Toast.LENGTH_LONG).show();
            finish();
        } else if(!nfcAdapter.isEnabled()){
            Toast.makeText(ScanActivity.this, "NFC NOT Enabled!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(ScanActivity.this, "NFC Enabled! Scan Your Tag to Get Started", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
        super.onResume();
    }

    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this, "NFC Intent Received!", Toast.LENGTH_LONG).show();
    }
//
//    private void getTagInfo(Intent intent) {
//        String action = intent.getAction();
//
//        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
//                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//        }
//
//    }
}