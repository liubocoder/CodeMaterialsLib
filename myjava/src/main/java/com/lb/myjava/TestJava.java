package com.lb.myjava;

public class TestJava {
    public static void main(String[] args) {

        int a = 255;
        byte b = (byte) a;
        int c = b & 0xFF;

        final byte[] lock = new byte[0];

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("aaaa");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock) {
                    lock.notifyAll();
                }
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                System.out.println("aaa-aa");
            }
        }).start();

        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("bbbb");



    }
}

