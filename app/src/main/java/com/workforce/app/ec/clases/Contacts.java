package com.workforce.app.ec.clases;

public class Contacts {

    private int id;
    private String tipo_contacto;
    private String valor;
    private String nombre;
    private int is_principal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo_contacto() {
        return tipo_contacto;
    }

    public void setTipo_contacto(String tipo_contacto) {
        this.tipo_contacto = tipo_contacto;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIs_principal() {
        return is_principal;
    }

    public void setIs_principal(int is_principal) {
        this.is_principal = is_principal;
    }

    public Contacts(int id, String tipo_contacto, String valor, String nombre, int is_principal) {
        this.id = id;
        this.tipo_contacto = tipo_contacto;
        this.valor = valor;
        this.nombre = nombre;
        this.is_principal = is_principal;
    }
}
