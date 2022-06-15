
package com.proyectofinal.tukiwallet.Repositorios;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CuentaRepositorio extends JpaRepository<Cuenta, String>{
    
    @Query("SELECT a FROM Cuenta a WHERE a.id = :id")
    public Cuenta buscarCuentaPorId (@Param("id") String id);
    
    @Query("SELECT a FROM Cuenta a WHERE a.alias = :alias")
    public Cuenta buscarCuentaPorAlias (@Param("alias") String alias);
    
    @Query("SELECT a FROM Cuenta a WHERE a.cvu = :cvu")
    public Cuenta buscarCuentaPorCvu (@Param("cvu") String cvu);
    
    @Query("SELECT a FROM Cuenta a WHERE a.alta = true")
    public List<Cuenta> mostrarCuentaAlta ();
    
    @Query("SELECT a FROM Cuenta a WHERE a.alta = false")
    public List<Cuenta> mostrarCuentaBaja ();
    
    @Query("SELECT a.actividad FROM Cuenta a WHERE a.id = :id")
    public List<Actividad> mostrarActividadDeCuenta(@Param("id") String id);
    
}
