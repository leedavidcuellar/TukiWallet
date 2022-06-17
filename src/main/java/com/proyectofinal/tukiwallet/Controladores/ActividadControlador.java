
package com.proyectofinal.tukiwallet.Controladores;

import com.proyectofinal.tukiwallet.Servicios.ActividadServicio;
import com.proyectofinal.tukiwallet.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class ActividadControlador {
    
    @Autowired
    ActividadServicio actividadServicio;
    
    @Autowired
    UsuarioServicio usuarioServicio;
    
    @GetMapping("/actividadC")
    public String listaActividades(ModelMap modelo, String cvu){
        modelo.addAttribute("lista", actividadServicio.listadoActividadEgreso(cvu));
        return "actividad.html";
    }
    
    @GetMapping("/actividadCC")
    public String listarActividadesCC(ModelMap modelo, String cvu){
        modelo.addAttribute("lista", actividadServicio.listadoActividadIngeso(cvu));
        return "actividadCC.html";
    }
    

}
