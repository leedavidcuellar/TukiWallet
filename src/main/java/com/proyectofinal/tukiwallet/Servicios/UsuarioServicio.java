package com.proyectofinal.tukiwallet.Servicios;

import com.proyectofinal.tukiwallet.Entidades.Cuenta;
import com.proyectofinal.tukiwallet.Entidades.CuentaComun;

import com.proyectofinal.tukiwallet.Entidades.Foto;
import com.proyectofinal.tukiwallet.Entidades.Usuario;
import com.proyectofinal.tukiwallet.Errores.ErrorServicio;
import com.proyectofinal.tukiwallet.Repositorios.CuentaComunRepositorio;
import com.proyectofinal.tukiwallet.Repositorios.CuentaRepositorio;
import com.proyectofinal.tukiwallet.Repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private CuentaServicio cuentaServicio;

    @Autowired
    private CuentaComunServicio cuentaComunServicio;

    @Autowired
    private CuentaRepositorio cuentaRepositorio;

    @Autowired
    private CuentaComunRepositorio cuentaComunRepositorio;

    @Autowired
    private FotoServicio fotoServicio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void registrarUsuario(MultipartFile archivo, String nombre, String apellido, Date fechaNacimiento, String dni, String mail, String clave1, String clave2) throws ErrorServicio, MessagingException {
        List<Usuario> listaUsuario = new ArrayList<Usuario>();
        List<CuentaComun> listaCuentaComun = new ArrayList<CuentaComun>();

        Cuenta cuenta = cuentaServicio.registrar(dni);

        validar(nombre, apellido, dni, mail, fechaNacimiento, clave1, clave2, cuenta);
        //a

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
        usuario.setDni(dni);
        String encriptada = new BCryptPasswordEncoder().encode(clave1);
        usuario.setClave(encriptada);
        usuario.setAlta(Boolean.TRUE);
        usuario.setFechaNacimiento(fechaNacimiento);

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);

        usuario.setCuenta(cuenta);

        usuarioRepositorio.save(usuario);

        listaUsuario.add(usuario);

        String auxnombreCC = apellido + dni;
        CuentaComun cuentaComun = cuentaComunServicio.crearCuentaComun(auxnombreCC, usuario.getId(), listaUsuario);

        listaCuentaComun.add(cuentaComun);

        usuario.setCuentaComun(listaCuentaComun);

        usuarioRepositorio.save(usuario);

        notificacionServicio.enviar("Bienvenido al TukiWallet", "Tuki Wallet", usuario.getMail());
        //notificacionServicio.enviarHtml("Bienvenido al TukiWallet", "Tuki Wallet", usuario.getMail(),usuario.getNombre());

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void modificarUsuario(MultipartFile archivo, String idUsuario, String nombre, String apellido, String dni, String mail, Date fechaNacimiento, String clave1, String clave2) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(mail);
            usuario.setDni(dni);
            if (fechaNacimiento != null) {
                usuario.setFechaNacimiento(fechaNacimiento);
            }

            if (clave1 == null) {
                String encriptada = new BCryptPasswordEncoder().encode(clave1);
                usuario.setClave(encriptada);
            }

            String idFoto = null;
            if (usuario.getFoto() == null) {

                Foto foto = fotoServicio.guardar(archivo);
                usuario.setFoto(foto);
            } else {
                idFoto = usuario.getFoto().getId();
                Foto foto = fotoServicio.actualizar(idFoto, archivo);
                usuario.setFoto(foto);
            }

            usuarioRepositorio.save(usuario);

            notificacionServicio.enviar("Se Modifico su Usuario de TukiWallet", "TukiWallet", usuario.getMail());
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void eliminarUsuario(String idUsuario) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            if (usuario.getAlta() == Boolean.FALSE) {
                usuarioRepositorio.delete(usuario);
            } else {
                throw new ErrorServicio("No se puede eliminar, porque Usuario esta activo, debe darlo baja");
            }
        } else {
            throw new ErrorServicio("No existe un Usuario con el identificador solicitado");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void deshabilitarUsuario(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setAlta(Boolean.FALSE);
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void habilitarUsuario(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setAlta(Boolean.TRUE);

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(String id) {
        return usuarioRepositorio.buscarPorId(id);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorDni(String dni) {
        return usuarioRepositorio.buscarPorDni(dni);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorMail(String mail) {
        return usuarioRepositorio.buscarPorMail(mail);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorCuentaId(String idCuenta) {
        return usuarioRepositorio.buscarPorCuentaId(idCuenta);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorCuentaComunId(String idCuentaComun) {
        return usuarioRepositorio.buscarPorCuentaComunId(idCuentaComun);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    private void validar(String nombre, String apellido, String dni, String mail, Date fechaNacimiento, String clave1, String clave2, Cuenta cuenta) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo. Por favor ingrese un nombre");
        }

        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no puede ser nulo. Por favor ingrese un apellido");
        }

        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo. Por favor ingrese una dirección de mail");
        }

        if (clave1 == null || clave1.trim().isEmpty() || clave1.length() < 6) {
            throw new ErrorServicio("La contraseña del usuario no puede ser nula o tener menos de 6 caracteres");
        }

        if (!clave1.equals(clave2)) {
            throw new ErrorServicio("Las claves deben ser iguales. Por favor ingrese las claves nuevamente");
        }

        if (cuenta == null) {
            throw new ErrorServicio("No se ha podido encontrar la cuenta solicitada.");
        }

        if (dni.isEmpty() || dni == null) {
            throw new ErrorServicio("El DNI no puede ser nulo. Por favor ingrese un numero de DNI");
        }
        if (usuarioRepositorio.buscarPorDni(dni) != null) {
            throw new ErrorServicio("El DNI ingresado ya tiene una cuenta TUKI vinculada!");
        }

        if (usuarioRepositorio.buscarPorMail(mail) != null) {
            throw new ErrorServicio("El e-mail ingresado ya tiene una cuenta TUKI vinculada!");
        }

        if (fechaNacimiento == null) {
            throw new ErrorServicio("La fecha de nacimiento debe ser valida");
        }

    }

    private void validar2(String nombre, String apellido, String dni, String mail, Date fechaNacimiento, String clave1, String clave2) throws ErrorServicio {
        if (nombre.isEmpty()) {
            throw new ErrorServicio("No puede ser espacio en nombre");
        }

        if (apellido.isEmpty()) {
            throw new ErrorServicio("No puede ser espacio en apellido");
        }

        if (mail.isEmpty()) {
            throw new ErrorServicio("No puede ser espacio en mail");
        }

        if (clave1.trim().isEmpty() || clave1.length() < 6) {
            throw new ErrorServicio("No puede ser espacio en clave");
        }

        if (!clave1.equals(clave2)) {
            throw new ErrorServicio("Las claves tiene que ser iguales");
        }

        if (dni.isEmpty()) {
            throw new ErrorServicio("No puede ser espacio en dni");
        }
        if (usuarioRepositorio.buscarPorDni(dni) != null) {
            throw new ErrorServicio("El DNI ingresado ya existe!");
        }
        if (usuarioRepositorio.buscarPorMail(mail) != null) {
            throw new ErrorServicio("El e-mail ingresado ya tiene una cuenta vinculada!");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);
        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();

            if (!"admin@tukiwalet.com".equals(usuario.getMail())) {
                GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
                permisos.add(p1);

                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attr.getRequest().getSession(true);
                session.setAttribute("usuariosession", usuario);

            } else {
                GrantedAuthority p2 = new SimpleGrantedAuthority("ROLE_ADMINISTRADOR");
                permisos.add(p2);

                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attr.getRequest().getSession(true);
                session.setAttribute("usuariosession", usuario);
            }

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;
        } else {
            return null;
        }
    }
}
