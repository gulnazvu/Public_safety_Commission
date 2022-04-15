package com.example.publicsafetycommission;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Permission;
import java.util.HashMap;
import java.util.Map;


public class registercomp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView selectImage, selectVideo, selectAudio, selectDoc;
    ImageView back, imageview, videoview, audioview, docview;
    EditText detail;
    Button add;
    TextView logout;
    Spinner myspinner,myspinnertwo;
    private int PICK_IMAGE_REQUEST = 1;
    int userid;
    Bitmap bitmap;
    String encodedImage, imageString;
    private static final String apiurl="https://ppsc.kp.gov.pk/Api_intern/complaint_register";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registercomp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        selectImage = findViewById(R.id.selectimage);
        selectVideo = findViewById(R.id.selectvideo);
        selectAudio = findViewById(R.id.selectaudio);
        selectDoc = findViewById(R.id.selectdoc);
        detail = findViewById(R.id.comdetail);
        add = findViewById(R.id.button2);
        logout = findViewById(R.id.logout);
        back = findViewById(R.id.btnBack);
        myspinner = findViewById(R.id.myspinner);
        myspinnertwo = findViewById(R.id.myspinnertwo);
        imageview = findViewById(R.id.imageview);
        videoview = findViewById(R.id.videoview);
        audioview = findViewById(R.id.audioview);
        docview = findViewById(R.id.docview);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.districts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner.setAdapter(adapter);
        myspinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adaptertwo = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adaptertwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinnertwo.setAdapter(adaptertwo);
        myspinnertwo.setOnItemSelectedListener(this);

        SharedPreferences spref = getSharedPreferences("YOUR_PREF_NAME", 0);
        userid = spref.getInt("user_id", 0);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registercomp.this,
                        HomeActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = spref.edit();
                editor.clear();
                Intent intent = new Intent(registercomp.this,LoginActivity.class);
                startActivity(intent);

            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             ShowFileChooser();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadtoserver();
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void ShowFileChooser() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("aspectX", 1);
        pickImageIntent.putExtra("aspectY", 1);
        pickImageIntent.putExtra("scale", true);
        pickImageIntent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Bitmap lastBitmap = null;
                lastBitmap = bitmap;
                //encoding image to string
                imageview.setImageBitmap(lastBitmap);
                imageString = getStringImage(lastBitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;


    }

    private void uploadtoserver()
    {
        final String district= String.valueOf(myspinner.getSelectedItemPosition());
        final String category = String.valueOf(myspinnertwo.getSelectedItemPosition());
        final String detaill = detail.getText().toString().trim();
        final String id = String.valueOf(userid);

        StringRequest request=new StringRequest(Request.Method.POST, apiurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Toast.makeText(getApplicationContext(),"FileUploaded Successfully",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> map=new HashMap<String, String>();
                map.put("district_id_fk", district);
                map.put("complaint_category_id_fk", category);
                map.put("complaint_detail", detaill);
                map.put("user_id", id);
                map.put("upload",imageString);
                return map;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        queue.add(request);


    }  // end of function uploadto DB


}