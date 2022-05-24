
package com.proyectofinal.tukiwallet.Entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;


@Entity
public class Actividad {
        @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String nOperacion;
    private Float monto;
    private String motivo;
    private Date fecha;
    private Boolean movimiento;
    private String cvu;
    private String cvu2;
    
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
    public Boolean getMovimiento() {
        return movimiento;
    }

    /**
     * @param alta the alta to set
     */
    public void setMovimiento(Boolean alta) {
        this.movimiento = alta;
    }

    @Override
    public String toString() {
        return "Actividad{" + "id=" + getId() + ", monto=" + getMonto() + ", motivo=" + getMotivo() + ", fecha=" + getFecha() + ", alta=" + getMovimiento() + '}';
    }

    /**
     * @return the nOperacion
     */
    public String getnOperacion() {
        return nOperacion;
    }

    /**
     * @param nOperacion the nOperacion to set
     */
    public void setnOperacion(String nOperacion) {
        this.nOperacion = nOperacion;
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
     * @return the cvu2
     */
    public String getCvu2() {
        return cvu2;
    }

    /**
     * @param cvu2 the cvu2 to set
     */
    public void setCvu2(String cvu2) {
        this.cvu2 = cvu2;
    }
 
}
