import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.util.ArrayList;

public class OutcomesData {
    private double[] outcomeX;

    private double[] outcomeY;

    OutcomesData(){

    }

    OutcomesData(ArrayList<Double> x, ArrayList<Double> y){
        outcomeX = new double[x.size()];
        outcomeY = new double[y.size()];

        for(int i = 0; i < x.size(); i++){
            outcomeX[i] = x.get(i);
        }
        for(int i = 0; i < y.size(); i++){
            outcomeY[i] = y.get(i);
        }
    }

    OutcomesData(int size){
        outcomeX = new double[size];
        outcomeY = new double[size];
    }

    public double[] getOutcomeX() {
        return outcomeX;
    }

    public void setOutcomeX(double[] outcomeX) {
        this.outcomeX = outcomeX;
    }

    public double[] getOutcomeY() {
        return outcomeY;
    }

    public void setOutcomeY(double[] outcomeY) {
        this.outcomeY = outcomeY;
    }


}
