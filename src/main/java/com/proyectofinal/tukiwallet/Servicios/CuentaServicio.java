/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Servicios;

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
    
    @Transactional(propagation = Propagation.NESTED)
    public void registrar(String dni) throws ErrorServicio{
        String alias = dni+".TUKI";
        Cuenta cuenta = new Cuenta();
        cuenta.setAlias(alias);
        cuenta.setSaldo(0f);
        cuenta.setCvu(dni);
        cuenta.setAlta(true);
        
        cuentaRepositorio.save(cuenta);
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void modificarAlias(String alias, String id) throws ErrorServicio{
        validarAlias(alias);
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();
            cuenta.setAlias(alias);
            cuentaRepositorio.save(cuenta);
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void depositar(Float deposito, String id) throws ErrorServicio{
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();
            cuenta.setSaldo(cuenta.getSaldo()+deposito);
            cuentaRepositorio.save(cuenta);
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void transferir(Float transferencia, String id) throws ErrorServicio{
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();
            cuenta.setSaldo(cuenta.getSaldo()-transferencia);
            cuentaRepositorio.save(cuenta);
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void baja(String id)throws ErrorServicio{
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta editorial = respuesta.get();
            editorial.setAlta(false);
            cuentaRepositorio.save(editorial);
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void alta(String id)throws ErrorServicio{
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta editorial = respuesta.get();
            editorial.setAlta(true);
            cuentaRepositorio.save(editorial);
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
}
