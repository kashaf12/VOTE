package www.kfstudio.vote;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

public class PollAdapter extends FirestoreRecyclerAdapter<Poll, PollAdapter.PollHolder> {
    View v;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public PollAdapter(@NonNull FirestoreRecyclerOptions<Poll> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PollHolder pollHolder, int i, @NonNull final Poll poll) {
        pollHolder.poll_name.setText(poll.getPoll_name());
        pollHolder.poll_email.setText(poll.getPoll_email());
        pollHolder.poll_title.setText(poll.getPoll_title());
        pollHolder.poll_1.setText(poll.getPoll_1());
        pollHolder.poll_2.setText(poll.getPoll_2());
        pollHolder.poll_3.setText(poll.getPoll_3());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://voteapp-master-8201e.appspot.com/"+poll.getPoll_phone()+"/");
        storageReference.child("profile_picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(v).load(uri)
                        .apply(new RequestOptions().placeholder(R.drawable.blank_profile_picture_973460_960_720))
                        .apply(RequestOptions.centerCropTransform())
                        .into(pollHolder.profile_poll);

            }
        });
        Glide.with(v).load(poll.getPoll_1_url())
                .apply(new RequestOptions().placeholder(R.drawable.blank_profile_picture_973460_960_720))
                .apply(RequestOptions.centerCropTransform())
                .into(pollHolder.option1_poll_image);
        Glide.with(v).load(poll.getPoll_2_url())
                .apply(new RequestOptions().placeholder(R.drawable.blank_profile_picture_973460_960_720))
                .apply(RequestOptions.centerCropTransform())
                .into(pollHolder.option2_poll_image);
        Glide.with(v).load(poll.getPoll_3_url())
                .apply(new RequestOptions().placeholder(R.drawable.blank_profile_picture_973460_960_720))
                .apply(RequestOptions.centerCropTransform())
                .into(pollHolder.option3_poll_image);

    }

    @NonNull
    @Override
    public PollHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v =LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_item, parent, false);
        return new PollHolder(v);
    }

    class PollHolder extends RecyclerView.ViewHolder{
       TextView poll_name;
       TextView poll_email;
       TextView poll_title;
       TextView poll_1;
       TextView poll_2;
       TextView poll_3;
       CircularImageView profile_poll;
       ImageView option1_poll_image,option2_poll_image,option3_poll_image;
        public PollHolder(@NonNull View itemView) {
            super(itemView);
            poll_name = itemView.findViewById(R.id.poll_name1);
            poll_email = itemView.findViewById(R.id.email_poll1);
            poll_title = itemView.findViewById(R.id.poll_title1);
            poll_1 = itemView.findViewById(R.id.option1_et_poll1);
            poll_2 = itemView.findViewById(R.id.option2_et_poll1);
            poll_3 = itemView.findViewById(R.id.option3_et_poll1);
            profile_poll=itemView.findViewById(R.id.profile_poll);
            option1_poll_image=itemView.findViewById(R.id.option1_image_poll);
            option2_poll_image=itemView.findViewById(R.id.option2_image_poll);
            option3_poll_image=itemView.findViewById(R.id.option3_image_poll);

        }
    }

}
