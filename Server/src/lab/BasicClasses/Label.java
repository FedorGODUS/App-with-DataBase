package lab.BasicClasses;

import java.io.Serializable;

public class Label implements Comparable<Label>, Serializable {

    private int sales; //Значение поля должно быть больше 0
    public Label( int sales){

        this.sales = sales;
    }
    Label(String name, String tracks, String length, String sales){
        this.sales = Integer.parseInt(sales);
    }

    public int getSales() {
        return sales;
    }

    @Override
    public String toString() {
        return "Продажи(" + sales + "$)";
    }


    @Override
    public int compareTo(Label otherLabel) {
        if(this.sales == otherLabel.sales){
            return 0;
        }
        else if(this.sales < otherLabel.sales){
            return -1;
        }
        else{
            return 1;
        }
    }
}