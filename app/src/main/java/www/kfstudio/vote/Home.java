package www.kfstudio.vote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Home extends AppCompatActivity{

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    RecyclerView recyclerView;
    String name="Loading";
    int poll_vote=0;
    Uri downloadUri;
    private File actualImage;
    int poll_vote_1=0;
    int poll_vote_2=0;
    int poll_vote_3=0;

    Uri uriTask1,uriTask2,uriTask3,uriTask_profile;
    private File compressedImage,profile_imag;
    private File compressedImage1;
    private File compressedImage2;
    private File compressedImage3;
    private FirebaseAuth mAuth;
    ArrayList<String> arrayList;
    Uri poll_profile_image;
    DocumentReference documentReference;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference dbr = db.collection("Polls");
    String currentDateTimeString;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Uri image;
    int calling;
    Dialog dialog;
    String email="Loading..";
    private int GALLERY = 1;
    String dob="Loading";
    String gender="Male";
    FloatingActionButton floatingActionButton;
    BottomAppBar bottomAppBar;
    EditText pollName,etOption1,etOption2,etOption3;
    Button publish;
    ImageView imageOption1,imageOption2,imageOption3;
    LinearLayout parentll;
    FirebaseUser currentUser;
    Map<String,Object> polling;
    private PollAdapter pollAdapter;
    int clicked=0;
    String pollNameString,option1string,option2string,option3string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        bottomAppBar=findViewById(R.id.bottom_app_bar);
        floatingActionButton = findViewById(R.id.fab);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        //main line for setting menu in bottom app bar
        setSupportActionBar(bottomAppBar);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database(currentUser.getPhoneNumber());
        if (bundle != null) {

            image = Uri.fromFile(new File(bundle.getString("image")));
            uploadProfile();
        }else{
            image = Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/drawable/blank_profile_picture_973460_960_720");
            downloadimage(currentUser.getPhoneNumber());
            }
        setUpRecyclerView();




        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(name.equals("Loading")){
//                    downloadimage(currentUser.getPhoneNumber());
                    database(currentUser.getPhoneNumber());
                }
                Intent intent = new Intent(Home.this, Register_Activity.class);
                Bundle extras = new Bundle();
                extras.putString("Name", name);
                extras.putString("Email", email);
                extras.putString("Dob", dob);
                extras.putString("Gender", gender);
                extras.putString("Image", image.toString());
                extras.putString("phoneNumber", currentUser.getPhoneNumber());
                intent.putExtras(extras);
                startActivity(intent);

                return false;
            }

        });
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open bottom sheet
                BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetNavigationFragment.newInstance(name,email,image.toString());
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(Home.this);
                dialog.setContentView(R.layout.poll_dialog);
                pollName =dialog.findViewById(R.id.poll_name);
                etOption1 =dialog.findViewById(R.id.option1_et);
                etOption2 =dialog.findViewById(R.id.option2_et);
                etOption3=dialog.findViewById(R.id.option3_et);
                publish = dialog.findViewById(R.id.publish_poll);
                imageOption1=dialog.findViewById(R.id.option1_image);
                imageOption2=dialog.findViewById(R.id.option2_image);
                imageOption3=dialog.findViewById(R.id.option3_image);
                parentll=dialog.findViewById(R.id.parent_ll);
                imageOption1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosePhotoFromGallary();
                        calling = 1;
//                Glide.with(getBaseContext())
//                        .load(compressedImage)
//                        .apply(RequestOptions.centerCropTransform())
//                        .apply(new RequestOptions().placeholder(R.drawable.blank_profile_picture_973460_960_720))
//                        .apply(new RequestOptions().override(50,50))
//                        .into(imageOption1);
                        //imageOption1.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
                    }
                });
                imageOption2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosePhotoFromGallary();
                        calling=2;
                    }
                });
                imageOption3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosePhotoFromGallary();
                        calling=3;
                    }
                });
                publish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pollNameString = pollName.getText().toString().trim();
                        option1string = etOption1.getText().toString().trim();
                        option2string = etOption2.getText().toString().trim();
                        option3string = etOption3.getText().toString().trim();
                        if (!(TextUtils.isEmpty(pollNameString) | TextUtils.isEmpty(option1string) | TextUtils.isEmpty(option2string) | TextUtils.isEmpty(option3string))) {
                            uploadImage("poll1");
                            uploadImage("poll2");
                            uploadImage("poll3");
                            uploadPoll(pollNameString,option1string,option2string,option3string);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(Home.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                dialog.show();
            }


        });
    }

    private void setUpRecyclerView() {
        Query query = dbr.orderBy("Time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Poll> options = new FirestoreRecyclerOptions.Builder<Poll>()
                .setQuery(query,Poll.class).build();
        pollAdapter = new PollAdapter(options);
        recyclerView= findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(pollAdapter);
        pollAdapter.setOnItemClickListener(new PollAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position,String pollString) {
                Poll poll = documentSnapshot.toObject(Poll.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                int poll_vote= Integer.parseInt(documentSnapshot.get("poll_vote").toString());
                poll_vote_1= Integer.parseInt(documentSnapshot.get("poll_vote_1").toString());
                poll_vote_2= Integer.parseInt(documentSnapshot.get("poll_vote_2").toString());
                poll_vote_3= Integer.parseInt(documentSnapshot.get("poll_vote_3").toString());
                arrayList = (ArrayList<String>) documentSnapshot.get("poll_clicked");
                for(String item : arrayList) {
                    if (item.equals(currentUser.getPhoneNumber())) {
                        Toast.makeText(Home.this, "You have already rated this poll!", Toast.LENGTH_SHORT).show();
                        return;

                    }
                }
                clicked++;
                switch (pollString){
                    case "poll1":poll_vote_1++;

                    break;
                    case "poll2":poll_vote_2++;
                        break;
                    case "poll3":poll_vote_3++;
                        break;
                }

                poll_vote++;
                Map<String,Object> polls = new HashMap<>();
                polls.put("poll_vote",Integer.toString(poll_vote));
                polls.put("poll_vote_1",Integer.toString(poll_vote_1));
                polls.put("poll_vote_2",Integer.toString(poll_vote_2));
                polls.put("poll_vote_3",Integer.toString(poll_vote_3));
                polls.put("poll_clicked", FieldValue.arrayUnion(currentUser.getPhoneNumber()));
                db.collection("Polls").document(id).update(polls);
               }
        });
    }

    private void uploadPoll(String pollNameString, String option1string, String option2string, String option3string) {
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Map<String,Object> poll = new HashMap<>();
        poll.put("poll_title",pollNameString);
        poll.put("poll_1",option1string);
        poll.put("poll_2",option2string);
        poll.put("poll_3",option3string);
        poll.put("poll_name",name);
        poll.put("poll_phone",currentUser.getPhoneNumber());
        poll.put("Time",currentDateTimeString);
        poll.put("poll_vote",Integer.toString(poll_vote));
        poll.put("poll_vote_1","0");
        poll.put("poll_vote_2","0");
        poll.put("poll_vote_3","0");
        poll.put("poll_email",email);
        poll.put("poll_clicked", Arrays.asList("0"));
        db.collection("Polls").document(currentDateTimeString).set(poll);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottomappbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private  void database(final String phone){

        documentReference = db.collection("Vote").document("Users")
                .collection(phone).document("ProfileInformation");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    name = documentSnapshot.getString("Name");
                    email = documentSnapshot.getString("Email");
                    dob = documentSnapshot.getString("Dob");
                    gender = documentSnapshot.getString("Gender");

                }
            }
        });

    }
