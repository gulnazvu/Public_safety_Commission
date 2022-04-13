package com.example.publicsafetycommission;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ComplaintRegistrationNew extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView selectImage, selectVideo, selectAudio, selectDoc;
    ImageView back, imageview, videoview, audioview, docview;
    EditText detail;
    Button add;
    TextView errorText;
    TextView logout;
    Spinner myspinner,myspinnertwo;
    String path;
    File file1;
    public static final int SELECT_PICTURE = 1;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private File file;
    private static final String PREFIX = "stream2file";
    private static final String SUFFIX = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_registration_new);

        selectImage = findViewById(R.id.selectimage);
        selectVideo = findViewById(R.id.selectvideo);
        selectAudio = findViewById(R.id.selectaudio);
        selectDoc = findViewById(R.id.selectdoc);
        detail=findViewById(R.id.comdetail);
        add=findViewById(R.id.button2);
        logout=findViewById(R.id.logout);
        back=findViewById(R.id.btnBack);
        myspinner=findViewById(R.id.myspinner);
        myspinnertwo=findViewById(R.id.myspinnertwo);
        imageview=findViewById(R.id.imageview);
        videoview=findViewById(R.id.videoview);
        audioview=findViewById(R.id.audioview);
        docview=findViewById(R.id.docview);


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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    addComplaintt(myspinner.getSelectedItemPosition(),
                            myspinnertwo.getSelectedItemPosition(),
                            detail.getText().toString().trim(),
                            userid);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                pickImageLauncher.launch(intent);
            }
        });

        picImageLauncher();
    }

    private void pickImage() {
        Intent inte = new Intent();
        inte.setType("image/*");
        inte.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(inte, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Uri uri = data.getData();
            Context context = ComplaintRegistrationNew.this;
            path = RealPathUtil.getRealPath(context,uri);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageview.setImageURI(uri);
            Log.d("Complaint form ", ": "+path);
        }
    }
    public void addComplaintt(int userDistrict, int Category, String details, int userid) throws FileNotFoundException {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://ppsc.kp.gov.pk/Api_intern/")
                .addConverterFactory(GsonConverterFactory.create()).build();

//        File file = new File(path);
        addImagetoFile();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("Attachment",file.getName(),requestFile);

        RequestBody com_district = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userDistrict));
        RequestBody com_category = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Category));
        RequestBody com_detail = RequestBody.create(MediaType.parse("multipart/form-data"), details);
        RequestBody com_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userid));

        apiset apisetObject = retrofit.create(apiset.class);

        ///something wrong there

        Call<pojo> call= apisetObject.addComplaint(body,com_district, com_category,com_detail,com_id);

        call.enqueue(new Callback<pojo>() {
            @Override
            public void onResponse(Call<pojo> call, Response<pojo> response) {
                pojo object=response.body();
                if (response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), object.getResponseMsg(), Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(getApplicationContext(), object.getResponseMsg(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<pojo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void picImageLauncher(){
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                            // There are no request codes



                            Intent data=result.getData();

                            try {
                                Bitmap user_image_bm= MediaStore.Images.Media.getBitmap(
                                        getContentResolver(),data.getData());
                                imageview.setImageBitmap(user_image_bm);
                            } catch (IOException e) {
                                e.printStackTrace();


                            }

                        }
                    }
                });
    }


    public File addImagetoFile(){

        imageview.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        try {
            file = stream2file(getBitmapAsFile(bitmap));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }



    private File stream2file(InputStream in) throws IOException {
        File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(in, out);
        return tempFile;
    }

    private InputStream getBitmapAsFile(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }



}