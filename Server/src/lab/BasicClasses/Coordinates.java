package lab.BasicClasses;

import java.io.Serializable;

public class Coordinates implements Comparable<Coordinates>, Serializable {
    private long x; //Значение поля должно быть больше -687
    private long y; //Поле не может быть null
    public Coordinates(long x, long y){
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    @Override
    public String toString() {
        return "x: (" + x + "), y: (" + y + ")";
    }

    @Override
    public int compareTo(Coordinates otherCoordinates) {
        if(this.x == otherCoordinates.x && this.y == otherCoordinates.x){
            return 0;
        }
        else if(this.x+this.y < otherCoordinates.x+otherCoordinates.y){
            return -1;
        }
        else{
            return 1;
        }
    }
}
