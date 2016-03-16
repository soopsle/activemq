/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsReceiver {
	private ConnectionFactory connectionFactory = null;
	private Connection connection = null;
	private Session session = null;
	private MessageConsumer consumer = null;
	private Destination destination = null;

	public void init() {
		connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616"); // ActiveMQ默认使用的TCP连接端口是61616
		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			/**
			 * 第一种方式：Queue
			 */
			// destination = session.createQueue("xkey");
			// consumer = session.createConsumer(destination);
			/**
			 * 第二种方式：Topic
			 */
			Topic topic = session.createTopic("xkey.Topic");
			consumer = session.createConsumer(topic);
			/**
        * 
        */
			while (true) {
				// 设置接收者接收消息的时间，为了便于测试，这里谁定为500s
				TextMessage message = (TextMessage) consumer.receive(500000);
				if (null != message) {
					System.out.println("Receiver " + message.getText());
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JmsReceiver jms = new JmsReceiver();
		jms.init();
	}
}