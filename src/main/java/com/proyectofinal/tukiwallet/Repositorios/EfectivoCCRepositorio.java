/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Repositorios;


import com.proyectofinal.tukiwallet.Entidades.EfectivoCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author HP
 */
@Repository
public interface EfectivoCCRepositorio extends JpaRepository<EfectivoCC, String> {
     @Query("SELECT sum(e.monto) FROM EfectivoCC e WHERE e.idUsuario = :idUsuario")
    public Float SumaMontoEfectivo (@Param("idUsuario") String idUsuario);
    
     @Query("SELECT e FROM EfectivoCC e WHERE e.id = :id")
    public EfectivoCC buscarEfectivoPorId (@Param("id") String id);  //por las dudas
}
