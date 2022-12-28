package commons;

import entities.Worker;

public class Session {
    private Worker worker;
    private static Session instance;

    private Session(){}

    public static Session getInstance() {
        if (instance == null){
            instance = new Session();
        }
        return instance;
    }
    public static void invalidate(){
        instance = null;
    }
    public void update(Worker worker){
        this.worker = worker;
    }
    public Worker getWorker(){
        return worker;
    }
}
