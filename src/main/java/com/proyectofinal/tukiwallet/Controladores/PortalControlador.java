/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author leedavidcuellar
 */
@Controller
@RequestMapping("/")
public class PortalControlador {
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(required=false)String error, ModelMap modelo,@RequestParam(required =false) String logout,  RedirectAttributes redirectAttrs) {
        if(error!= null){
            modelo.put("error","Usuario o Clave incorrecta");
        }
        
        if(logout != null){
            modelo.put("logout","Ha salido correctamente de la plataforma");
        }
        return "login.html";
    } 
    
    @GetMapping("/registrarse")
    public String registrarse(){
    return "registrarse.html";
    }
}
