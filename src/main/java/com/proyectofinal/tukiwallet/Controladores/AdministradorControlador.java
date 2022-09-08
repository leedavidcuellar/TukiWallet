/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.ActividadServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaComunServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author leedavidcuellar
 */
@Controller
@RequestMapping("/admin")
public class AdministradorControlador {

    @Autowired
    private CuentaComunServicio cuentaComunServicio;

    @Autowired
    private CuentaServicio cuentaServicio;

    @Autowired
    private ActividadServicio actividadServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/dashboard")
    public String panelAdministrador(HttpSession session, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }

        List<Cuenta> listaCuentas = cuentaServicio.mostrarTodos();
        model.addAttribute("listaCuentas", listaCuentas);
        model.addAttribute("usuariosession", login);

        return "administrador.html";
    }

    @PostMapping("/buscarPorAlias")
    public String buscarPorAlias(HttpSession session, @RequestParam String alias, ModelMap model) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }

        List<Cuenta> listaCuentas = new ArrayList<Cuenta>();
        Cuenta cuenta = cuentaServicio.buscarCuentaPorAlias(alias);

        if (cuenta != null) {
            listaCuentas.add(cuenta);

            model.addAttribute("listaCuentas", listaCuentas);
            model.addAttribute("usuariosession", login);

            return "administrador.html";
        } else {

            listaCuentas = cuentaServicio.mostrarTodos();
            model.addAttribute("listaCuentas", listaCuentas);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error el alias es incorrecto ");
            model.put("clase1", "danger");
            return "administrador.html";
        }
    }

    @PostMapping("/buscarPorCvu")
    public String buscarPorCvu(HttpSession session, @RequestParam String cvu, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<Cuenta> listaCuentas = new ArrayList<Cuenta>();
        Cuenta cuenta = cuentaServicio.buscarCuentaPorCbu(cvu);

        if (cuenta != null) {

            listaCuentas.add(cuenta);
            model.addAttribute("listaCuentas", listaCuentas);
            model.addAttribute("usuariosession", login);

            return "administrador.html";
        } else {
            listaCuentas = cuentaServicio.mostrarTodos();
            model.addAttribute("listaCuentas", listaCuentas);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error el Cvu es incorrecto ");
            model.put("clase1", "danger");
            return "administrador.html";
        }
    }

    @PostMapping("/buscarPorEstado")
    public String buscarPorEstado(HttpSession session, @RequestParam String estado, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<Cuenta> listaCuentas = new ArrayList<Cuenta>();

        if (estado.equals("Activo")) {

            listaCuentas = cuentaServicio.mostrarAlta();
        } else {
            listaCuentas = cuentaServicio.mostrarBaja();
        }

        if (listaCuentas.isEmpty()) {
            listaCuentas = cuentaServicio.mostrarTodos();
            model.addAttribute("listaCuentas", listaCuentas);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error - No Hay Usuarios " + estado);
            model.put("clase1", "danger");
            return "administrador.html";

        } else {

            model.addAttribute("listaCuentas", listaCuentas);
            model.addAttribute("usuariosession", login);

            return "administrador.html";

        }
    }

}
