package com.example.publicsafetycommission;



import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ComplaintRegistrationNew extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView selectImage, selectVideo, selectAudio, selectDoc;
    ImageView back, imageview, videoview, audioview, docview;
    EditText detail;
    Button add;
    TextView errorText;
    TextView logout;
    Spinner myspinner,myspinnertwo;
    String district, category, detaill,id;
    public static final int SELECT_PICTURE = 1;
    String imageString;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_registration_new);

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
        int userid = spref.getInt("user_id", 0);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComplaintRegistrationNew.this,
                        HomeActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = spref.edit();
                editor.clear();
                Intent intent = new Intent(ComplaintRegistrationNew.this,
                        LoginActivity.class);
                startActivity(intent);

            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        district= String.valueOf(myspinner.getSelectedItemPosition());
                        category = String.valueOf(myspinnertwo.getSelectedItemPosition());
                        detaill=detail.getText().toString().trim();
                        id = String.valueOf(userid);

                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(ComplaintRegistrationNew.this);
                    String URL = "https://ppsc.kp.gov.pk/Api_intern/complaint_register";
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("district_id_fk", district);
                    jsonBody.put("complaint_category_id_fk", category);
                    jsonBody.put("complaint_detail", detaill);
                    jsonBody.put("user_id", id);

                    jsonBody.put("Image", imageString);
                    final String requestBody = jsonBody.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY", response);
                            Toast.makeText(ComplaintRegistrationNew.this, "Volley response:" +response, Toast.LENGTH_SHORT).show();

                        }
                    }, error -> Log.e("VOLLEY", error.toString())) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                return null;
                            }
                        }

                        @Override
                        protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };

                    requestQueue.add(stringRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void showFileChooser() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }



}