import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisPubSub
{
    private static final String channel_name= "chat";

    public static void main(String[] args) throws Exception
    {
        JedisPool jedispool = new JedisPool("localhost");
        final Jedis subscriberJedis = jedispool.getResource();

        final Subscriber subscriber = new Subscriber();
        new Thread(new Runnable(){
            public void run()
            {
                try
                {
                    System.out.println("Subscribing to " +channel_name);
                    subscriberJedis.subscribe(subscriber,channel_name);
                    System.out.println("Subscription ended.");
                }
                catch (Exception e)
                {
                    System.out.println("Subscribing failed."+e);
                }
            }
        }).start();

        Jedis publisherJedis = jedispool.getResource();
        new Publisher(publisherJedis,channel_name).start();
        subscriber.unsubscribe();
        jedispool.returnResource(subscriberJedis);
        jedispool.returnResource(publisherJedis);
    }
}
