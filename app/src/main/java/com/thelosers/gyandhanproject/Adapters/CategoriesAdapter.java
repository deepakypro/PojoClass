package com.thelosers.gyandhanproject.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thelosers.gyandhanproject.Model.Categories;
import com.thelosers.gyandhanproject.R;

import java.util.List;

/**
 * Created by deepak on 03/01/18.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    private List<Categories> mCategoriesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mCatogeryName;

        public MyViewHolder(View view) {
            super(view);
            mCatogeryName = (TextView) view.findViewById(R.id.catogies_adapter_name);
        }
    }

    public CategoriesAdapter(Context context, List<Categories> horizontalList) {
        this.context = context;
        this.mCategoriesList = horizontalList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.catories_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Categories detail = mCategoriesList.get(position);
        holder.mCatogeryName.setText(detail.getName());
    }

    @Override
    public int getItemCount() {
        return mCategoriesList.size();
    }
}