//    private void downloadimage(String phone){
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReferenceFromUrl("gs://voteapp-master-8201e.appspot.com/"+phone+"/");
//        storageReference.child("profile_image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                image = uri;
//
//            }
//        });

  //  }
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
        if (resultCode == RESULT_CANCELED) {
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

                            switch (calling){
                                case 1: compressedImage1=file;
                                    Glide.with(getApplicationContext())
                                            .load(compressedImage1)
                                            .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
                                            .apply(RequestOptions.centerCropTransform())
                                            .into(imageOption1);
                                    break;
                                case 2:compressedImage2=file;
                                    Glide.with(getApplicationContext())
                                            .load(compressedImage2)
                                            .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
                                            .apply(RequestOptions.centerCropTransform())
                                            .into(imageOption2);
                                    break;
                                case 3:compressedImage3=file;
                                    Glide.with(getApplicationContext())
                                            .load(compressedImage3)
                                            .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
                                            .apply(RequestOptions.centerCropTransform())
                                            .into(imageOption3);
                                    break;



                            }

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
    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
    private void uploadImage(final String name) {
        switch (name){
            case "poll1":compressedImage=compressedImage1;
                break;
            case "poll2": compressedImage=compressedImage2;
                break;
            case "poll3":compressedImage=compressedImage3;
                break;
        }if(!name.isEmpty()) {

            storageReference = storage.getReferenceFromUrl
                    ("gs://voteapp-master-8201e.appspot.com/"
                            + currentUser.getPhoneNumber() + "/Polls/"
                            + currentDateTimeString + "/");
            if (compressedImage != null) {
                final StorageReference ref = storageReference.child(name);
                ref.putFile(Uri.fromFile(compressedImage)).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUri = task.getResult();
                            polling = new HashMap<>();
                            switch (name){
                                case "profile":uriTask_profile=downloadUri;
                                    polling.put("poll_profile_image",uriTask_profile.toString());
                                    db.collection("Polls").document(currentDateTimeString).update(polling);
                                    break;
                                case "poll1":uriTask1= downloadUri;
                                    polling.put("poll_1_url",uriTask1.toString());
                                    db.collection("Polls").document(currentDateTimeString).update(polling);

                                    break;
                                case "poll2": uriTask2=downloadUri;
                                    polling.put("poll_2_url",uriTask2.toString());
                                    db.collection("Polls").document(currentDateTimeString).update(polling);
                                    break;
                                case "poll3":uriTask3=downloadUri;
                                    polling.put("poll_3_url",uriTask3.toString());
                                    db.collection("Polls").document(currentDateTimeString).update(polling);
                                    break;
                            }
                        }
                    }
                });

            }
        }
    }
    private void uploadProfile(){
        storageReference = storage.getReferenceFromUrl("gs://voteapp-master-8201e.appspot.com/" + currentUser.getPhoneNumber() + "/");
            final StorageReference ref = storageReference.child("profile_picture");
            ref.putFile(image).continueWith(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        Map<String,Object> user = new HashMap<>();
                        user.put("profile_image", task.getResult().toString());
                        db.collection("Vote").document("Users")
                                .collection(currentUser.getPhoneNumber()).document("ProfileInformation")
                                .update(user);

                    }
                }
            });

    }
    @Override
    protected void onResume() {
        super.onResume();
        pollAdapter.startListening();
    }
    @Override
    protected void onStart(){
        super.onStart();
        pollAdapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        pollAdapter.stopListening();
    }
    @Override
    protected void onPause(){
        super.onPause();
        pollAdapter.stopListening();
    }

    private void downloadimage(String phone){
        storageReference = storage.getReferenceFromUrl("gs://voteapp-master-8201e.appspot.com/"+phone+"/");
        storageReference.child("profile_picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                    image = uri;
            }
        });

    }

}
