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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {
    
    @Autowired
    UsuarioServicio usuarioServicio;
    
    
    @PostMapping("/registrarUsuario")
    public String registrarUsuario(ModelMap model, HttpSession session, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String dni, @RequestParam String mail, @RequestParam String fechaNacimiento, @RequestParam String clave1, @RequestParam String clave2, @RequestParam MultipartFile archivo, RedirectAttributes redirectAttrs) throws ErrorServicio, ParseException{
        SimpleDateFormat formatoDateFecha = new SimpleDateFormat("dd-mm-yyyy");
        Date fechaNacimientoAux=formatoDateFecha.parse(fechaNacimiento);
            
        try {
            
            usuarioServicio.registrarUsuario(archivo, nombre, apellido, fechaNacimientoAux, dni, mail, clave1, clave2);
            Usuario usuario = usuarioServicio.buscarPorMail(mail);
            session.setAttribute("usuariosession", usuario);
            redirectAttrs
            .addFlashAttribute("mensaje", "Usuario agregado correctamente")
            .addFlashAttribute("clase", "success");
            
            return "redirect:/inicio";
        } catch (ErrorServicio e) {
            model.put("mensaje1","Error al cargar Usuario "+e.getMessage());
            model.put("clase1", "danger");
            model.put("nombre",nombre);
            model.put("apellido",apellido);
            model.put("dni",dni);
            model.put("mail",mail);
            model.put("fechaNacimiento",fechaNacimiento);
            return "/registrarse";
        }   
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/editar-perfil")
    public String editarUsuario(ModelMap model,  HttpSession session, MultipartFile archivo, @RequestParam String id,@RequestParam String nombre, @RequestParam String apellido, @RequestParam String dni, @RequestParam String mail, @RequestParam String fechaNacimiento, @RequestParam String clave1, @RequestParam String clave2,RedirectAttributes redirectAttrs) throws ErrorServicio, ParseException{
        SimpleDateFormat formatoDateFecha = new SimpleDateFormat("yyyy-mm-dd");
        Date fechaNacimientoAux=formatoDateFecha.parse(fechaNacimiento);
        System.out.println(fechaNacimientoAux +" "+fechaNacimiento);
        Usuario usuario = null;     
        try {
            usuario = usuarioServicio.buscarPorId(id);
          
            usuarioServicio.modificarUsuario(archivo, id,nombre, apellido,dni, mail,fechaNacimientoAux, clave1, clave2);
           
            session.setAttribute("usuariosession", usuario);
            redirectAttrs
            .addFlashAttribute("mensaje", "Usuario Editado correctamente")
            .addFlashAttribute("clase", "success");
            return "redirect:/inicio";

        } catch (ErrorServicio e) {
            e.printStackTrace();
            model.put("mensaje1","Error al editar Usuario "+e.getMessage());
            model.put("clase1", "danger");
            model.put("perfil", usuario);
            model.put("error", e.getMessage());

            model.put("clase1", "danger");
            model.put("nombre",nombre);
            model.put("apellido",apellido);
            model.put("dni",dni);
            model.put("mail",mail);
            model.put("fechaNacimiento",fechaNacimiento);
            return "tuperfil.html";
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
