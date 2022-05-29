package ru.sps38.accountmanager.utils;

import java.util.LinkedList;
import java.util.List;

public class BlockingQueue {

    List<String> requests = new LinkedList<>();

    public synchronized String get(){
        while (requests.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        String task = requests.get(0);
        requests.remove(task);
        return task;
    }

    public synchronized void put(String request){
        requests.add(request);
        notify();
    }
}
