package www.kfstudio.vote;

import android.net.Uri;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import me.ithebk.barchart.BarChart;
import me.ithebk.barchart.BarChartModel;
import me.ithebk.barchart.BarChartUtils;

public class PollAdapter extends FirestoreRecyclerAdapter<Poll, PollAdapter.PollHolder> {
    private OnItemClickListener listener;
    BarChartModel barChartModel;
    View v;

    Uri image;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public PollAdapter(@NonNull FirestoreRecyclerOptions<Poll> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PollHolder pollHolder, int i, @NonNull final Poll poll) {
        String phone =poll.getPoll_phone();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://voteapp-master-8201e.appspot.com/"+phone+"/");
        storageReference.child("profile_picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(v)
                        .load(uri)
                        .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
                        .apply(RequestOptions.centerCropTransform())
                        .into(pollHolder.profile_poll_image);
            }
        });

        pollHolder.poll_name.setText(poll.getPoll_name());
        pollHolder.poll_email.setText(poll.getPoll_email());
        pollHolder.poll_title.setText(poll.getPoll_title());
        pollHolder.poll_1.setText(poll.getPoll_1());
        pollHolder.poll_2.setText(poll.getPoll_2());
        pollHolder.poll_3.setText(poll.getPoll_3());
        pollHolder.poll_vote.setText("Votes :"+poll.getPoll_vote());

        Glide.with(v).load(poll.getPoll_profile_image_url())
                .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
                .apply(RequestOptions.centerCropTransform())
                .into(pollHolder.profile_poll_image);
        Glide.with(v).load(poll.getPoll_1_url())
                .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
                .apply(RequestOptions.centerCropTransform())
                .into(pollHolder.option1_poll_image);
        Glide.with(v).load(poll.getPoll_2_url())
                .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
                .apply(RequestOptions.centerCropTransform())
                .into(pollHolder.option2_poll_image);
        Glide.with(v).load(poll.getPoll_3_url())
                .apply(new RequestOptions().placeholder(R.drawable.progress_animation))
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
        TextView poll_2,bar_text;
        String poll_vote_1,poll_vote_2,poll_vote_3;
        TextView poll_3;
        TextView poll_vote;
        CircularImageView profile_poll_image;
        BarChart chart;
        int prevposition = 0;

        ImageView option1_poll_image,option2_poll_image,option3_poll_image;

        public PollHolder(@NonNull final View itemView) {
            super(itemView);
            poll_name = itemView.findViewById(R.id.poll_name1);
            poll_email = itemView.findViewById(R.id.email_poll1);
            poll_title = itemView.findViewById(R.id.poll_title1);
            poll_1 = itemView.findViewById(R.id.option1_et_poll1);
            poll_2 = itemView.findViewById(R.id.option2_et_poll1);
            poll_3 = itemView.findViewById(R.id.option3_et_poll1);
            profile_poll_image=itemView.findViewById(R.id.profile_poll_image);
            option1_poll_image=itemView.findViewById(R.id.option1_image_poll);
            option2_poll_image=itemView.findViewById(R.id.option2_image_poll);
            option3_poll_image=itemView.findViewById(R.id.option3_image_poll);
            poll_vote=itemView.findViewById(R.id.votes);
            bar_text=itemView.findViewById(R.id.text_bar);
            chart= itemView.findViewById(R.id.bar_chart_vertical);
            chart.setVisibility(View.GONE);
             bar_text.setVisibility(View.GONE);
            chart.clearAll();
            poll_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chart.clearAll();

                    chart.setVisibility(View.VISIBLE);
                    bar_text.setVisibility(View.VISIBLE);
                    int position = getAdapterPosition();
                    prevposition = position;
                    String poll_vote = (String) getSnapshots().getSnapshot(position).get("poll_vote");
                    String poll_vote_1 = (String) getSnapshots().getSnapshot(position).get("poll_vote_1");
                    String poll_vote_2 = (String) getSnapshots().getSnapshot(position).get("poll_vote_2");
                    String poll_vote_3 = (String) getSnapshots().getSnapshot(position).get("poll_vote_3");
                    if(position != RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position,"poll1");
                        int poll=0;
                        for (int i = 1; i < 4; i++) {
                            switch (i){
                                case 1: poll=Integer.parseInt(poll_vote_1);

                                    break;
                                case 2: poll=Integer.parseInt(poll_vote_2);
                                    break;
                                case 3: poll=Integer.parseInt(poll_vote_3);
                                    break;
                            }
                            double x;
                            int poll_vote1 =Integer.parseInt(poll_vote);
                            if(poll_vote1!=0){
                                x = Double.valueOf(poll)/Double.valueOf(poll_vote1);
                                x=x*100;
                            }else{
                                x=50;
                            }
                            barChartModel = new BarChartModel();
                            barChartModel.setBarValue((int) x);
                            barChartModel.setBarColor(BarChartUtils.getRandomColor());
                            barChartModel.setBarTag(null);
                            barChartModel.setBarText(String.valueOf(poll));
                            chart.addBar(barChartModel);
                        }
                    }
                }
            });
            poll_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chart.clearAll();
                    chart.setVisibility(View.VISIBLE);
                    bar_text.setVisibility(View.VISIBLE);
                    int position = getAdapterPosition();
                    String poll_vote = (String) getSnapshots().getSnapshot(position).get("poll_vote");
                    String poll_vote_1 = (String) getSnapshots().getSnapshot(position).get("poll_vote_1");
                    String poll_vote_2 = (String) getSnapshots().getSnapshot(position).get("poll_vote_2");
                    String poll_vote_3 = (String) getSnapshots().getSnapshot(position).get("poll_vote_3");
                    if(position != RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position,"poll2");
                        int poll=0;
                        for (int i = 1; i < 4; i++) {
                            switch (i){
                                case 1: poll=Integer.parseInt(poll_vote_1);
                                    break;
                                case 2: poll=Integer.parseInt(poll_vote_2);

                                    break;
                                case 3: poll=Integer.parseInt(poll_vote_3);
                                    break;
                            }
                            double x;
                            int poll_vote1 =Integer.parseInt(poll_vote);
                            if(poll_vote1!=0){
                                x = Double.valueOf(poll)/Double.valueOf(poll_vote1);
                                x=x*100;
                            }else{
                                x=50;
                            }
                            barChartModel = new BarChartModel();
                            barChartModel.setBarValue((int) x);
                            barChartModel.setBarColor(BarChartUtils.getRandomColor());
                            barChartModel.setBarTag(null);
                            barChartModel.setBarText(String.valueOf(poll));
                            chart.addBar(barChartModel);
                        }
                    }
                }
            });
            poll_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chart.clearAll();
                    chart.setVisibility(View.VISIBLE);
                    bar_text.setVisibility(View.VISIBLE);
                    int position = getAdapterPosition();
                    String poll_vote = (String) getSnapshots().getSnapshot(position).get("poll_vote");
                    String poll_vote_1 = (String) getSnapshots().getSnapshot(position).get("poll_vote_1");
                    String poll_vote_2 = (String) getSnapshots().getSnapshot(position).get("poll_vote_2");
                    String poll_vote_3 = (String) getSnapshots().getSnapshot(position).get("poll_vote_3");
                    if(position != RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position,"poll3");
                        int poll=0;
                        for (int i = 1; i < 4; i++) {
                            switch (i){
                                case 1: poll=Integer.parseInt(poll_vote_1);
                                    break;
                                case 2: poll=Integer.parseInt(poll_vote_2);
                                    break;
                                case 3: poll=Integer.parseInt(poll_vote_3);

                                    break;
                            }
                            double x;
                            int poll_vote1 =Integer.parseInt(poll_vote);
                            if(poll_vote1!=0){
                               x = Double.valueOf(poll)/Double.valueOf(poll_vote1);
                                x=x*100;
                            }else{
                                x=50;
                            }
                            barChartModel = new BarChartModel();
                            barChartModel.setBarValue((int) x);
                            barChartModel.setBarTag(poll);
                            barChartModel.setBarColor(BarChartUtils.getRandomColor());
                            barChartModel.setBarTag(null);
                            barChartModel.setBarText(String.valueOf(poll));
                            chart.addBar(barChartModel);
                        }
                    }
                }
            });


        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, String poll);
    }

    public void setOnItemClickListener(OnItemClickListener listener ){
        this.listener=listener;

    }
}
