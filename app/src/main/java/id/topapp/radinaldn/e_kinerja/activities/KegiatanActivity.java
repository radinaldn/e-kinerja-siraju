package id.topapp.radinaldn.e_kinerja.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.topapp.radinaldn.e_kinerja.R;
import id.topapp.radinaldn.e_kinerja.adapters.KegiatanAdapter;
import id.topapp.radinaldn.e_kinerja.adapters.KerjaAdapter;
import id.topapp.radinaldn.e_kinerja.database.DatabaseHelper;
import id.topapp.radinaldn.e_kinerja.models.Kegiatan;
import id.topapp.radinaldn.e_kinerja.models.Kerja;

public class KegiatanActivity extends AppCompatActivity {

    private static final String TAG = KegiatanActivity.class.getSimpleName();
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private KegiatanAdapter adapter;
    private List<Kegiatan> kegiatanList = new ArrayList<>();
    public static DatabaseHelper db;


    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    View dialogView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kegiatan);

        db = new DatabaseHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_kegiatan);
        toolbar.setTitle(R.string.master_kegiatan);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        // click button back pada title bar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipe_activity_kegiatan);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh, R.color.refresh1, R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // handling refresh recyclerview
            }
        });

        // data dummy
        refreshUI();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCreate();
            }
        });
    }

    private void refreshUI() {
        kegiatanList.clear();

        kegiatanList.addAll(db.getAllKegiatan());
        adapter = new KegiatanAdapter(kegiatanList);
        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(KegiatanActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void actionCreate(){
        alertDialogBuilder = new AlertDialog.Builder(KegiatanActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_kegiatan, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.logo_performance);
        alertDialogBuilder.setTitle(R.string.form_kegiatan);

        final EditText et_nama_kegiatan = dialogView.findViewById(R.id.et_nama_kegiatan);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nama_kegiatan = et_nama_kegiatan.getText().toString();

                        long insert = db.insertKegiatan(nama_kegiatan);
                        Toast.makeText(getApplicationContext(), "Kegiatan berhasil di entry : "+insert, Toast.LENGTH_LONG).show();
                        refreshUI();
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

    public void actionUpdate(final String id){
        alertDialogBuilder = new AlertDialog.Builder(KegiatanActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_kegiatan, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.logo_performance);
        alertDialogBuilder.setTitle(R.string.form_kegiatan);

        final EditText et_nama_kegiatan = dialogView.findViewById(R.id.et_nama_kegiatan);

        Kegiatan kegiatan = db.getKegiatan(Long.parseLong(id));
        et_nama_kegiatan.setText(kegiatan.getNama_kegiatan());

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nama_kegiatan = et_nama_kegiatan.getText().toString();

                        db.updateKegiatan(new Kegiatan(id, nama_kegiatan));
                        Toast.makeText(getApplicationContext(), "Kegiatan berhasil di update ", Toast.LENGTH_LONG).show();
                        refreshUI();
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
