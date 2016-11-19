package com.miaplicacion.lau.miaplicacion;

/**
 * Created by lau on 17/10/16.
 */

public class Producto {

    private int idProducto;

    private String nombre;

    private int precio;

    private int existencias;

    private int stockactual;

    public Producto(int idProducto, String nombre, int precio, int existencias, int stockactual) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.existencias = existencias;
        this.stockactual = stockactual;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getExistencias() {
        return existencias;
    }

    public void setExistencias(int existencias) {
        this.existencias = existencias;
    }

    public int getStockactual() {
        return stockactual;
    }

    public void setStockactual(int stockactual) {
        this.stockactual = stockactual;
    }
}