package socialnetwork.model;

import java.util.Objects;

public class Tuple<E extends Comparable<E>> {
    private E e1;
    private E e2;

    public Tuple(E e1, E e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public void orderTuple(){
        E aux;
        if(this.e1.compareTo(this.e2) > 0){
            aux = this.e1;
            this.e1 = e2;
            this.e2 = aux;
        }

    }

    public E getFirst() {
        return e1;
    }

    public void setFirst(E e1) {
        this.e1 = e1;
    }

    public E getSecond() {
        return e2;
    }

    public void setSecond(E e2) {
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "" + e1 + "," + e2;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Tuple that))
            return false;
        return (getFirst().equals(that.getFirst()) && getSecond().equals(that.getSecond())) ||
                (getFirst().equals(that.getSecond()) && getSecond().equals(that.getFirst()));
    }

    @Override
    public int hashCode() {
        if(e1.compareTo(e2) < 0)
            return Objects.hash(e1, e2);
        return Objects.hash(e2, e1);
    }
}