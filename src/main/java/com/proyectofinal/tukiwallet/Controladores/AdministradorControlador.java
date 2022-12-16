/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
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
    
    @GetMapping("/transferencias")
    public String panelTransferencias(HttpSession session, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }

        List<Actividad> listaActividades = actividadServicio.listarTodasActividades();
        model.addAttribute("listaActividades", listaActividades);
        model.addAttribute("usuariosession", login);

        return "administradorTransf.html";
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
    
    @PostMapping("/buscarPorCvuOrigen")
    public String buscarPorCvuOrigen(HttpSession session, @RequestParam String cvuO, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        
        List<Actividad> listaActividades = actividadServicio.listadoActividadEgreso(cvuO);
       

        if (listaActividades != null) {

            model.addAttribute("listaActividades", listaActividades);
            model.addAttribute("usuariosession", login);

            return "administradorTransf.html";
        } else {
            listaActividades = actividadServicio.listarTodasActividades();
            model.addAttribute("listaActividades", listaActividades);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error el Cvu Origen es incorrecto ");
            model.put("clase1", "danger");
            return "administradorTransf.html";
        }
    }
    
    @PostMapping("/buscarPorCvuDestino")
    public String buscarPorCvuDestino(HttpSession session, @RequestParam String cvuD, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        
        List<Actividad> listaActividades = actividadServicio.listadoActividadIngreso(cvuD);
       

        if (listaActividades != null) {

            model.addAttribute("listaActividades", listaActividades);
            model.addAttribute("usuariosession", login);

            return "administradorTransf.html";
        } else {
            listaActividades = actividadServicio.listarTodasActividades();
            model.addAttribute("listaActividades", listaActividades);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error el Cvu Destino es incorrecto ");
            model.put("clase1", "danger");
            return "administradorTransf.html";
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
    
    
@PostMapping("/buscarPorMovimiento")
    public String buscarPorMovimiento(HttpSession session, @RequestParam String movimiento, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<Actividad> listaActividades = new ArrayList<Actividad>();

        if (movimiento.equals("Ingreso")) {

            listaActividades = actividadServicio.listadoTodosMovimientoIngeso();
        } else {
            listaActividades = actividadServicio.listadoTodosMovimientoEgresoSalida();
        }

        if (listaActividades.isEmpty()) {
            listaActividades = actividadServicio.listarTodasActividades();
            model.addAttribute("listaActividades", listaActividades);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error - No Hay Transferencias " + movimiento);
            model.put("clase1", "danger");
            return "administradorTransf.html";

        } else {

            model.addAttribute("listaActividades", listaActividades);
            model.addAttribute("usuariosession", login);

            return "administradorTransf.html";

        }
    }    
    
@PostMapping("/buscarPorDni")
    public String buscarPorDni(HttpSession session, @RequestParam String dni, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<Usuario> listaUsuarios = new ArrayList<Usuario>();
        Usuario usuarioAux = usuarioServicio.buscarPorDni(dni);

        if (usuarioAux != null) {

            listaUsuarios.add(usuarioAux);
            model.addAttribute("listaUsuarios", listaUsuarios);
            model.addAttribute("usuariosession", login);

            return "administradorUs.html";
        } else {
            listaUsuarios = usuarioServicio.listarUsuarios();
            model.addAttribute("listaUsuarios", listaUsuarios);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error el Dni es incorrecto, no Hay Usuario ");
            model.put("clase1", "danger");
            return "administradorUs.html";
        }
    }
    
    @PostMapping("/buscarPorMail")
    public String buscarPorMail(HttpSession session, @RequestParam String mail, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<Usuario> listaUsuarios = new ArrayList<Usuario>();
        Usuario usuarioAux = usuarioServicio.buscarPorMail(mail);

        if (usuarioAux != null) {

            listaUsuarios.add(usuarioAux);
            model.addAttribute("listaUsuarios", listaUsuarios);
            model.addAttribute("usuariosession", login);

            return "administradorUs.html";
        } else {
            listaUsuarios = usuarioServicio.listarUsuarios();
            model.addAttribute("listaUsuarios", listaUsuarios);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error el Mail es incorrecto, no Hay Usuario ");
            model.put("clase1", "danger");
            return "administradorUs.html";
        }
    }
    
    @PostMapping("/buscarPorOperacion")
    public String buscarPorOperacion(HttpSession session, @RequestParam String nOperacion, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if (login == null) {

            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        List<Actividad> listaActividades = new ArrayList<Actividad>();
        Actividad actividadAux = actividadServicio.buscarActividadPorNroOperacion(nOperacion);

        if (actividadAux != null) {

            listaActividades.add(actividadAux);
            model.addAttribute("listaActividades", listaActividades);
            model.addAttribute("usuariosession", login);

            return "administradorTransf.html";
        } else {
            listaActividades = actividadServicio.listarTodasActividades();
            model.addAttribute("listaActividades", listaActividades);
            model.addAttribute("usuariosession", login);
            model.put("mensaje1", "Error el Nro de Operacion es incorrecto, no Hay Transferencia con el mismo ");
            model.put("clase1", "danger");
            return "administradorTransf.html";
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
    
    @PostMapping("/deshabilitarUs")
    public String deshabilitarUsuario(ModelMap modelo, @RequestParam String idD, RedirectAttributes redirectAttrs) {
        try {
            usuarioServicio.deshabilitarUsuario(idD);
            redirectAttrs
            .addFlashAttribute("mensaje", "Usuario Deshabilitado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/usuarios";
        } catch (ErrorServicio ex) {
            List<Usuario> listaUsuarios = new ArrayList<Usuario>();
            listaUsuarios = usuarioServicio.listarUsuarios();
            modelo.put("mensaje1","Error al deshabilitar Usuario "+ex.getMessage());
            modelo.put("clase1", "danger");
            modelo.addAttribute("listaUsuarios", listaUsuarios);
           // modelo.addAttribute("usuariosession", login);
            return "administradorUs.html";
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
    
    @PostMapping("/habilitarUs")
    public String habilitarUsuario(ModelMap modelo, @RequestParam String idD, RedirectAttributes redirectAttrs) {
        try {
            usuarioServicio.habilitarUsuario(idD);
            redirectAttrs
            .addFlashAttribute("mensaje", "Usuario habilitado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/usuarios";
        } catch (ErrorServicio ex) {
            List<Usuario> listaUsuarios = new ArrayList<Usuario>();
            listaUsuarios = usuarioServicio.listarUsuarios();
            modelo.put("mensaje1","Error al habilitar Usuario "+ex.getMessage());
            modelo.put("clase1", "danger");
            modelo.addAttribute("listaUsuarios", listaUsuarios);
           // modelo.addAttribute("usuariosession", login);
            return "administradorUs.html";
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
    
    @PostMapping("/eliminarUs")
    public String eliminarUsuarios(ModelMap modelo, @RequestParam String idE, RedirectAttributes redirectAttrs) {
        try {
            usuarioServicio.eliminarUsuario(idE);
            redirectAttrs
            .addFlashAttribute("mensaje", "Usuario Eliminado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/usuarios";
        } catch (ErrorServicio ex) {
            List<Usuario> listaUsuarios = new ArrayList<Usuario>();
            listaUsuarios = usuarioServicio.listarUsuarios();
            modelo.addAttribute("listaUsuarios", listaUsuarios);
            modelo.put("mensaje1","Error al eliminar Usuario "+ex.getMessage());
            modelo.put("clase1", "danger");
            return "administradorUs.html";
        }
    }
    
    @PostMapping("/eliminarTransf")
    public String eliminarTransf(ModelMap modelo, @RequestParam String idE, RedirectAttributes redirectAttrs) {
        try {
            actividadServicio.borrarPorId(idE);
            redirectAttrs
            .addFlashAttribute("mensaje", "Transferencia Eliminado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/admin/transferencias";
        } catch (ErrorServicio ex) {
            List<Actividad> listaActividades = new ArrayList<Actividad>();
            listaActividades = actividadServicio.listarTodasActividades();
            modelo.addAttribute("listaActividades", listaActividades);
            modelo.put("mensaje1","Error al eliminar Transferencia "+ex.getMessage());
            modelo.put("clase1", "danger");
            return "administradorTransf.html";
        }
    }
    
}
