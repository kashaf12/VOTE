package www.kfstudio.vote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class  MainActivity extends AppCompatActivity {
    private Handler handler1;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private EditText phoneText;
    private CountryCodePicker ccpGetNumber;
    private Button sendButton;
     FirebaseAuth firebaseAuth;
    private String mVerificationId;
    String verifyCode;
    Dialog dialog;
    PhoneAuthCredential credential;
    DocumentReference documentReference;
    FirebaseFirestore db;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneText = (EditText) findViewById(R.id.emaillog);
        ccpGetNumber = findViewById(R.id.ccp_getFullNumber);
        sendButton = findViewById(R.id.btnuserlog);
        sendButton.setVisibility(View.INVISIBLE);
        linearLayout =findViewById(R.id.layout_linear);
        progressBar =findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
        phoneText.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        requestStoragePermission();
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
                                if(!verifyCode.trim().isEmpty()) {

                                    credential = PhoneAuthProvider.getCredential(mVerificationId, verifyCode);
                                    signInWithPhoneAuthCredential(credential);
                                }else{
                                    Toast.makeText(MainActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                                }

                            }

                        });
                        resendButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
        dialog.dismiss();
        linearLayout.setVisibility(View.GONE);
        phoneText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.GONE);
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

        Intent intent = new Intent(MainActivity.this,Register_Activity.class);
        intent.putExtra("phoneNumber",phoneNumber);
        progressBar.setVisibility(View.GONE);
        startActivity(intent);
        finish();
    }
    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(

                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isNetworkAvailable()) {
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//                                if (currentUser != null) {
//                                    Intent intent = new Intent(MainActivity.this, Home.class);
//                                    progressBar.setVisibility(View.GONE);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
                                    linearLayout.setVisibility(View.VISIBLE);
                                    phoneText.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    sendButton.setVisibility(View.VISIBLE);

//                                }
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void database(String phone){
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("Vote").document("Users")
                .collection(phone).document("Profile_Informaation");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                }else{

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}
