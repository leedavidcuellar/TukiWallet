
package com.proyectofinal.tukiwallet.Entidades;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.GenericGenerator;


@Entity
public class CuentaComun {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    private String nombre;
    private Boolean alta;
    private String cvuCC;
    private String movimientoCC;
    private Float saldoCC;
    private String aliasCC;

    @OneToMany
    private List<Usuario> usuarios;
    
    @OneToOne
    private List<Actividad> actividad;

    public CuentaComun() {
    }

public CuentaComun(String nombre, List<Usuario> usuarios) {
        this.nombre = nombre;
        this.usuarios = usuarios;
        this.alta = Boolean.TRUE;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the alta
     */
    public Boolean getAlta() {
        return alta;
    }

    /**
     * @param alta the alta to set
     */
    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

    /**
     * @return the cvuCC
     */
    public String getCvuCC() {
        return cvuCC;
    }

    /**
     * @param cvuCC the cvuCC to set
     */
    public void setCvuCC(String cvuCC) {
        this.cvuCC = cvuCC;
    }

    /**
     * @return the movimientoCC
     */
    public String getMovimientoCC() {
        return movimientoCC;
    }

    /**
     * @param movimientoCC the movimientoCC to set
     */
    public void setMovimientoCC(String movimientoCC) {
        this.movimientoCC = movimientoCC;
    }

    /**
     * @return the saldoCC
     */
    public Float getSaldoCC() {
        return saldoCC;
    }

    /**
     * @param saldoCC the saldoCC to set
     */
    public void setSaldoCC(Float saldoCC) {
        this.saldoCC = saldoCC;
    }

    /**
     * @return the aliasCC
     */
    public String getAliasCC() {
        return aliasCC;
    }

    /**
     * @param aliasCC the aliasCC to set
     */
    public void setAliasCC(String aliasCC) {
        this.aliasCC = aliasCC;
    }

    /**
     * @return the usuarios
     */
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * @param usuarios the usuarios to set
     */
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * @return the actividad
     */
    public List<Actividad> getActividad() {
        return actividad;
    }

    /**
     * @param actividad the actividad to set
     */
    public void setActividad(List<Actividad> actividad) {
        this.actividad = actividad;
    }

    @Override
    public String toString() {
        return "CuentaComun{" + "id=" + id + ", nombre=" + nombre + ", alta=" + alta + ", cvuCC=" + cvuCC + ", movimientoCC=" + movimientoCC + ", saldoCC=" + saldoCC + ", aliasCC=" + aliasCC + ", usuarios=" + usuarios + ", actividad=" + actividad + '}';
    }

   
}
