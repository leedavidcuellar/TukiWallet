
package com.proyectofinal.tukiwallet.Repositorios;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ActividadRepositorio extends JpaRepository<Actividad, String>{
    
    @Query("SELECT a FROM Actividad a WHERE a.id = :id")
    public Actividad buscarPorId(@Param("id") String id);
    
    @Query("SELECT max(a.nOperacion) FROM Actividad a")
    public Integer buscarNumOperacionMayor();
    
    @Query("SELECT a FROM Actividad a WHERE a.cvu = :cvu AND a.movimiento = true")
    public List<Actividad> buscarActividadEsegreso(@Param("cvu") String cvu);
    
    @Query("SELECT a FROM Actividad a WHERE a.cvu2 = :cvu AND a.movimiento = false")
    public List<Actividad> buscarActividadesIngreso(@Param("cvu") String cvu);
}

