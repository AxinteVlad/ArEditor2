package com.axintevlad.areditor2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.axintevlad.areditor2.adapter.FurnitureAdapter;
import com.axintevlad.areditor2.model.FurnitureObject;
import com.axintevlad.areditor2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FurnitureAdapter adapter;
    private List<FurnitureObject> furnitureList, furnitureListAll;
    private ProgressDialog progressDialog;
    public final static int AR_DONE = 100;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setBackgroundColor(getColor(R.color.colorPrimaryDark));

        recyclerView = findViewById(R.id.recycler_view);
        furnitureList = new ArrayList<>();
        furnitureListAll = new ArrayList<>();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> imageIds = new ArrayList<>();

                for (FurnitureObject item : furnitureList) {
                    if(item.isSelected()) {
                        imageIds.add(item.getPhotoId());
                    }
                }
                Intent intent = new Intent(HomeActivity.this, ARActivity.class);
                intent.putExtra("images", imageIds);
                startActivityForResult(intent, AR_DONE);
            }
        });

        prepareFurnitureItems();



    }


    private void prepareFurnitureItems() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("items");

        FurnitureObject furnitureObject1 = new FurnitureObject("Anim","anim","Anim",1200,4.4f,"andy_dance");
        ref.child("15").setValue(furnitureObject1);

        progressDialog = new ProgressDialog(this, R.style.MyProgressDialogTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                furnitureList.clear();
                furnitureListAll.clear();
                for (DataSnapshot valueRes : dataSnapshot.getChildren()) {
                    FurnitureObject item = valueRes.getValue(FurnitureObject.class);
                    furnitureListAll.add(item);
                    furnitureList.add(item);
                }

                adapter = new FurnitureAdapter(HomeActivity.this, furnitureList);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
                if (furnitureList.size() > 0) {
                    ((TextView) findViewById(R.id.no_item_found)).setVisibility(View.INVISIBLE);
                } else {
                    ((TextView) findViewById(R.id.no_item_found)).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(searchViewItem);

        androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.white));

        ImageView iconSearch = searchView.findViewById(R.id.search_button);
        iconSearch.setColorFilter(getResources().getColor(android.R.color.white));

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                if (adapter.getItemCount() == 0) {
                    ((TextView) findViewById(R.id.no_item_found)).setVisibility(View.VISIBLE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        MenuItemCompat.setOnActionExpandListener(searchViewItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ((TextView) findViewById(R.id.no_item_found)).setVisibility(View.INVISIBLE);

                adapter.restoreData();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}

