package com.example.firebaseupload.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebaseupload.AddStoryActivity;
import com.example.firebaseupload.Model.Story;
import com.example.firebaseupload.Model.User;
import com.example.firebaseupload.R;
import com.example.firebaseupload.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Context mContext;
    private List<Story> mStory;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, parent, false);
            return new StoryAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Story story = mStory.get(i);

        userInfo(viewHolder, story.getUserId(), i);

        if (viewHolder.getAdapterPosition() != 0) {
            seenStory(viewHolder, story.getUserId());
        }

        if (viewHolder.getAdapterPosition() == 0) {
            myStory(viewHolder.addStory_text, viewHolder.story_plus, false);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.getAdapterPosition() == 0) {
                    myStory(viewHolder.addStory_text, viewHolder.story_photo, true);
                } else {
                    // TODO: go to story
                    Intent intent = new Intent(mContext, StoryActivity.class);
                    intent.putExtra("userId", story.getUserId());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView story_photo, story_plus, story_photo_seen;
        public TextView story_username, addStory_text;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_username = itemView.findViewById(R.id.story_username);
            story_plus = itemView.findViewById(R.id.story_plus);
            addStory_text = itemView.findViewById(R.id.addStory_text);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    private void userInfo(final ViewHolder viewHolder, String userId, final int pos) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
               // Glide.with(mContext).load(user.getImageUrl()).into(viewHolder.story_photo);
                if (pos != 0) {
                    Glide.with(mContext).load(user.getImageUrl()).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                long timeCurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    if (timeCurrent > story.getTimeStart() && timeCurrent < story.getTimeEnd()) {
                        count++;
                    }
                }

                if (click) {
                    if (count > 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View Event",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO: go to story
                                        Intent intent = new Intent(mContext, StoryActivity.class);
                                        intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Event",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                    }
                } else {
                    if (count > 0) {
                        textView.setText("My story");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Add story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void seenStory(final ViewHolder viewHolder, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.child("views")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .exists() && System.currentTimeMillis() < snapshot.getValue(Story.class).getTimeEnd()) {
                        i++;
                    }
                }

                if (i > 0) {
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                } else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



























}
