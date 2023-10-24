package com.example.myphotos;

//Aarushi Vashistha and Riddhi Patel

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class DisplayPhoto extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public static Photo currentPhoto;
    public static AlbumT currentAlbum;
    public static Account acc;


    private Button backButton;
    private Button nextButton;
    private Button delete;
    private Button backtoPhotolist;
    private Spinner spinner;

    private TextInputEditText textinput;

    private int selectedIndex = -1;
    private ListView taglist;
    private ArrayAdapter<String> adapter3;
    private ArrayAdapter<String> spinAdapter;
    private ArrayList<String> photoTags; //Tag arraylist
    private ImageView display;
    String[] tagOptions = {"No Selection","Location", "Person"};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //setting up the buttons, listview, etc..
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photodisplay);


        backButton = findViewById(R.id.backDisplayButton);
        nextButton = findViewById(R.id.forwardDisplayButton);
        backtoPhotolist = findViewById(R.id.backtoPhotolist);
        delete = findViewById(R.id.deletetag);
        taglist = findViewById(R.id.taglist);
        display = findViewById(R.id.photoDisplay);
        textinput = findViewById(R.id.input2);
        spinner = findViewById(R.id.spinner);



        //Set up textbox input
        textinput = findViewById(R.id.input2);
        photoTags = new ArrayList<>();



        for (String key : currentPhoto.tags.keySet()) {
            for (String val : currentPhoto.tags.get(key)) {
                photoTags.add(key + "=" + val);
            }
        }

        adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, photoTags);
        spinner.setOnItemSelectedListener(this);
        spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,tagOptions);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taglist.setAdapter(adapter3);
        spinner.setAdapter(spinAdapter);
        spinner.setSelection(0);

        //Display current photo selected
        display.setImageURI(Uri.parse(currentPhoto.photoRef));


        //When using the back button on the top left corner:
        backtoPhotolist.setOnClickListener(view -> {
            sendUserDataBack();
        });

        delete.setOnClickListener(view -> {
            if (taglist.getSelectedItem()==null) {
                makeAlerts("Please select a tag to delete.");
                return;
            }
        });
        //deleting tag
        taglist.setOnItemClickListener((adapter, v, position, id) -> {
            delete.setOnClickListener(view->{
                String removeTag = taglist.getItemAtPosition(position).toString();
                String[] temp = removeTag.split("=");
                currentPhoto.tags.get(temp[0]).remove(temp[1]);
                photoTags.remove(temp[0]+"="+temp[1]);
                writeAcc();
                adapter3.notifyDataSetChanged();
            });
        });


        //When using back button in the middle that goes to prev photo

        backButton.setOnClickListener(view -> {
            currentPhoto = currentAlbum.albumPhoto.get(((currentAlbum.albumPhoto.indexOf(currentPhoto) - 1) + currentAlbum.albumPhoto.size()) % currentAlbum.albumPhoto.size());
           adapter3.clear();
            for (String key : currentPhoto.tags.keySet()) {
                for (String val : currentPhoto.tags.get(key)) {
                    photoTags.add(key + "=" + val);
                }
            }
            adapter3.notifyDataSetChanged();
            display.setImageURI(Uri.parse(currentPhoto.photoRef));

        });

        //When using next button in middle- going to next photo
        nextButton.setOnClickListener(view -> {
            currentPhoto = currentAlbum.albumPhoto.get((currentAlbum.albumPhoto.indexOf(currentPhoto) + 1) % currentAlbum.albumPhoto.size());
            adapter3.clear();
            for (String key : currentPhoto.tags.keySet()) {
                for (String val : currentPhoto.tags.get(key)) {
                    photoTags.add(key + "=" + val);
                }
            }
            adapter3.notifyDataSetChanged();
            display.setImageURI(Uri.parse(currentPhoto.photoRef));
        });

    }

    private void sendUserDataBack() {
        Intent send = new Intent(DisplayPhoto.this,InsideAlbum.class);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = tagOptions[i].toLowerCase();
        for(String tagType : acc.tagTypes) {
            if (textinput.getText().toString().isEmpty() && !item.equals("no selection")) {
                makeAlerts("Please specify tag value.");
                spinner.setSelection(0);
                spinAdapter.notifyDataSetChanged();
                return;
            }
            if (tagType.equals(item) && !textinput.getText().toString().isEmpty()) {
                if(textinput.getText().toString().contains("=")) {
                    makeAlerts("No '=' in tag value.");
                    spinner.setSelection(0);
                    spinAdapter.notifyDataSetChanged();
                    return;
                }
                if (tagType.equals("location") && currentPhoto.tags.get(tagType).size()>0) {
                    makeAlerts("Location tag already exists.");
                    spinner.setSelection(0);
                    spinAdapter.notifyDataSetChanged();
                    return;
                }
                if (currentPhoto.tags.get(tagType).contains(textinput.getText().toString())) {
                    makeAlerts("Tag already exists.");
                    spinner.setSelection(0);
                    spinAdapter.notifyDataSetChanged();
                    return;
                }
                currentPhoto.tags.get(tagType).add(textinput.getText().toString());
                photoTags.add(tagType + "=" + textinput.getText().toString());
                writeAcc();
                adapter3.notifyDataSetChanged();
                spinner.setSelection(0);
                spinAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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