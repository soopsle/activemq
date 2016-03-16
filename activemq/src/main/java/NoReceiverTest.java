import javax.jms.DeliveryMode;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class NoReceiverTest {
	// 发送次数
	public static final int SEND_NUM = 5;
	// tcp 地址
	public static final String BROKER_URL = "tcp://localhost:61616";
	// 目标，在ActiveMQ管理员控制台创建 http://localhost:8161/admin/queues.jsp
	public static final String DESTINATION = "hoo.mq.topic";

	private static Topic topic = null;
	private static TopicConnection connection = null;
	private static TopicSession session = null;

	public static void run() throws Exception {

		try {
			// 创建链接工厂
			TopicConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
			// 通过工厂创建一个连接
			connection = factory.createTopicConnection();
			// 启动连接
			connection.start();
			 // 创建一个session会话
			 session = connection.createTopicSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);
			 // 创建一个消息队列
			 topic = session.createTopic(DESTINATION);
			// // 创建消息发送者
			// TopicPublisher publisher = session.createPublisher(topic);
			// // 设置持久化模式
			// publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			sentPersistent();
			sentNonPersistent();
			// sendMessage(session, publisher);
			// 提交会话
			// session.commit();

		} catch (Exception e) {
			throw e;
		} finally {
			// 关闭释放资源
			if (session != null) {
				session.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}
	public static void main(String[] args) {
		try {
			run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sentPersistent() throws Exception {
		TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

		TopicPublisher publihser = session.createPublisher(topic);

		publihser.setDeliveryMode(DeliveryMode.PERSISTENT);

		for (int i = 0; i < 3; i++) {
			String text = "I am persistent message.order=" + i;

			TextMessage message = session.createTextMessage(text);

			message.setJMSPriority(i);

			publihser.publish(message);
		}

	}

	public static void sentNonPersistent() throws Exception {
		TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

		TopicPublisher publihser = session.createPublisher(topic);

		publihser.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		for (int i = 0; i < 3; i++) {
			String text = "non-persistent message.id=" + i;

			TextMessage message = session.createTextMessage(text);

			publihser.publish(message);
		}

	}

}
