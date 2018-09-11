package com.sc.threadpool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadPoolMain {

    public static void main(String[] args) {

        ThreadPool tp = new DefaultThreadPool<Task>();
        int i = 0;
        while (i++ < 7) {
            tp.execute(new Task());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String command = "";
        while (true) {
            try {
                System.out.println("请输入指令：");
                command = br.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (command.equals("quit")) {
                break;
            } else if (command.equals("add")) {
                i = 0;
                while (i++ < 7) {
                    tp.execute(new Task());
                }
            } else if (command.equals("minus")) {
            } else if (command.equals("shutdown")) {
                tp.shutdown();
            }
        }
    }
}

class Task implements Runnable {
    @Override
    public void run() {
        System.out.println("Task 处理完毕！");
    }
}

