
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
    
@Query("SELECT cc FROM CuentaComun cc JOIN cc.usuarios u WHERE u.id = :id")
    public CuentaComun buscarCuentaComunPorIdUsuario (@Param("id") String id);
    
    @Query("SELECT u FROM CuentaComun cc JOIN cc.usuarios u WHERE u.id = :id")
    public Usuario buscarUsuarioCC (@Param("id") String id);      


@Query("SELECT cc FROM CuentaComun cc WHERE cc.nombre = :nombre")
    public CuentaComun mostrarPorNombre (@Param("nombre") String nombre);  

    
@Query("SELECT a FROM CuentaComun a WHERE a.cvuCC = :cvu")
    public CuentaComun buscarCuentaPorCvuCC (@Param("cvu") String cvu);
    
    
@Query("SELECT a FROM CuentaComun a WHERE a.aliasCC = :aliasCC")
    public CuentaComun buscarCuentaPorAliasCC (@Param("aliasCC") String alias); 
    
 @Query("SELECT sum(acc.monto) FROM CuentaComun a JOIN a.actividad acc WHERE acc.cvu = :idCvu")
    public Float sumaSaldoCCporCVU (@Param("idCvu") String idCvu);   //Engreso a CC, para saber usuario deposito

  @Query("SELECT sum(acc.monto) FROM CuentaComun a JOIN a.actividad acc WHERE acc.cvu = :idCvu2")
    public Float sumaSaldoCCporCVU2 (@Param("idCvu2") String idCvu2);  //Igreso a CC 
    
  @Query("SELECT sum(ecc.monto) FROM CuentaComun a JOIN a.efectivoCC ecc WHERE ecc.idUsuario = :idUsuario")
    public Float sumaSaldoEfectivoPorIdUsuario (@Param("idUsuario") String idUsuario);  
    
  @Query("SELECT sum(acc.monto) FROM CuentaComun a JOIN a.actividad acc WHERE acc.movimiento = True")
  public Float sumaGastoTotalCC ();  
  
  @Query("SELECT sum(ecc.monto) FROM CuentaComun a JOIN a.efectivoCC ecc")
  public Float sumaGastoTotalEfectivoCC ();   
    
}



