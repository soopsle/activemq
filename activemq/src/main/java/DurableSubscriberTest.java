import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class DurableSubscriberTest {
	// 发送次数
	public static final int SEND_NUM = 5;
	// tcp 地址
	public static final String BROKER_URL = "tcp://localhost:61616";
	// 目标，在ActiveMQ管理员控制台创建 http://localhost:8161/admin/queues.jsp
	public static final String DESTINATION = "hoo.mq.topic";

	private static TopicConnection connection = null;
	private static TopicSession session = null;

	public static void main(String[] args) throws Exception {
		// 创建链接工厂
		TopicConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
		// 通过工厂创建一个连接
		connection = factory.createTopicConnection();
		// 创建持久订阅的时候,必须要设置client,否则会报错:
		// javax.jms.JMSException: You cannot create a durable subscriber
		// without specifying a unique clientID on a Connection

		// 如果clientID重复(已经存在相同id的活动连接),会报错
		// javax.jms.InvalidClientIDException: Broker: localhost - Client: 1
		// already connected from tcp://127.0.0.1:2758
		connection.setClientID("1");
		// 启动连接
		connection.start();
		 // 创建一个session会话
		 session = connection.createTopicSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);
		// 创建一个消息队列
		Topic topic = session.createTopic(DESTINATION);
		TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		// 在同一个连接的ClientID下,持久订阅者的名称必须唯一
		// javax.jms.JMSException: Durable consumer is in use for client: 1 and
		// subscriptionName: 11

		// TopicSubscriber subscriber = session.createSubscriber(topic);
		TopicSubscriber subscriber = session.createDurableSubscriber(topic, "11");

		subscriber.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message msg) {
				try {
					TextMessage textMsg = (TextMessage) msg;
					System.out.println("DurableSubscriber get:" + textMsg.getText());
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});

		connection.start();// 一定要start
	}
}
