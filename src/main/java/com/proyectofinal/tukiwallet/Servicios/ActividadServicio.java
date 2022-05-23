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
import java.util.Optional;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * @author Joaquin Calderon
 */
@Service
public class ActividadServicio {
    
    @Autowired
    private ActividadRepositorio actividadRepositorio;
    
    public void registrar(String motivo, Float monto) throws ErrorServicio{
        validar(motivo, monto);
        Actividad actividad = new Actividad();
        actividad.setMonto(monto);
        actividad.setMotivo(motivo);
        actividad.setFecha(new Date());
        actividad.setAlta(Boolean.TRUE);
        
    }
    
  /*  public void darDeBaja(String id) throws ErrorServicio{
       Optional<Actividad> resp = actividadRepositorio.findById(id);
       if(resp.isPresent()){
           Actividad actividad = resp.get();
           actividad.setAlta(Boolean.FALSE);
           actividadRepositorio.save(actividad);
       }else{
           throw new ErrorServicio("La operacion solicitada no se encuentra registrada");
       }
   } 
   
      public void darDeAlta(String id) throws ErrorServicio{
       Optional<Actividad> resp = actividadRepositorio.findById(id);
       if(resp.isPresent()){
           Actividad actividad = resp.get();
           actividad.setAlta(Boolean.TRUE);
           actividadRepositorio.save(actividad);
       }else{
           throw new ErrorServicio("La operacion solicitada no se encuentra registrada");
       }
   } */

    public void validar(String motivo, Float monto) throws ErrorServicio{
        if(motivo == null){
            throw new ErrorServicio("Debe registrar un motivo o registro valido");
        }//preguntar si se puede hacer que tenga un maximo de caracteres. y si da, como rayos se hace
        if(monto == null){
            throw new ErrorServicio("El monto no puede ser nulo");
        }
        if(monto <=10){
            throw new ErrorServicio("El monto a ingresar debe ser mayor a $10");
        }

    }
}
