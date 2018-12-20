package com.workforce.app.ec.clases;

/**
 * Created by USUARIO-PC on 20/03/2017.
 */
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String id;
    private String email;
    private String url_imagen;
    private String firebase_code;
    private String provider;
    private String date_created;
    private String firebaseId;
    private String name;
    private String lastname;
    private String fecha_nac;
    private String genero;
    private String lat;
    private String log;
    private String biografia;
    private String type;
    private String valoracion;
    private String mobile;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }

    public String getFirebase_code() {
        return firebase_code;
    }

    public void setFirebase_code(String firebase_code) {
        this.firebase_code = firebase_code;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFecha_nac() {
        return fecha_nac;
    }

    public void setFecha_nac(String fecha_nac) {
        this.fecha_nac = fecha_nac;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }



    public User() {
    }


    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public User(String id, String email, String url_imagen, String firebase_code, String provider, String date_created, String firebaseId, String name, String lastname, String fecha_nac, String genero, String lat, String log, String biografia, String type, String valoracion, String mobile) {
        this.id = id;
        this.email = email;
        this.url_imagen = url_imagen;
        this.firebase_code = firebase_code;
        this.provider = provider;
        this.date_created = date_created;
        this.firebaseId = firebaseId;
        this.name = name;
        this.lastname = lastname;
        this.fecha_nac = fecha_nac;
        this.genero = genero;
        this.lat = lat;
        this.log = log;
        this.biografia = biografia;
        this.type = type;
        this.valoracion = valoracion;
        this.mobile = mobile;
    }
}
