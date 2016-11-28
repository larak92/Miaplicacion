package com.miaplicacion.lau.miaplicacion;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActividadPrincipal extends AppCompatActivity {

    public Spinner spcliente;
    public Button btsiguiente;
    public TextView tvdireccion;
    public TextView tvtelefono;
    public String f;
    public List<Cliente> clientes;
    private BaseDatosPedidos bdp;
    private SQLiteDatabase db;
    public int n;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bdp = new BaseDatosPedidos(this);
        db = bdp.getReadableDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);

        // traer todas las filas de la tabla cliente, instanciar en objetos y almacenarlos en una lista
        clientes = new ArrayList<>();
        String query = "SELECT * FROM cliente;";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            int cid = Integer.parseInt(cursor.getString(0));
            String cnombre = cursor.getString(1);
            String capellido = cursor.getString(2);
            String ctelefono = cursor.getString(3);
            String cdireccion = cursor.getString(4);
            Cliente cliente = new Cliente(cid, cnombre, capellido, ctelefono, cdireccion);
            clientes.add(cliente);
        }

        // para mostrar en el spinner el nombre+apellido
        String valuesspinner [] = new String[clientes.size()+1];
        n=0;
        valuesspinner[n] = "Seleccione una opción";
        for (int i=0; i<clientes.size(); i++){
            n++;
            valuesspinner[n] = clientes.get(i).getNombre() +" "+ clientes.get(i).getApellido();
        }

        // obtenemos una referencia a los controles de la interfaz
        spcliente = (Spinner)findViewById(R.id.spinner2);
        tvdireccion = (TextView) findViewById(R.id.textView12);
        tvtelefono = (TextView) findViewById(R.id.textView13);
        tvdireccion.setText("- -");
        tvtelefono.setText("- -");

        // al seleccionar al cliente del spinner se muestra la dirección y el teléfono
        spcliente.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, valuesspinner));
        spcliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0 ){
                    tvdireccion.setText(clientes.get(position-1).getDireccion());
                    tvtelefono.setText(clientes.get(position-1).getTelefono());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // obtenemos la fecha del sistema
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        f = df.format(c.getTime());

        // mientras no seleccione un cliente y presione el boton, no podra pasar a la siguiente actividad
        // al seleccionar un cliente y presionar el boton, se crea la cabecera del pedido y pasa a la siguiente actividad
        btsiguiente = (Button)findViewById(R.id.button2);
        btsiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spcliente.getSelectedItemPosition() == 0){
                    Toast.makeText(ActividadPrincipal.this, "Error: Debe seleccionar un cliente.", Toast.LENGTH_SHORT).show();
                }else{
                    // agrega la cabecera del pedido
                    db.beginTransaction();
                    try{
                        ContentValues values = new ContentValues();
                        values.put("id_cliente", clientes.get(spcliente.getSelectedItemPosition()-1).getIdCliente());
                        values.put("fecha", f);
                        db.insert("cabecera_pedido", null, values);
                        db.setTransactionSuccessful();
                    }finally {
                        db.endTransaction();
                        Toast.makeText(ActividadPrincipal.this, "Cliente registrado con éxito!", Toast.LENGTH_SHORT).show();
                    }
                    // pasa a la siguiente actividad
                    Intent intento = new Intent(ActividadPrincipal.this, ActividadPedido.class);
                    startActivity(intento);
                }
            }
        });
    }
}
