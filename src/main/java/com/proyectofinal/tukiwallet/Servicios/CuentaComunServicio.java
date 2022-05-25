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
    
    @Autowired
    private ActividadServicio actividadServicio;
    

    @Transactional(propagation = Propagation.NESTED)
    public void crearCuentaComun(String nombre, List<Usuario> usuarios) throws ErrorServicio {

        validar(nombre, usuarios);

     
        CuentaComun cuentaComun = new CuentaComun();
        cuentaComun.setNombre(nombre);
        cuentaComun.setUsuarios(usuarios);
        String alias = nombre+ "CC" + ".TUKI";
        cuentaComun.setAliasCC(alias);
        cuentaComun.setAlta(true);
        cuentaComun.setSaldo(0f);
        cuentaComun.setCvuCC(crearCvuCC());

        cuentaComunRepositorio.save(cuentaComun);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void modificarCuentaComun(String id, String nombre, List<Usuario> usuarios) throws ErrorServicio {

        validar(nombre, usuarios);

        Optional<CuentaComun> respuesta = cuentaComunRepositorio.findById(id);
        if (respuesta.isPresent()) {
            CuentaComun cuentaComun = respuesta.get();
            cuentaComun.setNombre(nombre);
            cuentaComun.setUsuarios(usuarios);

            cuentaComunRepositorio.save(cuentaComun);

        } else {
            throw new ErrorServicio("NO se enceontró el usuario solicitado.");
        }
    }

    @Transactional
    public void divisionJusta(String idCuentaComun) throws ErrorServicio {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaComunPorId(idCuentaComun);
        
           for (Usuario usuario : cuentaComun.getUsuarios()) {
            
            }
        
        
        // depositos, usuario
        
        
        
        
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void transferirCC(Float cantidad, String idtransfiere, String iddeposita, String motivo) throws ErrorServicio{
        Optional<CuentaComun> respuesta = cuentaComunRepositorio.findById(idtransfiere);
        if (respuesta.isPresent()) {
            CuentaComun cuentaComun = respuesta.get();
            cuentaComun.setSaldo(cuentaComun.getSaldo()-cantidad);
            cuentaComunRepositorio.save(cuentaComun);
            actividadServicio.registrar(motivo,cantidad,true,idtransfiere,iddeposita);
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }
    
    public void transferenciaCC(Float cantidad, String idtransfiere, String iddeposita, String motivo) throws ErrorServicio{
        transferir(cantidad, iddeposita, idtransfiere, motivo);
        depositar(cantidad, iddeposita, idtransfiere, motivo);
    }
    

    @Transactional(propagation = Propagation.NESTED)
    public void deshabilitar(String id) throws ErrorServicio {

        Optional<CuentaComun> respuesta = cuentaComunRepositorio.findById(id);
        if (respuesta.isPresent()) {
            CuentaComun cuentaComun = respuesta.get();
            cuentaComun.setAlta(Boolean.FALSE);
            cuentaComunRepositorio.save(cuentaComun);
        } else {
            throw new ErrorServicio("NO se enceontró el usuario solicitado.");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void habilitar(String id) throws ErrorServicio {

        Optional<CuentaComun> respuesta = cuentaComunRepositorio.findById(id);
        if (respuesta.isPresent()) {
            CuentaComun cuentaComun = respuesta.get();
            cuentaComun.setAlta(Boolean.TRUE);
            cuentaComunRepositorio.save(cuentaComun);
        } else {
            throw new ErrorServicio("NO se enceontró el usuario solicitado.");
        }

    }

    public void validar(String nombre, List<Usuario> usuarios) throws ErrorServicio {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nobre del usuario no puede ser nulo.");
        }

        if (usuarios == null || usuarios.isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo.");
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> enlistar(String idCuentaComun) {
        return cuentaComunRepositorio.mostrarUsuarios(idCuentaComun);
    }

    public String crearCvuCC() {
        String cvu = "00002";
        Integer cant = 5; //Para agregar 00001 al principio
        cant = 20 - cant;
        Integer temp = 0;
        for (int i = 0; i < cant; i++) {
            temp = (int) (Math.random() * 10);
            cvu = cvu + temp.toString();
        }
        comprobarCvuCC(cvu);
        return cvu;
    }
    
     @Transactional(readOnly = true)
    public void comprobarCvuCC (String cvu){
        CuentaComun optional = cuentaComunRepositorio.buscarCuentaPorCvuCC(cvu);
        if (optional==null) {
            crearCvuCC();
        }
    }

    @Transactional(readOnly = true)
    public CuentaComun comprobarAliasCC(String alias, String nombre){
        CuentaComun optional = cuentaComunRepositorio.buscarCuentaPorAliasCC(alias);
        if (optional==null) {       
            alias = nombre+ "CC" + ".TUKI";
    // agrgar un numero despues de nombre//        
           
        }  
        
        return optional;
    }

    
    
}
