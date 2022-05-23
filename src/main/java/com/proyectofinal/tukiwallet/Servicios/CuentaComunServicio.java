
package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Repositorios.CuentaComunRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



@Service
public class CuentaComunServicio {

@Autowired
private CuentaComunRepositorio cuentaComunRepositorio;

@Transactional (propagation = Propagation.NESTED)
public void crearCuentaComun (String nombre, List<Usuario> usuarios) throws ErrorServicio{
 
    validar(nombre, usuarios);
    
    CuentaComun cuentaComun = new CuentaComun();
    cuentaComun.setNombre(nombre);
    cuentaComun.setUsuarios(usuarios);
    cuentaComun.setAlta(Boolean.TRUE);
    cuentaComunRepositorio.save(cuentaComun);   
}


@Transactional (propagation = Propagation.NESTED)
public void modificarCuentaComun (String id, String nombre, List<Usuario> usuarios) throws ErrorServicio{
      
     validar(nombre, usuarios);
    
      Optional <CuentaComun> respuesta = cuentaComunRepositorio.findById(id);
      if (respuesta.isPresent()){
      CuentaComun cuentaComun = respuesta.get();
      cuentaComun.setNombre(nombre);
      cuentaComun.setUsuarios(usuarios);

      cuentaComunRepositorio.save(cuentaComun);
      
  } else {
         throw new ErrorServicio ("NO se enceontró el usuario solicitado.") ;
      }
  }

@Transactional
public void depositarDinero (String nombre, List<Usuario> usuarios) throws ErrorServicio{
    
    
}
    
   
 @Transactional (propagation = Propagation.NESTED)
 public void deshabilitar (String id) throws ErrorServicio{
 
     Optional<CuentaComun> respuesta = cuentaComunRepositorio.findById(id);
      if (respuesta.isPresent()){
      CuentaComun cuentaComun = respuesta.get();
      cuentaComun.setAlta(Boolean.FALSE);
      cuentaComunRepositorio.save(cuentaComun);
  } else {
         throw new ErrorServicio ("NO se enceontró el usuario solicitado.") ;
      }  
  }
 

  @Transactional (propagation = Propagation.NESTED)
  public void habilitar(String id) throws ErrorServicio{

     Optional<CuentaComun> respuesta = cuentaComunRepositorio.findById(id);
      if (respuesta.isPresent()){
      CuentaComun cuentaComun = respuesta.get();
      cuentaComun.setAlta(Boolean.TRUE);
      cuentaComunRepositorio.save(cuentaComun);
  } else {
         throw new ErrorServicio ("NO se enceontró el usuario solicitado.") ;
      }
      
  }

  
   public void validar (String nombre, List<Usuario> usuarios) throws ErrorServicio{
         
      if(nombre ==null || nombre.trim().isEmpty()){
          throw new ErrorServicio("El nobre del usuario no puede ser nulo.");
      }
      
      if(usuarios ==null || usuarios.isEmpty()){
          throw new ErrorServicio("El mail del usuario no puede ser nulo.");
      }
   }
    
   @Transactional (readOnly = true)
   public List <Usuario> enlistar (String idCuentaComun){
       return cuentaComunRepositorio.mostrarUsuarios(idCuentaComun);    
   }
      
 
     
        
}
