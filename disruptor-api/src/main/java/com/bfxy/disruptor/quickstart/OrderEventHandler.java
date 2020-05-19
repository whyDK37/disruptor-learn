package com.bfxy.disruptor.quickstart;

import com.lmax.disruptor.EventHandler;

public class OrderEventHandler implements EventHandler<OrderEvent> {

  public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
//        Thread.sleep(1000);
    System.err.println(String.format("thread:%s消费者: %s, sequence:%d, endOfBatch:%s",
        Thread.currentThread().getName(), event.getValue(), sequence, endOfBatch));
  }

}
