package com.second.project.heysched.plan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.second.project.heysched.R;
import com.second.project.heysched.chatting.chat.MessageActivity;
import com.second.project.heysched.chatting.model.Usermodel;

import java.util.ArrayList;
import java.util.List;

public class InviteFriendActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        RecyclerView recyclerView=findViewById(R.id.selectFriendActivity_recyclerview);
        recyclerView.setAdapter(new SelectFriendRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button button=findViewById(R.id.selectFriendActivity_button);
        button.setText("초대하기");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 친구 초대 버튼 클릭 시
            }
        });
    }
    class SelectFriendRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<Usermodel> users;
        public SelectFriendRecyclerViewAdapter() {
            users=new ArrayList<>();
            final String myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //누적데이터를 없애기 위해서 클리어 (지웠다가 확인해보면 정확히 알수있다.)
                    users.clear();

                    //서버에서 온 데이터를 담는다.
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                        //내 아이디는 친구리스트에 안담는것
                        Usermodel usermodel=snapshot.getValue((Usermodel.class));
                        if(usermodel.uid.equals(myUid)){
                            continue;
                        }

                        users.add(snapshot.getValue(Usermodel.class));
                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_select,parent,false);


            return new SelectFriendRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

            Glide.with(holder.itemView.getContext()).load(users.get(position).profileImageUrl).apply(new RequestOptions().circleCrop()).into(((SelectFriendRecyclerViewAdapter.CustomViewHolder)holder).imageView);

            ((SelectFriendRecyclerViewAdapter.CustomViewHolder)holder).textView.setText(users.get(position).userName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(InviteFriendActivity.this,"position: "+position,Toast.LENGTH_SHORT).show();
                    /*Intent intent=new Intent(v.getContext(), MessageActivity.class);
                    // 위에 인텐트 선언했으므로 클릭한 상대방의 채팅방(MessageActivity)으로 옮긴다
                    intent.putExtra("destinationUid",usermodels.get(position).uid);

                    ActivityOptions activityOptions=null;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
                        activityOptions=ActivityOptions.makeCustomAnimation(v.getContext(),R.anim.fromright,R.anim.toleft);
                        startActivity(intent,activityOptions.toBundle());
                    }*/

                }
            });
            if (users.get(position).comment!=null){
                ((SelectFriendRecyclerViewAdapter.CustomViewHolder) holder).textView_comment.setText(users.get(position).comment);
            }else{
                ((SelectFriendRecyclerViewAdapter.CustomViewHolder) holder).textView_comment.setText("");
            }

            ((SelectFriendRecyclerViewAdapter.CustomViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //체크된 상태
                    if(isChecked){
                        //users.get(position).chk==true;
                        //chatModel.users.put(usermodels.get(position).uid,true);

                    }else{//체크 취소 상태
                        //chatModel.users.remove(usermodels.get(position));
                    }
                }

            });
            Log.d("finishh","확인");
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_comment;
            public CheckBox checkBox;

            public CustomViewHolder(View view) {
                super(view);
                imageView=view.findViewById(R.id.frienditem_imageview);
                textView=view.findViewById(R.id.frienditem_textview);
                textView_comment=view.findViewById(R.id.frienditem_textview_comment);
                checkBox=view.findViewById(R.id.friendItem_checkbox);
            }
        }
    }
}
