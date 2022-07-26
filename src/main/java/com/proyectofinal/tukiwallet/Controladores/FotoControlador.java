/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author leedavidcuellar
 */
@Controller
@RequestMapping("/foto")
public class FotoControlador {
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @GetMapping("/usuario/{id}")
    public ResponseEntity<byte[]> fotoUsuario(@PathVariable String id) throws ErrorServicio{
       Usuario usuario = usuarioServicio.buscarPorId(id);
       if(usuario.getFoto()==null){
           throw new ErrorServicio("El Usuario no tiene foto asignada");
       }
       
       byte[] foto = usuario.getFoto().getContenido();// saco la foto
       
       HttpHeaders headers = new HttpHeaders();//para indicar que es una foto
       headers.setContentType(MediaType.IMAGE_JPEG);
       return new ResponseEntity<>(foto,headers,HttpStatus.OK);//como devuelvo
    }
    
}
