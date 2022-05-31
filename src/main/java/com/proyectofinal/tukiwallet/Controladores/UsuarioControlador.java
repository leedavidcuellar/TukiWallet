package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/")
public class UsuarioControlador {
    
    @Autowired
    UsuarioServicio usuarioServicio;
    
    @PostMapping("/registrarse")
    public String registrarse() throws ErrorServicio{
        return "/registrarse";
    }
    
    @PostMapping("/registrarUsuario")
    public String registrarUsuario(ModelMap model, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String dni, @RequestParam String mail, @RequestParam String clave1, @RequestParam String clave2, @RequestParam MultipartFile archivo) throws ErrorServicio{
        try {
            
            usuarioServicio.registrarUsuario(archivo, nombre, apellido, dni, mail, clave1, clave2);
            return "/inicio";

        } catch (ErrorServicio e) {
            model.put("mensaje", e.getMessage());
            return "/registrarse";
        }   
    }
    
    @PostMapping("/editarUsuario")
    public String editarUsuario(ModelMap model, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String dni, @RequestParam String mail, @RequestParam String clave1, @RequestParam String clave2, @RequestParam MultipartFile archivo) throws ErrorServicio{
        try {
            
            usuarioServicio.registrarUsuario(archivo, nombre, apellido, dni, mail, clave1, clave2);
            return "/inicio";

        } catch (ErrorServicio e) {
            model.put("mensaje", e.getMessage());
            return "/registrarse";
        }   
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
            return "/registrarse";
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
