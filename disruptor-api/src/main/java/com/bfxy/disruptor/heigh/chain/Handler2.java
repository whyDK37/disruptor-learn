package com.bfxy.disruptor.heigh.chain;

import com.lmax.disruptor.EventHandler;
import java.util.UUID;

public class Handler2 implements EventHandler<Trade> {

  public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
    System.err.println(Thread.currentThread().getName() + ": handler 2 : SET ID");
    Thread.sleep(2000);
    event.setId(UUID.randomUUID().toString());
  }

}
