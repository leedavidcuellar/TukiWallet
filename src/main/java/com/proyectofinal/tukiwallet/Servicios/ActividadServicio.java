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
    
    @Transactional(propagation = Propagation.NESTED)
    public void registrar(String motivo, Float monto, Boolean movimiento, String cvu, String cvu2) throws ErrorServicio{
        validar(motivo, monto);
        Actividad actividad = new Actividad();
        actividad.setMonto(monto);
        actividad.setMotivo(motivo);
        actividad.setFecha(new Date());
        actividad.setMovimiento(movimiento);
        actividad.setCvu(cvu);
        actividad.setCvu2(cvu2);
        actividad.setnOperacion(generarNumDeOperacion());
        
        actividadRepositorio.save(actividad);
    
    }
    @Transactional(readOnly = true)      
    public String generarNumDeOperacion(){
       String n;
       return n = actividadRepositorio.buscarNumOperacionMayor() + 1;
    }
    


   /* public String tipoMovimiento(Boolean movimiento){
        if(movimiento.TRUE){
            return "Transferencia realizada";
        }else{
            return "Dinero recibido";
        }
            
    }
    */
    
    
    public void validar(String motivo, Float monto) throws ErrorServicio{
        if(motivo == null || monto.toString().length() > 3){
            throw new ErrorServicio("Debe registrar un motivo o registro valido");
        }
        if(monto == null){
            throw new ErrorServicio("El monto no puede ser nulo");
        }
        if(monto <=10){
            throw new ErrorServicio("El monto a ingresar debe ser mayor a $10");
        }

    }
}
