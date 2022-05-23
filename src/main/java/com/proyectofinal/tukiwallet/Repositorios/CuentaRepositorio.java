/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Repositorios;

import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author leedavidcuellar
 */

@Repository
public interface CuentaRepositorio extends JpaRepository<Cuenta, String>{
    
    @Query("SELECT a FROM Cuenta a WHERE a.alias = :alias")
    public Cuenta buscarCuentaPorAlias (@Param("alias") String alias);
    
    @Query("SELECT a FROM Cuenta a WHERE a.cvu = :cvu")
    public Cuenta buscarCuentaPorCvu (@Param("cvu") String cvu);
    
    @Query("SELECT a FROM Cuenta a WHERE a.alta = true")
    public List<Cuenta> mostrarCuentaAlta ();
    
    @Query("SELECT a FROM Cuenta a WHERE a.alta = false")
    public List<Cuenta> mostrarCuentaBaja ();
    
}
