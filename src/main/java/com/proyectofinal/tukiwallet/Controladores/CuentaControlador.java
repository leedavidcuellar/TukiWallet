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
import com.proyectofinal.tukiwallet.Servicios.CuentaComunServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import java.text.ParseException;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    private CuentaComunServicio cuentaComunServicio;

    @GetMapping("/micuenta")
    public String miCuenta(ModelMap modelo, HttpSession session, String id) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        Usuario usuarioCuenta = usuarioServicio.buscarPorId(id);
        List<CuentaComun> listaCC=cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuarioCuenta.getId());
        modelo.addAttribute("micuenta", usuarioCuenta);
        modelo.addAttribute("listaCC",listaCC);
        return "cuenta.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/editar-cuenta")
    public String editarCuenta(ModelMap model, HttpSession session, @RequestParam String id, @RequestParam String alias) throws ErrorServicio, ParseException {
        Cuenta cuenta = null;
        try {
            cuenta = cuentaServicio.buscarPorId(id);

            cuentaServicio.modificarAlias(alias, id);

            session.setAttribute("usuariosession", cuenta);
            //VER BIEN EL REDIRECT
            return "redirect:/inicio";

        } catch (ErrorServicio e) {
            model.put("perfil", cuenta);
            model.put("mensaje", e.getMessage());
            //VER BIEN EL REDIRECT
            return "tuperfil.html";
        }
    }

    @PostMapping("/altaCuenta/{id}")
    public String altaCuenta(ModelMap model, @PathVariable("id") String id) throws ErrorServicio {
        try {

            cuentaServicio.alta(id);
            //ver bien el Return
            return "/inicio";

        } catch (ErrorServicio e) {
            model.put("mensaje", e.getMessage());
            //ver bien el Return
            return "/index";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/bajaCuenta/{id}")
    public String bajaCuenta(ModelMap model, @PathVariable("id") String id) throws ErrorServicio {
        try {

            cuentaServicio.baja(id);
            //ver bien el Return
            return "/index";

        } catch (ErrorServicio e) {
            model.put("mensaje", e.getMessage());
            //ver bien el Return
            return "/inicio";
        }
    }

    @GetMapping("/eliminarCuenta/{id}")
    public String eliminarCuenta(ModelMap model, @PathVariable("id") String id) throws ErrorServicio {
        try {
            cuentaServicio.borrarPorId(id);
            //ver bien el Return
            return "redirect:/";
        } catch (ErrorServicio e) {
            //ver bien el Return
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/transferir")
    public String transferir(ModelMap modelo, HttpSession session, String id, String cvuCuenta, String cvuoAlias, String monto, String motivo) throws ErrorServicio {
        try {
//            Usuario login = (Usuario) session.getAttribute("usuariosession");
//            if (login == null || !login.getId().equals(id)) {
//            return "redirect:/login";
//            }
//            Usuario usuarioCuenta = usuarioServicio.buscarPorId(id);
//            modelo.addAttribute("micuenta", usuarioCuenta);
//            
            Float montof = Float.valueOf(monto);
            String cvu1 = cvuCuenta;
            String cvu2 = null;
            Boolean cocc = true;
            if (cvuoAlias.contains("C")) {
                CuentaComun cuenta2 = cuentaComunServicio.buscarCuentaPorAliasCC(cvuoAlias);
                cvu2 = cuenta2.getCvuCC();
                cocc = false;
            } else if (cvuoAlias.contains("T")) {
                Cuenta cuenta2 = cuentaServicio.buscarCuentaPorAlias(cvuoAlias);
                cvu2 = cuenta2.getCvu();
            } else if (cvuoAlias.substring(4, 5).equals("1")) {
                Cuenta cuenta2 = cuentaServicio.buscarCuentaPorid(cvuoAlias);
                cvu2 = cvuoAlias;
            } else if (cvuoAlias.substring(4, 5).equals("2")) {
                CuentaComun cuenta2 = cuentaComunServicio.buscarCuentaPorIdCC(cvuoAlias);
                cvu2 = cvuoAlias;
                cocc = false;
            }
            System.out.println("1");
            if (cvu2 == null) {
                cuentaServicio.ingresoCuenta(montof, cvu1, cvu2, motivo);
            }
            System.out.println("2");
            cuentaServicio.validarTransferenciaCuenta(montof, cvu1, cvu2);
            System.out.println("3");
            cuentaServicio.egresoCuenta(montof, cvu1, cvu2, motivo);
            System.out.println("4");
            if (cocc) {
                cuentaServicio.ingresoCuenta(montof, cvu1, cvu2, motivo);
            } else {
                System.out.println("5");
                cuentaComunServicio.ingresoCuentaComun(montof, cvu2, cvu1, motivo);
            }
            modelo.put("exito", "Se transfirio correctamente");
            return "redirect:/micuenta";
        } catch (ErrorServicio e) {
            modelo.put("error", e.getMessage());
            System.out.println(e.getMessage());
            return "redirect:/micuenta";
        }
    }

    @GetMapping("/transferirlink")
    public String transferirlink(ModelMap model, HttpSession session, String id) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        Usuario usuarioCuenta = usuarioServicio.buscarPorId(id);
        model.addAttribute("micuenta", usuarioCuenta);
        return "transferir.html";
    }

}
