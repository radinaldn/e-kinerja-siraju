package id.topapp.radinaldn.e_kinerja.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import id.topapp.radinaldn.e_kinerja.R;
import id.topapp.radinaldn.e_kinerja.activities.DetailActivity;
import id.topapp.radinaldn.e_kinerja.activities.KinerjaActivity;
import id.topapp.radinaldn.e_kinerja.models.Kerja;

/**
 * Created by radinaldn on 03/08/18.
 */

public class KinerjaAdapter extends RecyclerView.Adapter<KinerjaAdapter.KinerjaViewHolder> {

    private List<Kerja> dataList;
    Context mContext;
    private static final String TAG = KinerjaAdapter.class.getSimpleName();

    public KinerjaAdapter(List<Kerja> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public KinerjaAdapter.KinerjaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.kinerja_item, parent, false);

        return new KinerjaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KinerjaAdapter.KinerjaViewHolder holder, int position) {

        holder.tv_id_kerja.setText(dataList.get(position).getId_kerja());
        holder.tv_nama_kegiatan.setText(dataList.get(position).getNama_kegiatan());
        holder.tv_durasi.setText(dataList.get(position).getDurasi()+" menit");
        holder.tv_kategori.setText(dataList.get(position).getJenis());
        holder.tv_tanggal.setText(dataList.get(position).getTanggal());
        holder.tv_kuantitas.setText(dataList.get(position).getKuantitas());
        holder.tv_foto.setText(dataList.get(position).getFoto());


        Picasso.get().load("file:///storage/emulated/0/GaleriEKinerja/"+dataList.get(position).getFoto()).into(holder.iv_image);

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class KinerjaViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_id_kerja, tv_nama_kegiatan, tv_durasi, tv_kategori, tv_tanggal, tv_kuantitas, tv_foto;
        ImageView iv_image;

        public KinerjaViewHolder(final View itemView) {
            super(itemView);
            tv_id_kerja = itemView.findViewById(R.id.tv_id_kegiatan);
            tv_nama_kegiatan = itemView.findViewById(R.id.tv_nama_kegiatan);
            tv_durasi = itemView.findViewById(R.id.tv_durasi);
            tv_kategori = itemView.findViewById(R.id.tv_kategori);
            tv_tanggal = itemView.findViewById(R.id.tv_tanggal);
            tv_kuantitas = itemView.findViewById(R.id.tv_kuantitas);
            iv_image = itemView.findViewById(R.id.iv_foto_kinerja);
            tv_foto = itemView.findViewById(R.id.tv_foto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), DetailActivity.class);

                    intent.putExtra("id_kerja", tv_id_kerja.getText().toString());
                    intent.putExtra("nama_kegiatan", tv_nama_kegiatan.getText().toString());
                    intent.putExtra("durasi", tv_durasi.getText().toString());
                    intent.putExtra("kategori", tv_kategori.getText().toString());
                    intent.putExtra("tanggal", tv_tanggal.getText().toString());
                    intent.putExtra("kuantitas", tv_kuantitas.getText().toString());
                    intent.putExtra("foto", tv_foto.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });
        }


    }
}