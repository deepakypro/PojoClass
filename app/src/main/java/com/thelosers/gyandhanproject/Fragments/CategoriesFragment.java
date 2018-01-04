package com.thelosers.gyandhanproject.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thelosers.gyandhanproject.Adapters.CategoriesAdapter;
import com.thelosers.gyandhanproject.Model.Categories;
import com.thelosers.gyandhanproject.Model.Category;
import com.thelosers.gyandhanproject.Model.CategoryModel;
import com.thelosers.gyandhanproject.MyApp;
import com.thelosers.gyandhanproject.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thelosers.gyandhanproject.Utils.Urls.zomatoUrl;


public class CategoriesFragment extends Fragment {

    private Gson gson;
    private List<Categories> mArrayList;
    private RecyclerView mRecyclerView;
    private CategoriesAdapter mCategoriesAdapter;

    public CategoriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArrayList = new ArrayList<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
        fetchServerData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.category_recyclerview);
        LinearLayoutManager horizontalLayoutManagaer = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(horizontalLayoutManagaer);
        mRecyclerView.setHasFixedSize(true);
    }


    private void fetchServerData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, zomatoUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", "response => " + response);
                        List<CategoryModel> categoryModels = Arrays.asList(gson.fromJson(response, CategoryModel.class));
                        for (CategoryModel categoryModel : categoryModels) {
                            for (Category category : categoryModel.getCategories()) {
                                Categories categories = new Categories();
                                categories.setId(category.getCategories().getId());
                                categories.setName(category.getCategories().getName());
                                mArrayList.add(categories);
                            }
                        }

                        setDataToRecyclerView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user-key", "a9a7380677025f5b302c3aa587e7877d");
                params.put("Accept", "application/json");

                return params;
            }
        };


        MyApp.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

    private void setDataToRecyclerView() {
        if (mArrayList.size() > 0 && !mArrayList.isEmpty()) {
            mCategoriesAdapter = new CategoriesAdapter(getActivity(), mArrayList);
            mRecyclerView.setAdapter(mCategoriesAdapter);
            mCategoriesAdapter.notifyDataSetChanged();
        }
    }

}


