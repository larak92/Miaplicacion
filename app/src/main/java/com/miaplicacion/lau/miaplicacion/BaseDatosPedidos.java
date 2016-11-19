package com.miaplicacion.lau.miaplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosPedidos extends SQLiteOpenHelper {

    private static final String NOMBRE_BASE_DATOS = "pedidos.db";
    private static final int VERSION_ACTUAL = 1;

    public BaseDatosPedidos(Context context) {
        super(context, NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        // crear las tablas cliente, producto, cabecera_detalle, detalle_pedido
        db.execSQL("CREATE TABLE IF NOT EXISTS cliente(id_cliente INTEGER PRIMARY KEY," +
                "nombre TEXT, apellido TEXT, telefono TEXT, direccion TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS producto(id_producto INTEGER PRIMARY KEY," +
                "nombre TEXT, precio INTEGER, existencias INTEGER, stock_actual INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS cabecera_pedido(id_cabecera INTEGER PRIMARY KEY," +
                "id_cliente INTEGER, fecha TEXT," +
                "FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente));");
        db.execSQL("CREATE TABLE IF NOT EXISTS detalle_pedido(id_cabecera INTEGER," +
                "id_detalle INTEGER PRIMARY KEY, id_producto INTEGER, cantidad INTEGER," +
                "precio INTEGER, total INTEGER," +
                "FOREIGN KEY (id_cabecera) REFERENCES cabecera_pedido(id_cabecera)," +
                "FOREIGN KEY (id_producto) REFERENCES producto(id_producto));");

        // cargar tablas cliente y producto
        ContentValues valoresc1 = new ContentValues();
        valoresc1.put("id_cliente",1);
        valoresc1.put("nombre","Charlize");
        valoresc1.put("apellido","Theron");
        valoresc1.put("telefono","0981902352");
        valoresc1.put("direccion","Avda Azara");
        db.insert("cliente",null,valoresc1);
        ContentValues valoresc2 = new ContentValues();
        valoresc2.put("id_cliente",2);
        valoresc2.put("nombre","Jonh");
        valoresc2.put("apellido","Travolta");
        valoresc2.put("telefono","0981988880");
        valoresc2.put("direccion","Avda Brasil");
        db.insert("cliente",null,valoresc2);
        ContentValues valoresp1 = new ContentValues();
        valoresp1.put("id_producto",1);
        valoresp1.put("nombre","Gaseosa Pulp 500 ml");
        valoresp1.put("precio",5000);
        valoresp1.put("existencias",120);
        valoresp1.put("stock_actual",120);
        db.insert("producto",null,valoresp1);
        ContentValues valoresp2 = new ContentValues();
        valoresp2.put("id_producto",2);
        valoresp2.put("nombre","Galletita Terrabusi 300gr");
        valoresp2.put("precio",3000);
        valoresp2.put("existencias",100);
        valoresp2.put("stock_actual",100);
        db.insert("producto",null,valoresp2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + "cabecera_pedido");
            db.execSQL("DROP TABLE IF EXISTS " + "detalle_pedido");
            db.execSQL("DROP TABLE IF EXISTS " + "cliente");
            db.execSQL("DROP TABLE IF EXISTS " + "producto");
            onCreate(db);
    }
}
