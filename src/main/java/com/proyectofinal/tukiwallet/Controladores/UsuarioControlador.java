package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {
    
    @Autowired
    UsuarioServicio usuarioServicio;
    
    
    @PostMapping("/registrarUsuario")
    public String registrarUsuario(ModelMap model, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String dni, @RequestParam String mail, @RequestParam String fechaNacimiento, @RequestParam String clave1, @RequestParam String clave2, @RequestParam MultipartFile archivo) throws ErrorServicio, ParseException{
        SimpleDateFormat formatoDateFecha = new SimpleDateFormat("yyyy-mm-dd");
            Date fechaNacimientoAux=formatoDateFecha.parse(fechaNacimiento);
            
        try {
            
            usuarioServicio.registrarUsuario(archivo, nombre, apellido, fechaNacimientoAux, dni, mail, clave1, clave2);
            return "index.html";

        } catch (ErrorServicio e) {
            model.put("mensaje", e.getMessage());
            return "/registrarse";
        }   
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/editar-perfil")
    public String editarUsuario(ModelMap model, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String dni, @RequestParam String mail, @RequestParam Date fechaNacimiento, @RequestParam String clave1, @RequestParam String clave2, @RequestParam MultipartFile archivo) throws ErrorServicio{
        try {
            
            usuarioServicio.registrarUsuario(archivo, nombre, apellido,  fechaNacimiento, dni, mail, clave1, clave2);
            return "/inicio";

        } catch (ErrorServicio e) {
            model.put("mensaje", e.getMessage());
            return "/registrarse";
        }   
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap model) {

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        //try{
        Usuario usuario = usuarioServicio.buscarPorId(id);
        model.addAttribute("perfil", usuario);
//        } catch (ErrorServicio e){
//            model.addAttribute("error", e.getMessage());
//        }


        return "tuperfil.html";
    }
    
    
    
    @PostMapping("/altaUsuario/{id}")
    public String altaUsuario(ModelMap model, @PathVariable("id") String id) throws ErrorServicio{
        try {
            
            usuarioServicio.habilitarUsuario(id);
            //ver bien el Return
            return "/inicio";

        } catch (ErrorServicio e) {
            model.put("mensaje", e.getMessage());
            //ver bien el Return
            return "/index";
        }   
    }
    
    @PostMapping("/bajaUsuario/{id}")
    public String bajaUsuario(ModelMap model, @PathVariable("id") String id) throws ErrorServicio{
        try {
            
            usuarioServicio.deshabilitarUsuario(id);
            //ver bien el Return
            return "/index";

        } catch (ErrorServicio e) {
            model.put("mensaje", e.getMessage());
            //ver bien el Return
            return "/inicio";
        }   
    }
    
    
}
