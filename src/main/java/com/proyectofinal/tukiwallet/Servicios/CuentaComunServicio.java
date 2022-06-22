package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.Actividad;
import com.proyectofinal.tukiwallet.Entidades.CuentaComun;
import com.proyectofinal.tukiwallet.Entidades.EfectivoCC;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Repositorios.CuentaComunRepositorio;
import com.proyectofinal.tukiwallet.Repositorios.EfectivoCCRepositorio;
import com.proyectofinal.tukiwallet.Repositorios.UsuarioRepositorio;
import java.util.Iterator;
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

    @Autowired
    private EfectivoCCRepositorio efectivoCCRepositorio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public CuentaComun crearCuentaComun(String nombre, String idUsuario, List<Usuario> usuarios) throws ErrorServicio {

        validar(nombre, usuarios);

        CuentaComun cuentaComun = new CuentaComun();
        cuentaComun.setNombre(nombre);
        cuentaComun.setUsuarios(usuarios);
        String alias = nombre + "CC" + ".TUKI";
        cuentaComun.setAliasCC(alias);
        cuentaComun.setAlta(true);
        cuentaComun.setSaldoCC(0f);
        cuentaComun.setCvuCC(crearCvuCC());
        cuentaComun.setPropietario(idUsuario);

        cuentaComunRepositorio.save(cuentaComun);
        return cuentaComun;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CuentaComun modificarCuentaComun(String id, String nombre) throws ErrorServicio {
        validarNombreEditado(nombre);
        Optional<CuentaComun> respuesta = cuentaComunRepositorio.findById(id);
        if (respuesta.isPresent()) {
            CuentaComun cuentaComun = respuesta.get();
            cuentaComun.setNombre(nombre);
            cuentaComunRepositorio.save(cuentaComun);
            return cuentaComun;
        } else {
            throw new ErrorServicio("No se enceontró el usuario solicitado.");
        }
    }

    @Transactional
    public void agregarUsuarioCuentaComun(String id, List<Usuario> usuarios) throws ErrorServicio {

        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaComunPorId(id);
        if (cuentaComun != null) {

            cuentaComun.setUsuarios(usuarios);

            cuentaComunRepositorio.save(cuentaComun);

        } else {
            throw new ErrorServicio("No se encontró la cuenta comun  solicitada.");
        }
    }

    @Transactional
    public void agregarPersonaEfectivo(String id, List<EfectivoCC> efectivoCCPersonas) throws ErrorServicio {

        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaComunPorId(id);
        if (cuentaComun != null) {

            cuentaComun.setEfectivoCC(efectivoCCPersonas);

            cuentaComunRepositorio.save(cuentaComun);

        } else {
            throw new ErrorServicio("No se encontró la cuenta comun  solicitada.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void divisionJusta(String idCuentaComun) throws ErrorServicio {

        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaComunPorId(idCuentaComun);
//        for (EfectivoCC efectivo : cuentaComun.getEfectivoCC()) {
//
//            Usuario usuarioExtra = cuentaComunRepositorio.buscarUsuarioCC(efectivo.getId());
//
//            if (usuarioExtra == null) {
//                cuentaComun.getUsuarios().add(usuarioExtra);
//            }
//
//        }
        Integer cantidadUsuarios = cuentaComun.getUsuarios().size();
        System.out.println(cantidadUsuarios);
        String[][] aux = new String[cantidadUsuarios][4];
        Boolean flag = Boolean.TRUE;

        int i = 0;
        Float gastoTotal = cuentaComunRepositorio.sumaGastoTotalCC() + cuentaComunRepositorio.sumaGastoTotalEfectivoCC();

        System.out.println(gastoTotal);

        Float gastoPorPersona = gastoTotal / cantidadUsuarios;
        String mensaje = "No se pudo realizar la división ya que: ";

        for (Usuario usuario : cuentaComun.getUsuarios()) {
            if (usuario != null) {
                aux[i][0] = usuario.getId();
                if ((cuentaComunRepositorio.sumaSaldoCCporCVU(usuario.getCuenta().getCvu())) == null) {
                    Integer cero = 0;
                    aux[i][1] = cero.toString();

                } else {
                    aux[i][1] = (cuentaComunRepositorio.sumaSaldoCCporCVU(usuario.getCuenta().getCvu())).toString();
                }

                if ((cuentaComunRepositorio.sumaSaldoEfectivoPorIdUsuario(usuario.getId())) == null) {
                    Integer cero = 0;
                    aux[i][2] = cero.toString();
                } else {
                    aux[i][2] = (cuentaComunRepositorio.sumaSaldoEfectivoPorIdUsuario(usuario.getId())).toString();
                }
                System.out.println(aux[i][1]);
                System.out.println(aux[i][2]);
                System.out.println(gastoTotal);
                System.out.println(cantidadUsuarios);
                System.out.println(gastoPorPersona);

                aux[i][3] = ((Float) (Float.valueOf(aux[i][1]) + Float.valueOf(aux[i][2]) - gastoPorPersona)).toString();
                if (Float.valueOf(aux[i][2]) < 0) {
                    flag = Boolean.FALSE;
                    mensaje = mensaje + usuario.getNombre() + " debe " + aux[i][2] + "; ";
                }

                i++;
            } else {
                throw new ErrorServicio("NO hay otros usuarios");
            }
        }

        String cvu1 = cuentaComun.getCvuCC();
        if (flag == Boolean.TRUE) {
            for (int j = 0; j < cantidadUsuarios; j++) {
                Optional<Usuario> usuario = usuarioRepositorio.findById(aux[j][0]);
                if (usuario.isPresent()) {
                    String cvu2 = usuario.get().getCuenta().getCvu();
                    Float monto = Float.valueOf(aux[j][2]);
                    egresoCuentaComun(monto, cvu1, cvu2, "Division Cuenta Comun");
                    cuentaServicio.ingresoCuenta(monto, cvu1, cvu2, "Division Cuenta Comun");
                } else {
                    throw new ErrorServicio("NO se encontró el usuario solicitado.");
                }
            }
        } else {
            throw new ErrorServicio(mensaje);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void divisionSoloTuki(String idCuentaComun) throws ErrorServicio {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaComunPorId(idCuentaComun);
        Integer cantidadUsuarios = cuentaComun.getUsuarios().size();
        Float gastoTotal = cuentaComunRepositorio.sumaSaldoCCporCVU(cuentaComun.getCvuCC());
        Float gastoPorPersona = gastoTotal / cantidadUsuarios;
        boolean b = true;
        String mensaje = "No se puede realizar la división ya que: ";
        for (Usuario usuario : cuentaComun.getUsuarios()) {
            if (usuario != null) {
                throw new ErrorServicio("NO se encontró el usuario solicitado.");
            }
            Float pago = cuentaComunRepositorio.sumaSaldoCCporCVU(usuario.getCuenta().getCvu());
            if (pago < gastoPorPersona) {
                b = false;
                String nombre = usuario.getNombre();
                String debe = ((Float) (gastoPorPersona - pago)).toString();
                mensaje = mensaje + nombre + "debe: " + debe + "; ";
            }
        }
        String cvu1 = cuentaComun.getCvuCC();
        if (b) {
            for (Usuario usuario : cuentaComun.getUsuarios()) {
                String cvu2 = usuario.getCuenta().getCvu();
                Float pago = cuentaComunRepositorio.sumaSaldoCCporCVU(usuario.getCuenta().getCvu());
                Float monto = pago-gastoPorPersona;
                egresoCuentaComun(monto, cvu1, cvu2, "Division Cuenta Comun");
                cuentaServicio.ingresoCuenta(monto, cvu1, cvu2, "Division Cuenta Comun");
            }
        } else {
            throw new ErrorServicio(mensaje);
        }
    }

    //ingresa
    @Transactional(propagation = Propagation.NESTED)
    public void ingresoCuentaComun(Float cantidad, String cvuEgresa, String cvuIngresa, String motivo) throws ErrorServicio {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaPorCvuCC(cvuIngresa);
        if (cuentaComun != null) {
            cuentaComun.setSaldoCC(cuentaComun.getSaldoCC() + cantidad);
            cuentaComunRepositorio.save(cuentaComun);
            Actividad actividad = actividadServicio.registrar(motivo, cantidad, false, cvuEgresa, cvuIngresa);
            cuentaComun.setActividad(actividad);

        } else {
            throw new ErrorServicio("No se pudo ingresar Dinero, porque No se ha encontrado la Cuenta Comun");
        }
    }

    //egresa
    @Transactional(propagation = Propagation.NESTED)
    public void egresoCuentaComun(Float cantidad, String cvuEgresa, String cvuIngresa, String motivo) throws ErrorServicio {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaPorCvuCC(cvuEgresa);
        if (cuentaComun != null) {
            cuentaComun.setSaldoCC(cuentaComun.getSaldoCC() - cantidad);
            cuentaComunRepositorio.save(cuentaComun);
            Actividad actividad = actividadServicio.registrar(motivo, cantidad, true, cvuEgresa, cvuIngresa);
            cuentaComun.setActividad(actividad);
        } else {
            throw new ErrorServicio("No se pudo sacar Dinero, porque No se ha encontrado la Cuenta Comun");
        }
    }

    public void validarTransferenciaCuentaComun(Float cantidad, String cvu1, String cvu2) throws ErrorServicio {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaPorCvuCC(cvu1);
        if (cuentaComun != null) {
            if (cuentaComun.getSaldoCC() < cantidad) {
                throw new ErrorServicio("No tiene esa cantidad en su cuenta");
            }
            if (cantidad < 0) {
                throw new ErrorServicio("No puede transferir una cantidad negativa");
            }
            if (cvu2.equals(cvu1)) {
                throw new ErrorServicio("No se puede transferir al mismo cvu");
            }
        } else {
            throw new ErrorServicio("No se ha encontrado el cvu");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void deshabilitar(String id) throws ErrorServicio {

        Optional<CuentaComun> respuesta = cuentaComunRepositorio.findById(id);
        if (respuesta.isPresent()) {
            CuentaComun cuentaComun = respuesta.get();

            if (cuentaComun.getSaldoCC() == 0f) {

                cuentaComun.setAlta(Boolean.FALSE);
                cuentaComunRepositorio.save(cuentaComun);
            } else {
                throw new ErrorServicio("No se puede dar Baja porque tiene saldo Cuenta Comun, debe transferir a otra Cuenta");
            }

        } else {
            throw new ErrorServicio("NO se ha encontrado Id de la Cuenta Comun solicitada.");
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
            throw new ErrorServicio("NO se encontró el usuario solicitado.");
        }

    }

    public void validar(String nombre, List<Usuario> usuarios) throws ErrorServicio {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo.");
        }

        if (usuarios == null || usuarios.isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo.");
        }
    }

    public void validarNombreEditado(String nombre) throws ErrorServicio {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo.");
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> enlistar(String idCuentaComun) {
        return cuentaComunRepositorio.mostrarUsuarios(idCuentaComun);
    }

    @Transactional(readOnly = true)
    public List<EfectivoCC> enlistarEfectivos(String idCuentaComun) {
        return cuentaComunRepositorio.mostrarUsuariosEfectivos(idCuentaComun);
    }

    public Usuario buscarUsuarioEspecificoCC(String idCuentaComun) {
        return cuentaComunRepositorio.buscarUsuarioCC(idCuentaComun);
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
        if (optional != null) {
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

    @Transactional(readOnly = true)
    public CuentaComun buscarCuentaPorAliasCC(String alias) {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaPorAliasCC(alias);
        if (cuentaComun != null) {
            return cuentaComun;
        } else {
            return null;
        }
    }
    
     @Transactional(readOnly = true)
    public CuentaComun buscarCuentaPorCvuCC(String cvu1) {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaPorCvuCC(cvu1);
        if (cuentaComun != null) {
            return cuentaComun;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public CuentaComun buscarCuentaPorIdCC(String id) {
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaComunPorId(id);
        if (cuentaComun != null) {
            return cuentaComun;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<CuentaComun> buscarCuentaComunPorIdUsuario(String id) {
        List<CuentaComun> cuentaComun = cuentaComunRepositorio.buscarCuentaComunPorIdUsuario(id);
        if (cuentaComun != null) {

            return cuentaComun;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Float sumaSaldoPorUsuario(Usuario usuario) {

        Float saldoUsuario = cuentaComunRepositorio.sumaSaldoCCporCVU(usuario.getCuenta().getCvu());
        return saldoUsuario;
    }

    @Transactional(readOnly = true)
    public List<Actividad> mostrarActividadCuentaComun(String id) throws ErrorServicio {
        Optional<CuentaComun> optional = cuentaComunRepositorio.findById(id);
        if (optional.isPresent()) {
            List<Actividad> actividad = cuentaComunRepositorio.mostrarActividaddeCuentaComun(id);
            return actividad;
        } else {
            throw new ErrorServicio("No se encontró el id");
        }
    }

    @Transactional(readOnly = true) // no es de tuki
    public Float sumaSaldoPorUsuarioEfectivo(EfectivoCC efectivoCC) {

        Float saldoUsuarioEfectivo = cuentaComunRepositorio.sumaSaldoCCporUsuarioEfectivo(efectivoCC.getIdUsuario(), efectivoCC.getComentario());
        return saldoUsuarioEfectivo;
    }

    @Transactional(propagation = Propagation.NESTED)
    public void eliminardeListaUsuariosTuki(String idUsuario, String idCuentaComun) {
        Usuario usuario = usuarioRepositorio.buscarPorId(idUsuario);
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaComunPorId(idCuentaComun);
        List<Usuario> listaUsuarios = cuentaComun.getUsuarios();
        Iterator<Usuario> it2 = listaUsuarios.iterator();

        while (it2.hasNext()) {

            if (it2.next().equals(usuario)) {

                it2.remove();

            }
        }

        cuentaComun.setUsuarios(listaUsuarios);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void eliminardeListaUsuariosSinTuki(String idE2, String idCuentaComun) {
        EfectivoCC usuarioEfectivo = efectivoCCRepositorio.buscarEfectivoPorId(idE2);
        System.out.println(usuarioEfectivo);
        CuentaComun cuentaComun = cuentaComunRepositorio.buscarCuentaComunPorId(idCuentaComun);
        List<EfectivoCC> listaUsuariosEfectivo = cuentaComun.getEfectivoCC();
        Iterator<EfectivoCC> it2 = listaUsuariosEfectivo.iterator();

        while (it2.hasNext()) {

            if (it2.next().equals(usuarioEfectivo)) {

                it2.remove();
                efectivoCCRepositorio.deleteById(idE2);
            }
        }

        cuentaComun.setEfectivoCC(listaUsuariosEfectivo);
    }

}
