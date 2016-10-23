package download_attachment_mail;

import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.UnsupportedEncodingException;
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
    static String buffer;

    public static void checkConnect(String host, String storeType, String user, String password1){
        try {
//create properties
            Properties properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(host, user, password1);
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            Flags seen = new Flags(Flags.Flag.SEEN);


            //создадим видимый флаг
            //FlagTerm seenFlag = new FlagTerm(seen, true);


            FlagTerm unseenFlag = new FlagTerm(seen, false);

//find unread messages
            Message messages[] = emailFolder.search(unseenFlag);


            //ищем прочитанные письма
            //Message messages[] = emailFolder.search(seenFlag);

            //if (messages.length == 0) {
            //    System.out.println("UNREAD MESSAGES NOT FOUND");
            //   System.in.read();
            //    System.exit(0);
            //}

            System.out.println("UNREAD MESSAGES : " + emailFolder.getUnreadMessageCount());

            //количество сообщений
            //System.out.println("READ MESSAGES : " + emailFolder.getMessageCount());

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
                        String  saveFileName = MimeUtility.decodeText(fileName);

                        try{

                            buffer = saveFileName;
                            System.out.println(buffer);

                            part.saveFile(saveDir + "\\download_mail\\" + saveFileName);
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

    public static void main(String args[]) throws MessagingException, UnsupportedEncodingException {
        String host = "imap.gmail.com";//
        String mailStoreType = "imap";
        String username = "itisstream2016@gmail.com";//
        String password1 = "ItIsStream1";//

        String subject = "Subject";
        String content = "Test";
        String smtpHost="smtp.gmail.com";
        String addressFrom="jarustem@gmail.com";             String addressTo="jamrustem@mail.ru";
        String login="jarustem@gmail.com";
        String password2="318hac17";
        String smtpPort="587";

        checkConnect(host, mailStoreType, username, password1);

        System.out.println(buffer);

        String attachment = System.getProperty("user.home") + "\\download_mail\\" + buffer;
        TestMail.sendMultiMessage(login, password2, addressFrom, addressTo, content, subject, attachment, smtpPort, smtpHost);
    }
}
