package com.lty.util.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

@Component
public class MailReceiveUtil {

    private static final int LATEST_MAIL_COUNT = 30;

    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;

    /**
     * 获取主题为“xxx”的最新一封邮件中的xlsx附件。
     * 返回的流已复制到内存中，关闭邮箱连接后仍然可以正常读取。
     *
     * @return xlsx附件输入流；未找到符合条件的邮件或附件时返回null
     * @throws Exception 邮箱连接或附件读取失败
     */
    public InputStream getLatestXlsxInputStream(String subject) throws Exception {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", port);
        props.put("mail.imap.ssl.enable", true);

        Store store = null;
        Folder inbox = null;
        try {
            Session session = Session.getInstance(props);
            store = session.getStore("imap");
            store.connect(username, password);

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int messageCount = inbox.getMessageCount();
            if (messageCount == 0) {
                return null;
            }

            // 只获取收件箱最新30封邮件，并批量预取主题，减少IMAP网络请求。
            int startMessageNumber = Math.max(1, messageCount - LATEST_MAIL_COUNT + 1);
            Message[] messages = inbox.getMessages(startMessageNumber, messageCount);
            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);
            inbox.fetch(messages, fetchProfile);

            Message latestMessage = null;

            // 邮件按序号升序排列，从最新一封向前精确匹配主题。
            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];
                if (!subject.equals(message.getSubject())) {
                    continue;
                }
                latestMessage = message;
                break;
            }

            if (latestMessage == null) {
                return null;
            }

            byte[] attachment = findXlsxAttachment(latestMessage);
            return attachment == null ? null : new ByteArrayInputStream(attachment);
        } finally {
            if (inbox != null && inbox.isOpen()) {
                inbox.close(false);
            }
            if (store != null && store.isConnected()) {
                store.close();
            }
        }
    }

    private byte[] findXlsxAttachment(Part part) throws Exception {
        String fileName = part.getFileName();
        if (fileName != null) {
            fileName = MimeUtility.decodeText(fileName);
            if (fileName.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
                try (InputStream inputStream = part.getInputStream();
                     ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    return outputStream.toByteArray();
                }
            }
        }

        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                byte[] attachment = findXlsxAttachment(multipart.getBodyPart(i));
                if (attachment != null) {
                    return attachment;
                }
            }
        }
        return null;
    }
}