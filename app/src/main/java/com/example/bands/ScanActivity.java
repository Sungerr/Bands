package com.example.bands;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Placeholder;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
            IntentFilter[] intentFilter = new IntentFilter[]{};
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            //Toast.makeText(this, "NFC Intent!", Toast.LENGTH_LONG).show();

            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null && parcelables.length > 0) {
                getTagInfo((NdefMessage) parcelables[0]);
            } else {
                Toast.makeText(this, "No NDEF messages!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onNewIntent(intent);
    }

    private void getTagInfo(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getNdefInfo(ndefRecord);
            textViewInfo.setText(tagContent);
        } else {
            Toast.makeText(this, "No NDEF Records Found!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getNdefInfo(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String encoding = ((payload[0] & 128) == 0) ? "UTF-8" :"UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize+1,payload.length-languageSize-1, encoding);
        } catch (Exception e) {
            Log.e("getTextFromNdeRecord", e.getMessage(), e);
        }
        return tagContent;
    }
}