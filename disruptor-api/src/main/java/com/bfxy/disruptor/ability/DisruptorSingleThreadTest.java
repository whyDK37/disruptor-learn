package com.bfxy.disruptor.ability;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.concurrent.Executors;

public class DisruptorSingleThreadTest {

  public static void main(String[] args) {
    int ringBufferSize = 65536;
    final Disruptor<Data> disruptor = new Disruptor<>(
        Data::new,
        ringBufferSize,
        Executors.newSingleThreadExecutor(),
        ProducerType.SINGLE,
        //new BlockingWaitStrategy()
        new YieldingWaitStrategy()
    );

    EventHandler<Data> consumer = new EventHandler<Data>() {
      private long startTime = System.currentTimeMillis();
      private int i;

      public void onEvent(Data data, long seq, boolean bool)
          throws Exception {
        i++;
        if (i == Constants.EVENT_NUM_FM) {
          long endTime = System.currentTimeMillis();
          System.out.println("Disruptor costTime = " + (endTime - startTime) + "ms");
        }
      }
    };
    //消费数据
    disruptor.handleEventsWith(consumer, (event, sequence, endOfBatch) -> {
      System.out.println(event.getId());
    });
    RingBuffer<Data> start = disruptor.start();
    new Thread(() -> {
      RingBuffer<Data> ringBuffer = disruptor.getRingBuffer();
      for (long i = 0; i < Constants.EVENT_NUM_FM; i++) {
        long seq = ringBuffer.next();
        Data data = ringBuffer.get(seq);
        data.setId(i);
        data.setName("c" + i);
        ringBuffer.publish(seq);
      }
    }).start();
  }

}