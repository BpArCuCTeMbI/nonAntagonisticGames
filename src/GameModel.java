import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
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

    public void calcConvexHullJarvis(){
        //TODO: getting wrong results in the end because of repeating points (-1;-1), (-0.6; -0.4),etc

        double[][] fSet = new double[outData.getOutcomeX().length][2];
        for(int i = 0; i < fSet.length; i++){
            fSet[i][0] = outData.getOutcomeX()[i];
            fSet[i][1] = outData.getOutcomeY()[i];
        }

        //======================redundant code for tests
        ArrayList<Point2D> tmpArr = new ArrayList<>();
        for(int i = 0; i < fSet.length; i++){
            tmpArr.add(new Point2D(fSet[i][0], fSet[i][1]));
        }

        for(int i = 0; i < tmpArr.size(); i++){
            for(int j = i + 1; j < tmpArr.size(); j++){
                if(tmpArr.get(i).getX() == tmpArr.get(j).getX() && tmpArr.get(i).getY() == tmpArr.get(j).getY()){
                    tmpArr.remove(j);
                }
            }
        }

        //=========================
        //================

        fSet = new double[tmpArr.size()][2];
        for(int i = 0; i < tmpArr.size(); i++){
            fSet[i][0] = tmpArr.get(i).getX();
            fSet[i][1] = tmpArr.get(i).getY();
        }

        //========
        double[] tmpX = new double[tmpArr.size()];
        double[] tmpY = new double[tmpArr.size()];
        for(int i = 0; i < tmpArr.size(); i++){
            tmpX[i] = tmpArr.get(i).getX();
            tmpY[i] = tmpArr.get(i).getY();
        }
        /*
        OutcomesData tmpData = new OutcomesData();
        tmpData.setOutcomeX(tmpX);
        tmpData.setOutcomeY(tmpY);
        outData = tmpData;

        XYChart chart = new XYChartBuilder().width(400).height(400).xAxisTitle("Winnings of 1st player").yAxisTitle("Winnings of 2nd player").title("Set of Winnings").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.addSeries("Set of Wins", outData.getOutcomeX(), outData.getOutcomeY()).setMarker(SeriesMarkers.CIRCLE).setLineStyle(SeriesLines.NONE);
        new SwingWrapper(chart).displayChart().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        */
        //=====
        //====================
        ArrayList<Integer> auxArr = new ArrayList<>();

        for(int i = 0; i < fSet.length; i++){
            auxArr.add(i);
        }
        //set the leftmost point as first in the fSet
        for(int i = 0; i < fSet.length; i++){
            if(fSet[auxArr.get(i)][0] <= fSet[auxArr.get(0)][0]){
                int tmp = auxArr.get(i);
                auxArr.set(i, auxArr.get(0));
                auxArr.set(0, tmp);
            }
        }

        ArrayList<Integer> retHullIndexes = new ArrayList<>();
        //leftmost point is always in the hull set
        retHullIndexes.add(auxArr.get(0));
        auxArr.remove(0);
        auxArr.add(retHullIndexes.get(0));

        while (true){
            int right = 0;
            for(int i = 0; i < auxArr.size(); i++){
                if(checkPointAngle(retHullIndexes.get(retHullIndexes.size() - 1), auxArr.get(right), auxArr.get(i), fSet) < 0){
                    right = i;
                }
            }
            if (auxArr.get(right) == retHullIndexes.get(0)){
                break;
            }
            else{
                retHullIndexes.add(auxArr.get(right));
                auxArr.remove(right);
            }
            //System.out.println("x: " + fSet[(retHullIndexes.size() - 1)][0] + "; y: " + fSet[(retHullIndexes.size() - 1)][1]);
        }
        convexHullIndexes = retHullIndexes;

    }

    public void plotConvex(){
        double[] x = new double[convexHullIndexes.size()];
        double[] y = new double[convexHullIndexes.size()];

        for(int i = 0; i < convexHullIndexes.size(); i++){
            x[i] = outData.getOutcomeX()[convexHullIndexes.get(i)];
            y[i] = outData.getOutcomeY()[convexHullIndexes.get(i)];
        }
        chart.addSeries("Convex Hull", x, y).setMarker(SeriesMarkers.DIAMOND).setMarkerColor(Color.RED).setLineStyle(SeriesLines.SOLID).setLineColor(Color.BLACK);
        debugDummy();
    }

}
