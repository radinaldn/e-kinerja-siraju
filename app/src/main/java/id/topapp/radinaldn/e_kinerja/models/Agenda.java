package id.topapp.radinaldn.e_kinerja.models;

/**
 * Created by radinaldn on 31/07/18.
 */

public class Agenda {
    private String id_agenda;
    private String nama_agenda;
    private String ket;
    private String tanggal;

    public Agenda(String id_agenda, String nama_agenda, String ket, String tanggal) {
        this.id_agenda = id_agenda;
        this.nama_agenda = nama_agenda;
        this.ket = ket;
        this.tanggal = tanggal;
    }

    public Agenda(){

    }

    public static final String TABLE_NAME = "tb_agenda";
    public static final String COLUMN_ID = "id_agenda";
    public static final String COLUMN_NAMA_AGENDA = "nama_agenda";
    public static final String COLUMN_KET = "ket";
    public static final String COLUMN_TANGGAL = "tanggal";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAMA_AGENDA+ " TEXT,"
            + COLUMN_KET + " TEXT,"
            + COLUMN_TANGGAL + " DATETIME"
            + ")";

    public String getId_agenda() {
        return id_agenda;
    }

    public void setId_agenda(String id_agenda) {
        this.id_agenda = id_agenda;
    }

    public String getNama_agenda() {
        return nama_agenda;
    }

    public void setNama_agenda(String nama_agenda) {
        this.nama_agenda = nama_agenda;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
