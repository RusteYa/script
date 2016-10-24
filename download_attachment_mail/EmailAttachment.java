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
    private static String[] directories;

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
            Flags flag = new Flags(Flags.Flag.FLAGGED);

            FlagTerm unFlag = new FlagTerm(flag, false);

            //find unmarked messages
            Message messages[] = emailFolder.search(unFlag);

            if (messages.length == 0) {
                System.out.println("UNMARKED MESSAGES NOT FOUND");
                System.in.read();
                System.exit(0);
            }

            directories = new String[messages.length];

            System.out.println("UNMARKED MESSAGES : " + messages.length);

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

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                Object content = message.getContent();

                message.setFlag(Flags.Flag.FLAGGED, true);
                //no content
                if (!(content instanceof Multipart)){continue;}

                Multipart multiPart = (Multipart) content;
                int numberOfParts = multiPart.getCount();

                for (int partCount = 0; partCount < numberOfParts; partCount++) {

                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {

                        String fileName = part.getFileName();
                        String  saveFileName = MimeUtility.decodeText(fileName);

                        try{

                            directories[i] = saveFileName;
                            System.out.println(directories[i]);

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
        String host = "imap.gmail.com";
        String mailStoreType = "imap";
        String username = "itisstream2016@gmail.com";
        String password1 = "ItIsStream1";

        String subject = "Last fils";
        String content = "Last unmarked mails";
        String smtpHost="smtp.gmail.com";
        String addressFrom="userFrom@gmail.com";
        String addressTo="userTo@mail.ru";
        String login="user@gmail.com";
        String password2="password";
        String smtpPort="587";

        checkConnect(host, mailStoreType, username, password1);

        for (int i = 0; i < directories.length; i++){
            if (directories[i] != null){
                String attachment = System.getProperty("user.home") + "\\download_mail\\" + directories[i];
                TestMail.sendMultiMessage(login, password2, addressFrom, addressTo, content, subject, attachment, smtpPort, smtpHost);
                System.out.println(directories[i]);
            }
        }
    }
}
