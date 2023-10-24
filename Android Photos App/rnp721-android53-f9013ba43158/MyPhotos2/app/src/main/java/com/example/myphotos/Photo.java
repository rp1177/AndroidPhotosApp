package com.example.myphotos;

//Aarushi Vashistha and Riddhi Patel

import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Photo implements Serializable {
    public String photoRef;
    public String cap;
    public HashMap<String,ArrayList<String>> tags;


    public Photo(String path, String caption) {
        cap = caption;
        photoRef = path;
        tags = new HashMap<>();
        tags.put("person",new ArrayList<>());
        tags.put("location", new ArrayList<>());
    }

    @Override
    public String toString(){
        return cap;
    }
}
