package id.topapp.radinaldn.e_kinerja.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import id.topapp.radinaldn.e_kinerja.models.Agenda;
import id.topapp.radinaldn.e_kinerja.models.Kegiatan;
import id.topapp.radinaldn.e_kinerja.models.Kerja;

/**
 * Created by radinaldn on 01/08/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ekinerja_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Kegiatan.CREATE_TABLE);
        db.execSQL(Kerja.CREATE_TABLE);
        db.execSQL(Agenda.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Kegiatan.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Kerja.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Agenda.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // function for CREATE
    public long insertKerja(String nama_kegiatan, String durasi, String jenis, String tanggal, String foto, String kuantitas) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Kerja.COLUMN_NAMA_KEGIATAN, nama_kegiatan);
        values.put(Kerja.COLUMN_DURASI, durasi);
        values.put(Kerja.COLUMN_JENIS, jenis);
        values.put(Kerja.COLUMN_TANGGAL, tanggal);
        values.put(Kerja.COLUMN_FOTO, foto);
        values.put(Kerja.COLUMN_KUANTITAS, kuantitas);

        // insert row
        long id = db.insert(Kerja.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    // function for UPDATE
    public int updateKerja(Kerja kerja) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Kerja.COLUMN_NAMA_KEGIATAN, kerja.getNama_kegiatan());
        values.put(Kerja.COLUMN_DURASI, kerja.getDurasi());
        values.put(Kerja.COLUMN_JENIS, kerja.getJenis());
        values.put(Kerja.COLUMN_TANGGAL, kerja.getTanggal());
        values.put(Kerja.COLUMN_KUANTITAS, kerja.getKuantitas());
        values.put(Kerja.COLUMN_FOTO, kerja.getFoto());

        // updating row
        return db.update(Kerja.TABLE_NAME, values, Kerja.COLUMN_ID + " = ?",
                new String[]{String.valueOf(kerja.getId_kerja())});
    }

    // function for UPDATE
    public int updateKinerja(Kerja kerja) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Kerja.COLUMN_KUANTITAS, kerja.getKuantitas());
        values.put(Kerja.COLUMN_FOTO, kerja.getFoto());

        // updating row
        return db.update(Kerja.TABLE_NAME, values, Kerja.COLUMN_ID + " = ?",
                new String[]{String.valueOf(kerja.getId_kerja())});
    }

    // function for CREATE kegiatan
    public long insertKegiatan(String nama_kegiatan){
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Kegiatan.COLUMN_NAMA, nama_kegiatan);

        // insert row
        long id = db.insert(Kegiatan.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    // function for READ kegiatan
    public List<Kegiatan> getAllKegiatan() {
        List<Kegiatan> kegiatans = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Kegiatan.TABLE_NAME + " ORDER BY " +
                Kegiatan.COLUMN_ID+ " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Kegiatan kegiatan = new Kegiatan();
                kegiatan.setId_kegiatan(cursor.getString(cursor.getColumnIndex(Kegiatan.COLUMN_ID)));
                kegiatan.setNama_kegiatan(cursor.getString(cursor.getColumnIndex(Kegiatan.COLUMN_NAMA)));

                kegiatans.add(kegiatan);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return kegiatans;
    }

    // function for READ agenda by tanggal
    public List<Agenda> getAllAgendaByTanggal(String tanggal) {
        List<Agenda> agendas = new ArrayList<>();

        // Select All Query
//        String selectQuery = "SELECT * FROM " + Agenda.TABLE_NAME + " WHERE strftime('%d-%m-%Y', '"+Agenda.COLUMN_TANGGAL+"') = "+"'"+tanggal+"'"+" ORDER BY " +
//                Agenda.COLUMN_ID+ " DESC";

        String selectQuery = "SELECT * FROM " + Agenda.TABLE_NAME +" WHERE "+Agenda.COLUMN_TANGGAL+" BETWEEN '" +tanggal+ " 00:00:00' AND '"+tanggal+" 23:59:00'";

        System.out.println("selectedQuery : "+selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Agenda agenda = new Agenda();
                agenda.setId_agenda(cursor.getString(cursor.getColumnIndex(Agenda.COLUMN_ID)));
                agenda.setNama_agenda(cursor.getString(cursor.getColumnIndex(Agenda.COLUMN_NAMA_AGENDA)));
                agenda.setKet(cursor.getString(cursor.getColumnIndex(Agenda.COLUMN_KET)));
                agenda.setTanggal(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_TANGGAL)));

                agendas.add(agenda);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return agendas;
    }

    // function for READ kegiatan
    public List<String> getAllNamaKegiatan() {
        List<String> kegiatans = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Kegiatan.TABLE_NAME + " ORDER BY " +
                Kegiatan.COLUMN_ID+ " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                kegiatans.add(cursor.getString(cursor.getColumnIndex(Kegiatan.COLUMN_NAMA)));
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return kegiatans;
    }

    // function for READ
    public List<Kerja> getAllKerja() {
        List<Kerja> kerjas = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Kerja.TABLE_NAME + " ORDER BY " +
                Kerja.COLUMN_ID+ " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Kerja kerja = new Kerja();
                kerja.setId_kerja(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_ID)));
                kerja.setNama_kegiatan(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_NAMA_KEGIATAN)));
                kerja.setDurasi(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_DURASI)));
                kerja.setJenis(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_JENIS)));
                kerja.setTanggal(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_TANGGAL)));
                kerja.setFoto(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_FOTO)));
                kerja.setKuantitas(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_KUANTITAS)));

                kerjas.add(kerja);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return kerjas;
    }

    public Kegiatan getKegiatan(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Kegiatan.TABLE_NAME,
                new String[]{Kegiatan.COLUMN_ID, Kegiatan.COLUMN_NAMA},
                Kegiatan.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Kegiatan kegiatan = new Kegiatan(
                cursor.getString(cursor.getColumnIndex(Kegiatan.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Kegiatan.COLUMN_NAMA)));

        // close the db connection
        cursor.close();

        return kegiatan;
    }

    public int updateKegiatan(Kegiatan kegiatan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Kegiatan.COLUMN_NAMA, kegiatan.getNama_kegiatan());

        // updating row
        return db.update(Kegiatan.TABLE_NAME, values, Kegiatan.COLUMN_ID + " = ?",
                new String[]{String.valueOf(kegiatan.getId_kegiatan())});
    }

    public void deleteKegiatan(Kegiatan kegiatan) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Kegiatan.TABLE_NAME, Kegiatan.COLUMN_ID + " = ?",
                new String[]{String.valueOf(kegiatan.getId_kegiatan())});
        db.close();
    }


    // function for READ by tanggal
    public List<Kerja> getAllKerjaByTanggal(String tanggal) {
        List<Kerja> kerjas = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + Kerja.TABLE_NAME + " WHERE "+Kerja.COLUMN_TANGGAL+" = "+"'"+tanggal+"'"+" ORDER BY " +
                Kerja.COLUMN_ID+ " DESC";

        System.out.println("selectedQuery : "+selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Kerja kerja = new Kerja();
                kerja.setId_kerja(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_ID)));
                kerja.setNama_kegiatan(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_NAMA_KEGIATAN)));
                kerja.setDurasi(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_DURASI)));
                kerja.setJenis(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_JENIS)));
                kerja.setTanggal(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_TANGGAL)));
                kerja.setFoto(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_FOTO)));
                kerja.setKuantitas(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_KUANTITAS)));

                kerjas.add(kerja);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return kerjas;
    }

    // function for READ by tanggal
    public List<Kerja> getAllKinerjaByTanggal(String tanggal) {
        List<Kerja> kerjas = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + Kerja.TABLE_NAME + " WHERE "+Kerja.COLUMN_TANGGAL+" = "+"'"+tanggal+"'"+" ORDER BY " +
                Kerja.COLUMN_ID+ " DESC";

        System.out.println("selectedQuery : "+selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Kerja kerja = new Kerja();
                kerja.setId_kerja(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_ID)));
                kerja.setNama_kegiatan(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_NAMA_KEGIATAN)));
                kerja.setDurasi(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_DURASI)));
                kerja.setJenis(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_JENIS)));
                kerja.setTanggal(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_TANGGAL)));
                kerja.setFoto(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_FOTO)));
                kerja.setKuantitas(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_KUANTITAS)));

                kerjas.add(kerja);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return kerjas;
    }


    public long insertAgenda(String nama_agenda, String ket, String waktu) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Agenda.COLUMN_NAMA_AGENDA, nama_agenda);
        values.put(Agenda.COLUMN_KET, ket);
        values.put(Agenda.COLUMN_TANGGAL, waktu);

        // insert row
        long id = db.insert(Agenda.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public int updateAgenda(Agenda agenda) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Agenda.COLUMN_NAMA_AGENDA, agenda.getNama_agenda());
        values.put(Agenda.COLUMN_KET, agenda.getKet());
        values.put(Agenda.COLUMN_TANGGAL, agenda.getTanggal());

        // updating row
        return db.update(Agenda.TABLE_NAME, values, Agenda.COLUMN_ID + " = ?",
                new String[]{String.valueOf(agenda.getId_agenda())});
    }

    public void deleteAgenda(String id_agenda) {

        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " +Agenda.TABLE_NAME+" WHERE " +a);
        db.delete(Agenda.TABLE_NAME, Agenda.COLUMN_ID + " = ?",
                new String[]{id_agenda});
        db.close();
    }

    public int getSumDurasiByBulan(String int_month){
        int total = 0;
        String countQuery = "SELECT SUM("+Kerja.COLUMN_DURASI+") FROM " + Kerja.TABLE_NAME + " WHERE strftime('%m', " + Kerja.COLUMN_TANGGAL + ") = '"+int_month+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.moveToFirst()){
            total = cursor.getInt(0);
        }
        while (cursor.moveToNext());


        // return count
        return total;
    }

    public int getCountTotalKerjaByBulan(String int_month){
        int total = 0;
        String countQuery = "SELECT "+Kerja.COLUMN_ID+" FROM " + Kerja.TABLE_NAME + " WHERE strftime('%m', " + Kerja.COLUMN_TANGGAL + ") = '"+int_month+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public ArrayList<String> getTestTanggalAja(){
        ArrayList<String> list = new ArrayList<>();
        String selectedQuery = "SELECT strftime('%m', "+ Kerja.COLUMN_TANGGAL +") FROM " + Kerja.TABLE_NAME;

        System.out.println(selectedQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectedQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                list.add(cursor.getString(cursor.getColumnIndex(Kerja.COLUMN_TANGGAL)));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return list;

    }
}