package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaComunServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/cuentaComun")
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class CuentaComunControlador {
    
    @Autowired
    private CuentaServicio cuentaServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private CuentaComunServicio cuentaComunServicio;
    
    @GetMapping("/micuentaC")
    public String miCuenta(ModelMap modelo, HttpSession session, String id) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(id);
        modelo.addAttribute("micuentaC", usuarioCuentaC);

        return "cuentaComun.html";
    }
    
    
    @GetMapping("/transferir")
    public String transferir(ModelMap modelo, HttpSession session, String id, String cvu1, String cvuoAlias, String monto, String motivo) throws ErrorServicio {
        //cvu1 = cvu de la cuenta comun
        //cvuoAlias = cvu/alias al que transfiere
        
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/login";
            }
            Float motivof = Float.valueOf(motivo);
            String cvu2 = null;
            Boolean cocc = true;
            if (cvuoAlias.contains("C")) {
                CuentaComun cuenta2 = cuentaComunServicio.buscarCuentaPorAliasCC(cvuoAlias);
                cvu2 = cuenta2.getCvuCC();
                cocc = false;
            } else if (cvuoAlias.contains("T")) {
                Cuenta cuenta2 = cuentaServicio.buscarCuentaPorAlias(cvuoAlias);
                cvu2 = cuenta2.getCvu();
            } else if (cvuoAlias.substring(4).equals("1")) {
                Cuenta cuenta2 = cuentaServicio.buscarCuentaPorid(cvuoAlias);
                cvu2 = cuenta2.getCvu();
            } else if (cvuoAlias.substring(4).equals("2")) {
                CuentaComun cuenta2 = cuentaComunServicio.buscarCuentaPorIdCC(cvuoAlias);
                cvu2 = cuenta2.getCvuCC();
                cocc = false;
            }
            if (cvu2 == null) {
                cuentaServicio.ingresoCuenta(motivof, cvu1, cvu2, motivo);
            }
            cuentaComunServicio.validarTransferenciaCuentaComun(motivof, cvu1, cvu2);
            cuentaComunServicio.egresoCuentaComun(motivof, cvu1, cvu2, motivo);
            if (cocc) {
                cuentaServicio.ingresoCuenta(motivof, cvu1, cvu2, motivo);
            } else {
                cuentaComunServicio.ingresoCuentaComun(motivof, cvu2, cvu1, motivo);
            }
        } catch (ErrorServicio e) {
            modelo.put("mensaje", e.getMessage());
            //REDIRECT: A LA CUENTA COMUN (como no está todavía no la puedo poner)
            return "redirect:/cuentaComun";
        }
        //REDIRECT: A LA CUENTA COMUN (como no está todavía no la puedo poner)
        return "redirect:/micuenta";
    }

    
    @PostMapping("/nuevaCuentaComun")
    public String crearCC(ModelMap modelo, @RequestParam String nombre, @RequestParam List<Usuario> usuarios) throws ErrorServicio {

        try {
            cuentaComunServicio.crearCuentaComun(nombre, usuarios);
        } catch (ErrorServicio error) {
            modelo.put("error", error.getMessage());
            return "crearCuentaComun.html"; //check 
        }
        return "misCuentasComunes.html"; //check   
    }

    
    @PostMapping("/deshabilitarCC")
    public String deshabilitarCC(ModelMap modelo, @PathVariable("id") String id) throws ErrorServicio {

        try {
            cuentaComunServicio.deshabilitar(id);
        } catch (ErrorServicio error) {
            modelo.put("error", error.getMessage());
            return "misCuentasComunes.html"; //check 
        }
        return "miCuenta.html"; //check   
    }

    
    @PostMapping("/habilitarCC")
    public String habilitarCC(ModelMap modelo, @PathVariable("id") String id) throws ErrorServicio {

        try {
            cuentaComunServicio.habilitar(id);
        } catch (ErrorServicio error) {
            modelo.put("error", error.getMessage());
            return "misCuentasComunes.html"; //check 
        }
        return "miCuenta.html"; //check   
    }


    @PostMapping("/editarCuentaComun")
    public String editarCC(ModelMap modelo, @RequestParam String nombre, @RequestParam List<Usuario> usuarios) throws ErrorServicio {

        try {
            cuentaComunServicio.modificarCuentaComun(nombre, nombre, usuarios);
        } catch (ErrorServicio error) {
            modelo.put("error", error.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("usuarios", usuarios);
            return "editarCuentaComun.html"; //check 
        }
        return "misCuentasComunes.html"; //check   
    }

}
