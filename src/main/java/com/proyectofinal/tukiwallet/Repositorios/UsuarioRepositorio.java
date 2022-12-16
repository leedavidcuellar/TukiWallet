/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Repositorios;

import com.proyectofinal.tukiwallet.Entidades.Usuario;
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
public interface UsuarioRepositorio extends JpaRepository<Usuario, String>{
    @Query("SELECT u FROM Usuario u WHERE u.mail = :mail")
    public Usuario buscarPorMail(@Param("mail") String mail);
    
    @Query("SELECT u FROM Usuario u WHERE u.dni = :dni")
    public Usuario buscarPorDni(@Param("dni") String dni);
    
    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
    public Usuario buscarPorId(@Param("id") String id);
    
    @Query("SELECT u FROM Usuario u WHERE u.cuenta.id = :idCuenta")
    public Usuario buscarPorCuentaId(@Param("idCuenta") String idCuenta);
    
    @Query("SELECT a FROM Usuario a WHERE a.alta = true")
    public List<Usuario> mostrarUsuarioAlta ();
    
    @Query("SELECT a FROM Usuario a WHERE a.alta = false")
    public List<Usuario> mostrarUsuarioBaja ();
    
    @Query("SELECT u FROM Usuario u JOIN u.cuentaComun cc WHERE cc.id = :idCuentaComun")
    public Usuario buscarPorCuentaComunId(@Param("idCuentaComun") String idCuentaComun);

}
