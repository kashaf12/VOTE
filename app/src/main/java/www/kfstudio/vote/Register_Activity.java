package www.kfstudio.vote;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import io.reactivex.ObservableSource;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Register_Activity extends AppCompatActivity {
    String genderbundle = "male";
    private File actualImage;
    private File compressedImage;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Map<String, Object> user;
    FirebaseFirestore db;
    Uri contentURI;
    StorageReference ref;
    String res = null;
    int height = 120;
    int width = 120;
    private EditText name;
    private EditText email;
    private String selectedImagePath;
    private EditText dob;
    private RadioGroup gender;
    private RadioButton radioButton;
    private Button save;
    private static final String MyPREFERENCES = "MyPrefs";
    private static final String Phone = "phoneKey";
    private SharedPreferences sharedpreferences;
    private CircularImageView circleImageView;
    private int GALLERY = 1;
    String phoneNumber;
    RadioButton male, female;
    LinearLayout linearLayout;
    ScrollView scrollView;
    ProgressBar progressBar;
    TextView uploading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        db = FirebaseFirestore.getInstance();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        gender = findViewById(R.id.gender);
        dob = findViewById(R.id.dob);
        male = findViewById(R.id.MALE);
        female = findViewById(R.id.FEMALE);
        male.setChecked(true);
        dob.setFocusable(false);
        dob.setLongClickable(false);
        scrollView =findViewById(R.id.scroll_view);
        linearLayout=findViewById(R.id.ll1);
        uploading = findViewById(R.id.uploading_txt);
        circleImageView = findViewById(R.id.profile_image);
        storage = FirebaseStorage.getInstance();
        progressBar =findViewById(R.id.progress_bar);
        EditText pHonenumber = findViewById(R.id.phonnumber);
         showProgress(1);
        save = findViewById(R.id.save);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dob.setText(sdf.format(myCalendar.getTime()));
            }

        };
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Register_Activity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        circleImageView = findViewById(R.id.profile_image);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        if (bundle != null) {
            String phoneNumber = (String) bundle.get("phoneNumber");
            Boolean main = (Boolean) bundle.getBoolean("Main");
            pHonenumber.setFocusable(false);
            pHonenumber.setEnabled(false);
            pHonenumber.setCursorVisible(false);
            pHonenumber.setKeyListener(null);
            pHonenumber.setText(phoneNumber);
            pHonenumber.setTextColor(Color.WHITE);
            editor.putString(Phone, phoneNumber);
            editor.apply();
            if (!main) {
                name.setText(bundle.getString("Name"));
                email.setText(bundle.getString("Email"));
                Glide.with(this)
                        .load(Uri.parse(bundle.getString("Image")))
                        .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
                        .apply(RequestOptions.centerCropTransform())
                        .into(circleImageView);


                dob.setText(bundle.getString("Dob"));
                genderbundle = bundle.getString("Gender");
                save.setText("Save");
                if (genderbundle.equals("Male")) {
                    male.setChecked(true);
                } else {
                    female.setChecked(true);
                }
            }
        }
        phoneNumber = sharedpreferences.getString(Phone, null);
        if (phoneNumber == null) {
            sendToAuth();
        } else {
            pHonenumber.setFocusable(false);
            pHonenumber.setEnabled(false);
            pHonenumber.setCursorVisible(false);
            pHonenumber.setKeyListener(null);
            pHonenumber.setText(phoneNumber);
            pHonenumber.setTextColor(Color.WHITE);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    showProgress(2);
                    save.setText("Sending .... ");
                    save.setClickable(false);
                    String Name = name.getText().toString().trim();
                    String Dob = dob.getText().toString().trim();
                    String Email = email.getText().toString().trim();
                    int selectedId = gender.getCheckedRadioButtonId();
                    radioButton = findViewById(selectedId);
                    String radiosex = radioButton.getText().toString();
                    user = new HashMap<>();
                    user.put("Name", Name);
                    user.put("Email", Email);
                    user.put("Dob", Dob);
                    user.put("Gender", radiosex);
                    db.collection("Vote").document("Users")
                            .collection(phoneNumber).document("ProfileInformation")
                            .set(user);

                    uploadImage();

                }else{
                    showProgress(1);
                    Toast.makeText(Register_Activity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToAuth() {
        sharedpreferences.edit().clear().apply();
        Intent intent = new Intent(Register_Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean validateForm() {
        boolean alldone;
        String Name = name.getText().toString().trim();
        String Dob = dob.getText().toString().trim();
        String Email = email.getText().toString().trim();
        int selectedId = gender.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);
        if (radioButton == null) {
            radioButton.setError("Select your gender", getDrawable(R.drawable.ic_error_white_24dp));
            return false;
        }
        CharSequence radiosex = radioButton.getText();
        if (TextUtils.isEmpty(Name)) {
            name.setError("Enter your name", getDrawable(R.drawable.ic_error_white_24dp));
            return false;
        } else {
            name.setError(null);
        }
        if (TextUtils.isEmpty(Email)) {
            email.setError("Enter your Email", getDrawable(R.drawable.ic_error_white_24dp));
            return false;
        } else {

            email.setError(null);
        }
        if (TextUtils.isEmpty(Dob)) {
            dob.setError("Enter your DOB", getDrawable(R.drawable.ic_error_white_24dp));
            return false;
        } else {
            dob.setError(null);
        }
        if (TextUtils.isEmpty(radiosex)) {
            radioButton.setError("Select your gender", getDrawable(R.drawable.ic_error_white_24dp));
            return false;
        } else {

            alldone = true;
            radioButton.setError(null);
        }

        return true;
    }

    private void showPictureDialog() {
        choosePhotoFromGallary();
    }

    public void choosePhotoFromGallary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                try {
                    actualImage = FileUtil.from(this, data.getData());
                    customCompressImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }


    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void customCompressImage() {
        if (actualImage == null) {
            showError("Please choose an image!");
        } else {
            new Compressor(this)
                    .setMaxWidth(60)
                    .setMaxHeight(60)
                    .setQuality(50)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFileAsFlowable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            compressedImage = file;
                            setCompressedImage();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            showError(throwable.getMessage());
                        }
                    });
        }
    }

    private void setCompressedImage() {
        circleImageView.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));

    }

    private void uploadImage() {
        showProgress(2);
        storageReference = storage.getReferenceFromUrl("gs://voteapp-master-8201e.appspot.com/" + phoneNumber + "/");
        if (compressedImage != null) {
            final StorageReference ref = storageReference.child("profile_picture");
            ref.putFile(Uri.fromFile(compressedImage)).continueWith(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Task<Uri>>() {
                @Override
                public void onComplete(@NonNull Task<Task<Uri>> task) {
                    if(task.isSuccessful()) {
                        user.put("profile_image", task.getResult().toString());
                        db.collection("Vote").document("Users")
                                .collection(phoneNumber).document("ProfileInformation")
                                .update(user);

                    }
                }
            });




        }
        String path = compressedImage.getAbsolutePath();
        Intent intent = new Intent(Register_Activity.this, Home.class);
        intent.putExtra("image",path);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        showProgress(1);
        startActivity(intent);
        finish();

    }
    private void showProgress(int x){
        switch (x){
            case 1:
                scrollView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            uploading.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            circleImageView.setVisibility(View.VISIBLE);
            break;
            case 2: scrollView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                uploading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                circleImageView.setVisibility(View.GONE);
                break;
        }

    }
}