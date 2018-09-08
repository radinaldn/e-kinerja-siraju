package id.topapp.radinaldn.e_kinerja.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.topapp.radinaldn.e_kinerja.R;
import id.topapp.radinaldn.e_kinerja.activities.KegiatanActivity;
import id.topapp.radinaldn.e_kinerja.activities.KerjaActivity;
import id.topapp.radinaldn.e_kinerja.database.DatabaseHelper;
import id.topapp.radinaldn.e_kinerja.models.Kegiatan;
import id.topapp.radinaldn.e_kinerja.models.Kerja;

/**
 * Created by radinaldn on 31/07/18.
 */

public class KerjaAdapter extends RecyclerView.Adapter<KerjaAdapter.KerjaViewHolder> {

    private List<Kerja> dataList;
    Context mContext;
    private static final String TAG = KerjaAdapter.class.getSimpleName();

    public KerjaAdapter(List<Kerja> dataList, Context context) {
        this.dataList = dataList;
        this.mContext = context;
    }

    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    View dialogView;

    @NonNull
    @Override
    public KerjaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.kerja_item, parent, false);

        return new KerjaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KerjaViewHolder holder, int position) {
        holder.tv_id_kerja.setText(dataList.get(position).getId_kerja());
        holder.tv_nama_kegiatan.setText(dataList.get(position).getNama_kegiatan());
        holder.tv_durasi.setText(dataList.get(position).getDurasi()+" menit");
        holder.tv_kategori.setText(dataList.get(position).getJenis());
        holder.tv_tanggal.setText(dataList.get(position).getTanggal());

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class KerjaViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_id_kerja, tv_nama_kegiatan, tv_durasi, tv_kategori, tv_tanggal;
        ImageButton bt_edit, bt_delete;

        public KerjaViewHolder(final View itemView) {
            super(itemView);
            tv_id_kerja = itemView.findViewById(R.id.tv_id_kegiatan);
            tv_nama_kegiatan = itemView.findViewById(R.id.tv_nama_kegiatan);
            tv_durasi = itemView.findViewById(R.id.tv_durasi);
            tv_kategori = itemView.findViewById(R.id.tv_kategori);
            tv_tanggal = itemView.findViewById(R.id.tv_tanggal);
            bt_edit = itemView.findViewById(R.id.bt_edit);
            bt_delete = itemView.findViewById(R.id.bt_hapus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // handling for action click item
                }
            });

            bt_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Anda menekan edit", Toast.LENGTH_SHORT).show();
                    actionUpdateKinerja(tv_id_kerja.getText().toString());
                }
            });

            bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Anda menekan hapus", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void actionUpdateKinerja(final String id){
            alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
            inflater = LayoutInflater.from(itemView.getContext());
            dialogView = inflater.inflate(R.layout.form_kinerja, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.drawable.logo_performance);
            alertDialogBuilder.setTitle(R.string.form_kinerja);

            final EditText et_kuantitas = dialogView.findViewById(R.id.et_kuantitas);
            TextView pop_kerja = dialogView.findViewById(R.id.tv_kerja);
            TextView pop_durasi = dialogView.findViewById(R.id.tv_durasi);
            TextView pop_kategori = dialogView.findViewById(R.id.tv_kategori);
            final ImageButton bt_kamera = dialogView.findViewById(R.id.bt_kamera);
            ImageButton bt_galeri = dialogView.findViewById(R.id.bt_galeri);
            ImageView iv_image = dialogView.findViewById(R.id.iv_image);

            pop_kerja.setText(tv_nama_kegiatan.getText().toString());
            pop_durasi.setText(tv_durasi.getText().toString());
            pop_kategori.setText(tv_kategori.getText().toString());
            final String nama_foto = "kinerja-"+id+".png";

            bt_kamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KerjaActivity.takePhoto((Activity)itemView.getContext(), nama_foto, itemView.getContext());
                    bt_kamera.setEnabled(false);
                    bt_kamera.setBackgroundResource(R.drawable.bt_disabled);
                }
            });


            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String kuantitas = et_kuantitas.getText().toString();
                            System.out.println("kuantitas = "+kuantitas);

                            KerjaActivity.db.updateKinerja(new Kerja(id, kuantitas, nama_foto));

                            Toast.makeText(itemView.getContext(), "Kinerja berhasil di update ", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(itemView.getContext(), KerjaActivity.class);
                            ((Activity)itemView.getContext()).finish();
                            itemView.getContext().startActivity(i);


                            for (int j = 0; j < KerjaActivity.db.getAllKerja().size(); j++) {
                                Log.d(TAG, "onClick: "+KerjaActivity.db.getAllKerja().get(j).getId_kerja());
                                Log.d(TAG, "onClick: "+KerjaActivity.db.getAllKerja().get(j).getNama_kegiatan());
                                Log.d(TAG, "onClick: "+KerjaActivity.db.getAllKerja().get(j).getKuantitas());
                                Log.d(TAG, "onClick: "+KerjaActivity.db.getAllKerja().get(j).getFoto());
                            }


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


}
