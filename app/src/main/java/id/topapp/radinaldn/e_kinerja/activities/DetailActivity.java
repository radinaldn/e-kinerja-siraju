package id.topapp.radinaldn.e_kinerja.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.topapp.radinaldn.e_kinerja.R;
import id.topapp.radinaldn.e_kinerja.database.DatabaseHelper;
import id.topapp.radinaldn.e_kinerja.models.Kerja;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    TextView tv_id, tv_durasi, tv_kategori, tv_kuantitas, tv_tanggal;
    ImageView iv_foto, iv_foto2;
    String nama_foto;

    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    public DatabaseHelper db;
    public File file;
    public SimpleDateFormat dateFormatter;
    public final String IMAGE_DIRECTORY = "GaleriEKinerja";
    public final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private File sourceFile;
    public File destFile;
    public Uri imageCaptureUri;
    private File actualImage;
    private File compressedImage;
    Bitmap bmp;

    String finalPhotoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        db = new DatabaseHelper(this);

        tv_id = findViewById(R.id.tv_id_kerja);
        iv_foto = findViewById(R.id.iv_foto);
        tv_durasi = findViewById(R.id.tv_durasi);
        tv_kategori = findViewById(R.id.tv_kategori);
        tv_kuantitas = findViewById(R.id.tv_kuantitas);
        tv_tanggal = findViewById(R.id.tv_tanggal);
        iv_foto2 = findViewById(R.id.iv_foto_kinerja);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        String id_kerja = getIntent().getStringExtra("id_kerja");
        String nama = getIntent().getStringExtra("nama_kegiatan");
        String durasi = getIntent().getStringExtra("durasi");
        String kategori = getIntent().getStringExtra("kategori");
        String tanggal = getIntent().getStringExtra("tanggal");
        String kuantitas = getIntent().getStringExtra("kuantitas");
        String foto = getIntent().getStringExtra("foto");

        //Picasso.get().load("file:///storage/emulated/0/GaleriEKinerja/"+foto).into(iv_foto2);

        tv_id.setText(id_kerja);
        tv_durasi.setText(durasi);
        tv_kategori.setText(kategori);
        tv_tanggal.setText(tanggal);
        tv_kuantitas.setText(kuantitas);


        Picasso.get().load("file:///storage/emulated/0/GaleriEKinerja/"+foto).into(iv_foto);

        Toast.makeText(getApplicationContext(), nama+durasi+kategori+tanggal+kuantitas+foto, Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);
        // Set title of Detail page
        collapsingToolbar.setTitle(nama);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Mengupdate data dengan id : " +tv_id.getText().toString(), Toast.LENGTH_SHORT).show();
                actionUpdate(tv_id.getText().toString());
            }
        });
    }

    private void actionUpdate(final String id) {
        alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_update_kinerja, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.logo_performance);
        alertDialogBuilder.setTitle(R.string.form_kinerja);

        final boolean[] waktu_otomatis = {true};

        final Spinner sp_kerja = dialogView.findViewById(R.id.sp_kerja);
        final String[] str_kerja = new String[1];

        List<String> listKerja = new ArrayList<>();

        listKerja.addAll(db.getAllNamaKegiatan());

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listKerja);

        sp_kerja.setAdapter(adapter);

        sp_kerja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_kerja[0] = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final EditText et_durasi = dialogView.findViewById(R.id.et_durasi);

        String [] durasi_menit = tv_durasi.getText().toString().split(" ");
        et_durasi.setText(durasi_menit[0]);

        final TextInputLayout til_waktu = dialogView.findViewById(R.id.til_waktu);
        final RadioGroup rg = dialogView.findViewById(R.id.rg);
        final RadioButton rb_tupoksi = dialogView.findViewById(R.id.rb_tupoksi);
        final RadioButton rb_nontupoksi = dialogView.findViewById(R.id.rb_nontupoksi);
        final RadioGroup rg_waktu = dialogView.findViewById(R.id.rg_waktu);
        final RadioButton rb_otomatis = dialogView.findViewById(R.id.rb_otomatis);
        final RadioButton rb_manual = dialogView.findViewById(R.id.rb_manual);
        final TextView tv_datepicker = dialogView.findViewById(R.id.tv_datepicker);
        tv_datepicker.setText(tv_tanggal.getText().toString());
        final CheckBox cb_datepicker = dialogView.findViewById(R.id.cb_datepicker);
        final DatePicker dp_datepicker = dialogView.findViewById(R.id.dp_datepicker);
        final EditText et_kuantitas = dialogView.findViewById(R.id.et_kuantitas);
        et_kuantitas.setText(tv_kuantitas.getText().toString());
        final ImageButton bt_kamera = dialogView.findViewById(R.id.bt_kamera);

        nama_foto = "kinerja-"+id+".png";

        cb_datepicker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    dp_datepicker.setVisibility(View.VISIBLE);
                } else {
                    dp_datepicker.setVisibility(View.GONE);
                }
            }
        });

        Calendar today = Calendar.getInstance();

        dp_datepicker.init(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = (monthOfYear+1);

                        String tahun = String.valueOf(year);
                        String bulan = ((month< 10 ? "0" : "") +month);
                        String tanggal = (dayOfMonth < 10 ? "0" : "") + dayOfMonth;

                        tv_datepicker.setText(tahun+"-"+bulan+"-"+tanggal);
                    }
                });


        bt_kamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(nama_foto);
                bt_kamera.setEnabled(false);
                bt_kamera.setBackgroundResource(R.drawable.bt_disabled);
            }
        });

        final String[] str_kategori = new String[1];

        // default
        str_kategori[0] = "Tupoksi";

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rb_tupoksi.isChecked()){
                    str_kategori[0] = "Tupoksi";
                } else if(rb_nontupoksi.isChecked()) {
                    str_kategori[0] = "Non Tupoksi";
                }
            }
        });



        rg_waktu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rb_otomatis.isChecked()){
                    // jika otomatis di check
                    //til_waktu.setVisibility(View.GONE);
                    waktu_otomatis[0] = true;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    tv_datepicker.setText(formatter.format(date));

                } else if (rb_manual.isChecked()){
                    // jika manual di check
                    //til_waktu.setVisibility(View.VISIBLE);
                    waktu_otomatis[0] = false;
                    int month2 = (dp_datepicker.getMonth()+1);
                    int tahun = dp_datepicker.getYear();
                    int hari = dp_datepicker.getDayOfMonth();
                    String bulan = ((month2 < 10 ? "0" : "") +month2);
                    String tgl = (hari < 10 ? "0" : "") + hari;
                    String final_tanggal = tahun+"-"+bulan+"-"+tgl;
                    tv_datepicker.setText(final_tanggal);
                } else {
                    // jika otomatis di check
                    //til_waktu.setVisibility(View.GONE);

                }
            }
        });



        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // action for click Simpan
                        //final String str_kerja = et_kerja.getText().toString();
                        final String str_durasi = et_durasi.getText().toString();

                        String kerja = str_kerja[0];
                        String durasi = str_durasi;
                        String kategori = str_kategori[0];
                        String waktu = tv_datepicker.getText().toString();
                        String foto = nama_foto;
                        String kuantitas = et_kuantitas.getText().toString();


                        Toast.makeText(getApplicationContext(), "kerja : "+kerja +"\ndurasi : "+str_durasi+ "\nkategori : "+ kategori +"\nwaktu : "+waktu+"\nkuantitas : "+kuantitas+"\nfoto : "+foto, Toast.LENGTH_LONG).show();


                        // entry data
                        db.updateKerja(new Kerja(id, kerja, durasi, kategori, waktu, foto, kuantitas));
                        Toast.makeText(getApplicationContext(), "Data berhasil di update", Toast.LENGTH_LONG).show();
                        finish();
                        getIntent().setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(getIntent());

                    }
                })

                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void takePhoto(final String photo_name) {
        Dexter.withActivity(DetailActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            finalPhotoName = photo_name;
                            destFile = new File(file, photo_name);
                            imageCaptureUri = Uri.fromFile(destFile);

                            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                            intentCamera.putExtra("photo_name", photo_name);

                            startActivityForResult(intentCamera, 101);
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode){
                case 101:
                    if (resultCode == Activity.RESULT_OK) {
//                        upflag = true;
                        Log.d(TAG + ".PICK_CAMERA_IMAGE", "Selected image uri path :" + imageCaptureUri);

                        // compress image
                        bmp = decodeFile(destFile, finalPhotoName);

                        // replace to the original image
                        saveFile(destFile);

                        Toast.makeText(getApplicationContext(), "Gambar berhasil di ambil, silahkan simpan", Toast.LENGTH_SHORT).show();
                        //ivImage.setImageURI(imageCaptureUri);

                    }
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private Bitmap decodeFile(File f, String final_photo_name) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());

        String name  = final_photo_name;
        destFile = new File(file, name);

        imageCaptureUri = Uri.fromFile(destFile);

        System.out.println("Selected image uri path decode:" + imageCaptureUri);

        return b;



    }

    // for saving image to storage
    private void saveFile(File destination) {
        if (destination.exists()) destination.delete();

        try {
            FileOutputStream out = new FileOutputStream(destFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 70, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
