
package com.proyectofinal.tukiwallet.Entidades;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.GenericGenerator;


@Entity
public class CuentaComun {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    private String nombreCC;
    private Boolean altaCC;
    private String cvuCC;
    private String movimientoCC;
    private Float saldoCC;
    private String aliasCC;
    private String propietario;
    
    @ManyToMany ( targetEntity=Usuario.class )
    private List<Usuario> usuarios;
    
    @OneToMany
    private List<Actividad> actividadCC;
    
    @OneToMany
    private List<EfectivoCC> efectivoCC;

    public CuentaComun() {
    }

public CuentaComun(String nombreCC, List<Usuario> usuarios) {
        this.nombreCC = nombreCC;
        this.usuarios = usuarios;
        this.altaCC = Boolean.TRUE;
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
     * @return the nombreCC
     */
    public String getNombreCC() {
        return nombreCC;
    }

    /**
     * @param nombreCC the nombreCC to set
     */
    public void setNombreCC(String nombreCC) {
        this.nombreCC = nombreCC;
    }

    /**
     * @return the altaCC
     */
    public Boolean getAltaCC() {
        return altaCC;
    }

    /**
     * @param altaCC the altaCC to set
     */
    public void setAltaCC(Boolean altaCC) {
        this.altaCC = altaCC;
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
        this.usuarios=usuarios;
    }

    /**
     * @return the actividadCC
     */
    public List<Actividad> getActividadCC() {
        return actividadCC;
    }

    /**
     * @param actividadCC the actividadCC to set
     */
    public void setActividadCC(Actividad actividadCC) {
        this.actividadCC.add(actividadCC);
    }
    /**
     * @return the efectivoCC
     */
    public List<EfectivoCC> getEfectivoCC() {
        return efectivoCC;
    }

    /**
     * @param efectivoCC the efectivoCC to set
     */
    public void setEfectivoCC(List<EfectivoCC> efectivoCC) {
        this.efectivoCC = efectivoCC;
    }

       /**
     * @return the propietario
     */
    public String getPropietario() {
        return propietario;
    }

    /**
     * @param propietario the nombreCC to set
     */
    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    @Override
    public String toString() {
        return "CuentaComun{" + "nombreCC=" + nombreCC + ", altaCC=" + altaCC + ", cvuCC=" + cvuCC + ", movimientoCC=" + movimientoCC + ", saldoCC=" + saldoCC + ", aliasCC=" + aliasCC + ", propietario=" + propietario + ", usuarios=" + usuarios + ", actividadCC=" + actividadCC + ", efectivoCC=" + efectivoCC + '}';
    }
    
}
