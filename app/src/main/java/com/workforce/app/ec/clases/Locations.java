package com.workforce.app.ec.clases;

public class Locations {

    private int id;
    private String nombre;
    private String latitud;
    private String longitud;
    private String direccion;
    private String piso;
    private String departamento;
    private int is_principal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public int getIs_principal() {
        return is_principal;
    }

    public void setIs_principal(int is_principal) {
        this.is_principal = is_principal;
    }

    public Locations(int id, String nombre, String latitud, String longitud, String direccion, String piso, String departamento, int is_principal) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.piso = piso;
        this.departamento = departamento;
        this.is_principal = is_principal;
    }
}
