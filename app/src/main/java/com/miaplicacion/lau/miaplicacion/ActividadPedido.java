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

    //public TextView tvcantidad;
    //public Button bcantidad;
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
    public int pedidohecho; // funciona como una bandera
    public ArrayAdapter<String> spinnerData;

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
            int pstockactual = Integer.parseInt(cursor.getString(4));
            Producto producto = new Producto(pid, pnombre, pprecio, pexistencia, pstockactual);
            productos.add(producto);
        }

        //para mostrar en el spinner el nombre de los productos
        valuesspinner = new String[productos.size()+1];
        n=0;
        valuesspinner[n] = "Seleccione una opcion";
        for (int i=0; i<productos.size(); i++){
            n++;
            valuesspinner[n] = productos.get(i).getNombre();
        }

        // obtenemos una referencia a los controles de la interfaz
        spproducto = (Spinner)findViewById(R.id.spinner);
        tvprecio = (TextView)findViewById(R.id.textView5);
        tvprecio.setText("- -"); // agregado hoy 26/11

        // al seleccionar un producto se aguarda la posicion a lo cual lo utilizamos para acceder a  lista de
        // productos y asi obtener el precio correspondiente al producto
        spinnerData = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, valuesspinner);
        spproducto.setAdapter(spinnerData);
        spproducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0 ){
                    tvprecio.setText(String.valueOf(productos.get(position-1).getPrecio()));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // el boton cantidad llama a la funcion del numberpicker con un minimo y maximo
        //bcantidad = (Button)findViewById(R.id.button6);
        //bcantidad.setOnClickListener(this);
        //tvcantidad = (TextView)findViewById(R.id.textView3);
        etcantidad = (EditText)findViewById(R.id.editText);
        etcantidad.setText("0"); // agregado hoy 26/11
        etcantidad.setOnClickListener(this);
        tvtotal = (TextView)findViewById(R.id.textView8);
        tvtotal.setText("0"); // agregado hoy 26/11
        pedidohecho = 0;

        // traer el id de la cabecera creada en la vista anterior
        Cursor cursorid = db.rawQuery("SELECT MAX(id_cabecera) AS cabecera FROM cabecera_pedido", null);
        cursorid.moveToFirst();
        idcabecera = cursorid.getInt( cursorid.getColumnIndex("cabecera"));

        // al presionar el boton agregar se crea el detalle del pedido
        bagregar = (Button)findViewById(R.id.button);
        bagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spproducto.getSelectedItemPosition() == 0){
                    Toast.makeText(ActividadPedido.this, "Error: Debe seleccionar un producto.", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(etcantidad.getText().toString()) == 0){
                    Toast.makeText(ActividadPedido.this, "Error: Debe seleccionar una cantidad.", Toast.LENGTH_SHORT).show();
                }else{
                    pedidohecho++;
                    ContentValues values = new ContentValues();
                    values.put("id_cabecera",  idcabecera);
                    values.put("id_producto", productos.get(spproducto.getSelectedItemPosition()-1).getIdProducto());
                    //values.put("cantidad", Integer.parseInt(tvcantidad.getText().toString()));
                    values.put("cantidad", Integer.parseInt(etcantidad.getText().toString()));
                    values.put("precio", Integer.parseInt(tvprecio.getText().toString()));
                    values.put("total", Integer.parseInt(tvtotal.getText().toString()));
                    db.insert("detalle_pedido", null, values);
                    Toast.makeText(ActividadPedido.this, "Producto registrado con éxito!", Toast.LENGTH_SHORT).show();
                    // Actualizar el stock_actual
                    resto = productos.get(spproducto.getSelectedItemPosition()-1).getStockactual() - Integer.parseInt(etcantidad.getText().toString());
                    ContentValues cv = new ContentValues();
                    cv.put("stock_actual",resto);// borrar resto cuando se pueda
                    idproupdate = productos.get(spproducto.getSelectedItemPosition()-1).getIdProducto();
                    db.update("producto",cv,"id_producto ="+idproupdate,null);
                    // limpiar el producto, precio, cantidad y el total luego de ser agregado
                    spproducto.setAdapter(spinnerData);
                    tvprecio.setText("- -");
                    etcantidad.setText("0");
                    tvtotal.setText("0");
                }
            }
        });

        // al presionar el boton siguiente pasa a la siguiente actividad
        bsiguiente = (Button)findViewById(R.id.button3);
        bsiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (spproducto.getSelectedItemPosition() == 0){
                    Toast.makeText(ActividadPedido.this, "Error: Debe seleccionar un producto.", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(etcantidad.getText().toString()) == 0){
                    Toast.makeText(ActividadPedido.this, "Error: Debe seleccionar una cantidad.", Toast.LENGTH_SHORT).show();
                    // para que al hacer el primer pedido pueda pasar a la tercera vista si NO quiere mas pedidos
                }else if (pedidohecho == 0){
                    Toast.makeText(ActividadPedido.this, "Error: Debe agregar el pedido.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intento =  new Intent(ActividadPedido.this, ActividadRegistrados.class);
                    startActivity(intento);
                    Toast.makeText(ActividadPedido.this, "Pedido registrado con éxito.", Toast.LENGTH_SHORT).show();
                }
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
        myNumberPicker.setMaxValue(productos.get(spproducto.getSelectedItemPosition()-1).getStockactual());// no es asi nomas
        myNumberPicker.setMinValue(1);
        NumberPicker.OnValueChangeListener myValChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //tvcantidad.setText(""+newVal);
                etcantidad.setText(""+newVal);
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
