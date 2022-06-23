package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.EfectivoCC;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaComunServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import java.text.ParseException;
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
    public String miCuenta(ModelMap modelo, HttpSession session, String id) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        Usuario usuarioCuenta = usuarioServicio.buscarPorId(id);

        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuarioCuenta.getId());
        modelo.addAttribute("micuenta", usuarioCuenta);
        modelo.addAttribute("listaCC", listaCC);

        modelo.addAttribute("actividad", usuarioCuenta.getCuenta().getActividad());

        return "cuenta.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/editar-cuenta")
    public String editarCuenta(ModelMap model, HttpSession session, @RequestParam String idUsuario, @RequestParam String id, @RequestParam String alias) throws ErrorServicio, ParseException {
        Cuenta cuenta = null;
        try {
            cuenta = cuentaServicio.buscarPorId(id);

            cuentaServicio.modificarAlias(alias, id);
            Usuario usuarioCuenta = usuarioServicio.buscarPorId(idUsuario);
        
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuarioCuenta.getId());
        model.addAttribute("micuenta", usuarioCuenta);
        model.addAttribute("listaCC", listaCC);
        model.addAttribute("actividad", usuarioCuenta.getCuenta().getActividad());
        session.setAttribute("usuariosession", usuarioCuenta);
 
        model.put("mensaje", "Se modifico correctamente el alias");
        model.put("clase", "success");
            
            return "cuenta.html";

        } catch (ErrorServicio e) {
        Usuario usuarioCuenta = usuarioServicio.buscarPorId(idUsuario);
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuarioCuenta.getId());
        model.addAttribute("micuenta", usuarioCuenta);
        model.addAttribute("listaCC", listaCC);
        model.addAttribute("actividad", usuarioCuenta.getCuenta().getActividad());
        session.setAttribute("usuariosession", usuarioCuenta);
        
            model.put("mensaje1", "Error al modificar el alias " + e.getMessage());
            model.put("clase1", "danger");;
            return "cuenta.html";
        }
    }

    @PostMapping("/altaCuenta")
    public String altaCuenta(ModelMap model, @RequestParam String idCuenta, @RequestParam String idUsuario) throws ErrorServicio {
        try {

            cuentaServicio.alta(idCuenta);
            Usuario usuario = usuarioServicio.buscarPorId(idUsuario);
            model.addAttribute("perfil", usuario);
            List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuario.getId());
            model.addAttribute("micuenta", usuario);
            model.addAttribute("listaCC", listaCC);
             model.addAttribute("actividad", usuario.getCuenta().getActividad());
            model.put("mensaje", "Se Habilitado correctamente la Cuenta");
            model.put("clase", "success");
            return "cuenta.html";
        } catch (ErrorServicio e) {
            model.put("mensaje", e.getMessage());
            Usuario usuario = usuarioServicio.buscarPorId(idUsuario);
            model.addAttribute("perfil", usuario);
            List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuario.getId());
            model.addAttribute("micuenta", usuario);
             model.addAttribute("actividad", usuario.getCuenta().getActividad());
            model.addAttribute("listaCC", listaCC);
            model.put("mensaje1", "Error al Habilitar la Cuenta " + e.getMessage());
            model.put("clase1", "danger");
            return "cuenta.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/bajaCuenta")
    public String bajaCuenta(ModelMap model, @RequestParam String idCuenta, @RequestParam String idUsuario) throws ErrorServicio {
        try {

            cuentaServicio.baja(idCuenta);
            Usuario usuario = usuarioServicio.buscarPorId(idUsuario);
            //model.addAttribute("perfil", usuario);
            List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuario.getId());
            model.addAttribute("miCuenta", usuario);
            model.addAttribute("listaCC", listaCC);
             model.addAttribute("actividad", usuario.getCuenta().getActividad());
            model.put("mensaje", "Se Deshabilitado correctamente la Cuenta");
            model.put("clase", "success");
            return "cuenta.html";

        } catch (ErrorServicio e) {

            Usuario usuario = usuarioServicio.buscarPorId(idUsuario);

            List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuario.getId());
            model.addAttribute("miCuenta", usuario);
            model.addAttribute("listaCC", listaCC);
             model.addAttribute("actividad", usuario.getCuenta().getActividad());
            model.put("mensaje1", "Error al Deshabilitar la Cuenta " + e.getMessage());
            model.put("clase1", "danger");
            return "cuenta.html";
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
    public String transferir(ModelMap modelo, HttpSession session, String id, String idCuentaComun, String cvuCuenta, String cvuCuentaComun, String cvuoAlias, String monto, String motivo) throws ErrorServicio {
        Usuario usuarioCuenta = usuarioServicio.buscarPorId(id);
        String cvu1 = null;
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/login";
            }

            if (cvuCuentaComun == null) {
                cvu1 = cvuCuenta;
                System.out.println("llegue");
               
            } else {
                cvu1 = cvuCuentaComun;
                System.out.println("llegue1c");
            }
 System.out.println(cvu1);
            Float montof = Float.valueOf(monto);

            String cvu2 = null;
            Boolean cocc = true;
            if (cvuoAlias.contains("C")) {
                CuentaComun cuenta2 = cuentaComunServicio.buscarCuentaPorAliasCC(cvuoAlias);
                cvu2 = cuenta2.getCvuCC();
                cocc = false;
                System.out.println("llegue1cH");
            } else if (cvuoAlias.contains("T")) {
                Cuenta cuenta2 = cuentaServicio.buscarCuentaPorAlias(cvuoAlias);
                cvu2 = cuenta2.getCvu();
                 System.out.println("llegeueeee");
            } else if (cvuoAlias.substring(4, 5).equals("1")) {
                Cuenta cuenta2 = cuentaServicio.buscarCuentaPorid(cvuoAlias);
                cvu2 = cvuoAlias;
                System.out.println("llegeueeee222222");
            } else if (cvuoAlias.substring(4, 5).equals("2")) {
                CuentaComun cuenta2 = cuentaComunServicio.buscarCuentaPorIdCC(cvuoAlias);
                cvu2 = cvuoAlias;
                cocc = false;
                System.out.println("llegeue3456456eee");
            }
            System.out.println(cvu2);
            if (cvu2 == null) {
                cuentaServicio.ingresoCuenta(montof, cvu1, cvu2, motivo);
            }
            
            if(cvuCuentaComun == null){
                cuentaServicio.validarTransferenciaCuenta(montof, cvu1, cvu2);
            }else{
                cuentaComunServicio.validarTransferenciaCuentaComun(montof, cvu1, cvu2);
            }

            
            if (cvuCuentaComun == null) {

                cuentaServicio.egresoCuenta(montof, cvu1, cvu2, motivo);
            } else {
                cuentaComunServicio.egresoCuentaComun(montof, cvu1, cvu2, motivo);
            }

            if (cocc) {
                cuentaServicio.ingresoCuenta(montof, cvu1, cvu2, motivo);
            } else {
                cuentaComunServicio.ingresoCuentaComun(montof, cvu1, cvu2, motivo);
            }

            //vistas
            if (cvuCuentaComun == null) {
               

                List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuarioCuenta.getId());
                modelo.addAttribute("miCuenta", usuarioCuenta);
                modelo.addAttribute("listaCC", listaCC);
                modelo.addAttribute("actividad", usuarioCuenta.getCuenta().getActividad());
                modelo.put("mensaje", "Desde su Cuenta se transfirio correctamente");
                modelo.put("clase", "success");
                return "cuenta.html";
            } else {
                //para que se vea usuarios con tuki
                List<Float> saldosUsuarios = new ArrayList<Float>();
                Usuario usuarioCuentaC = usuarioServicio.buscarPorId(id);
                CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCuentaComun);
                List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
                for (Usuario usuarioAux : listaUsuarios) {
                    saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
                }
                List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(id);

                //para que se vea usuarios sin tuki
                List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
                List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
                for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
                    saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
                }

                List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCuentaComun);
                modelo.addAttribute("actividad", actividad);
                 modelo.addAttribute("miCuenta", usuarioCuenta);
                modelo.addAttribute("listaCC", listaCC);
                modelo.addAttribute("micuentaC", usuarioCuentaC);
                modelo.addAttribute("cuentaComun", cuentaComun);
                modelo.addAttribute("listaUsuarios", listaUsuarios);
                modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

                modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
                modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

                modelo.put("mensaje", "Desde su Cuenta Comun se transfirio correctamente");
                modelo.put("clase", "success");

                return "cuentaComun.html";
            }

        } catch (ErrorServicio e) {
            modelo.put("error", e.getMessage());
            System.out.println(e.getMessage());
            
            if(idCuentaComun==null){
            modelo.put("mensaje1", "No se pudo transferir desde su Cuenta porque: "+ e.getMessage());
            modelo.put("clase1", "danger");
            modelo.put("error", "Error al cargar Usuario " + e.getMessage());
            List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuarioCuenta.getId());
            modelo.addAttribute("micuenta", usuarioCuenta);
            modelo.addAttribute("listaCC", listaCC);
            modelo.addAttribute("actividad", usuarioServicio.buscarPorId(id).getCuenta().getActividad());
            return "cuenta.html";
            }else{
                                //para que se vea usuarios con tuki
                List<Float> saldosUsuarios = new ArrayList<Float>();
                Usuario usuarioCuentaC = usuarioServicio.buscarPorId(id);
                CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCuentaComun);
                List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
                for (Usuario usuarioAux : listaUsuarios) {
                    saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
                }
                List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(id);

                //para que se vea usuarios sin tuki
                List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
                List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
                for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
                    saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
                }

                List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCuentaComun);
                modelo.addAttribute("actividad", actividad);
                 modelo.addAttribute("micuenta", usuarioCuenta);
                modelo.addAttribute("listaCC", listaCC);
                modelo.addAttribute("micuentaC", usuarioCuentaC);
                modelo.addAttribute("cuentaComun", cuentaComun);
                modelo.addAttribute("listaUsuarios", listaUsuarios);
                modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

                modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
                modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

                modelo.put("mensaje1", "No se trasfirio desde su Cuenta Comun por: "+e.getMessage());
                modelo.put("clase1", "danger");
                return "cuentaComun.html";
            }
        }
    }

    @GetMapping("/transferirlink")
    public String transferirlink(ModelMap model, HttpSession session, String id) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        Usuario usuarioCuenta = usuarioServicio.buscarPorId(id);
        model.addAttribute("transferirlink", usuarioCuenta);
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(id);
         model.addAttribute("actividad", usuarioCuenta.getCuenta().getActividad());
        //model.addAttribute("cuentaComun", cuentaComun);
        model.addAttribute("listaCC", listaCC);
        return "transferir.html";
    }

}
