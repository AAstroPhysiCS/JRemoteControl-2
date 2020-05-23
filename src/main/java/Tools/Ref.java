package Tools;

//this class tries to simulate the known "ref" keyword in C#.
//Java is always pass by value.
public class Ref<T> {

    public T obj;

    public Ref(T obj){
        this.obj = obj;
    }

    public Ref(){
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
