/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Repositorios.CuentaRepositorio;
import com.proyectofinal.tukiwallet.Repositorios.UsuarioRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Joaquin Calderon
 */
@Service
public class CuentaServicio {
    
    @Autowired 
    private CuentaRepositorio cuentaRepositorio;
    
    @Autowired 
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private ActividadServicio actividadServicio;
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Cuenta registrar(String dni) throws ErrorServicio{
        String alias = dni+".TUKI";
        comprobarAlias(alias);
        Cuenta cuenta = new Cuenta();
        cuenta.setAlias(alias);
        cuenta.setSaldo(0f);
        cuenta.setCvu(crearCvu(dni));
        cuenta.setAlta(true);
        
        cuentaRepositorio.save(cuenta);
        return cuenta;
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public Cuenta modificarAlias(String alias, String id) throws ErrorServicio{
        validarAlias(alias);
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();
            cuenta.setAlias(alias);
            cuentaRepositorio.save(cuenta);
            return cuenta;
            
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
        
    }
    
    //ingresa
    @Transactional(propagation = Propagation.NESTED)
    public void ingresoCuenta(Float cantidad, String cvuEgresa, String cvuIngresa, String motivo) throws ErrorServicio {
        Cuenta cuenta = cuentaRepositorio.buscarCuentaPorCvu(cvuIngresa);
        if (cuenta != null) {
            cuenta.setSaldo(cuenta.getSaldo() + cantidad);
            cuentaRepositorio.save(cuenta);
            actividadServicio.registrar(motivo, cantidad, false, cvuEgresa, cvuIngresa);
        } else {
            throw new ErrorServicio("No se ha encontrado el Cuenta");
        }
    }
    
    //egresa
    @Transactional(propagation = Propagation.NESTED)
    public void egresoCuenta(Float cantidad, String cvuEgresa, String cvuIngresa, String motivo) throws ErrorServicio {
        Cuenta cuenta = cuentaRepositorio.buscarCuentaPorCvu(cvuEgresa);
        if (cuenta != null) {
            cuenta.setSaldo(cuenta.getSaldo() - cantidad);
            cuentaRepositorio.save(cuenta);
            actividadServicio.registrar(motivo, cantidad, true, cvuEgresa, cvuIngresa);
        } else {
            throw new ErrorServicio("No se ha encontrado el Cuenta");
        }
    }
    
    public void validarTransferenciaCuenta(Float cantidad, String idtransfiere) throws ErrorServicio{
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(idtransfiere);
        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();
            if (cuenta.getSaldo()<cantidad) {
                throw new ErrorServicio("No tiene esa cantidad en su cuenta");
            }
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void baja(String id)throws ErrorServicio{
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();
            cuenta.setAlta(false);
            cuentaRepositorio.save(cuenta);
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void alta(String id)throws ErrorServicio{
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();
            cuenta.setAlta(true);
            cuentaRepositorio.save(cuenta);
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void borrarPorId (String id) throws ErrorServicio{
        Optional<Cuenta> optional = cuentaRepositorio.findById(id);
        if (optional.isPresent()) {
            cuentaRepositorio.delete(optional.get());
        }else{
            throw new ErrorServicio("No se encontró el id");
        }
    } 
    
    public void validarSaldo(Float saldo) throws ErrorServicio{
        if (saldo == null || saldo.toString().trim().isEmpty()) {
            throw new ErrorServicio("El saldo no puede ser nulo"); 
        }
    }
    
    public void validarAlias(String alias) throws ErrorServicio{
        if (alias == null || alias.trim().isEmpty()) {
            throw new ErrorServicio("El alias no puede ser nulo"); 
        }
    }
    
    @Transactional(readOnly = true)
    public Cuenta buscarCuentaPorAlias(String alias){
        Cuenta autor = cuentaRepositorio.buscarCuentaPorAlias(alias);
        if (autor!=null) {
           return cuentaRepositorio.buscarCuentaPorAlias(alias); 
        }else{
            return null;
        }      
    }
    
    @Transactional(readOnly = true)
    public List<Cuenta> mostrarTodos(){
        return cuentaRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Cuenta> mostrarAlta(){
        return cuentaRepositorio.mostrarCuentaAlta();
    }
    
    @Transactional(readOnly = true)
    public List<Cuenta> mostrarBaja(){
        return cuentaRepositorio.mostrarCuentaBaja();
    }
    
    public String crearCvu (String dni){
        String cvu = "00001" + dni;
        Integer cant = dni.length();
        cant = cant + 5; //Para agregar 00001 al principio
        cant = 20-cant;
        Integer temp = 0;
        for (int i = 0; i < cant; i++) {
            temp = (int)(Math.random()*10);
            cvu = cvu + temp.toString();
        }
        comprobarCvu(cvu, dni);
        return cvu;
    }
    
    @Transactional(readOnly = true)
    public void comprobarCvu (String cvu, String dni){
        Cuenta optional = cuentaRepositorio.buscarCuentaPorCvu(cvu);
        if (optional!=null) {
            crearCvu(dni);
        }
    }
    
    @Transactional(readOnly = true)
    public String comprobarAlias (String alias){
        Cuenta optional = cuentaRepositorio.buscarCuentaPorAlias(alias);
        if (optional==null) {
            return alias;
        }else{
            Integer temp;
            for (int i = 0; i < 5; i++) {
            temp = (int)(Math.random()*10);
            alias = temp.toString() + alias;
            }
            return alias;
        }        
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void agregarActividad(Actividad actividad, String id) throws ErrorServicio{
        Optional<Cuenta> optional = cuentaRepositorio.findById(id);
        if (optional.isPresent()) {
            Cuenta cuenta = optional.get();
            List<Actividad> actividades = cuenta.getActividad();
            actividades.add(actividad);
            cuentaRepositorio.save(cuenta);
        }else{
            throw new ErrorServicio("No se encontró el id");
        }
    }
    
    
}
