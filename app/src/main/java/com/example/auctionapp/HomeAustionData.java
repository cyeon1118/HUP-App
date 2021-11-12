//package com.example.auctionapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class HomeAustionData extends Fragment {
//    ViewGroup viewGroup;
//
//    HomeAustionDataAdapter adapterAuction;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_itemlist, container, false);
//
//        init();
//        getData();
//
//        return viewGroup;
//    }
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    private void init(){
//        RecyclerView recyclerView = viewGroup.findViewById(R.id.recyclerView);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        adapterAuction = new HomeAustionDataAdapter();
//        recyclerView.setAdapter(adapterAuction);
//
//        adapterAuction.setOnItemClickListener(new ItemDataAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
////                Toast.makeText(getContext(), "pos: " + position, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getContext(), ItemDetail.class);
//                startActivity(intent);
//            }
//        }) ;
//
//        //구분선
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(getContext()).getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
//    }
//
//    private void getData(){
//        //일단 레이아웃만
//        ItemData data = new ItemData(R.drawable.rectangle, "아이폰 11 프로 256GB", 530000, "14:46", 30, 4);
//        adapterAuction.addItem(data);
//        data = new ItemData(R.drawable.rectangle, "아이폰 11 프로 64GB", 500000, "82:33", 32, 5);
//        adapterAuction.addItem(data);
//        data = new ItemData(R.drawable.rectangle, "아이폰 11 미니 A급 256GB", 420000, "34:07", 23, 1);
//        adapterAuction.addItem(data);
//        data = new ItemData(R.drawable.rectangle, "아이폰 11 미니 256GB 레드 미개봉 중고", 552000, "34:04", 27, 2);
//        adapterAuction.addItem(data);
//
//    }
//}