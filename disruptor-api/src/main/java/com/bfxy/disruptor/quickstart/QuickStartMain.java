package com.bfxy.disruptor.quickstart;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QuickStartMain {

  public static void main(String[] args) {

    // 参数准备工作
    OrderEventFactory orderEventFactory = new OrderEventFactory();
    int ringBufferSize = 4;
    ExecutorService executor = Executors.newFixedThreadPool(100);

    /*
     * 1 eventFactory: 消息(event)工厂对象
     * 2 ringBufferSize: 容器的长度
     * 3 executor: 线程池(建议使用自定义线程池) RejectedExecutionHandler
     * 4 ProducerType: 单生产者 还是 多生产者
     * 5 waitStrategy: 等待策略
     */
    //1. 实例化disruptor对象
    Disruptor<OrderEvent> disruptor = new Disruptor<>(orderEventFactory,
        ringBufferSize,
        executor,
        ProducerType.SINGLE,
        new BlockingWaitStrategy());

    //2. 添加消费者的监听 (构建disruptor 与 消费者的一个关联关系)
    disruptor.handleEventsWith(new OrderEventHandler(),
        (event, sequence, endOfBatch) -> {
          TimeUnit.MILLISECONDS.sleep(100L);
          System.out.println(String.format("thread:%s消费者: %s, sequence:%d, endOfBatch:%s",
              Thread.currentThread().getName(), event.getValue(), sequence, endOfBatch));
        });

    //3. 启动disruptor
    RingBuffer<OrderEvent> ringBuffer = disruptor.start();

    //4. 获取实际存储数据的容器: RingBuffer
    System.out.println(
        "disruptor.getRingBuffer() == ringBuffer = " + (disruptor.getRingBuffer() == ringBuffer));

    ByteBuffer buffer = ByteBuffer.allocate(8);
    OrderEventProducer producer = new OrderEventProducer(ringBuffer);
    for (long i = 0; i < 50; i++) {
      buffer.putLong(0, i);
      producer.sendData(buffer);
    }

    disruptor.shutdown();
    executor.shutdown();

  }
}
