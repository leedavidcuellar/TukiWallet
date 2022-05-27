/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.EfectivoCC;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Repositorios.EfectivoCCRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author HP
 */
@Service
public class EfectivoCCServicio {

    @Autowired
    private EfectivoCCRepositorio efectivoCCRepositorio;

    public void crear(String idUsuario, Float monto, String comentario) throws ErrorServicio {
        validarMonto(monto);
        EfectivoCC efectivoCC = new EfectivoCC();
        efectivoCC.setIdUsuario(idUsuario);
        efectivoCC.setMonto(monto);
        efectivoCC.setComentario(comentario);

        efectivoCCRepositorio.save(efectivoCC);
    }

    public void modificar(String id, String idUsuario, Float monto, String comentario) throws ErrorServicio {
        validarMonto(monto);
        Optional<EfectivoCC> resp = efectivoCCRepositorio.findById(id);
        if (resp.isPresent()) {
            EfectivoCC efectivoCC = resp.get();
            efectivoCC.setIdUsuario(idUsuario);
            efectivoCC.setMonto(monto);
            efectivoCC.setComentario(comentario);

            efectivoCCRepositorio.save(efectivoCC);
        }else{
            throw new ErrorServicio("Quien soy vo?");
        }
    }
    public void eliminar(String id) throws ErrorServicio {
        Optional<EfectivoCC> resp = efectivoCCRepositorio.findById(id);
        if (resp.isPresent()) {
            EfectivoCC efectivoCC = resp.get();
            efectivoCCRepositorio.delete(efectivoCC);
        }else{
            throw new ErrorServicio("Quien soy vo?");
        }
    }
    
    public void validarMonto(Float monto) throws ErrorServicio {
        if (monto < 0f) {
            throw new ErrorServicio("El monto es menor a cero");
        }
    }

}
