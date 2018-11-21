import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.ArrayList;

public class GameModel {

    private XYChart chart;

    private double[][] winValue;

    private DoubleProperty incr;

    private OutcomesData outData;

    private ArrayList<Integer> convexHullIndexes;

    /**
     *
     * @param a is the index of the first point in the fSet
     * @param b is the index of the 2nd point in the fSet
     * @param c is the index of the 3rd point in the fSet, we are trying to understand
     *          if this point is to the left or to the right relative to the vector ab.
     * @return z > 0 => c to the right; z < 0 - c to the left, z = 0 - collinear
     */
    private double checkPointAngle(int a, int b, int c, double[][] fSet){
        double ret = (fSet[b][0] - fSet[a][0]) * (fSet[c][1] - fSet[b][1])
                - (fSet[b][1] - fSet[a][1]) * (fSet[c][0] - fSet[b][0]);
        return ret;
    }

    private double S1(double x, double y){

        return winValue[0][0] * x * y
                + winValue[1][0] * x * (1 - y)
                + winValue[2][0] * y * (1 - x)
                + winValue[3][0] * (1 - x) * (1 - y);
    }

    private double S2(double x, double y){
        return winValue[0][1] * x * y
                + winValue[1][1] * x * (1 - y)
                + winValue[2][1] * y * (1 - x)
                + winValue[3][1] * (1 - x) * (1 - y);
    }

    private double probOfFirstStratFirst;
    private double probOfFirstStratSecond;

    public void debugDummy(){

    }
    public void setProbOfFirstStratFirst(double probOfFirstStratFirst) {
        this.probOfFirstStratFirst = probOfFirstStratFirst;
    }

    public void setProbOfFirstStratSecond(double probOfFirstStratSecond) {
        this.probOfFirstStratSecond = probOfFirstStratSecond;
    }

    public double getProbOfFirstStratFirst() {
        return probOfFirstStratFirst;
    }

    public double getProbOfFirstStratSecond() {
        return probOfFirstStratSecond;
    }

    public XYChart getChart() {
        return chart;
    }

    public void setChart(XYChart chart) {
        this.chart = chart;
    }

    public OutcomesData getOutData() {
        return outData;
    }

    public void setOutData(OutcomesData outData) {
        this.outData = outData;
    }

    public DoubleProperty getIncr() {
        return incr;
    }

    public double[][] getWinValue() {
        return winValue;
    }

    GameModel(){
        incr = new SimpleDoubleProperty();
        winValue = new double[4][2];
        probOfFirstStratFirst = 0;
        probOfFirstStratSecond = 0;

    }

    public OutcomesData calcWinning() {

        double inc = incr.getValue();
        int size = (int) ((1 / inc) + 1);
        OutcomesData data = new OutcomesData();

        double[] outX = new double[size * size];
        double[] outY = new double[size * size];

        int k = 0;

        for (double i = 0; i <= 1; i+= inc)
        {
            for (double j = 0; j <= 1; j+= inc)
            {
                outX[k] = S1(i, j);
                outY[k] = S2(i, j);
                k++;
            }
        }

        data.setOutcomeX(outX);
        data.setOutcomeY(outY);

        return data;
    }

    public void calcConvexSet(){
        //TODO: delete equal points!!!
        //graham scan

        double[][] fSet = new double[outData.getOutcomeX().length][2];
        int size = fSet.length;

        for(int i = 0; i < size; i++){
            fSet[i][0] = outData.getOutcomeX()[i];
            fSet[i][1] = outData.getOutcomeY()[i];
        }
        int[] auxArr = new int[size];

        for(int i = 0; i < size; i++){
            auxArr[i] = i;
        }

        //find the leftmost point
        for(int i = 1; i < size; i++){
            // firstly in if- <; then changed to <=
            if(fSet[auxArr[i]][0] <= fSet[auxArr[0]][0]){
                int tmp = auxArr[i];
                auxArr[i] = auxArr[0];
                auxArr[0] = tmp;
            }
        }

        //sort all points except 0 by polar angle
        for(int i = 2; i < size; i++){
            int pos = i;
            while(pos > 1 && checkPointAngle(auxArr[0], auxArr[pos - 1], auxArr[pos], fSet) < 0){
                int tmp = auxArr[pos];
                auxArr[pos] = auxArr[pos - 1];
                auxArr[pos - 1] = tmp;
                pos -= 1;
            }
        }
        //last step - remove redundant points using stack
        ArrayList<Integer> auxStackList = new ArrayList<>();
        auxStackList.add(auxArr[0]);
        auxStackList.add(auxArr[1]);

        for(int i = 1; i < size; i++){
            while (checkPointAngle(auxStackList.size() - 2, auxStackList.size() - 1, auxArr[i], fSet) < 0){
                auxStackList.remove(auxStackList.size() - 1);
            }
            auxStackList.add(auxArr[i]);
        }
        convexHullIndexes = auxStackList;

    }

    public void plotConvex(){
        double[] x = new double[convexHullIndexes.size()];
        double[] y = new double[convexHullIndexes.size()];

        for(int i = 0; i < convexHullIndexes.size(); i++){
            x[i] = outData.getOutcomeX()[convexHullIndexes.get(i)];
            y[i] = outData.getOutcomeY()[convexHullIndexes.get(i)];
        }
        chart.addSeries("Convex Hull", x, y).setMarker(SeriesMarkers.DIAMOND).setMarkerColor(Color.RED).setLineStyle(SeriesLines.SOLID).setLineColor(Color.BLACK);
    }

}
