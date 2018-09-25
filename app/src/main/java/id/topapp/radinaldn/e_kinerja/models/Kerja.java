package id.topapp.radinaldn.e_kinerja.models;

/**
 * Created by radinaldn on 31/07/18.
 */

public class Kegiatan {
    private String id_kegiatan;
    private String nama_kegiatan;

    public static final String TABLE_NAME = "tb_kegiatan";
    public static final String COLUMN_ID = "id_kegiatan";
    public static final String COLUMN_NAMA = "nama_kegiatan";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAMA + " TEXT"
                    + ")";

    public Kegiatan(){

    }

    public Kegiatan(String id_kegiatan, String nama_kegiatan) {
        this.id_kegiatan = id_kegiatan;
        this.nama_kegiatan = nama_kegiatan;
    }

    public String getId_kegiatan() {
        return id_kegiatan;
    }

    public void setId_kegiatan(String id_kegiatan) {
        this.id_kegiatan = id_kegiatan;
    }

    public String getNama_kegiatan() {
        return nama_kegiatan;
    }

    public void setNama_kegiatan(String nama_kegiatan) {
        this.nama_kegiatan = nama_kegiatan;
    }
}
