package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaComunServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


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
            return "redirect:/micuenta";
        }
        //REDIRECT: A LA CUENTA COMUN (como no está todavía no la puedo poner)
        return "redirect:/micuenta";
    }

    
}
