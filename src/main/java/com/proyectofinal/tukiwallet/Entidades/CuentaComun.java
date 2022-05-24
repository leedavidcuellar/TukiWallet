
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
    private String movimiento;
    private Float saldo;
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
     * @return the movimiento
     */
    public String getMovimiento() {
        return movimiento;
    }

    /**
     * @param movimiento the movimiento to set
     */
    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    /**
     * @return the saldo
     */
    public Float getSaldo() {
        return saldo;
    }

    /**
     * @param saldo the saldo to set
     */
    public void setSaldo(Float saldo) {
        this.saldo = saldo;
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

    @Override
    public String toString() {
        return "CuentaComun{" + "id=" + id + ", nombre=" + nombre + ", alta=" + alta + ", cvuCC=" + cvuCC + ", movimiento=" + movimiento + ", saldo=" + saldo + ", aliasCC=" + aliasCC + ", usuarios=" + usuarios + ", actividad=" + actividad + '}';
    }

}
