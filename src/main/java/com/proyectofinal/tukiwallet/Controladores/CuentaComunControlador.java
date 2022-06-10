package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaComunServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import java.util.ArrayList;
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
    public String miCuentaC(ModelMap modelo, HttpSession session, String idUsuario, String idCC) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(idUsuario)) {
            return "redirect:/login";
        }
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
        }
        List<CuentaComun> listaCC=cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);
        modelo.addAttribute("listaCC",listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);
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
            modelo.put("exito","Se realizo la transferencia con Exito ");
            return "redirect:/micuentaC";
            
        } catch (ErrorServicio e) {
            modelo.put("mensaje", e.getMessage());
            modelo.put("error","Error al transferir "+e.getMessage());
            //REDIRECT: A LA CUENTA COMUN (como no está todavía no la puedo poner)
            return "cuentaComun.html";
        }
        //REDIRECT: A LA CUENTA COMUN (como no está todavía no la puedo poner)
        
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

    @PostMapping("/agregarUsuarioCC")
    public String agregarUsuarioCC(ModelMap modelo, HttpSession session, @RequestParam String idUsuario,@RequestParam String idCC, String cvuUsuario, String aliasCC) throws ErrorServicio {
        List<Usuario> listaUsuario = new ArrayList<Usuario>();
        try {

            Cuenta cuenta = cuentaServicio.buscarCuentaPorAlias(aliasCC);

            if (cuenta == null) {

                cuenta = cuentaServicio.buscarCuentaPorCbu(cvuUsuario);
            }

            Usuario usuario = usuarioServicio.buscarPorCuentaId(cuenta.getId());
            listaUsuario.add(usuario);
            CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
            listaUsuario.addAll(cuentaComun.getUsuarios());
           

            cuentaComunServicio.agregarUsuarioCuentaComun(cuentaComun.getId(), listaUsuario);

           
            List<Float> saldosUsuarios = new ArrayList<Float>();
            Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
            //CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaComunPorIdUsuario(id);
            List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
            for (Usuario usuarioAux : listaUsuarios) {
                saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
            }
          
            modelo.addAttribute("micuentaC", usuarioCuentaC);
            modelo.addAttribute("cuentaComun", cuentaComun);
            modelo.addAttribute("listaUsuarios", listaUsuarios);
            modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);
           
            return "cuentaComun.html"; //check   
        } catch (ErrorServicio e) {
            e.printStackTrace();
            modelo.put("error","Error al editar Usuario "+e.getMessage());
            modelo.put("mensaje1","Error al editar Usuario "+e.getMessage());
            modelo.put("clase1", "danger");
//            modelo.put("nombre", nombre);
//            modelo.put("usuarios", usuarios);
            return "principalFinal.html"; //check 
        }
    }

}
