package com.sc.mail;

public class SendEmail {
	public static void main(String[] args) {

		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("senssic@163.com");
		mailInfo.setPassword("qiyu0126");
		mailInfo.setFromAddress("senssic@163.com");
		mailInfo.setToAddress("463349267@qq.com");
		mailInfo.setSubject("物流预警");
		mailInfo.setContent("亲,这些都是超时的信息,请你查收");
		mailInfo.setFileName("预警清单.wps");
		mailInfo.setByt(new String("aaff").getBytes());// 发送附件

		ComplexMailSender sms = new ComplexMailSender();
		sms.sendAttachmentMail(mailInfo);// 发送文体格式
	}
}
