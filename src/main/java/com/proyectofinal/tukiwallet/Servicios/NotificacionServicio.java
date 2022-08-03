
package com.proyectofinal.tukiwallet.Servicios;


import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificacionServicio {
    @Autowired
    private JavaMailSender mailSender;
    
    @Async
    public void enviar(String cuerpo, String titulo, String mail){
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(mail);
        mensaje.setFrom("no-reply@tukitukiwallet.com");
        mensaje.setSubject(titulo);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }
    
    public void enviarHtml(String cuerpo, String titulo, String mail, String nombre) throws MessagingException{
        
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
 
        helper.setSubject(titulo);
        helper.setFrom("no-reply@tukitukiwallet.com");
        helper.setTo(mail);
 
        String content = "<b>Dear ${nombre}</b>,<br><i>${cuerpo}</i>"
        + "<br><img src='cid:tukifinal'/><br><b>Saludos Equipo TukiWallet</b>";
        helper.setText(content, true);
 
        FileSystemResource resource = new FileSystemResource("src/main/resource/assets/img/tukifinal.jpg");
        helper.addInline("tukifinal", resource);
 
        mailSender.send(message);
        
    }
    
}
