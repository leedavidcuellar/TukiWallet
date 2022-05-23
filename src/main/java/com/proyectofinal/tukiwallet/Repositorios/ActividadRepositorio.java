/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Repositorios;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author leedavidcuellar
 */

@Repository
public interface ActividadRepositorio extends JpaRepository<Actividad, String>{
    @Query("SELECT a FROM Actividad a WHERE a.id = :id")
    public Actividad buscarPorId(@Param("id") String id);
    
}
