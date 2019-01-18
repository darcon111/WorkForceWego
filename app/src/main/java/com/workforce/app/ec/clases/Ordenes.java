package com.workforce.app.ec.clases;

public class Ordenes {

    private int id;
    private String cliente;
    private String servicio;
    private int estado;
    private String fecha;
    private String costo;
    private String direccion;
    private String longitud;
    private String latitud;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public Ordenes(int id, String cliente, String servicio, int estado, String fecha, String costo, String direccion, String longitud, String latitud) {
        this.id = id;
        this.cliente = cliente;
        this.servicio = servicio;
        this.estado = estado;
        this.fecha = fecha;
        this.costo = costo;
        this.direccion = direccion;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public Ordenes() {
    }
}
