package www.kfstudio.vote;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class BottomSheetNavigationFragment extends BottomSheetDialogFragment {


    public static BottomSheetNavigationFragment newInstance(String name,String email,String imagePath) {

        Bundle args = new Bundle();
        args.putString("Name",name);
        args.putString("Email",email);
        args.putString("Image",imagePath);
        BottomSheetNavigationFragment fragment = new BottomSheetNavigationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Bottom Sheet Callback
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }


    };
    Dialog dialog;
    private FirebaseAuth mAuth;
    private ImageView profile;
    DocumentReference documentReference;
    FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private TextView name;
    private TextView email;
    private String username;
    private String useremail;
    private String userimage;
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Get the content View
        View contentView = View.inflate(getContext(), R.layout.bottom_navigation_drawer, null);
        dialog.setContentView(contentView);
        profile = contentView.findViewById(R.id.profile_image);
        name=contentView.findViewById(R.id.user_name);
        email = contentView.findViewById(R.id.user_email);
        name.setText(username);
        email.setText(useremail);
        Glide.with(getActivity())
                .load(Uri.parse(userimage))
                .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
                .apply(RequestOptions.centerCropTransform())
                .into(profile);
        NavigationView navigationView = contentView.findViewById(R.id.navigation_view);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav16:
                        if (currentUser != null) {
                            mAuth.signOut();
                            sendToAuth();
                        } else {
                            sendToAuth();
                        }
                        break;
                    case R.id.nav61:
                        dialog();
                        break;
                }
                return false;
            }


        });

        //Set the coordinator layout behavior
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        //Set callback
        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    private void dialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.about_dialog);
        Button button = dialog.findViewById(R.id.publish_poll1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //contItems = new ArrayList<hash>();
        if(getArguments() != null) {
           username= getArguments().getString("Name");
            useremail=getArguments().getString("Email");
            userimage=getArguments().getString("Image");


        }
    }
    private void sendToAuth(){

        Intent intent = new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}