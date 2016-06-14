package test.test_module;


public interface KafkaProperties
{
    final static String zkConnect = "10.10.239.172:2181";
    final static String groupId = "GroupTest";
    final static String topic = "test";
    final static String kafkaServerURL = "10.10.239.172";
    final static int kafkaServerPort = 9092;
    final static int kafkaProducerBufferSize = 64 * 1024;
    final static int connectionTimeOut = 20000;
    final static int reconnectInterval = 10000;
    final static String topic2 = "topic2";
    final static String topic3 = "topic3";
    final static String clientId = "SimpleConsumerDemoClient";
}