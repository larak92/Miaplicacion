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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActividadPedido extends Activity implements View.OnClickListener{

    public Spinner spproducto;
    public TextView tvprecio;
    public EditText etcantidad;
    public TextView tvtotal;
    public Button bsiguiente;
    public Button bagregar;
    public int idcabecera;
    public List <Producto> productos;
    private BaseDatosPedidos bdp;
    private SQLiteDatabase db;
    public int resto;
    public int idproupdate;
    public int n;
    public String valuesspinner [];
    public int pedidohecho;
    public int cantidadinicial;
    public ArrayAdapter<String> spinnerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bdp = new BaseDatosPedidos(this);
        db = bdp.getReadableDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_pedido);

        // traer todas las filas de la tabla producto, instanciar en objetos y almacenarlos en una lista
        pedidohecho = 0;
        productos = new ArrayList<>();
        String query = "SELECT * FROM producto;";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            int pid = Integer.parseInt(cursor.getString(0));
            String pnombre = cursor.getString(1);
            int pprecio = Integer.parseInt(cursor.getString(2));
            int pexistencia = Integer.parseInt(cursor.getString(3));
            int pstockactual = Integer.parseInt(cursor.getString(4));
            Producto producto = new Producto(pid, pnombre, pprecio, pexistencia, pstockactual);
            productos.add(producto);
        }

        //para mostrar en el spinner el nombre de los productos
        valuesspinner = new String[productos.size()+1];
        n=0;
        valuesspinner[n] = "Seleccione una opción";
        for (int i=0; i<productos.size(); i++){
            n++;
            valuesspinner[n] = productos.get(i).getNombre();
        }

        // obtenemos una referencia a los controles de la interfaz
        spproducto = (Spinner)findViewById(R.id.spinner);
        tvprecio = (TextView)findViewById(R.id.textView5);
        tvprecio.setText("- -");

        // al seleccionar un producto se aguarda la posicion a lo cual lo utilizamos para acceder a  lista de
        // productos y asi obtener el precio correspondiente al producto
        // obs: si la posicion es cero no se muestra ningun dato
        spinnerData = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, valuesspinner);
        spproducto.setAdapter(spinnerData);
        spproducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0 ){
                    tvprecio.setText(String.valueOf(productos.get(position-1).getPrecio()));
                    pedidohecho = 0;
                }else{
                    tvprecio.setText("- -");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // obtenemos una referencia a los controles de la interfaz
        etcantidad = (EditText)findViewById(R.id.editText);
        etcantidad.setText("0");
        etcantidad.setOnClickListener(this);
        tvtotal = (TextView)findViewById(R.id.textView8);
        tvtotal.setText("0");

        // traer el id de la cabecera creada en la vista anterior
        Cursor cursorid = db.rawQuery("SELECT MAX(id_cabecera) AS cabecera FROM cabecera_pedido", null);
        cursorid.moveToFirst();
        idcabecera = cursorid.getInt( cursorid.getColumnIndex("cabecera"));

        /*
        Al presionar el boton agregar se evaluan las siguientes condiciones:
        1. No selecciono nada, no podra agregar el pedido
        2. Al seleccionar un producto pero no la cantidad, no podra agregar el pedido
        3. Al seleccionar un producto y la cantidad pero no agregar, no podra pasar a la siguiente vista
        4. Al seleccionar un producto, la cantidad y agregar, el selector vuelve a su valor por defecto (sin seleccion) y
            la cantidad (cero)
        5. Al agregar un producto se podra realizar otro pedido o pasar a la siguiente vista
         */
        bagregar = (Button)findViewById(R.id.button);
        bagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spproducto.getSelectedItemPosition() == 0){
                    Toast.makeText(ActividadPedido.this, "Error: Debe seleccionar un producto.", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(etcantidad.getText().toString()) == 0){
                    Toast.makeText(ActividadPedido.this, "Error: Debe seleccionar una cantidad.", Toast.LENGTH_SHORT).show();
                }else{
                    // agrega el detalle del pedido
                    pedidohecho++;
                    ContentValues values = new ContentValues();
                    values.put("id_cabecera",  idcabecera);
                    values.put("id_producto", productos.get(spproducto.getSelectedItemPosition()-1).getIdProducto());
                    values.put("cantidad", Integer.parseInt(etcantidad.getText().toString()));
                    values.put("precio", Integer.parseInt(tvprecio.getText().toString()));
                    values.put("total", Integer.parseInt(tvtotal.getText().toString()));
                    db.insert("detalle_pedido", null, values);
                    Toast.makeText(ActividadPedido.this, "Producto registrado con éxito!", Toast.LENGTH_SHORT).show();

                    // Actualizar el stock_actual
                    idproupdate = productos.get(spproducto.getSelectedItemPosition()-1).getIdProducto();
                    resto = productos.get(spproducto.getSelectedItemPosition()-1).getStockactual() - Integer.parseInt(etcantidad.getText().toString());
                    productos.get(spproducto.getSelectedItemPosition()-1).setStockactual(resto);//guardando en el array
                    ContentValues cv = new ContentValues();
                    cv.put("stock_actual",resto);// guardando en el db
                    db.update("producto",cv,"id_producto ="+idproupdate,null);

                    // si el stock_actual llega a cero entonces se recarga con la existencia inicial
                    if ( productos.get(spproducto.getSelectedItemPosition()-1).getStockactual() ==0){
                        cantidadinicial = productos.get(spproducto.getSelectedItemPosition()-1).getExistencias();
                        ContentValues cvstock = new ContentValues();
                        cvstock.put("stock_actual",cantidadinicial);
                        db.update("producto",cvstock,"id_producto ="+idproupdate,null);
                    }
                    // limpiar el producto, precio, cantidad y el total luego de ser agregado
                    spproducto.setAdapter(spinnerData);
                    tvprecio.setText("- -");
                    etcantidad.setText("0");
                    tvtotal.setText("0");
                }
            }
        });

        /*
        Al presionar el boton siguiente se evaluan las siguientes condiciones:
        1. No selecciono nada, no podra pasar a la siguiente vista
        2. Al seleccionar un producto pero no la cantidad, no podra pasar a la siguiente vista
        3. Al seleccionar un producto y la cantidad pero no agregar, no podra pasar a la siguiente vista
        4. Aumenta el contador de pedidohecho al agregar correctamente el pedido
        5. Al agregar un producto se podra pasar a la siguiente vista
         */
        bsiguiente = (Button)findViewById(R.id.button3);
        bsiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(spproducto.getSelectedItemPosition() == 0 && Integer.parseInt(etcantidad.getText().toString()) == 0 && pedidohecho ==0){
                    Toast.makeText(ActividadPedido.this, "Error: Campos obligatorios faltantes.", Toast.LENGTH_SHORT).show();
                }else if ((spproducto.getSelectedItemPosition() == 0 || Integer.parseInt(etcantidad.getText().toString()) == 0) && pedidohecho ==0){
                    Toast.makeText(ActividadPedido.this, "Error: Campos obligatorios faltantes.", Toast.LENGTH_SHORT).show();
                }
                if (spproducto.getSelectedItemPosition() != 0 && Integer.parseInt(etcantidad.getText().toString()) != 0 && pedidohecho ==0){
                    Toast.makeText(ActividadPedido.this, "Error: Debe agregar el pedido.", Toast.LENGTH_SHORT).show();
                }
                if (spproducto.getSelectedItemPosition() == 0 && Integer.parseInt(etcantidad.getText().toString()) == 0 && pedidohecho >0){
                    Toast.makeText(ActividadPedido.this, "Ha finalizado el pedido.", Toast.LENGTH_SHORT).show();
                    Intent intento =  new Intent(ActividadPedido.this, ActividadRegistrados.class);
                    startActivity(intento);
                }
            }
        });

    }

    // al seleccionar un numero en el numberpicker este se visualizara en un edittext como la cantidad
    // al mismo momento que el numero seleccionado  se multiplica por el precio y este se visualizara en un textview como el total
    @Override
    public void onClick(View view){
        //numberPickerDialog();
        NumberPicker myNumberPicker = new NumberPicker(this);
        myNumberPicker.setMaxValue(productos.get(spproducto.getSelectedItemPosition()-1).getStockactual());
        myNumberPicker.setMinValue(1);
        NumberPicker.OnValueChangeListener myValChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //tvcantidad.setText(""+newVal);
                etcantidad.setText(""+newVal);
                tvtotal.setText(""+newVal * Integer.parseInt(tvprecio.getText().toString()));
                pedidohecho = 0;
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

    // al seleccionar un numero en el numberpicker este se visualizara en un textview como la cantidad
    // al mismo momento que el numero seleccionado  se multiplica por el precio y este se visualizara en un textview como el total
    //private void numberPickerDialog(){}
}
