package com.example.instagramdownloader.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramdownloader.Adapters.DownloadHistoryAdaptor;
import com.example.instagramdownloader.R;

public class DownloadFragment extends Fragment {
    private View rootView;
    RecyclerView rvhistoryItems;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_downloads, container, false);
            initialiseView();
            return rootView;
        } else {
            if (rootView.getParent() != null) {
                ((ViewGroup) rootView.getParent()).removeView(rootView);
            }
            return rootView;
        }
    }

    public void initialiseView(){
        rvhistoryItems=rootView.findViewById(R.id.rvhistoryItems);
        rvhistoryItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvhistoryItems.setItemAnimator(new DefaultItemAnimator());
        rvhistoryItems.setNestedScrollingEnabled(false);
        rvhistoryItems.setAdapter(new DownloadHistoryAdaptor());
    }
}
