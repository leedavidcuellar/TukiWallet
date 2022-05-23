/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author leedavidcuellar
 */
@Entity
public class Actividad {
        @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private Integer nOperacion;
    private Float monto;
    private String motivo;
    private Date fecha;
    private Boolean alta;

    public Actividad() {
    }

    public Actividad(Float monto, String motivo, Date fecha) {
        this.monto = monto;
        this.motivo = motivo;
        this.fecha = fecha;
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
     * @return the monto
     */
    public Float getMonto() {
        return monto;
    }

    /**
     * @param monto the monto to set
     */
    public void setMonto(Float monto) {
        this.monto = monto;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
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

    @Override
    public String toString() {
        return "Actividad{" + "id=" + getId() + ", monto=" + getMonto() + ", motivo=" + getMotivo() + ", fecha=" + getFecha() + ", alta=" + alta + '}';
    }

    /**
     * @return the nOperacion
     */
    public Integer getnOperacion() {
        return nOperacion;
    }

    /**
     * @param nOperacion the nOperacion to set
     */
    public void setnOperacion(Integer nOperacion) {
        this.nOperacion = nOperacion;
    }
 
}
