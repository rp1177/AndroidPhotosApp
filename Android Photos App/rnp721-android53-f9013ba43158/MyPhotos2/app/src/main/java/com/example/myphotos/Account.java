package com.example.myphotos;

//Aarushi Vashistha and Riddhi Patel

import java.util.ArrayList;
import java.util.HashMap;

public class Account {

    public ArrayList<AlbumT> albums;
    public ArrayList<String> tagTypes;
    public HashMap<String, Photo> photos;

    public Account () {
        albums = new ArrayList<>();
        photos = new HashMap<>();
        tagTypes = new ArrayList<>();
        tagTypes.add("person");
        tagTypes.add("location");
    }
}
