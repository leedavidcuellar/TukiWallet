/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.Foto;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Repositorios.CuentaComunRepositorio;
import com.proyectofinal.tukiwallet.Repositorios.CuentaRepositorio;
import com.proyectofinal.tukiwallet.Repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Joaquin Calderon
 */

@Service
public class UsuarioServicio {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private NotificacionServicio notificacionServicio;
    
    @Autowired
    private CuentaRepositorio cuentaRepositorio;
    
    @Autowired
    private CuentaComunRepositorio cuentaComunRepositorio;
    
    @Autowired
    private FotoServicio fotoServicio;
    
     @Transactional(propagation = Propagation.REQUIRED)
     public void registrarUsuario(MultipartFile archivo, String nombre, String apellido, String dni, String mail, String clave1, String clave2, String idCuenta, String idCuentaComun) throws ErrorServicio{
         List<CuentaComun>listaCuentaComun = new ArrayList<CuentaComun>();
         Cuenta cuenta = cuentaRepositorio.getById(idCuenta);
         CuentaComun cuentaComun = cuentaComunRepositorio.getById(idCuentaComun);
         
         validar(nombre, apellido, dni, mail, clave1, clave2, cuenta, cuentaComun);
         listaCuentaComun.add(cuentaComun);
         
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
         
        String encriptada = new BCryptPasswordEncoder().encode(clave1);
        usuario.setClave(encriptada);
        usuario.setAlta(Boolean.TRUE);

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);
        
        usuario.setCuenta(cuenta);
        usuario.setCuentaComun(listaCuentaComun);
        
        usuarioRepositorio.save(usuario);
        
        notificacionServicio.enviar("Bienvenido al TukiWallet", "Tuki Wallet", usuario.getMail());
        
     }
     
    @Transactional(propagation = Propagation.NESTED) 
    public void modificarUsuario(MultipartFile archivo, String idUsuario, String nombre, String apellido, String dni, String mail, String clave1, String clave2, String idCuenta, String idCuentaComun) throws ErrorServicio{
        List<CuentaComun>listaCuentaComun = new ArrayList<CuentaComun>();
        Cuenta cuenta = cuentaRepositorio.getById(idCuenta);
        CuentaComun cuentaComun = cuentaComunRepositorio.getById(idCuentaComun);
        
        validar(nombre, apellido, dni, mail, clave1, clave2, cuenta, cuentaComun);
        listaCuentaComun.add(cuentaComun);
        
         Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
         if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(mail);
            
            String encriptada = new BCryptPasswordEncoder().encode(clave1);
            usuario.setClave(encriptada);

            String idFoto = null;
            if(usuario.getFoto()!= null){
                idFoto = usuario.getFoto().getId();
            }
            
            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);
            
            usuario.setCuenta(cuenta);
            usuario.setCuentaComun(listaCuentaComun);
            
            usuarioRepositorio.save(usuario);
            
            notificacionServicio.enviar("Se Modifico su Usuario de TukiWallet", "TukiWallet", usuario.getMail());
         }else{
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
        
    } 
     
    @Transactional(propagation = Propagation.REQUIRED)
    public void eliminarUsuario(String idUsuario) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            if(usuario.getAlta()== Boolean.FALSE){
            usuarioRepositorio.delete(usuario);
            }else{
               throw new ErrorServicio("No se puede eliminar, porque Usuario esta activo, debe darlo baja"); 
            }
        } else {
            throw new ErrorServicio("No existe un Usuario con el identificador solicitado");
        }
    }
     
     
    @Transactional(propagation = Propagation.NESTED)
    public void deshabilitarUsuario(String id) throws ErrorServicio{
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setAlta(Boolean.FALSE);

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }   
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void habilitarUsuario(String id) throws ErrorServicio{
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setAlta(Boolean.TRUE);

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }   
    }
    
    @Transactional(readOnly = true)
    public Usuario buscarPorId(String id){
        return usuarioRepositorio.buscarPorId(id);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorDni(String dni){
        return usuarioRepositorio.buscarPorDni(dni);
    }    
    
    @Transactional(readOnly = true)
    public Usuario buscarPorMail(String mail){
        return usuarioRepositorio.buscarPorMail(mail);
    }
    
    @Transactional(readOnly = true)
    public Usuario buscarPorCuentaId(String idCuenta){
        return usuarioRepositorio.buscarPorCuentaId(idCuenta);
    }
    
    @Transactional(readOnly = true)
    public Usuario buscarPorCuentaComunId(String idCuentaComun){
        return usuarioRepositorio.buscarPorCuentaComunId(idCuentaComun);
    }  
    
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios(){
        return usuarioRepositorio.findAll();
    }
    
    
    private void validar(String nombre, String apellido, String dni, String mail, String clave1, String clave2, Cuenta cuenta, CuentaComun cuentaComun) throws ErrorServicio{
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo");
        }

        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no puede ser nulo");
        }

        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo");
        }

        if (clave1 == null || clave1.isEmpty() || clave1.length() < 6) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo y no puede ser menor de 6 caracteres");
        }
        
        if (!clave1.equals(clave2)) {
            throw new ErrorServicio("Las claves tiene que ser iguales");
        }
        
        if(cuenta == null){
            throw new ErrorServicio("No se encontro la cuenta solicitada");
        }
        
        if(cuentaComun == null){
            throw new ErrorServicio("No se encontro la cuenta comun solicitada");
        }
     }
    
}
