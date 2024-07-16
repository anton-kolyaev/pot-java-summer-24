package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.entity.NotificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  @Autowired
  private JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String fromMail;

  public void sendMail(String mail, NotificationEntity notificationEntity) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setFrom(fromMail);
    mailMessage.setSubject(notificationEntity.getSubject());
    mailMessage.setText(notificationEntity.getMessage());
    mailMessage.setTo(mail);

    javaMailSender.send(mailMessage);
  }
}
