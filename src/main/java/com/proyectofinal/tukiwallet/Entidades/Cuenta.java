/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Entidades;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author leedavidcuellar
 */
@Entity
public class Cuenta {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    private String alias;
    private Float saldo;
    private String cvu;
    private Boolean alta;
    
    @OneToOne
    private List<Actividad> actividad;

    public Cuenta() {
    }

    public Cuenta(String alias, Float saldo, String cvu, List<Actividad> actividad) {
        this.alias = alias;
        this.saldo = saldo;
        this.cvu = cvu;
        this.actividad = actividad;
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
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
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
     * @return the cvu
     */
    public String getCvu() {
        return cvu;
    }

    /**
     * @param cvu the cvu to set
     */
    public void setCvu(String cvu) {
        this.cvu = cvu;
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
        return "Cuenta{" + "id=" + id + ", alias=" + alias + ", saldo=" + saldo + ", cvu=" + cvu + ", alta=" + alta + ", actividad=" + actividad + '}';

}

}
