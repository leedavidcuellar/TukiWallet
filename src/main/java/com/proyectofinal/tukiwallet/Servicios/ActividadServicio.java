/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
import com.proyectofinal.tukiwallet.Repositorios.ActividadRepositorio;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Joaquin Calderon
 */
@Service
public class ActividadServicio {

    @Autowired
    private ActividadRepositorio actividadRepositorio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void registrar(String motivo, Float monto, boolean movimiento, String cvu, String cvu2) throws ErrorServicio {
        validar(motivo, monto);
        Actividad actividad = new Actividad();
        actividad.setMonto(monto);
        actividad.setMotivo(motivo);
        actividad.setFecha(new Date());
        if (movimiento) {
            actividad.setMovimiento(Boolean.TRUE);  
        }else{
            actividad.setMovimiento(Boolean.FALSE);  
        }
        actividad.setCvu(cvu);
        actividad.setCvu2(cvu2);
        actividad.setnOperacion(generarNumDeOperacion());
        
        actividadRepositorio.save(actividad);

    }

    @Transactional(readOnly = true)
    public String generarNumDeOperacion() {
        String n;
        if (actividadRepositorio.buscarNumOperacionMayor()==null) {
            return "1";
        }else{
            return n = actividadRepositorio.buscarNumOperacionMayor().getnOperacion() + 1;
        }
    }

    /* public String tipoMovimiento(Boolean movimiento){
        if(movimiento.TRUE){
            return "Transferencia realizada";
        }else{
            return "Dinero recibido";
        }
            
    }
     */
    public void validar(String motivo, Float monto) throws ErrorServicio {
        if (motivo == null || monto.toString().length() < 1) {
            throw new ErrorServicio("Debe registrar un motivo o registro valido");
        }
        if (monto == null) {
            throw new ErrorServicio("El monto no puede ser nulo");
        }
        if (monto <= 10) {
            throw new ErrorServicio("El monto a ingresar debe ser mayor a $10");
        }

    }

    public List<Actividad> listadoActividad() {
        return (List<Actividad>) actividadRepositorio.findAll();
    }

    public List<Actividad> listadoActividadCC() {
        return (List<Actividad>) actividadRepositorio.findAll();
    }
}
