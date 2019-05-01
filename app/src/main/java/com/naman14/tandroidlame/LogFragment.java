/*
 * Copyright (C) 2016 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.tandroidlame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class LogFragment extends Fragment {

    LogAdapter adapter;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_log, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.logrecyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        adapter = new LogAdapter(new ArrayList<String>());
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public void addLog(final String log) {
        adapter.addLog(log);
    }

    private class LogAdapter extends RecyclerView.Adapter<LogAdapter.ItemHolder> {

        public List<String> logList;

        public LogAdapter(List<String> list) {
            this.logList = list;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_log_item, null);
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(ItemHolder itemHolder, int i) {
            itemHolder.logText.setText(logList.get(i));
        }

        @Override
        public int getItemCount() {
            return (null != logList ? logList.size() : 0);
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            TextView logText;

            public ItemHolder(View view) {
                super(view);
                logText = (TextView) view.findViewById(R.id.text);
            }

        }

        public void addLog(String log) {
            logList.add(log);
            notifyDataSetChanged();
            recyclerView.scrollToPosition(logList.size() - 1);
        }

    }


}
