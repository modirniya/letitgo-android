package app.crows.letitgo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ForumFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String Emotion = "Emotion";
    static final String Lang = "Language";

    private String mLastMsgId;

    private String mEmotion, mLang, mDraft = "";
    private String sYear, sDate;

    private static final String TAG = "ForumFragment";

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayout linearLayLoading;

    private FirebaseFunctions mFunctions;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder>
            mFirebaseAdapter;
    private Message mLastMessage;

    private enum functions {
        newEntry, newLike, newReport
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        //ImageView messageImageView;
        private TextView tvSender, tvMessage, tvLikeCount;
        private ImageView ivReport, ivLike;
        //CircleImageView messengerImageView;


        public MessageViewHolder(View v) {
            super(v);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            //messageImageView = itemView.findViewById(R.id.messageImageView);
            tvSender = itemView.findViewById(R.id.tvSender);
            //messengerImageView = itemView.findViewById(R.id.messengerImageView);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivReport = itemView.findViewById(R.id.ivReport);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);

        }
    }


    public ForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param emotion Parameter 1.
     * @param lang    Parameter 2.
     * @return A new instance of fragment FroumFragment.
     */
    public static ForumFragment newInstance(String emotion, String lang) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putString(Emotion, emotion);
        args.putString(Lang, lang);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmotion = getArguments().getString(Emotion);
            mLang = getArguments().getString(Lang);
        }

    }

    private void getDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd");
        sYear = yearFormat.format(date);
        sDate = dateFormat.format(date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDate();
        mFunctions = FirebaseFunctions.getInstance();
        final View view = inflater.inflate(R.layout.fragment_forum, container, false);
        // Inflate the layout for this fragment
        mMessageRecyclerView = view.findViewById(R.id.messageRecyclerView);
        linearLayLoading = view.findViewById(R.id.linearLayLoading);
        // Admob init
        MobileAds.initialize(container.getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<Message> parser = new SnapshotParser<Message>() {
            @Override
            public Message parseSnapshot(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                linearLayLoading.setVisibility(View.GONE);
                if (message != null)
                    message.setId(dataSnapshot.getKey());
                return message;
            }
        };
        DatabaseReference messagesRef = mFirebaseDatabaseReference.child("Responses").child(mLang)
                .child(mEmotion);
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(messagesRef, parser)
                        .build();


        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            final int position,
                                            final Message message) {
                final String uId = FirebaseAuth.getInstance().getUid();
                if (mLastMsgId != null)
                    if (mLastMsgId.equals(message.getId())) {
                        viewHolder.ivLike.setColorFilter(Color.parseColor("#FF0000"));
                        viewHolder.ivLike.setEnabled(false);
                    }
                if (message.getText() != null) {
                    viewHolder.tvMessage.setText(message.getText());
                    viewHolder.tvMessage.setVisibility(TextView.VISIBLE);
                    viewHolder.tvLikeCount.setText(String.valueOf(message.getVote_count()));

                    if (message.getId().equals(uId)) {
                        viewHolder.tvSender.setText("You said:");
                        viewHolder.tvLikeCount.setVisibility(View.VISIBLE);
                        viewHolder.ivReport.setVisibility(View.GONE);
                    } else {
                        viewHolder.tvSender.setText(String.format("%s said:", message.getName()));
                        viewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewHolder.ivLike.setColorFilter(Color.parseColor("#FF0000"));
                                viewHolder.ivLike.setEnabled(false);
                                Map<String, Object> data = new HashMap<>();
                                data.put("language", mLang);
                                data.put("topic", mEmotion);
                                data.put("target_uid", message.getId());
                                callFunctions(functions.newLike.toString(), data);
                                mLastMsgId = message.getId();
                            }
                        });

                        viewHolder.ivReport.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // v.setEnabled(false);
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View subView = inflater.inflate(R.layout.send_report, null);
                                final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                                builder.setTitle("Report comment");
                                builder.setView(subView);
                                final AlertDialog dialog = builder.create();
                                initReportDialog(subView, message.getId(), dialog);
                                dialog.show();
                            }
                        });
                    }
                }

            }

            private void initReportDialog(View subView, final String target, final AlertDialog dialog) {
                final RadioGroup rgReportCat = subView.findViewById(R.id.rgReportCat);
                final Button btSubmit = subView.findViewById(R.id.btSubmit);
                btSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int iCategory;
                        switch (rgReportCat.getCheckedRadioButtonId()) {
                            case R.id.rbCat1:
                                iCategory = 1;
                                break;
                            case R.id.rbCat2:
                                iCategory = 2;
                                break;
                            case R.id.rbCat3:
                                iCategory = 3;
                                break;
                            case R.id.rbCat4:
                                iCategory = 4;
                                break;
                            default:
                                iCategory = 0;
                        }
                        if (iCategory != 0) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("language", mLang);
                            data.put("topic", mEmotion);
                            data.put("category", String.valueOf(iCategory));
                            data.put("target_uid", target);
                            Toast.makeText(getContext(), "This comment been flagged.", Toast.LENGTH_SHORT).show();
                            callFunctions(functions.newReport.toString(), data);
                        }
                        dialog.dismiss();
                    }
                });

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();

                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        Button btCreateEmotion = view.findViewById(R.id.btCreateEmotion);
        btCreateEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createResponse();
            }

            private void createResponse() {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View subView = inflater.inflate(R.layout.add_log, null);
                final EditText etContent = subView.findViewById(R.id.etContent);
                final Button btShare = subView.findViewById(R.id.btShare);
                final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                etContent.setText(mDraft);
                builder.setView(subView);
                final AlertDialog dialog = builder.create();
                btShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etContent.length() > 999) {
                            etContent.setError("Input is too long(more than 999 characters)");

                        } else if (etContent.length() < 5) { //TODO Change 5 to 50
                            etContent.setError("Input is too short(less than 50 characters)");
                            //Toast.makeText(getContext(), "Input is too short(less than 50 characters)", Toast.LENGTH_SHORT).show();
                        } else {
                            // Create the arguments to the callable function.
                            Map<String, Object> data = new HashMap<>();
                            data.put("language", mLang);
                            data.put("topic", mEmotion);
                            data.put("entry", etContent.getText().toString());
                            callFunctions(functions.newEntry.toString(), data);
                            dialog.dismiss();
                            /*String uID = FirebaseAuth.getInstance().getUid();
                            String pushKey = mFirebaseDatabaseReference.child("likes").push().getKey();
                            mFirebaseDatabaseReference.child("likes").child(pushKey).child("count").setValue(0);
                            Message message = new
                                    Message(etContent.getText().toString(),
                                    "Someone else", pushKey);
                            mFirebaseDatabaseReference.child(mLang).child(mEmotion)
                                    .child(sYear).child(sDate)
                                    .child(uID).setValue(message);*/
                            mDraft = "";
                        }
                    }
                });
                dialog.show();

            }
        });
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        return view;
    }


    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }

    private Task<String> callFunctions(String funcName, Map<String, Object> data) {
        return mFunctions
                .getHttpsCallable(funcName)
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();

                        return result;
                    }
                });
    }
}
