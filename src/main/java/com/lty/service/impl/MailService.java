package com.lty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * 邮件服务实现类
 * @author lty
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.mail.username")
public class MailService {

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender mailSender;

    /**
     * 发送简单的文本邮件
     *
     * @param to      收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendSimpleMail(String[] to, String subject, String content) {
        log.info("发送文本邮件: 收件人={}, 主题={}, 内容={}", to, subject, content);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
        log.info("邮件发送成功！");
    }

    /**
     * 发送HTML格式的邮件
     *
     * @param to      收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @throws MessagingException 邮件发送异常
     */
    public void sendHtmlMail(String[] to, String subject, String content) throws MessagingException {
        log.info("发送HTML邮件: 收件人={}, 主题={}, 内容={}", to, subject, content);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
        log.info("邮件发送成功！");
    }

    /**
     * 发送带附件的邮件
     *
     * @param to      收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param file    附件文件
     * @throws MessagingException 邮件发送异常
     */
    public void sendAttachmentMail(String[] to, String subject, String content, File file) throws MessagingException {
        log.info("发送附件邮件: 收件人={}, 主题={}, 内容={}", to, subject, content);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        String fileName = file.getName();
        helper.addAttachment(fileName, fileSystemResource);
        mailSender.send(message);
        log.info("邮件发送成功！");
    }

    /**
     * 发送带附件的邮件（支持字节作为附件）
     *
     * @param to              收件人
     * @param subject         邮件主题
     * @param content         邮件内容
     * @param attachmentBytes 附件内容的字节数组
     * @param fileName        附件文件名
     * @throws MessagingException 邮件发送异常
     */
    public void sendAttachmentMail(String[] to, String subject, String content,
                                   byte[] attachmentBytes, String fileName) throws MessagingException {
        log.info("发送字节附件邮件: 收件人={}, 主题={}, 内容={}", to, subject, content);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        ByteArrayResource byteArrayResource = new ByteArrayResource(attachmentBytes);
        helper.addAttachment(fileName, byteArrayResource);
        mailSender.send(message);
        log.info("邮件发送成功！");
    }

}