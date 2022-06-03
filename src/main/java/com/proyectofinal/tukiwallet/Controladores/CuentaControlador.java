/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Servicios.CuentaServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author leedavidcuellar
 */
@Controller
@RequestMapping("/cuenta")
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class CuentaControlador {
    @Autowired
    private CuentaServicio cuentaServicio;
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @GetMapping("/micuenta")
    public String miCuenta(ModelMap modelo, HttpSession session, String id){
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
    Usuario usuarioCuenta = usuarioServicio.buscarPorId(id);
        modelo.addAttribute("micuenta", usuarioCuenta);

        return "cuenta.html";
    }
    
   
    
}
