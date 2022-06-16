package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Repositorios.CuentaRepositorio;
import com.proyectofinal.tukiwallet.Repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuentaServicio {

    @Autowired
    private CuentaRepositorio cuentaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ActividadServicio actividadServicio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Cuenta registrar(String dni) throws ErrorServicio {
        String alias = dni + ".TUKI";
        comprobarAlias(alias);
        Cuenta cuenta = new Cuenta();
        cuenta.setAlias(alias);
        cuenta.setSaldo(0f);
        cuenta.setCvu(crearCvu(dni));
        cuenta.setAlta(true);

        cuentaRepositorio.save(cuenta);
        return cuenta;
    }

    @Transactional(propagation = Propagation.NESTED)
    public Cuenta modificarAlias(String alias, String id) throws ErrorServicio {
        comprobarAliasCambiado(alias);
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();
            cuenta.setAlias(alias);
            cuentaRepositorio.save(cuenta);
            return cuenta;

        } else {
            throw new ErrorServicio("No se ha encontrado el id cuenta");
        }

    }

    //ingresa
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void ingresoCuenta(Float cantidad, String cvuEgresa, String cvuIngresa, String motivo) throws ErrorServicio {
        Cuenta cuenta = cuentaRepositorio.buscarCuentaPorCvu(cvuIngresa);
        if (cuenta != null) {
            cuenta.setSaldo(cuenta.getSaldo() + cantidad);
            cuentaRepositorio.save(cuenta);
            List<Actividad> actividades = new ArrayList<Actividad>();
            actividades.add(actividadServicio.registrar(motivo, cantidad, false, cvuEgresa, cvuIngresa));
            cuenta.setActividad(actividades);

            System.out.println("llegue a ingreso cuenta");

        } else {
            throw new ErrorServicio("No se ha encontrado la Cuenta Destino que ingresa dinero");
        }
    }

    //egresa
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void egresoCuenta(Float cantidad, String cvuEgresa, String cvuIngresa, String motivo) throws ErrorServicio {
        List<Actividad> actividades = new ArrayList<Actividad>();
        Cuenta cuenta = cuentaRepositorio.buscarCuentaPorCvu(cvuEgresa);
        if (cuenta != null) {
            cuenta.setSaldo(cuenta.getSaldo() - cantidad);
            cuentaRepositorio.save(cuenta);

            System.out.println("llegue a egreso");

            Actividad actividad = actividadServicio.registrar(motivo, cantidad, true, cvuEgresa, cvuIngresa);
            actividades.add(actividad);
            cuenta.setActividad(actividades);

        } else {
            throw new ErrorServicio("No se ha encontrado la Cuenta a origen que egresa dinero");
        }
    }

    public void validarTransferenciaCuenta(Float cantidad, String cvu1, String cvu2) throws ErrorServicio {
        Cuenta cuenta = cuentaRepositorio.buscarCuentaPorCvu(cvu1);
        if (cuenta != null) {
            if (cuenta.getSaldo() < cantidad) {
                throw new ErrorServicio("No tiene esa cantidad en su cuenta");
            }
            if (cantidad < 0) {
                throw new ErrorServicio("No puede transferir una cantidad negativa");
            }
            if (cvu2.equals(cvu1)) {
                throw new ErrorServicio("No se puede transferir al mismo cvu cuenta");
            }
        } else {
            throw new ErrorServicio("No se ha encontrado el cvu de la cuenta");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void baja(String id) throws ErrorServicio {
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();

            if (cuenta.getSaldo() == 0f) {
                cuenta.setAlta(false);
                cuentaRepositorio.save(cuenta);
            } else {
                throw new ErrorServicio("No se puede dar Baja porque tiene saldo la Cuenta, debe transferir a otra Cuenta");
            }
        } else {
            throw new ErrorServicio("No se ha encontrado el id de la Cuenta");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void alta(String id) throws ErrorServicio {
        Optional<Cuenta> respuesta = cuentaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cuenta cuenta = respuesta.get();
            cuenta.setAlta(true);
            cuentaRepositorio.save(cuenta);
        } else {
            throw new ErrorServicio("No se ha encontrado el id cuenta");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void borrarPorId(String id) throws ErrorServicio {
        Optional<Cuenta> optional = cuentaRepositorio.findById(id);
        if (optional.isPresent()) {
            cuentaRepositorio.delete(optional.get());
        } else {
            throw new ErrorServicio("No se encontró el id cuenta");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public Cuenta buscarPorId(String id) throws ErrorServicio {
        Optional<Cuenta> optional = cuentaRepositorio.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new ErrorServicio("No se encontró el Id cuenta");
        }
    }

    public void validarSaldo(Float saldo) throws ErrorServicio {
        if (saldo == null || saldo.toString().trim().isEmpty()) {
            throw new ErrorServicio("El saldo no puede ser nulo cuenta");
        }
    }

    public void validarAlias(String alias) throws ErrorServicio {
        if (alias == null || alias.trim().isEmpty()) {
            throw new ErrorServicio("El alias no puede ser nulo cuenta");
        }
    }

    @Transactional(readOnly = true)
    public Cuenta buscarCuentaPorAlias(String alias) {
        Cuenta cuenta = cuentaRepositorio.buscarCuentaPorAlias(alias);
        if (cuenta != null) {
            return cuenta;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Cuenta buscarCuentaPorCbu(String cbu) {
        Cuenta cuenta = cuentaRepositorio.buscarCuentaPorAlias(cbu);
        if (cuenta != null) {
            return cuenta;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Cuenta buscarCuentaPorid(String id) {
        Cuenta cuenta = cuentaRepositorio.buscarCuentaPorId(id);
        if (cuenta != null) {
            return cuenta;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Cuenta> mostrarTodos() {
        return cuentaRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Cuenta> mostrarAlta() {
        return cuentaRepositorio.mostrarCuentaAlta();
    }

    @Transactional(readOnly = true)
    public List<Cuenta> mostrarBaja() {
        return cuentaRepositorio.mostrarCuentaBaja();
    }

    public String crearCvu(String dni) {
        String cvu = "00001" + dni;
        Integer cant = dni.length();
        cant = cant + 5; //Para agregar 00001 al principio
        cant = 20 - cant;
        Integer temp = 0;
        for (int i = 0; i < cant; i++) {
            temp = (int) (Math.random() * 10);
            cvu = cvu + temp.toString();
        }
        comprobarCvu(cvu, dni);
        return cvu;
    }

    @Transactional(readOnly = true)
    public void comprobarCvu(String cvu, String dni) {
        Cuenta optional = cuentaRepositorio.buscarCuentaPorCvu(cvu);
        if (optional != null) {
            crearCvu(dni);
        }
    }

    @Transactional(readOnly = true)
    public String comprobarAlias(String alias) {
        Cuenta optional = cuentaRepositorio.buscarCuentaPorAlias(alias);
        if (optional == null) {
            return alias;
        } else {
            Integer temp;
            for (int i = 0; i < 5; i++) {
                temp = (int) (Math.random() * 10);
                alias = temp.toString() + alias;
            }
            return alias;
        }
    }

    @Transactional(readOnly = true)
    public void comprobarAliasCambiado(String alias) throws ErrorServicio {
        Cuenta cuenta = cuentaRepositorio.buscarCuentaPorAlias(alias);
        if (cuenta == null) {
            cuenta.setAlias(alias);
            cuentaRepositorio.save(cuenta);
        } else {
            throw new ErrorServicio("El alias ingresado pertenece a otra cuenta");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void agregarActividad(Actividad actividad, String id) throws ErrorServicio {
        Optional<Cuenta> optional = cuentaRepositorio.findById(id);
        if (optional.isPresent()) {
            Cuenta cuenta = optional.get();
            List<Actividad> actividades = cuenta.getActividad();
            actividades.add(actividad);
            cuentaRepositorio.save(cuenta);
        } else {
            throw new ErrorServicio("No se encontró el id cuenta");
        }
    }

    @Transactional(readOnly = true)
    public List<Actividad> verActividadCuenta(String id) throws ErrorServicio {
        Optional<Cuenta> optional = cuentaRepositorio.findById(id);
        if (optional.isPresent()) {
            List<Actividad> actividad = cuentaRepositorio.mostrarActividadDeCuenta(id);
            return actividad;
        } else {
            throw new ErrorServicio("No se encontró el id");
        }
    }

}
