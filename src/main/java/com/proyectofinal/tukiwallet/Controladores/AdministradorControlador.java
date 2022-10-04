/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    
    @GetMapping("/cuentasComunes")
    public String panelCuentasComunes(HttpSession session, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }

        List<CuentaComun> listaCuentasComunes = cuentaComunServicio.mostrarTodos();
        model.addAttribute("listaCuentasComunes", listaCuentasComunes);
        model.addAttribute("usuariosession", login);

        return "AdministradorCC.html";
    }
    
    @GetMapping("/usuarios")
    public String panelUsuarios(HttpSession session, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }

        List<Usuario> listaUsuarios = usuarioServicio.listarUsuarios();
        model.addAttribute("listaUsuarios", listaUsuarios);
        model.addAttribute("usuariosession", login);

        return "administradorUs.html";
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

    @PostMapping("/buscarPorAliasCC")
    public String buscarPorAliasCC(HttpSession session, @RequestParam String alias, ModelMap model) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }

        List<CuentaComun> listaCuentasComunes = new ArrayList<CuentaComun>();
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorAliasCC(alias);

        if (cuentaComun != null) {
            listaCuentasComunes.add(cuentaComun);

            model.addAttribute("listaCuentas", listaCuentasComunes);
            model.addAttribute("usuariosession", login);

            return "administradorCC.html";
        } else {

           listaCuentasComunes = cuentaComunServicio.mostrarTodos();
            model.addAttribute("listaCuentas", listaCuentasComunes);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error el alias es incorrecto ");
            model.put("clase1", "danger");
            return "administradorCC.html";
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

    @PostMapping("/buscarPorCvuCC")
    public String buscarPorCvuCC(HttpSession session, @RequestParam String cvu, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<CuentaComun> listaCuentasComunes = new ArrayList<CuentaComun>();
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorCvuCC(cvu);

        if (cuentaComun != null) {

            listaCuentasComunes.add(cuentaComun);
            model.addAttribute("listaCuentas", listaCuentasComunes);
            model.addAttribute("usuariosession", login);

            return "administradorCC.html";
        } else {
            listaCuentasComunes = cuentaComunServicio.mostrarTodos();
            model.addAttribute("listaCuentas", listaCuentasComunes);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error el Cvu es incorrecto ");
            model.put("clase1", "danger");
            return "administradorCC.html";
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
    
    @PostMapping("/buscarPorEstadoCC")
    public String buscarPorEstadoCC(HttpSession session, @RequestParam String estado, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<CuentaComun> listaCuentasComunes = new ArrayList<CuentaComun>();

        if (estado.equals("Activo")) {

            listaCuentasComunes = cuentaComunServicio.mostrarAlta();
        } else {
            listaCuentasComunes = cuentaComunServicio.mostrarBaja();
        }

        if (listaCuentasComunes.isEmpty()) {
            listaCuentasComunes = cuentaComunServicio.mostrarTodos();
            model.addAttribute("listaCuentasComunes", listaCuentasComunes);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error - No Hay Usuarios " + estado);
            model.put("clase1", "danger");
            return "administradorCC.html";

        } else {
            listaCuentasComunes = cuentaComunServicio.mostrarTodos();
            model.addAttribute("listaCuentasComunes", listaCuentasComunes);
            model.addAttribute("usuariosession", login);

            return "administradorCC.html";

        }
    }
    
    @PostMapping("/buscarPorEstadoUs")
    public String buscarPorEstadoUs(HttpSession session, @RequestParam String estado, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<Usuario> listaUsuarios = new ArrayList<Usuario>();

        if (estado.equals("Activo")) {

            listaUsuarios = usuarioServicio.mostrarAlta();
        } else {
            listaUsuarios = usuarioServicio.mostrarBaja();
        }

        if (listaUsuarios.isEmpty()) {
            listaUsuarios = usuarioServicio.listarUsuarios();
            model.addAttribute("listaUsuarios", listaUsuarios);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error - No Hay Usuarios " + estado);
            model.put("clase1", "danger");
            return "administradorUs.html";

        } else {
            listaUsuarios = usuarioServicio.listarUsuarios();
            model.addAttribute("listaUsuarios", listaUsuarios);
            model.addAttribute("usuariosession", login);

            return "administradorUs.html";

        }
    }
    
    @PostMapping("/deshabilitar")
    public String deshabilitarCuenta(ModelMap modelo, @RequestParam String idD, RedirectAttributes redirectAttrs) {
        try {
            cuentaServicio.baja(idD);
            redirectAttrs
            .addFlashAttribute("mensaje", "Cuenta Deshabilitado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/dashboard";
        } catch (ErrorServicio ex) {
            List<Cuenta> listaCuentas = new ArrayList<Cuenta>();
            listaCuentas = cuentaServicio.mostrarTodos();
            modelo.put("mensaje1","Error al deshabilitar Cuenta "+ex.getMessage());
            modelo.put("clase1", "danger");
            modelo.addAttribute("listaCuentas", listaCuentas);
           // modelo.addAttribute("usuariosession", login);
            return "administrador.html";
        }  
    }
    
        @PostMapping("/deshabilitarCC")
    public String deshabilitarCuentaCC(ModelMap modelo, @RequestParam String idD, RedirectAttributes redirectAttrs) {
        try {
            cuentaComunServicio.deshabilitar(idD);
            redirectAttrs
            .addFlashAttribute("mensaje", "Cuenta Comun Deshabilitado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/cuentasComunes";
        } catch (ErrorServicio ex) {
            List<CuentaComun> listaCuentasComunes = new ArrayList<CuentaComun>();
            listaCuentasComunes = cuentaComunServicio.mostrarTodos();
            modelo.put("mensaje1","Error al deshabilitar Cuenta Comun "+ex.getMessage());
            modelo.put("clase1", "danger");
            modelo.addAttribute("listaCuentasComunes", listaCuentasComunes);
           // modelo.addAttribute("usuariosession", login);
            return "administradorCC.html";
        }  
    }
    
    
    @PostMapping("/habilitar")
    public String habilitarCuenta(ModelMap modelo, @RequestParam String id, RedirectAttributes redirectAttrs) {
        try {
            cuentaServicio.alta(id);
            redirectAttrs
            .addFlashAttribute("mensaje", "Cuenta habilitado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/dashboard";
        } catch (ErrorServicio ex) {
            List<Cuenta> listaCuentas = new ArrayList<Cuenta>();
            listaCuentas = cuentaServicio.mostrarTodos();
            modelo.put("mensaje1","Error al habilitar Cuenta "+ex.getMessage());
            modelo.put("clase1", "danger");
            modelo.addAttribute("listaCuentas", listaCuentas);
           // modelo.addAttribute("usuariosession", login);
            return "administrador.html";
        }  
    }
    
        @PostMapping("/habilitarCC")
    public String habilitarCuentaCC(ModelMap modelo, @RequestParam String id, RedirectAttributes redirectAttrs) {
        try {
            cuentaComunServicio.habilitar(id);
            redirectAttrs
            .addFlashAttribute("mensaje", "Cuenta habilitado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/cuentasComunes";
        } catch (ErrorServicio ex) {
            List<CuentaComun> listaCuentasComunes = new ArrayList<CuentaComun>();
            listaCuentasComunes = cuentaComunServicio.mostrarTodos();
            modelo.put("mensaje1","Error al habilitar Cuenta Comun "+ex.getMessage());
            modelo.put("clase1", "danger");
            modelo.addAttribute("listaCuentasComunes", listaCuentasComunes);
           // modelo.addAttribute("usuariosession", login);
            return "administradorCC.html";
        }  
    }
    
    
    @PostMapping("/eliminar")
    public String eliminarCuenta(ModelMap modelo, @RequestParam String idE, RedirectAttributes redirectAttrs) {
        try {
            cuentaServicio.borrarPorId(idE);
            redirectAttrs
            .addFlashAttribute("mensaje", "Cuenta Eliminado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/dashboard";
        } catch (ErrorServicio ex) {
            List<Cuenta> listaCuentas = new ArrayList<Cuenta>();
            listaCuentas = cuentaServicio.mostrarTodos();
            modelo.addAttribute("listaCuentas", listaCuentas);
            modelo.put("mensaje1","Error al eliminar Cuenta "+ex.getMessage());
            modelo.put("clase1", "danger");
            return "administrador.html";
        }
    }
    
    @PostMapping("/eliminarCC")
    public String eliminarCuentaComun(ModelMap modelo, @RequestParam String idE, RedirectAttributes redirectAttrs) {
        try {
            cuentaComunServicio.borrarPorIdCC(idE);
            redirectAttrs
            .addFlashAttribute("mensaje", "Cuenta Eliminado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/cuentaComunes";
        } catch (ErrorServicio ex) {
            List<Cuenta> listaCuentas = new ArrayList<Cuenta>();
            listaCuentas = cuentaServicio.mostrarTodos();
            modelo.addAttribute("listaCuentas", listaCuentas);
            modelo.put("mensaje1","Error al eliminar Cuenta "+ex.getMessage());
            modelo.put("clase1", "danger");
            return "administradorCC.html";
        }
    }
    
}
