package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etTo, etContent;
    Button btnSend, btnSendExternal;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTo = findViewById(R.id.etTo);
        etContent = findViewById(R.id.etContent);
        btnSend = findViewById(R.id.btnSend);
        btnSendExternal = findViewById(R.id.btnSendExternal);

        br = new MessageReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(); // Permission check

                validationChecks(); // Check if fields are empty

                String recipient = etTo.getText().toString().trim();// Get number of recipients
                String[] recipients = recipient.split(",");

                String message = etContent.getText().toString();// Get message content

                // Send Message
                SmsManager smsManager = SmsManager.getDefault();
                for (int i = 0; i < recipients.length; i++) {
                    smsManager.sendTextMessage(recipients[i], null, message, null, null);
                }
            }
        });

        btnSendExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validationChecks(); // Check if fields are empty

                String recipient = etTo.getText().toString().trim(); // Get number of Recipients

                String message = etContent.getText().toString(); // Get message content

                Uri smsUri = Uri.parse("sms:" + recipient);
                Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                intent.putExtra("address", recipient);
                intent.putExtra("sms_body", message);
                startActivity(intent);


            }
        });
    }

    private void validationChecks(){
    String to = etTo.getText().toString().trim();
    String content = etContent.getText().toString().trim();

        if (to.isEmpty() && content.isEmpty()) {
        Toast.makeText(this, "Enter all fields!", Toast.LENGTH_SHORT).show();
    } else {
        if (to.isEmpty()) {
            Toast.makeText(this, "Enter a recipient.", Toast.LENGTH_SHORT).show();
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Enter content.", Toast.LENGTH_SHORT).show();
        }
    }
}
    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
