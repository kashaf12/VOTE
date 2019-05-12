package www.kfstudio.vote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PollAdapter extends FirestoreRecyclerAdapter<Poll, PollAdapter.PollHolder> {

    public PollAdapter(@NonNull FirestoreRecyclerOptions<Poll> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PollHolder pollHolder, int i, @NonNull Poll poll) {
        pollHolder.poll_name.setText(poll.getPoll_name());
        pollHolder.poll_email.setText(poll.getPoll_email());
        pollHolder.poll_title.setText(poll.getPoll_title());
        pollHolder.poll_1.setText(poll.getPoll_1());
        pollHolder.poll_2.setText(poll.getPoll_2());
        pollHolder.poll_3.setText(poll.getPoll_3());

    }

    @NonNull
    @Override
    public PollHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_item, parent, false);
        return new PollHolder(v);
    }

    class PollHolder extends RecyclerView.ViewHolder{
       TextView poll_name;
       TextView poll_email;
       TextView poll_title;
       TextView poll_1;
       TextView poll_2;
       TextView poll_3;
        public PollHolder(@NonNull View itemView) {
            super(itemView);
            poll_name = itemView.findViewById(R.id.poll_name1);
            poll_email = itemView.findViewById(R.id.email_poll1);
            poll_title = itemView.findViewById(R.id.poll_title1);
            poll_1 = itemView.findViewById(R.id.option1_et_poll1);
            poll_2 = itemView.findViewById(R.id.option2_et_poll1);
            poll_3 = itemView.findViewById(R.id.option3_et_poll1);
        }
    }
}
