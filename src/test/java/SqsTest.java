import com.amazonaws.services.sqs.AmazonSQS;
import com.github.elasticmq.sqs.AmazonSqsFactory;
import com.github.elasticmq.sqs.SqsConfig;
import com.github.elasticmq.sqs.SqsQueue;
import com.github.elasticmq.sqs.SqsQueueConfig;
import com.github.elasticmq.sqs.queue.ConsumedElement;
import org.junit.Before;
import org.junit.Test;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 2:46 pm
 */
public class SqsTest {

    private static final String MQ_URL = "http://localhost:9324";
    private static final String ACCESS_KEY = "ak";
    private static final String SECRET_KEY = "sk";
    private AmazonSQS client;
    private SqsConfig sqsConfig;

    @Before
    public void init() {
        sqsConfig = new SqsConfig(MQ_URL, ACCESS_KEY, SECRET_KEY);
        client = AmazonSqsFactory.getSqs(sqsConfig);
    }

    @Test
    public void testSQS() {
        SqsQueueConfig queueConfig = new SqsQueueConfig("myQueue", true);
        SqsQueue<String> queue = new SqsQueue<String>(client, queueConfig, String.class);
        boolean enqueue = queue.enqueue("this is my element");
        System.out.println("enqueue: " + enqueue);
        String dequeue = queue.dequeue();
        System.out.println("dequeue: " + dequeue);
    }

    @Test
    public void testChangeMessageVisibility() throws InterruptedException {
        SqsQueueConfig queueConfig = new SqsQueueConfig("myQueue", true);
        SqsQueue<String> queue = new SqsQueue<String>(client, queueConfig, String.class);
        queue.enqueue("this is my element");
        ConsumedElement<String> message = queue.peek();
        System.out.println("get message is: " + message.getElement());
        System.out.println("will change message visibility to 5 seconds");
        message.suspend(5000);
        for (;;) {
            ConsumedElement<String> peek = queue.peek();
            if (peek != null) {
                peek.ack();
                System.out.println("delete message: " + peek.getElement());
                return;
            }
            System.out.println("will wait 1 seconds");
            Thread.sleep(1000);
        }
    }

    @Test
    public void testReleaseMessage() throws InterruptedException {
        SqsQueueConfig queueConfig = new SqsQueueConfig("myQueue", true);
        SqsQueue<String> queue = new SqsQueue<String>(client, queueConfig, String.class);
        queue.enqueue("this is my element");
        ConsumedElement<String> message = queue.peek();
        System.out.println("get message is: " + message.getElement());
        System.out.println("will change message visibility to 5 seconds");
        message.suspend(5000);
        for (;;) {
            ConsumedElement<String> peek = queue.peek();
            if (peek != null) {
                // delete message
                peek.ack();
                System.out.println("delete message: " + peek.getElement());
                return;
            }
            System.out.println("will wait 1 seconds");
            Thread.sleep(1000);
            System.out.println("release message let it visible immediately!");
            message.release();
        }
    }
}
