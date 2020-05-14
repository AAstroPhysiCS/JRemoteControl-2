package Features.Sender;

import Features.Feature;

import java.util.function.Supplier;

public class FeatureDataSender<T extends Feature> {

    private T obj;

    public FeatureDataSender(Supplier<T> supplier){
        this.obj = supplier.get();
    }

    public void send(){

    }
}
