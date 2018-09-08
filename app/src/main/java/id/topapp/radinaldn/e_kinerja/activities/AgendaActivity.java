package id.topapp.radinaldn.e_kinerja.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.github.badoualy.datepicker.MonthView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.topapp.radinaldn.e_kinerja.R;
import id.topapp.radinaldn.e_kinerja.adapters.AgendaAdapter;
import id.topapp.radinaldn.e_kinerja.adapters.KinerjaAdapter;
import id.topapp.radinaldn.e_kinerja.database.DatabaseHelper;
import id.topapp.radinaldn.e_kinerja.models.Agenda;

public class AgendaActivity extends AppCompatActivity {
    private static final String TAG = AgendaActivity.class.getSimpleName();
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private AgendaAdapter adapter;

    DatePickerTimeline timeline;

    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    private List<Agenda> agendaList = new ArrayList<>();

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
        setContentView(R.layout.activity_agenda);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_agenda);
        toolbar.setTitle(R.string.agenda_saya);
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
        swipeRefreshLayout = findViewById(R.id.swipe_activity_agenda);
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

        agendaList.clear();


        agendaList.addAll(db.getAllAgendaByTanggal(tahun+"-"+bulan+"-"+tanggal));
        adapter = new AgendaAdapter(agendaList);
        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(AgendaActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < agendaList.size(); i++) {
            Log.d(TAG, "onCreate getAllKinerja: "+ agendaList.get(i).getNama_agenda());
        }
    }

    private void actionCreate(){
        alertDialogBuilder = new AlertDialog.Builder(AgendaActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_agenda, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.logo_performance);
        alertDialogBuilder.setTitle(R.string.form_agenda);

        final EditText et_nama_agenda = dialogView.findViewById(R.id.et_nama_agenda);
        final EditText et_ket = dialogView.findViewById(R.id.et_ket);
        final CheckBox cb_datepicker = dialogView.findViewById(R.id.cb_datepicker);
        final DatePicker dp_datepicker = dialogView.findViewById(R.id.dp_datepicker);
        final TextView tv_datepicker  =dialogView.findViewById(R.id.tv_datepicker);
        final CheckBox cb_timepicker = dialogView.findViewById(R.id.cb_timepicker);
        final TimePicker tp_timepicker = dialogView.findViewById(R.id.tp_timepicker);
        final TextView tv_timepicker  =dialogView.findViewById(R.id.tv_timepicker);
        tv_timepicker.setText("00:00:00");

        Calendar hari_ini = Calendar.getInstance();

        int month = (hari_ini.get(Calendar.MONTH)+1);

        String tahun = String.valueOf(hari_ini.get(Calendar.YEAR));
        String bulan = ((month < 10 ? "0" : "") +month);
        String tanggal = (hari_ini.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + hari_ini.get(Calendar.DAY_OF_MONTH);
        tv_datepicker.setText(tahun+"-"+bulan+"-"+tanggal);

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

        cb_timepicker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tp_timepicker.setVisibility(View.VISIBLE);
                } else {
                    tp_timepicker.setVisibility(View.GONE);
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

        tp_timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                String jam = ((hourOfDay< 10 ? "0" : "") +hourOfDay);
                String menit = (minute < 10 ? "0" : "") + minute;

                tv_timepicker.setText(jam+":"+menit+":"+"00");
            }
        });



        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int month2 = (dp_datepicker.getMonth()+1);
                                final int tahun = dp_datepicker.getYear();
                                int hari = dp_datepicker.getDayOfMonth();
                                String nama_agenda = et_nama_agenda.getText().toString();

                                String ket  = et_ket.getText().toString();
                                final String bulan = ((month2 < 10 ? "0" : "") +month2);
                                final String tgl = (hari < 10 ? "0" : "") + hari;
                                String waktu = tv_datepicker.getText().toString()+" "+tv_timepicker.getText().toString();


                        Toast.makeText(getApplicationContext(), nama_agenda+" "+ket+" "+waktu, Toast.LENGTH_LONG).show();
                        long insert = db.insertAgenda(nama_agenda, ket, waktu);
                        Toast.makeText(getApplicationContext(), "Agenda berhasil di entry : "+insert, Toast.LENGTH_LONG).show();

                        finish();
                        getIntent().setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(getIntent());



                    }
                })

                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


}
