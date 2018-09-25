package id.topapp.radinaldn.e_kinerja.models;

/**
 * Created by radinaldn on 31/07/18.
 */

public class Kerja {
    private String id_kerja;
    private String nama_kegiatan;
    private String durasi;
    private String jenis;
    private String tanggal;
    private String foto;
    private String kuantitas;

    public static final String TABLE_NAME = "tb_kerja";
    public static final String COLUMN_ID = "id_kerja";
    public static final String COLUMN_NAMA_KEGIATAN = "nama_kegiatan";
    public static final String COLUMN_DURASI = "durasi";
    public static final String COLUMN_JENIS = "jenis";
    public static final String COLUMN_TANGGAL = "tanggal";
    public static final String COLUMN_FOTO = "foto";
    public static final String COLUMN_KUANTITAS = "kuantitas";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAMA_KEGIATAN + " TEXT,"
                    + COLUMN_DURASI + " TEXT,"
                    + COLUMN_JENIS + " TEXT,"
                    + COLUMN_TANGGAL + " DATETIME,"
                    + COLUMN_KUANTITAS + " TEXT,"
                    + COLUMN_FOTO + " TEXT"
                    + ")";

    public Kerja(){

    }

    public Kerja(String id_kerja, String kuantitas, String foto){
        this.id_kerja = id_kerja;
        this.kuantitas = kuantitas;
        this.foto = foto;
    }

    public Kerja(String id_kerja, String nama_kegiatan, String durasi, String jenis, String tanggal, String foto, String kuantitas) {
        this.id_kerja = id_kerja;
        this.nama_kegiatan = nama_kegiatan;
        this.durasi = durasi;
        this.jenis = jenis;
        this.tanggal = tanggal;
        this.foto = foto;
        this.kuantitas = kuantitas;
    }

    public String getId_kerja() {
        return id_kerja;
    }

    public void setId_kerja(String id_kerja) {
        this.id_kerja = id_kerja;
    }

    public String getNama_kegiatan() {
        return nama_kegiatan;
    }

    public void setNama_kegiatan(String nama_kegiatan) {
        this.nama_kegiatan = nama_kegiatan;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(String kuantitas) {
        this.kuantitas = kuantitas;
    }
}