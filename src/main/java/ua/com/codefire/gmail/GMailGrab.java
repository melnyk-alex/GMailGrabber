/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.gmail;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author human
 */
public class GMailGrab {

    private static final Logger LOG = Logger.getLogger(GMailGrab.class.getName());
    static {
        try {
            LOG.addHandler(new FileHandler("GMailGrab.log"));
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(GMailGrab.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    private Session session;
    private final String username;
    private final String password;
    private Folder openFolder;

    public GMailGrab(String username, String password) throws IOException {
        this.username = username;
        this.password = password;

        initialize();
    }

    private void initialize() throws IOException {
        Properties props = System.getProperties();
        props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.store.protocol", "imaps");

        session = Session.getDefaultInstance(System.getProperties(), null);
    }

    private Folder getRootFolder() throws MessagingException {
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", username, password);

        return store.getDefaultFolder();
    }
    
    public List<Message> getMessages() throws MessagingException {
        openFolder = getRootFolder().getFolder("INBOX");
        openFolder.open(Folder.READ_ONLY);
        
        return Arrays.asList(openFolder.getMessages());
    }

    public Set<InternetAddress> retriveMails(List<Message> messages) {
        Set<InternetAddress> addresses = new HashSet<>();

        try {
            for (Message message : messages) {
                try {
                    Address[] from = message.getFrom();

                    for (Address addr : from) {
                        addresses.add((InternetAddress) addr);
                    }
                } catch (AddressException ex) {
//                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        } catch (NoSuchProviderException ex) {
//            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (MessagingException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return addresses;
    }
}
