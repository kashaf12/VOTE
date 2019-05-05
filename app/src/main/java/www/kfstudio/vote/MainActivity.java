package www.kfstudio.vote;

import android.app.Dialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class  MainActivity extends AppCompatActivity {

    private EditText phoneText;
    private CountryCodePicker ccpGetNumber;
    private Button sendButton;
    private FirebaseAuth firebaseAuth;
    private String mVerificationId;
    String verifyCode;
    Dialog dialog;
    PhoneAuthCredential credential;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneText = (EditText) findViewById(R.id.emaillog);
        ccpGetNumber = findViewById(R.id.ccp_getFullNumber);
        sendButton = findViewById(R.id.btnuserlog);
        firebaseAuth = FirebaseAuth.getInstance();
        handler=new Handler(getApplicationContext().getMainLooper());
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {



            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                sendButton.setText("Send");
                sendButton.setEnabled(true);
                sendButton.setClickable(true);
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(String verificationId,PhoneAuthProvider.ForceResendingToken token){
                mVerificationId = verificationId;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Verification code has been sent", Toast.LENGTH_SHORT).show();
                        dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.verification_dialog);
                        dialog.setCancelable(false);
                        final TextView textView = dialog.findViewById(R.id.dialog_text);
                        textView.setText("We sent a 6 digit verification code to "+ ccpGetNumber.getFullNumberWithPlus() +". Enter it below.");
                        final TextView verifyButton = dialog.findViewById(R.id.btn_verify);
                        TextView resendButton = dialog.findViewById(R.id.btn_resend);
                        verifyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText verificationCode  = dialog.findViewById(R.id.verification_code);
                                verifyCode = verificationCode.getText().toString();
                                verifyButton.setClickable(false);
                                verifyButton.setEnabled(false);
                                credential = PhoneAuthProvider.getCredential(mVerificationId, verifyCode);
                                signInWithPhoneAuthCredential(credential);
                            }

                        });
                        resendButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendCode();
                                dialog.dismiss();
                                sendButton.setText("LOGIN");
                                sendButton.setEnabled(true);
                                sendButton.setClickable(true);
                            }
                        });
                        dialog.show();
                    }
                });
            }
        };
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();

            }
        });
    }
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Invalid Phone Number", Toast.LENGTH_LONG).show();

        sendButton.setEnabled(true);
        sendButton.setClickable(true);
    }
    public void login() {
        ccpGetNumber.registerCarrierNumberEditText(phoneText);

        if (!(ccpGetNumber.isValidFullNumber())) {
            onLoginFailed();
            return;
        }
        sendButton.setText("Wait...");
        sendButton.setEnabled(false);
        sendButton.setClickable(false);
        new  Thread(new Runnable() {
            @Override
            public void run() {
                sendCode();


            }
        }).start();
    }
    private void sendCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                ccpGetNumber.getFullNumberWithPlus(),
                60,
                TimeUnit.SECONDS,
                MainActivity.this,
                mCallbacks

        );

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            checkdata(ccpGetNumber.getFullNumberWithPlus());

                        }
                        else{
                            if(task.getException()instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(MainActivity.this, "Credential error !! ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    private void checkdata(final String phoneNumber) {
        Toast.makeText(this, "checkData called", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

}
