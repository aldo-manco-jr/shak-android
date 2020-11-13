package org.aldofrank.shak.people.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.aldofrank.shak.R;
import org.aldofrank.shak.authentication.models.User;

import java.util.LinkedList;

public class PeopleList extends AppCompatActivity {

    ListView peopleListView;
    LinkedList<User> peopleArray;
    ArrayAdapter<User> peopleArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_list);

        peopleListView = findViewById(R.id.peopleListView);
        peopleArray = new LinkedList<>();
        peopleArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, peopleArray);
        peopleListView.setAdapter(peopleArrayAdapter);


    }
}
