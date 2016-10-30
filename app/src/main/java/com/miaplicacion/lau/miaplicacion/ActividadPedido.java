package com.miaplicacion.lau.miaplicacion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActividadPedido extends Activity implements View.OnClickListener{

    public Spinner spproducto;
    public TextView tvprecio;
    public TextView tvcantidad;
    public Button bcantidad;
    public TextView tvtotal;
    public Button bsiguiente;
    public Button bagregar;
    public int idcabecera;
    public List <Producto> productos;
    private BaseDatosPedidos bdp;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bdp = new BaseDatosPedidos(this);
        db = bdp.getReadableDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_pedido);

        // traer todas las filas de la tabla producto, instanciar en objetos y almacenarlos en una lista
        productos = new ArrayList<>();
        String query = "SELECT * FROM producto;";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            int pid = Integer.parseInt(cursor.getString(0));
            String pnombre = cursor.getString(1);
            int pprecio = Integer.parseInt(cursor.getString(2));
            int pexistencia = Integer.parseInt(cursor.getString(3));
            Producto producto = new Producto(pid, pnombre, pprecio, pexistencia);
            productos.add(producto);
        }

        //para mostrar en el spinner el nombre de los productos
        String valuesspinner [] = new String[productos.size()];
        for (int i=0; i<productos.size(); i++){
            valuesspinner[i] = productos.get(i).getNombre();
        }

        // obtenemos una referencia a los controles de la interfaz
        spproducto = (Spinner)findViewById(R.id.spinner);
        tvprecio = (TextView)findViewById(R.id.textView5);

        // al seleccionar un producto se aguarda la posicion a lo cual lo utilizamos para acceder a  lista de
        // productos y asi obtener el precio correspondiente al producto
        spproducto.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, valuesspinner));
        spproducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                tvprecio.setText(String.valueOf(productos.get(position).getPrecio()));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // el boton cantidad llama a la funcion del numberpicker con un minimo y maximo
        bcantidad = (Button)findViewById(R.id.button6);
        bcantidad.setOnClickListener(this);
        tvcantidad = (TextView)findViewById(R.id.textView3);
        tvtotal = (TextView)findViewById(R.id.textView8);

        // traer el id de la cabecera creada en la vista anterior
        Cursor cursorid = db.rawQuery("SELECT MAX(id_cabecera) AS cabecera FROM cabecera_pedido", null);
        cursorid.moveToFirst();
        idcabecera = cursorid.getInt( cursorid.getColumnIndex("cabecera"));

        // al presionar el boton agregar se crea el detalle del pedido
        bagregar = (Button)findViewById(R.id.button);
        bagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("id_cabecera",  idcabecera);
                values.put("id_producto", productos.get(spproducto.getSelectedItemPosition()).getIdProducto());
                values.put("cantidad", Integer.parseInt(tvcantidad.getText().toString()));
                values.put("precio", Integer.parseInt(tvprecio.getText().toString()));
                values.put("total", Integer.parseInt(tvtotal.getText().toString()));
                db.insert("detalle_pedido", null, values);
                Toast.makeText(ActividadPedido.this, "Registrado!", Toast.LENGTH_SHORT).show();
            }
        });

        // al presionar el boton siguiente pasa a la siguiente actividad
        bsiguiente = (Button)findViewById(R.id.button3);
        bsiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intento =  new Intent(ActividadPedido.this, ActividadRegistrados.class);
                startActivity(intento);
            }
        });

    }

    @Override
    public void onClick(View view){
        numberPickerDialog();
    }

    // al seleccionar un numero en el numberpicker este se visualizara en un textview como la cantidad
    // al mismo momento que el numero seleccionado  se multiplica por el precio y este se visualizara en un textview como el total
    private void numberPickerDialog(){
        NumberPicker myNumberPicker = new NumberPicker(this);
        myNumberPicker.setMaxValue(productos.get(spproducto.getSelectedItemPosition()).getExistencias());
        myNumberPicker.setMinValue(1);
        NumberPicker.OnValueChangeListener myValChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                tvcantidad.setText(""+newVal);
                tvtotal.setText(""+newVal * Integer.parseInt(tvprecio.getText().toString()));
            }
        };
        myNumberPicker.setOnValueChangedListener(myValChangeListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(myNumberPicker);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
