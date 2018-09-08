package id.topapp.radinaldn.e_kinerja.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.topapp.radinaldn.e_kinerja.R;
import id.topapp.radinaldn.e_kinerja.adapters.KerjaAdapter;
import id.topapp.radinaldn.e_kinerja.adapters.KinerjaAdapter;
import id.topapp.radinaldn.e_kinerja.database.DatabaseHelper;
import id.topapp.radinaldn.e_kinerja.models.Kerja;

/**
 * Created by radinaldn on 03/08/18.
 */

public class KinerjaActivity extends AppCompatActivity {

    private static final String TAG = KerjaActivity.class.getSimpleName();
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private KinerjaAdapter adapter;

    DatePickerTimeline timeline;

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
    private static File destFile;
    private static Uri imageCaptureUri;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kinerja);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_kerja);
        toolbar.setTitle(R.string.kinerja_saya);
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

    }

    private void refreshUI(String tahun, String bulan, String tanggal){

        kerjaList.clear();

        kerjaList.addAll(db.getAllKinerjaByTanggal(tahun+"-"+bulan+"-"+tanggal));
        adapter = new KinerjaAdapter(kerjaList, KinerjaActivity.this);
        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(KinerjaActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < kerjaList.size(); i++) {
            Log.d(TAG, "onCreate getAllKinerja: "+kerjaList.get(i).getNama_kegiatan());
        }
    }


}
