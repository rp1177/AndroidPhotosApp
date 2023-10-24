package com.example.myphotos;

//Aarushi Vashistha and Riddhi Patel

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class searchcontroller extends Activity {

    public static Account acc;
    private Button back;
    private Button search;
    private ListView searchlist;
    private TextInputEditText input;
    private ArrayList<Photo> photoCap;
    private Thumbnail adapterP;
    private HashMap<String,Photo> listToPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        //setting up the buttons, input text and search
        back = findViewById(R.id.backsearch);
        search = findViewById(R.id.searchbutton);
        searchlist = findViewById(R.id.searchlist);
        input = findViewById(R.id.input);
        photoCap = new ArrayList<>();
        listToPhoto = new HashMap<>();

        for (AlbumT a : acc.albums) {
            for(Photo p : a.albumPhoto) {
                if (!photoCap.contains(p)) {
                    photoCap.add(p);
                    listToPhoto.put(p.cap + " - Path: " + p.photoRef,p);
                }
            }
        }
        adapterP = new Thumbnail(this, android.R.layout.simple_list_item_1, photoCap);
        searchlist.setAdapter(adapterP);


        back.setOnClickListener(view -> {
            sendUserDataBack();

        });

        search.setOnClickListener(view -> {
            if (input.getText().toString().isEmpty()) {
                makeAlerts("Please enter tag value pair to search for.");
                return;
            }
            if(input.getText().toString().startsWith("=")) {
                    makeAlerts("Please enter valid tag value pair.");
                    return;
                }

            if (input.getText().toString().contains(" AND ")) {
                String[] result = input.getText().toString().split(" AND ");
                String[] leftResult = result[0].split("=");
                String[] rightResult = result[1].split("=");

                adapterP.clear();
                adapterP.notifyDataSetChanged();

                for (AlbumT a : acc.albums) {
                    for (Photo p : a.albumPhoto) {
                        String s = p.cap + " - Path: " + p.photoRef;

                        if (!input.getText().toString().isEmpty() && (p.tags.get(leftResult[0]) == null || p.tags.get(rightResult[0]) == null)) {
                            adapterP.remove(p);
                            continue;
                        }
                        if (!input.getText().toString().isEmpty()) {
                            for (String tag : p.tags.get(leftResult[0])) {
                                if (tag.startsWith(leftResult[1])) {
                                    for (String t : p.tags.get(rightResult[0])) {
                                        if (t.startsWith(rightResult[1])) {
                                            if (adapterP.getPosition(p)==-1) {
                                                adapterP.add(p);
                                            }
                                        } else {
                                            adapterP.remove(p);
                                        }
                                    }
                                }
                            }

                        }
                        adapterP.notifyDataSetChanged();
                    }
                }
                return;
            }

            if (input.getText().toString().contains(" OR ")) {
                String[] result = input.getText().toString().split(" OR ");
                String[] leftResult = result[0].split("=");
                String[] rightResult = result[1].split("=");

               adapterP.clear();
                adapterP.notifyDataSetChanged();

                for (AlbumT a : acc.albums) {
                    for (Photo p : a.albumPhoto) {
                        String s = p.cap + " - Path: " + p.photoRef;
                        if (!input.getText().toString().isEmpty() && (p.tags.get(leftResult[0]) == null && p.tags.get(rightResult[0]) == null)) {
                            adapterP.remove(p);
                            continue;
                        }
                        if (p.tags.get(leftResult[0]) == null) {
                            p.tags.put(leftResult[0], new ArrayList<>());
                        }
                        if (p.tags.get(leftResult[1]) == null) {
                            p.tags.put(leftResult[1], new ArrayList<>());
                        }

                            if (!input.getText().toString().isEmpty()) {
                                for (String tag : p.tags.get(leftResult[0])) {
                                    if (tag.startsWith(leftResult[1])) {
                                        if (adapterP.getPosition(p)==-1) {
                                            adapterP.add(p);
                                        }
                                    } else {
                                       for (String t : p.tags.get(rightResult[0])) {
                                           if (t.startsWith(rightResult[1])) {
                                               if (adapterP.getPosition(p)==-1) {
                                                   adapterP.add(p);
                                               }
                                           } else {
                                               adapterP.remove(p);
                                           }
                                       }
                                    }
                                }
                        }
                    }
                    adapterP.notifyDataSetChanged();
                }
                return;
            }

            String[] result = input.getText().toString().split("=");
            adapterP.clear();
            adapterP.notifyDataSetChanged();
            for (AlbumT a : acc.albums) {
                for (Photo p : a.albumPhoto) {

                    String s = p.cap + " - Path: " + p.photoRef;
                    if (!input.getText().toString().isEmpty() && p.tags.get(result[0]) == null) {
                        adapterP.remove(p);
                        continue;
                    }

                    if (!input.getText().toString().isEmpty()) {
                        for (String tag : p.tags.get(result[0])) {
                            if (tag.startsWith(result[1])) {
                                if (adapterP.getPosition(p)==-1) {
                                    adapterP.add(p);
                                }
                            } else {
                                adapterP.remove(p);
                            }
                        }
                    }
                }
                adapterP.notifyDataSetChanged();
            }

        });

    }


    private void sendUserDataBack() {
        Intent send = new Intent(searchcontroller.this, Album.class);
        writeAcc();
        startActivity(send);
    }

    public void writeAcc() {
        try {
            String p = this.getApplicationInfo().dataDir + "/appdata.dat";
            FileOutputStream fos = new FileOutputStream(p);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(acc);
            fos.close();
            oos.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void readAcc(){
        try {
            String p = this.getApplicationInfo().dataDir + "/appdata.dat";
            FileInputStream fis = new FileInputStream(p);
            ObjectInputStream ois = new ObjectInputStream(fis);
            acc = (Account) ois.readObject();
            fis.close();
            ois.close();
        } catch (Exception ignored) {
            ;
        }
    }
    private void makeAlerts(String Context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(Context);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}