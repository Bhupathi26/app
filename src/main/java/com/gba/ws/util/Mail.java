package com.gba.ws.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Provides mail configuration details
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:12:54 PM
 */
public class Mail {

	private static final Logger LOGGER = Logger.getLogger(Mail.class);

	// private constructor to hide the implicit public one.
	private Mail() {
		super();
	}

	private static final String CONTENT_TYPE = "text/html; charset=UTF-8;";
	private static final String FROM_EMAIL_ADDRESS = "from.email.address";
	private static final String FROM_EMAIL_USER = "from.email.user";

	/**
	 * Create the session for the credencials provided
	 * 
	 * @author Mohan
	 * @return the {@link Session}
	 */
	public static Session mailSession() {
		LOGGER.info("INFO: Mail - mailSession() :: starts");
		Properties props = new Properties();
		Session session = null;
		try {
			props.put("mail.smtp.host", AppUtil.getAppProperties().get("smtp.hostname"));
			props.put("mail.smtp.port", AppUtil.getAppProperties().get("smtp.portvalue"));

			if (AppUtil.getAppProperties().get("gba.env") != null
					&& AppUtil.getAppProperties().get("gba.env").equalsIgnoreCase("local")) {
				// local mail config
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.starttls.enable", "true"); // for outlook 365 configuration
				props.put("mail.smtp.tls", "true");
				props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
				props.put("mail.smtp.ssl.protocols", "TLSv1.2");
				session = Session.getInstance(props, new javax.mail.Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(AppUtil.getAppProperties().get(FROM_EMAIL_USER),
								AppUtil.getAppProperties().get("from.email.pwd"));
					}
				});
			} else {
				// labkey mail config
				props.put("mail.smtp.auth", "false");
				session = Session.getInstance(props);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: Mail - mailSession()", e);
		}
		LOGGER.info("INFO: Mail - mailSession() :: ends");
		return session;
	}

	/**
	 * Add recipients to the mail
	 * 
	 * @author Mohan
	 * @param message
	 *            the message details
	 * @param toMailList
	 *            the to mail list
	 * @param ccMailList
	 *            the cc mail list
	 * @param bccMailList
	 *            the bcc mail list
	 * @return the {@link Message} details
	 */
	public static Message addRecipintsToMail(Message message, List<String> toMailList, List<String> ccMailList,
			List<String> bccMailList) {
		LOGGER.info("INFO: Mail - addRecipintsToMail() :: starts");
		List<String> toMailListNew = new ArrayList<>();
		try {
			// Recipients TO list
			if (toMailList != null && !toMailList.isEmpty()) {
				for (String email : toMailList) {
					email = email.trim().toLowerCase();
					toMailListNew.add(email);
					LOGGER.info("INFO: Mail - addRecipintsToMail() :: MAIL SENT TO ==> " + email);
				}
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(StringUtils.join(toMailListNew, ',')));
			}

			// Recipients CC list
			if (ccMailList != null && !ccMailList.isEmpty()) {
				message.setRecipients(Message.RecipientType.CC,
						InternetAddress.parse(StringUtils.join(ccMailList, ',')));
			}

			// Recipients BCC list
			if (bccMailList != null && !bccMailList.isEmpty()) {
				message.setRecipients(Message.RecipientType.BCC,
						InternetAddress.parse(StringUtils.join(bccMailList, ',')));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: Mail - addRecipintsToMail()", e);
		}
		LOGGER.info("INFO: Mail - addRecipintsToMail() :: ends");
		return message;
	}

	/**
	 * Add an attchment to the mail
	 * 
	 * @author Mohan
	 * @param message
	 *            the message details
	 * @param content
	 *            the content of the mail
	 * @param file
	 *            the file path details
	 * @return the {@link Message} details
	 */
	public static Message addAnAttachmentToMail(Message message, String content, String file) {
		LOGGER.info("INFO: Mail - addAnAttachmentToMail() :: starts");
		try {
			BodyPart messageBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();

			// set message body first
			messageBodyPart.setContent(content, CONTENT_TYPE);
			multipart.addBodyPart(messageBodyPart); // set to the multipart

			// set attachment second
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, CONTENT_TYPE);
			String currentPath = System.getProperty(AppUtil.getAppProperties().get(AppConstants.GBA_CURRENT_PATH));
			String rootPath = currentPath.replace('\\', '/')
					+ AppUtil.getAppProperties().get(AppConstants.GBA_DOCS_CONSENT_PATH);
			String filePath = rootPath + file; // get the file download path
			LOGGER.info("INFO: Mail - addAnAttachmentToMail() :: CONSENT FILE DOWNLOAD PATH ==> " + filePath);
			DataSource source = new FileDataSource(filePath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(file);
			multipart.addBodyPart(messageBodyPart); // set to multipart

			// Send the complete message parts
			message.setContent(multipart);
		} catch (Exception e) {
			LOGGER.error("ERROR: Mail - addAnAttachmentToMail()", e);
		}
		LOGGER.info("INFO: Mail - addAnAttachmentToMail() :: ends");
		return message;
	}

	/**
	 * Send mail to one recipient at once
	 * 
	 * @author Mohan
	 * @param email
	 *            the email name
	 * @param subject
	 *            the subject details
	 * @param content
	 *            the mail content details
	 * @return true or false
	 */
	public static boolean sendEmail(String email, String subject, String content) {
		LOGGER.info("INFO: Mail - sendEmail() :: starts");
		boolean sentMail = false;
		try {
			Session session = mailSession(); // get mail session config

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(AppUtil.getAppProperties().get(FROM_EMAIL_ADDRESS)));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(subject);

			byte[] bytes = content.getBytes();
			DataSource dataSourceHtml = new ByteArrayDataSource(bytes, CONTENT_TYPE);
			MimeBodyPart bodyPart = new MimeBodyPart();
			bodyPart.setDataHandler(new DataHandler(dataSourceHtml));
			MimeMultipart mimeMultipart = new MimeMultipart();
			mimeMultipart.addBodyPart(bodyPart);

			message.setContent(mimeMultipart, CONTENT_TYPE);
			Transport.send(message);
			sentMail = true;
			LOGGER.info("INFO: Mail - sendEmail() :: MAIL SENT TO ==> " + email);
		} catch (Exception e) {
			sentMail = false;
			LOGGER.error("ERROR: Mail - sendEmail()", e);
		}
		LOGGER.info("INFO: Mail - sendEmail() :: ends");
		return sentMail;
	}

	/**
	 * Send mail to one recipient at once with an attachment
	 * 
	 * @author Mohan
	 * @param email
	 *            the email name
	 * @param subject
	 *            the subject details
	 * @param content
	 *            the content of the mail
	 * @param file
	 *            the file path details
	 * @return true or false
	 */
	public static boolean sendEmailWithAnAttachment(String email, String subject, String content, String file) {
		LOGGER.info("INFO: Mail - sendEmailWithAnAttachment() :: starts");
		boolean sentMail = false;
		try {
			Session session = mailSession(); // get mail session config

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(AppUtil.getAppProperties().get(FROM_EMAIL_ADDRESS)));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(subject);

			addAnAttachmentToMail(message, content, file); // add attachment to mail

			Transport.send(message);
			sentMail = true;
			LOGGER.info("INFO: Mail - sendEmailWithAnAttachment() :: MAIL SENT TO ==> " + email);
		} catch (Exception e) {
			sentMail = false;
			LOGGER.error("ERROR: Mail - sendEmailWithAnAttachment()", e);
		}
		LOGGER.info("INFO: Mail - sendEmailWithAnAttachment() :: ends");
		return sentMail;
	}

	/**
	 * Send mail to many recipients at once
	 * 
	 * @author Mohan
	 * @param subject
	 *            the subject details
	 * @param content
	 *            the mail content
	 * @param toMailList
	 *            the to mail list
	 * @param ccMailList
	 *            the cc mail list
	 * @param bccMailList
	 *            the bcc mail list
	 * @return true or false
	 */
	public static boolean sendEmailToMany(String subject, String content, List<String> toMailList,
			List<String> ccMailList, List<String> bccMailList) {
		LOGGER.info("INFO: Mail - sendEmailToMany() :: starts");
		boolean sentMail = false;
		try {
			Session session = mailSession(); // get mail session config

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(AppUtil.getAppProperties().get(FROM_EMAIL_ADDRESS)));
			message.setSubject(subject);
			message.setContent(content, CONTENT_TYPE);

			addRecipintsToMail(message, toMailList, ccMailList, bccMailList); // add recipients to the mail

			Transport.send(message);
			sentMail = true;
		} catch (Exception e) {
			sentMail = false;
			LOGGER.error("ERROR: Mail - sendEmailToMany()", e);
		}
		LOGGER.info("INFO: Mail - sendEmailToMany() :: ends");
		return sentMail;
	}

	/**
	 * Send mail to many at once with an attachment
	 * 
	 * @author Mohan
	 * @param subject
	 *            the subject details
	 * @param content
	 *            the mail content
	 * @param toMailList
	 *            the to mail list
	 * @param ccMailList
	 *            the cc mail list
	 * @param bccMailList
	 *            the bcc mail list
	 * @param file
	 *            the file path name
	 * @return true or false
	 */
	public static boolean sendEmailToManyWithAnAttachment(String subject, String content, List<String> toMailList,
			List<String> ccMailList, List<String> bccMailList, String file) {
		LOGGER.info("INFO: Mail - sendEmailToManyWithAnAttachment() :: starts");
		boolean sentMail = false;
		try {
			Session session = mailSession(); // get mail session config

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(AppUtil.getAppProperties().get(FROM_EMAIL_ADDRESS)));
			message.setSubject(subject);
			message.setContent(content, CONTENT_TYPE);

			addRecipintsToMail(message, toMailList, ccMailList, bccMailList); // add recipients to the mail
			addAnAttachmentToMail(message, content, file); // add attachment to mail

			Transport.send(message);
			sentMail = true;
		} catch (Exception e) {
			sentMail = false;
			LOGGER.error("ERROR: Mail - sendEmailToManyWithAnAttachment()", e);
		}
		LOGGER.info("INFO: Mail - sendEmailToManyWithAnAttachment() :: ends");
		return sentMail;
	}
}