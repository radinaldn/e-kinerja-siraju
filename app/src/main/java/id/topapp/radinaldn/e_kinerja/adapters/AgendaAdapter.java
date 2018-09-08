package id.topapp.radinaldn.e_kinerja.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import id.topapp.radinaldn.e_kinerja.R;
import id.topapp.radinaldn.e_kinerja.activities.AgendaActivity;
import id.topapp.radinaldn.e_kinerja.activities.KegiatanActivity;
import id.topapp.radinaldn.e_kinerja.activities.KerjaActivity;
import id.topapp.radinaldn.e_kinerja.models.Agenda;
import id.topapp.radinaldn.e_kinerja.models.Kegiatan;
import id.topapp.radinaldn.e_kinerja.models.Kerja;

/**
 * Created by radinaldn on 03/08/18.
 */

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder> {

    private List<Agenda> dataList;
    private static final String TAG = AgendaAdapter.class.getSimpleName();

    public AgendaAdapter(List<Agenda> dataList) {
        this.dataList = dataList;
    }

    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    View dialogView;

    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.agenda_item, parent, false);

        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendaAdapter.AgendaViewHolder holder, int position) {
        holder.tv_id_agenda.setText(dataList.get(position).getId_agenda());
        holder.tv_nama_agenda.setText(dataList.get(position).getNama_agenda());
        holder.tv_ket.setText(dataList.get(position).getKet());
        holder.tv_tanggal.setText(dataList.get(position).getTanggal());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class AgendaViewHolder extends RecyclerView.ViewHolder {

        TextView tv_id_agenda, tv_nama_agenda, tv_ket, tv_tanggal;
        ImageButton bt_edit, bt_hapus;

        public AgendaViewHolder(View itemView) {
            super(itemView);
            tv_id_agenda = itemView.findViewById(R.id.tv_id_agenda);
            tv_nama_agenda = itemView.findViewById(R.id.tv_nama_agenda);
            tv_ket  = itemView.findViewById(R.id.tv_ket);
            tv_tanggal = itemView.findViewById(R.id.tv_tanggal);
            bt_edit = itemView.findViewById(R.id.bt_edit);
            bt_hapus = itemView.findViewById(R.id.bt_hapus);

            bt_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionUpdateAgenda(tv_id_agenda.getText().toString());
                }
            });

            bt_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionDeleteAgenda(tv_id_agenda.getText().toString());
                }
            });


        }

        // action delete
        public void actionDeleteAgenda(final String id_agenda){
            AlertDialog.Builder confirmBox = new AlertDialog.Builder(itemView.getContext());
            confirmBox.setTitle("Konfirmasi");
            confirmBox.setIcon(R.drawable.ic_help_black_24dp);
            confirmBox.setMessage("Anda yakin ingin menghapus agenda ini ?");
            confirmBox.setCancelable(false);

            confirmBox.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AgendaActivity.db.deleteAgenda(id_agenda);
                    Toast.makeText(itemView.getContext(), "Agenda " +tv_nama_agenda.getText().toString()+ " berhasil di hapus ", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(itemView.getContext(), AgendaActivity.class);
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

        // action update
        public void actionUpdateAgenda(final String id){
            alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
            inflater = LayoutInflater.from(itemView.getContext());
            dialogView = inflater.inflate(R.layout.form_agenda, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.drawable.logo_performance);
            alertDialogBuilder.setTitle(R.string.form_agenda);


            final EditText et_nama_agenda = dialogView.findViewById(R.id.et_nama_agenda);
            final EditText et_ket = dialogView.findViewById(R.id.et_ket);
            final DatePicker dp_datepicker = dialogView.findViewById(R.id.dp_datepicker);
            final CheckBox cb_datepicker = dialogView.findViewById(R.id.cb_datepicker);
            final TextView tv_datepicker  =dialogView.findViewById(R.id.tv_datepicker);
            final CheckBox cb_timepicker = dialogView.findViewById(R.id.cb_timepicker);
            final TimePicker tp_timepicker = dialogView.findViewById(R.id.tp_timepicker);
            final TextView tv_timepicker  =dialogView.findViewById(R.id.tv_timepicker);
            tv_timepicker.setText("00:00:00");


            et_nama_agenda.setText(tv_nama_agenda.getText().toString());
            et_ket.setText(tv_ket.getText().toString());

            // split tanggal value into tanggal and waktu
            String [] datetime = tv_tanggal.getText().toString().split(" ");

            tv_datepicker.setText(datetime[0]);
            tv_timepicker.setText(datetime[1]);

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

                            final String bulan = ((month2 < 10 ? "0" : "") +month2);
                            final String tgl = (hari < 10 ? "0" : "") + hari;
                            String waktu = tv_datepicker.getText().toString()+" "+tv_timepicker.getText().toString();

                            String nama_agenda = et_nama_agenda.getText().toString();
                            String ket = et_ket.getText().toString();


                            AgendaActivity.db.updateAgenda(new Agenda(id, nama_agenda, ket, waktu));

//                          //Toast.makeText(itemView.getContext(), "Agenda berhasil di update : "+nama_agenda+" "+ket+" "+waktu, Toast.LENGTH_LONG).show();
                            Intent i = new Intent(itemView.getContext(), AgendaActivity.class);
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

    }
}
