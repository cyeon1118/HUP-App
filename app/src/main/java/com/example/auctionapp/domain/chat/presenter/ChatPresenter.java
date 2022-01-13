package com.example.auctionapp.domain.chat.presenter;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.auctionapp.databinding.ActivityChatBinding;
import com.example.auctionapp.domain.chat.adapter.chatListAdapter;
import com.example.auctionapp.domain.chat.model.ChatModel;
import com.example.auctionapp.domain.chat.model.chatListData;
import com.example.auctionapp.domain.chat.view.ChatView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatPresenter implements Presenter{
    private ChatView view;
    private chatListData model;

    // Attributes
    private ChatView chatView;
    private ActivityChatBinding mBinding;
    private Context context;

    // Constructor
    public ChatPresenter(ChatView chatView, ActivityChatBinding mBinding, Context getApplicationContext){
        this.chatView = chatView;
        this.mBinding = mBinding;
        this.context = getApplicationContext;
    }

    //firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    //uid___수정필요
    String myuid = "상대방";
    String chatRoomUid;
    Long itemId;
    //chatting room list
    ArrayList<chatListData> chatroomList = new ArrayList<chatListData>();
    com.example.auctionapp.domain.chat.adapter.chatListAdapter chatListAdapter;


    @Override
    public void init() {
        database = FirebaseDatabase.getInstance("https://auctionapp-f3805-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = database.getReference();

        chatListAdapter = new chatListAdapter(context, chatroomList);
        mBinding.chattingRoomListView.setAdapter(chatListAdapter);
    }

    @Override
    public void getChatList() {
        databaseReference.child("chatrooms").orderByChild("users/"+myuid).equalTo(true).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatroomList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    chatRoomUid = dataSnapshot.getKey();
                    itemId = dataSnapshot.child("itemId/itemId").getValue(Long.class);
                    String temp = chatModel.users.toString();
                    String [] array = temp.split("=true");
                    for(int i=0; i<array.length; i++) {
                        // 내가 들어가 있는 채팅방의 상대방유저 id 가져오기
                        String usersIdStr = array[i];
                        if(usersIdStr.contains(myuid)) continue;
                        usersIdStr = usersIdStr.replace("{","");
                        usersIdStr = usersIdStr.replace("}","");
                        usersIdStr = usersIdStr.replace(",","");
                        usersIdStr = usersIdStr.replace(" ","");
                        if(!usersIdStr.equals(myuid) && !usersIdStr.equals("")) {
                            setChatList(chatRoomUid, usersIdStr, itemId);
                        }
                    }

                }
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void setChatList(String chatRoomUid, String oppId, Long itemIdL) {
        databaseReference.child("chatrooms/" + chatRoomUid + "/comments").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastChat = "";
                String lastChatTime = "";
                String lastChatTimeStr = "";
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) //마지막 채팅, 시간 가져오기
                {
                    lastChat = dataSnapshot.child("message").getValue(String.class);
                    lastChatTimeStr = dataSnapshot.child("timestamp").getValue(String.class);
                }
                String [] array = lastChatTimeStr.split("-");
                String month = array[1];
                String [] array2 = array[2].split("T");
                String [] array3 = array2[1].split(":");
                lastChatTime = month + "월 " + array2[0] + "일 " + array3[0] + ":" + array3[1];
                chatroomList.add(new chatListData(itemIdL, oppId, lastChatTime, lastChat));
                chatListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}