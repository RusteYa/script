package download_attachment_mail;

import javax.mail.search.FlagTerm;
import java.io.File;
import java.nio.file.AccessDeniedException;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

public class EmailAttachment {

    public static void checkConnect(String host, String storeType, String user,String password){
        try {
//create properties
            Properties properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(host, user, password);
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlag = new FlagTerm(seen, false);

//find unread messages
            Message messages[] = emailFolder.search(unseenFlag);

            if (messages.length == 0) {
                System.out.println("UNREAD MESSAGES NOT FOUND");
                System.in.read();
                System.exit(0);
            }

            System.out.println("UNREAD MESSAGES : " + emailFolder.getUnreadMessageCount());
            String saveDir = System.getProperty("user.home");
            File isDir = new File(saveDir + "\\download_mail");

            if(isDir.exists() && (isDir.isDirectory())){
//dir write?
                if (!isDir.canWrite()) {
                    System.out.println("Access to the directory denied");
                    System.in.read();
                    System.exit(0);
                }
            }

            else {
//create dir
                boolean makeDir =  isDir.mkdir();
                if (!makeDir){
                    System.out.println("Error creating directory");
                    System.in.read();
                    System.exit(0);
                }
            }

            for (int i = 0, n = messages.length; i < n; i++) {

                Message message = messages[i];
                Object content = message.getContent();
//no content
                if (!(content instanceof Multipart)){message.setFlag(Flags.Flag.SEEN, false); continue;}

                Multipart multiPart = (Multipart) content;
                int numberOfParts = multiPart.getCount();

                for (int partCount = 0; partCount < numberOfParts; partCount++) {

                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
//message.setFlag(Flags.Flag.DELETED, true);
                        String fileName = part.getFileName();
                        String saveFileName = MimeUtility.decodeText(fileName);

                        try{
                            part.saveFile(saveDir + "\\download_mail" +"" + saveFileName);
                        }

                        catch(AccessDeniedException exc){System.out.println("Access denied to file");
                            System.exit(1);}
                    }
                }
            }
//close store,folder
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException exc) {
            exc.printStackTrace();
        } catch (MessagingException exc) {
            exc.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    public static void main(String[] args) {

        String host = "imap.mail.ru";//
        String mailStoreType = "imap";
        String username = "user@mail.ru";//
        String password = "password";//

        checkConnect(host, mailStoreType, username, password);
    }
}
