package com.miaplicacion.lau.miaplicacion;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ActividadRegistrados extends Activity{

    public ListView lvpedidos;
    public Button bfinalizar;
    public Button batras;
    public int idcab;
    public int idcli;
    private BaseDatosPedidos bdp;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bdp = new BaseDatosPedidos(this);
        db = bdp.getReadableDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_registrados);

        ArrayList<String> listaDatos = new ArrayList<String>();
        String item = "";
        // obtener el cliente actual seleccionado de la tabla cabecera_pedido
        Cursor c1 = db.rawQuery("SELECT MAX(id_cabecera) AS cabecera FROM cabecera_pedido", null);
        c1.moveToFirst();
        idcab = c1.getInt(c1.getColumnIndex("cabecera"));
        // obtener la fecha
        Cursor cf = db.rawQuery(String.format("SELECT fecha FROM cabecera_pedido WHERE id_cabecera = %s;", idcab), null);
        cf.moveToFirst();
        item += "Fecha: "+ cf.getString(cf.getColumnIndex("fecha"));
        listaDatos.add(item);
        item = "";
        // obtener el ID de la tabla cliente
        Cursor c2 = db.rawQuery(String.format("SELECT id_cliente FROM cabecera_pedido WHERE id_cabecera = %s;", idcab), null);
        c2.moveToFirst();
        idcli = c2.getInt(c2.getColumnIndex("id_cliente"));
        // obtener el nombre y apellido del cliente seleccionado
        Cursor c3 = db.rawQuery(String.format("SELECT nombre, apellido FROM cliente WHERE id_cliente = %s;", idcli), null);
        c3.moveToFirst();
        item += "Cliente: " + c3.getString(0) + " "+c3.getString(1) + "\r\n";
        listaDatos.add(item);
        item = "";
        // obtenemos todos los detalles del pedido del cliente
        Cursor c4 = db.rawQuery(String.format("SELECT id_producto, cantidad, precio, total FROM detalle_pedido WHERE id_cabecera = %s;", idcab), null);
        while (c4.moveToNext()){
            item += "Codigo Producto: " + c4.getString(0) + "\r\n";
            item += "Cantidad: " + c4.getString(1) + "\r\n";
            item += "Precio Gs.: " + c4.getString(2) + "\r\n";
            item += "Total Gs.: " + c4.getString(3) + "";
            listaDatos.add(item);
            item = "";
        }

        // se obtiene una referencia al control de la interfaz para luego mostrar en el listview los pedidos realizados
        lvpedidos = (ListView)findViewById(R.id.listview1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listaDatos);
        lvpedidos.setAdapter(adapter);
        lvpedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lvpedidos.getItemAtPosition(position);
            }
        });

        // al presionar el boton finalizar pasa a la primera actividad
        bfinalizar = (Button)findViewById(R.id.button5);
        bfinalizar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(ActividadRegistrados.this, ActividadPrincipal.class);
                startActivity(intento);
            }
        });

        // al presionar el boton atras pasa a la segunda actividad
        batras = (Button)findViewById(R.id.button4);
        batras.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
