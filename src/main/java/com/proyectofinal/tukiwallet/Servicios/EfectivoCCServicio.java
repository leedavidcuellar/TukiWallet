
package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.EfectivoCC;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Repositorios.EfectivoCCRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EfectivoCCServicio {

    @Autowired
    private EfectivoCCRepositorio efectivoCCRepositorio;

    public EfectivoCC crearEfectivo(String idUsuario, Float monto, String comentario) throws ErrorServicio {
        validarMonto(monto);
        EfectivoCC efectivoCC = new EfectivoCC();
        efectivoCC.setIdUsuario(idUsuario);
        efectivoCC.setMonto(monto);
        efectivoCC.setComentario(comentario);

        efectivoCCRepositorio.save(efectivoCC);
        return efectivoCC;
    }

    public void modificarEfectivo(String id, String idUsuario, Float monto, String comentario) throws ErrorServicio {
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
    public void eliminarEfectivo(String id) throws ErrorServicio {
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
