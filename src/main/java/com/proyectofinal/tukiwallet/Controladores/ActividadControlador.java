/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Servicios.ActividadServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author leedavidcuellar
 */
public class ActividadControlador {
    


    @Autowired
    ActividadServicio actividadServicio;
    
    @Autowired
    UsuarioServicio usuarioServicio;
    
    @GetMapping("/actividadC")
    public String listaActividades(ModelMap modelo){
        modelo.addAttribute("lista", actividadServicio.listadoActividad());
        return "actividad.html";
    }
    
    @GetMapping("/actividadCC")
    public String listarActividadesCC(ModelMap modelo){
        modelo.addAttribute("lista", actividadServicio.listadoActividadCC());
        return "actividadCC.html";
    }
    

}
