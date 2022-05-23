
package com.proyectofinal.tukiwallet.Repositorios;


import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CuentaComunRepositorio extends JpaRepository<CuentaComun, String> {
   
 
@Query("SELECT cc FROM CuentaComun cc WHERE cc.id = :id")
    public CuentaComun buscarCuentaComunPorId (@Param("id") String id);  

    
@Query("SELECT cc.usuarios FROM CuentaComun cc WHERE cc.id = :id")
    public List<Usuario> mostrarUsuarios (@Param("id") String id);  


@Query("SELECT cc FROM CuentaComun cc WHERE cc.nombre = :nombre")
    public CuentaComun mostrarPorNombre (@Param("nombre") String nombre);  

    
}



