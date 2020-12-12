package org.aldofrankmarco.shak.profile.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.aldofrankmarco.shak.R;

public class SendMailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView titleTextView;

    private EditText mEditTextTo;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;

    private Button sendMailButton;

    private String userEmail;
    private String userUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);

        this.userEmail = getIntent().getExtras().getString("email");
        this.userUsername = getIntent().getExtras().getString("username");

        mEditTextSubject = findViewById(R.id.edit_text_subject);
        mEditTextMessage = findViewById(R.id.edit_text_message);
        titleTextView = findViewById(R.id.title_send_mail_to);
        sendMailButton = findViewById(R.id.button_send_mail);

        sendMailButton.setOnClickListener(this);

        String title = "Send an email to " + userUsername;
        titleTextView.setText(title);
        setTitle(title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_send_mail:
                sendMail();
                break;
        }
    }

    public void sendMail() {
        String subject = mEditTextSubject.getText().toString();
        String message = mEditTextMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {userEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }
}
