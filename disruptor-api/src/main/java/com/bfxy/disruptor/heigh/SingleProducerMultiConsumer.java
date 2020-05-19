package com.bfxy.disruptor.heigh;

import com.bfxy.disruptor.heigh.chain.Trade;
import com.bfxy.disruptor.heigh.chain.TradePushlisher;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleProducerMultiConsumer {

  public static void main(String[] args) throws InterruptedException {
    //构建一个线程池用于提交任务
    ExecutorService es1 = Executors.newFixedThreadPool(1);
    ExecutorService es2 = Executors.newFixedThreadPool(5);
    //1 构建Disruptor
    Disruptor<Trade> disruptor = new Disruptor<>(
        new EventFactory<Trade>() {
          public Trade newInstance() {
            return new Trade();
          }
        },
        1024 * 1024,
        es2,
        ProducerType.SINGLE,
        new BusySpinWaitStrategy());

    //2.4 六边形操作
    disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
          System.err.println("handler 1 : ");
          Thread.sleep(1000);
        },
        (event, sequence, endOfBatch) -> {
          System.err.println("handler 2 : ");
          Thread.sleep(1000);
        });

    //3 启动disruptor
    CountDownLatch latch = new CountDownLatch(1);

    long begin = System.currentTimeMillis();

    es1.submit(new TradePushlisher(latch, disruptor));

    latch.await();  //进行向下

    disruptor.shutdown();
    es1.shutdown();
    es2.shutdown();
    System.err.println("总耗时: " + (System.currentTimeMillis() - begin));

  }
}
