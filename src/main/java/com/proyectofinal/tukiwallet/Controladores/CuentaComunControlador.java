package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.EfectivoCC;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaComunServicio;
import com.proyectofinal.tukiwallet.Servicios.CuentaServicio;
import com.proyectofinal.tukiwallet.Servicios.EfectivoCCServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Autowired
    private EfectivoCCServicio efectivoCCServicio;

    @GetMapping("/micuentaC")
    public String miCuentaC(ModelMap modelo, HttpSession session, String idUsuario, String idCC) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(idUsuario)) {
            return "redirect:/login";
        }
        //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
            
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }

        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("micuenta", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);
        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

        
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCC);
        modelo.addAttribute("actividad", actividad);
        
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
            modelo.put("exito", "Se realizo la transferencia con Exito ");
            return "redirect:/micuentaC";

        } catch (ErrorServicio e) {
            modelo.put("mensaje", e.getMessage());
            modelo.put("error", "Error al transferir " + e.getMessage());
            //REDIRECT: A LA CUENTA COMUN (como no está todavía no la puedo poner)
            return "cuentaComun.html";
        }
        //REDIRECT: A LA CUENTA COMUN (como no está todavía no la puedo poner)

    }
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/nuevaCuentaComun")
    public String crearCC(ModelMap modelo, @RequestParam String nombre, @RequestParam String idUsuario, @RequestParam List<Usuario> usuarios) throws ErrorServicio {

        try {
            cuentaComunServicio.crearCuentaComun(nombre, idUsuario, usuarios);
            return "misCuentasComunes.html"; //check  
            
        } catch (ErrorServicio error) {
            modelo.put("error", error.getMessage());
            return "crearCuentaComun.html"; //check 
        }
         
    }

    @PostMapping("/deshabilitarCC")
    public String deshabilitarCC(ModelMap modelo, @RequestParam String idCuentaComun, @RequestParam String idUsuario) throws ErrorServicio {

        try {
            cuentaComunServicio.deshabilitar(idCuentaComun);
            //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCuentaComun);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }
        
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCuentaComun);
        modelo.addAttribute("actividad", actividad);

        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);
                    modelo.put("mensaje", "Se Deshabilitado correctamente la Cuenta Comun");
            modelo.put("clase", "success");
        
             return "cuentaComun.html"; //check 
            
        } catch (ErrorServicio e) {
            
            //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCuentaComun);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }
        
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCuentaComun);
        modelo.addAttribute("actividad", actividad);

        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);
            
                    modelo.put("mensaje1", "Error al Deshabilitar la Cuenta Comun " + e.getMessage());
            modelo.put("clase1", "danger");
            return "cuentaComun.html"; //check 
        }
         
    }

    @PostMapping("/habilitarCC")
    public String habilitarCC(ModelMap modelo, @RequestParam String idCuentaComun, @RequestParam String idUsuario) throws ErrorServicio {

        try {
            cuentaComunServicio.habilitar(idCuentaComun);
            
            //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCuentaComun);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }

        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCuentaComun);
        modelo.addAttribute("actividad", actividad);
        
        
        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);
             modelo.put("mensaje", "Se Habilitado correctamente la Cuenta Comun");
            modelo.put("clase", "success");
            
            return "cuentaComun.html"; //check
            
        } catch (ErrorServicio e) {
            //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCuentaComun);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }
        
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCuentaComun);
        modelo.addAttribute("actividad", actividad);

        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);
            
                    modelo.put("mensaje1", "Error al Habilitar la Cuenta Comun " + e.getMessage());
            modelo.put("clase1", "danger");
            return "cuentaComun.html"; 
        }
         
    }
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/editarCuentaComun")
    public String editarCC(ModelMap modelo, HttpSession session, @RequestParam String idUsuario, @RequestParam String id ,@RequestParam String nombre) throws ErrorServicio {

        CuentaComun cuentaComun = null;
        try {
            cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(id);
            
            cuentaComunServicio.modificarCuentaComun(id, nombre);
            Usuario usuarioCC = usuarioServicio.buscarPorId(idUsuario);
            
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuarioCC.getId());
        modelo.addAttribute("micuenta", usuarioCC);
        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("actividad", usuarioCC.getCuenta().getActividad());
        session.setAttribute("usuariosession", usuarioCC);
 
        modelo.put("mensaje", "Se modifico correctamente el nombre");
        modelo.put("clase", "success");
            
            return "cuentaComun.html";
            
        } catch (ErrorServicio e) { 
            
        Usuario usuarioCC = usuarioServicio.buscarPorId(idUsuario);
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(usuarioCC.getId());
        modelo.addAttribute("micuenta", usuarioCC);
        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("actividad", usuarioCC.getCuenta().getActividad());
        session.setAttribute("usuariosession", usuarioCC);
        
            modelo.put("mensaje1", "Error al modificar el alias " + e.getMessage());
            modelo.put("clase1", "danger");;
            return "cuentaComun.html"; 
        } 
    }

    @PostMapping("/agregarUsuarioCC")
    public String agregarUsuarioCC(ModelMap modelo, HttpSession session, @RequestParam String idUsuario, @RequestParam String idCC, String cvuoAliasUsuario) throws ErrorServicio {
        List<Usuario> listaUsuario = new ArrayList<Usuario>();
        try {

            Cuenta cuenta = cuentaServicio.buscarCuentaPorAlias(cvuoAliasUsuario);

            if (cuenta == null) {

                cuenta = cuentaServicio.buscarCuentaPorCbu(cvuoAliasUsuario);
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
            
             //para que se vean suaurios sin tuki efectivo
            List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
            //Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
            List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
            for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
                saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
            }
            
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCC);
        modelo.addAttribute("actividad", actividad);

            modelo.addAttribute("micuentaC", usuarioCuentaC);
            modelo.addAttribute("cuentaComun", cuentaComun);
            modelo.addAttribute("listaUsuarios", listaUsuarios);
            modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);
                        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
            modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

            return "cuentaComun.html"; //check   
        } catch (ErrorServicio e) {
            e.printStackTrace();
           //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
            
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }

        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("micuenta", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);
        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

        
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCC);
        modelo.addAttribute("actividad", actividad);
        
            modelo.put("mensaje1", "Error al editar Usuario " + e.getMessage());
            modelo.put("clase1", "danger");

            return "cuentaComun.html"; //check 
        }
    }

    @PostMapping("/agregarUsuarioCCTKEfectivo")
    public String agregarUsuarioCCTKEfectivo(ModelMap modelo, HttpSession session, @RequestParam String idUsuario, @RequestParam String idCC, String cvuUsuario, String aliasCC, String montoEfectivoTk) throws ErrorServicio {
        List<Usuario> listaUsuario = new ArrayList<Usuario>();
        try {

            Cuenta cuenta = cuentaServicio.buscarCuentaPorAlias(aliasCC);

            if (cuenta == null) {

                cuenta = cuentaServicio.buscarCuentaPorCbu(cvuUsuario);
            }

            //para agrego usuarios a CC
            Usuario usuario = usuarioServicio.buscarPorCuentaId(cuenta.getId());
            CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
            //primero verifico que no este agregado ya
            if (usuario.getId() == cuentaComunServicio.buscarUsuarioEspecificoCC(usuario.getId()).getId()) {
                //registro el monto efectivo
                Float montof = Float.valueOf(montoEfectivoTk);
                efectivoCCServicio.crear(idUsuario, montof, cuentaComunServicio.buscarUsuarioEspecificoCC(usuario.getId()).getNombre());
                //aca deberia agregar actividad el importe falta
            } else {

                listaUsuario.add(usuario);

                listaUsuario.addAll(cuentaComun.getUsuarios());

                cuentaComunServicio.agregarUsuarioCuentaComun(cuentaComun.getId(), listaUsuario);

                //registro el monto efectivo
                Float montof = Float.valueOf(montoEfectivoTk);
                efectivoCCServicio.crear(idUsuario, montof, usuario.getNombre());

                //aca deberia agregar actividad el importe falta
            }

            //en listo montos aportados de todos
            List<Float> saldosUsuarios = new ArrayList<Float>();
            Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);

            List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
            for (Usuario usuarioAux : listaUsuarios) {
                saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
            }

            
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCC);
        modelo.addAttribute("actividad", actividad);
            modelo.addAttribute("micuentaC", usuarioCuentaC);
            modelo.addAttribute("cuentaComun", cuentaComun);
            modelo.addAttribute("listaUsuarios", listaUsuarios);
            modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

            return "cuentaComun.html"; //check   
        } catch (ErrorServicio e) {
            e.printStackTrace();
           //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
            
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }

        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("micuenta", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);
        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

        
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCC);
        modelo.addAttribute("actividad", actividad);
            return "cuentaComun.html"; //check 
        }
    }

    @PostMapping("/agregarUsuarioCCEfectivo")
    public String agregarUsuarioCCEfectivo(ModelMap modelo, HttpSession session, @RequestParam String idUsuario, @RequestParam String idCC, String nombreEfectivo, String montoEfectivo) throws ErrorServicio {
        List<EfectivoCC> listaUsuariosEfectivo = new ArrayList<EfectivoCC>();
        try {

            Float montof = Float.valueOf(montoEfectivo);
            listaUsuariosEfectivo.add(efectivoCCServicio.crear("efectivo", montof, nombreEfectivo));
            
            CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
            listaUsuariosEfectivo.addAll(cuentaComun.getEfectivoCC());
            cuentaComunServicio.agregarPersonaEfectivo(cuentaComun.getId(), listaUsuariosEfectivo);

            //para que se vean suaurios sin tuki efectivo
            List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
            Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
            List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
            for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
                saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
            }

            //para que se vean usuarios de tuki
            List<Float> saldosUsuarios = new ArrayList<Float>();
            List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
            for (Usuario usuarioAux : listaUsuarios) {
                saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
            }

        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCC);
        modelo.addAttribute("actividad", actividad);
        
            modelo.addAttribute("listaUsuarios", listaUsuarios);
            modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

            modelo.addAttribute("micuentaC", usuarioCuentaC);
            modelo.addAttribute("cuentaComun", cuentaComun);
            modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
            modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

            return "cuentaComun.html"; //check   
        } catch (ErrorServicio e) {
            e.printStackTrace();
           //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
            
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }

        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("micuenta", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);
        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

        
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCC);
        modelo.addAttribute("actividad", actividad);
            return "cuentaComun.html"; //check 
        }
    }
    
    @PostMapping("/eliminardeListaUsuariosTuki")
    public String eliminardeListaUsuariosTuki(HttpSession session, ModelMap modelo,@RequestParam String idUsuario, @RequestParam String idE, @RequestParam String idCC) throws ErrorServicio {
   
            cuentaComunServicio.eliminardeListaUsuariosTuki(idE, idCC);

//para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }

        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCC);
        modelo.addAttribute("actividad", actividad);
        
        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

        return "cuentaComun.html";

    }
    
        @PostMapping("/eliminardeListaUsuariosSinTuki")
    public String eliminardeListaUsuariosSinTuki(HttpSession session, ModelMap modelo,@RequestParam String idUsuario, @RequestParam String idE2, @RequestParam String idCC) throws ErrorServicio {
   
            cuentaComunServicio.eliminardeListaUsuariosSinTuki(idE2, idCC);

//para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCC);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }
        
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCC);
        modelo.addAttribute("actividad", actividad);

        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);

        return "cuentaComun.html";

    }
    
    @PostMapping("/divisionJusta")
    public String divisionJusta(HttpSession session, ModelMap modelo,@RequestParam String idUsuario, @RequestParam String idCuentaComun) throws ErrorServicio {
        try {
            cuentaComunServicio.divisionJusta(idCuentaComun);
            

            
            //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCuentaComun);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }
        
        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCuentaComun);
        modelo.addAttribute("actividad", actividad);

        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);
            
            
            modelo.put("mensaje", "El saldo de la cuenta ha sido repartido exitosamente");
            modelo.put("clase", "success");
            
            return "cuentaComun.html";
        } catch (ErrorServicio e) {
            
            
            //para que se vea usuarios con tuki
        List<Float> saldosUsuarios = new ArrayList<Float>();
        Usuario usuarioCuentaC = usuarioServicio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunServicio.buscarCuentaPorIdCC(idCuentaComun);
        List<Usuario> listaUsuarios = cuentaComunServicio.enlistar(cuentaComun.getId());
        for (Usuario usuarioAux : listaUsuarios) {
            saldosUsuarios.add(cuentaComunServicio.sumaSaldoPorUsuario(usuarioAux));
        }
        List<CuentaComun> listaCC = cuentaComunServicio.buscarCuentaComunPorIdUsuario(idUsuario);

        //para que se vea usuarios sin tuki
        List<Float> saldosUsuariosEfectivo = new ArrayList<Float>();
        List<EfectivoCC> listaUsuariosEfectivo2 = cuentaComunServicio.enlistarEfectivos(cuentaComun.getId());
        for (EfectivoCC usuarioEfectivoAux : listaUsuariosEfectivo2) {
            saldosUsuariosEfectivo.add(cuentaComunServicio.sumaSaldoPorUsuarioEfectivo(usuarioEfectivoAux));
        }

        List<Actividad> actividad = cuentaComunServicio.mostrarActividadCuentaComun(idCuentaComun);
        modelo.addAttribute("actividad", actividad);
        modelo.addAttribute("listaCC", listaCC);
        modelo.addAttribute("micuentaC", usuarioCuentaC);
        modelo.addAttribute("cuentaComun", cuentaComun);
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        modelo.addAttribute("listaSaldosUsuarios", saldosUsuarios);

        modelo.addAttribute("listaUsuariosEfectivo2", listaUsuariosEfectivo2);
        modelo.addAttribute("listaSaldosUsuariosEfectivo", saldosUsuariosEfectivo);
            
                    modelo.put("mensaje1", "Error al editar Usuario " + e.getMessage());
            modelo.put("clase1", "danger");
        return "cuentaComun.html";
        }
        
    }

}
