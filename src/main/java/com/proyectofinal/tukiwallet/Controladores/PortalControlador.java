/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaComunServicio;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    
    @Autowired
    private CuentaComunServicio cuentaComunServicio;
    
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
    
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")    
    @GetMapping("/inicio")
    public String inicio(HttpSession session, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if(login == null){
            
            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(login.getId());
        model.addAttribute("listaCC",listaCC);
                return "principalFinal.html";
    }
}
