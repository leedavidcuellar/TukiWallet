package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Repositorios.CuentaComunRepositorio;
import com.proyectofinal.tukiwallet.Repositorios.UsuarioRepositorio;
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
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private CuentaServicio cuentaServicio;

    @Transactional(propagation = Propagation.NESTED)
    public void crearCuentaComun(String nombre, List<Usuario> usuarios) throws ErrorServicio {

        validar(nombre, usuarios);

        CuentaComun cuentaComun = new CuentaComun();
        cuentaComun.setNombre(nombre);
        cuentaComun.setUsuarios(usuarios);
        String alias = nombre + "CC" + ".TUKI";
        cuentaComun.setAliasCC(alias);
        cuentaComun.setAlta(true);
        cuentaComun.setSaldoCC(0f);
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
        Integer cantidadUsuarios = cuentaComun.getUsuarios().size();
        String[][] aux = new String[cantidadUsuarios][3];
        Boolean flag = Boolean.TRUE;
        
        int i = 0;
        Float gastoTotal = cuentaComunRepositorio.sumaGastoTotalCC();
        Float gastoPorPersona = gastoTotal/cantidadUsuarios;
        String mensaje = "No se pudo realizar la división ya que: ";
        
        for (Usuario usuario : cuentaComun.getUsuarios()) {
            if (usuario != null) {
                aux[i][0] = usuario.getId();
                aux[i][1] = (cuentaComunRepositorio.sumaSaldoCCporCVU(usuario.getCuenta().getCvu())).toString();                
                aux[i][2] = ((Float)(Float.valueOf(aux[i][1]) - gastoPorPersona)).toString();
                if(Float.valueOf(aux[i][2]) < 0){
                    flag = Boolean.FALSE;
                    mensaje = mensaje + usuario.getNombre() + " debe " + aux[i][2] + "; ";
                }
                i++;
            } else {
                throw new ErrorServicio("NO hay otros usuarios");
            }
        }
        
        String cvu1 = cuentaComun.getCvuCC();
        if(flag=Boolean.TRUE){
            for (int j = 0; j < cantidadUsuarios; j++) {
                Optional<Usuario> usuario = usuarioRepositorio.findById(aux [j][0]);
                if (usuario.isPresent()) {
                    String cvu2 = usuario.get().getCuenta().getCvu();
                    Float monto = Float.valueOf(aux[j][2]);
                    egresoCuentaComun(monto,cvu1,cvu2,"Division Cuenta Comun");
                    cuentaServicio.ingresoCuenta(monto,cvu1,cvu2,"Division Cuenta Comun");
                }else{
                    throw new ErrorServicio("NO se encontró el usuario solicitado.");
                }
            }
        }else{
            throw new ErrorServicio(mensaje);
        }
    }

    
    public void listaPersonasEfectivo(){
        
    }
    
    //ingresa
    @Transactional(propagation = Propagation.NESTED)
    public void ingresoCuentaComun(Float cantidad, String cvuEgresa, String cvuIngresa, String motivo) throws ErrorServicio {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaPorCvuCC(cvuIngresa);
        if (cuentaComun != null) {
            cuentaComun.setSaldoCC(cuentaComun.getSaldoCC() + cantidad);
            cuentaComunRepositorio.save(cuentaComun);
            actividadServicio.registrar(motivo, cantidad, false, cvuEgresa, cvuIngresa);
        } else {
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }
    
    //egresa
    @Transactional(propagation = Propagation.NESTED)
    public void egresoCuentaComun(Float cantidad, String cvuEgresa, String cvuIngresa, String motivo) throws ErrorServicio {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaPorCvuCC(cvuEgresa);
        if (cuentaComun != null) {
            cuentaComun.setSaldoCC(cuentaComun.getSaldoCC() - cantidad);
            cuentaComunRepositorio.save(cuentaComun);
            actividadServicio.registrar(motivo, cantidad, true, cvuEgresa, cvuIngresa);
        } else {
            throw new ErrorServicio("No se ha encontrado el id");
        }
    }
    
    public void validarTransferenciaCuentaComun(Float cantidad, String idtransfiere) throws ErrorServicio{
        Optional<CuentaComun> respuesta = cuentaComunRepositorio.findById(idtransfiere);
        if (respuesta.isPresent()) {
            CuentaComun CuentaComun = respuesta.get();
            if (CuentaComun.getSaldoCC()<cantidad) {
                throw new ErrorServicio("No tiene esa cantidad en su cuenta");
            }
        }else{
            throw new ErrorServicio("No se ha encontrado el id");
        }
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
    public void comprobarCvuCC(String cvu) {
        CuentaComun optional = cuentaComunRepositorio.buscarCuentaPorCvuCC(cvu);
        if (optional == null) {
            crearCvuCC();
        }
    }

    @Transactional(readOnly = true)
    public CuentaComun comprobarAliasCC(String alias, String nombre) {
        CuentaComun optional = cuentaComunRepositorio.buscarCuentaPorAliasCC(alias);
        if (optional == null) {
            alias = nombre + "CC" + ".TUKI";
            // agrgar un numero despues de nombre//        

        }
        return optional;
    }

}
