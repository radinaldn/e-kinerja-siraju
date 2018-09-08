package id.topapp.radinaldn.e_kinerja.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.github.badoualy.datepicker.MonthView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

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
import java.util.Locale;

import id.topapp.radinaldn.e_kinerja.R;
import id.topapp.radinaldn.e_kinerja.adapters.KerjaAdapter;
import id.topapp.radinaldn.e_kinerja.database.DatabaseHelper;
import id.topapp.radinaldn.e_kinerja.models.Kerja;

public class KerjaActivity extends AppCompatActivity {

    private static final String TAG = KerjaActivity.class.getSimpleName();
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private KerjaAdapter adapter;

    DatePickerTimeline timeline;

    private static Activity sContext;
    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    private List<Kerja> kerjaList = new ArrayList<>();

    public static DatabaseHelper db;
    public static File file;
    public static SimpleDateFormat dateFormatter;
    public static final String IMAGE_DIRECTORY = "GaleriEKinerja";
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private File sourceFile;
    public static File destFile;
    public static Uri imageCaptureUri;
    private static File actualImage;
    private static File compressedImage;
    Bitmap bmp;

    static String finalPhotoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sContext = KerjaActivity.this;
        setContentView(R.layout.activity_kerja);

        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_kerja);
        toolbar.setTitle(R.string.kerja);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        // click button back pada title bar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = new DatabaseHelper(this);

        timeline = findViewById(R.id.dpt_timeline);
        timeline.setDateLabelAdapter(new MonthView.DateLabelAdapter() {
            @Override
            public CharSequence getLabel(Calendar calendar, int index) {
                return Integer.toString(calendar.get(Calendar.MONTH) + 1) +"/" +(calendar.get(Calendar.YEAR) % 2000);
            }
        });



        // get current date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String [] cur_dates = (formatter.format(date)).split("-");
        int cur_year = Integer.parseInt(cur_dates[0]);
        int cur_month = Integer.parseInt(cur_dates[1]);
        int cur_day = Integer.parseInt(cur_dates[2]);
        System.out.println(cur_year+", "+cur_month+", "+cur_day);

        // init widget datetimeline
        timeline.setFirstVisibleDate(cur_year, Calendar.JULY, 1);
        timeline.setSelectedDate(cur_year, (cur_month-1), cur_day);
        timeline.setLastVisibleDate(2020, Calendar.JULY, 1);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipe_activity_histori_mengajar);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh, R.color.refresh1, R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // handling refresh recyclerview
            }
        });

        // data dummy


        String tahun = String.valueOf(cur_year);
        String bulan = ((cur_month < 10 ? "0" : "") +cur_month);
        String tanggal = (cur_day < 10 ? "0" : "") + cur_day;


        refreshUI(tahun, bulan, tanggal);

        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                //Toast.makeText(getApplicationContext(), "Sekarang tahun "+year+", bulan "+(month+1)+", tanggal "+day, Toast.LENGTH_LONG).show();

                int month2 = month+1;
                String tahun = String.valueOf(year);
                String bulan = ((month2 < 10 ? "0" : "") +month2);
                String tanggal = (day < 10 ? "0" : "") + day;


                refreshUI(tahun, bulan, tanggal);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCreate();
            }
        });
    }

    private void refreshUI(String tahun, String bulan, String tanggal){

        kerjaList.clear();

        kerjaList.addAll(db.getAllKerjaByTanggal(tahun+"-"+bulan+"-"+tanggal));
        adapter = new KerjaAdapter(kerjaList, KerjaActivity.this);
        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(KerjaActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < kerjaList.size(); i++) {
            Log.d(TAG, "onCreate getAllKerja: "+kerjaList.get(i).getNama_kegiatan());
        }
    }

    private void actionCreate(){

        alertDialogBuilder = new AlertDialog.Builder(KerjaActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_kerja, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.logo_performance);
        alertDialogBuilder.setTitle(R.string.form_kerja);

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
        final TextInputLayout til_waktu = dialogView.findViewById(R.id.til_waktu);
        final RadioGroup rg = dialogView.findViewById(R.id.rg);
        final RadioButton rb_tupoksi = dialogView.findViewById(R.id.rb_tupoksi);
        final RadioButton rb_nontupoksi = dialogView.findViewById(R.id.rb_nontupoksi);
        final RadioGroup rg_waktu = dialogView.findViewById(R.id.rg_waktu);
        final RadioButton rb_otomatis = dialogView.findViewById(R.id.rb_otomatis);
        final RadioButton rb_manual = dialogView.findViewById(R.id.rb_manual);
        final DatePicker dt_datepicker = dialogView.findViewById(R.id.dp_datepicker);

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
                    til_waktu.setVisibility(View.GONE);
                    waktu_otomatis[0] = true;

                } else if (rb_manual.isChecked()){
                    // jika manual di check
                    til_waktu.setVisibility(View.VISIBLE);
                    waktu_otomatis[0] = false;
                } else {
                    // jika otomatis di check
                    til_waktu.setVisibility(View.GONE);

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

                        String tanggal;
                        if (waktu_otomatis[0]){
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            tanggal = formatter.format(date);

                            // jika waktu manual
                        } else {
                                int month2 = (dt_datepicker.getMonth()+1);
                                int tahun = dt_datepicker.getYear();
                                int hari = dt_datepicker.getDayOfMonth();
                                String bulan = ((month2 < 10 ? "0" : "") +month2);
                                String tgl = (hari < 10 ? "0" : "") + hari;
                                tanggal = tahun+"-"+bulan+"-"+tgl;
                        }


                        String kerja = str_kerja[0];
                        String durasi = str_durasi;
                        String kategori = str_kategori[0];
                        String waktu = tanggal;
                        String foto = "";
                        String kuantitas = "";


                        Toast.makeText(getApplicationContext(), "kerja : "+str_kerja[0] +"\ndurasi : "+str_durasi+ "\nkategori : "+ str_kategori[0]+"\nwaktu : "+tanggal, Toast.LENGTH_LONG).show();


                        // entry data
                        long insert = db.insertKerja(kerja, durasi, kategori, waktu, foto, kuantitas);
                        Toast.makeText(getApplicationContext(), "Data berhasil di entry : "+insert, Toast.LENGTH_LONG).show();
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

    public static void takePhoto(final Activity activity, final String photo_name, Context context) {
        //
        Dexter.withActivity(sContext)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(sContext, "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            finalPhotoName = photo_name;
                            destFile = new File(KerjaActivity.file, photo_name);
                            imageCaptureUri = Uri.fromFile(destFile);

                            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                            intentCamera.putExtra("photo_name", photo_name);

                            activity.startActivityForResult(intentCamera, 101);
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
                        Toast.makeText(sContext, "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
        //

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user txo app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private static void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(sContext);
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
    private static void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", sContext.getPackageName(), null);
        intent.setData(uri);
        sContext.startActivityForResult(intent, 101);
    }


    public static void showError(String errorMessage) {
        Log.e(TAG, "showError: "+ errorMessage);
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
}
