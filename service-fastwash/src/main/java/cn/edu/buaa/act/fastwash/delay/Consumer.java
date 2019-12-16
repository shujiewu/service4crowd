package cn.edu.buaa.act.fastwash.delay;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.DelayQueue;

public class Consumer implements Runnable {
    // 延时队列 ,消费者从其中获取消息进行消费  
    private DelayQueue<Message> queue;

    public DelayQueue<Message> getQueue() {
        return queue;
    }

    @Autowired
    private DelayService delayService;

    public Consumer(DelayQueue<Message> queue) {
        this.queue = queue;        //new的时候注入需要的bean
        this.delayService = ApplicationContextProvider.getBean(DelayService.class);
    }
    @Override  
    public void run() {  
        while (true) {  
            try {  
                Message take = queue.take();
                delayService.findRecentTask(take);
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}  