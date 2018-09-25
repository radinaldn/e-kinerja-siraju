package id.topapp.radinaldn.e_kinerja.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import id.topapp.radinaldn.e_kinerja.R;
import id.topapp.radinaldn.e_kinerja.activities.KerjaActivity;
import id.topapp.radinaldn.e_kinerja.models.Kerja;

/**
 * Created by radinaldn on 31/07/18.
 */

public class KegiatanAdapter extends RecyclerView.Adapter<KegiatanAdapter.KegiatanViewHolder> {

    private List<Kerja> dataList;
    private static final String TAG = KegiatanAdapter.class.getSimpleName();

    public KegiatanAdapter(List<Kerja> dataList) {
        this.dataList = dataList;
    }

    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    View dialogView;

    @NonNull
    @Override
    public KegiatanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.kegiatan_item, parent, false);

        return new KegiatanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KegiatanViewHolder holder, int position) {

        holder.tv_id_kegiatan.setText(dataList.get(position).getId_kegiatan());
        holder.tv_nama_kegiatan.setText(dataList.get(position).getNama_kegiatan());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class KegiatanViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_id_kegiatan, tv_nama_kegiatan;
        ImageButton bt_edit, bt_hapus;

        public KegiatanViewHolder(final View itemView) {
            super(itemView);
            tv_id_kegiatan = itemView.findViewById(R.id.tv_id_kegiatan);
            tv_nama_kegiatan = itemView.findViewById(R.id.tv_nama);
            bt_edit = itemView.findViewById(R.id.bt_edit);
            bt_hapus = itemView.findViewById(R.id.bt_hapus);

            bt_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionUpdate(tv_id_kegiatan.getText().toString());
                }
            });

            bt_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    actionDelete(tv_id_kegiatan.getText().toString());
                }
            });
        }

        public void actionUpdate(final String id){
            alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
            inflater = LayoutInflater.from(itemView.getContext());
            dialogView = inflater.inflate(R.layout.form_kegiatan, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.drawable.logo_performance);
            alertDialogBuilder.setTitle(R.string.form_kegiatan);

            final EditText et_nama_kegiatan = dialogView.findViewById(R.id.et_nama_kegiatan);

            Kerja kerja = KerjaActivity.db.getKegiatan(Long.parseLong(id));
            et_nama_kegiatan.setText(kerja.getNama_kegiatan());

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String nama_kegiatan = et_nama_kegiatan.getText().toString();

                            KerjaActivity.db.updateKegiatan(new Kerja(id, nama_kegiatan));
                            Toast.makeText(itemView.getContext(), "Kerja berhasil di update ", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(itemView.getContext(), KerjaActivity.class);
                            ((Activity)itemView.getContext()).finish();
                            itemView.getContext().startActivity(i);


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

        public void actionDelete(final String id){
            AlertDialog.Builder confirmBox = new AlertDialog.Builder(itemView.getContext());
            confirmBox.setTitle("Konfirmasi");
            confirmBox.setIcon(R.drawable.ic_help_black_24dp);
            confirmBox.setMessage("Anda yakin ingin menghapus kegiatan ini ?");
            confirmBox.setCancelable(false);

            confirmBox.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    KerjaActivity.db.deleteKegiatan(dataList.get(Integer.parseInt(id)-1));
                    Toast.makeText(itemView.getContext(), "Kerja berhasil di hapus ", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(itemView.getContext(), KerjaActivity.class);
                    ((Activity)itemView.getContext()).finish();
                    itemView.getContext().startActivity(i);
                }
            });
            confirmBox.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialogKonfirmasi = confirmBox.create();
            alertDialogKonfirmasi.show();

        }
    }


}