import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GameModel {

    private XYChart chart;

    private double[][] winValue;

    private DoubleProperty incr;

    private OutcomesData outData;

    private double[] convexHullX;
    private double[] convexHullY;

    /**
     *
     * @param a is the base point
     * @param b is the point which is
     * @param c is the index of the 3rd point in the fSet, we are trying to understand
     *          if this point is to the left or to the right relative to the vector ab.
     * @return z > 0 => c to the right; z < 0 - c to the left, z = 0 - collinear
     */

    private double crossProduct(Point2D a, Point2D b, Point2D c){
        Point2D vec1 = new Point2D(b.getX() - a.getX(), b.getY() - a.getY());
        Point2D vec2 = new Point2D(c.getX() - a.getX(), c.getY() - a.getY());

        double ret = vec1.getX() * vec2.getY() - vec1.getY() * vec2.getX();
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

    public void debugDummy(){

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

    public double[] getConvexHullX() {
        return convexHullX;
    }

    public double[] getConvexHullY() {
        return convexHullY;
    }

    GameModel(){
        incr = new SimpleDoubleProperty();
        winValue = new double[4][2];

    }

    public void calcWinning() {

        double inc = incr.getValue();

        ArrayList<Point2D> supportArray = new ArrayList<>();

        for (double i = 0; i <= 1; i+= inc)
        {
            for (double j = 0; j <= 1; j+= inc)
            {
                supportArray.add(new Point2D(S1(i, j), S2(i, j)));
            }
        }

        //remove duplicate points
        supportArray.trimToSize();
        for(int i = 0; i < supportArray.size(); i++){
            for(int j = i + 1; j < supportArray.size(); j++){
                if(supportArray.get(i).getX() == supportArray.get(j).getX() && supportArray.get(i).getY() == supportArray.get(j).getY()){
                    supportArray.remove(j);
                }
            }
        }


        double[] outX = new double[supportArray.size()];
        double[] outY = new double[supportArray.size()];

        int i = 0;
        for(Point2D pnt : supportArray){
            outX[i] = pnt.getX();
            outY[i] = pnt.getY();
            i++;
        }

        OutcomesData data = new OutcomesData();

        data.setOutcomeX(outX);
        data.setOutcomeY(outY);

        outData = data;
        outData = data;
    }

    private double isCloserToCurrentPoint(Point2D a, Point2D b, Point2D c){
        double distAB = Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
        double distAC = Math.sqrt(Math.pow(c.getX() - a.getX(), 2) + Math.pow(c.getY() - a.getY(), 2));
        return Double.compare(distAB, distAC);
    }

    public void calcConvexHull(){

        //TODO: rewrite, recheck and refactor calcConvexHull()
        //unpack outcomes to tmpArr

        ArrayList<Point2D> tmpArr = new ArrayList<>();
        for(int i = 0; i < outData.getOutcomeX().length; i++){
            tmpArr.add(new Point2D(outData.getOutcomeX()[i], outData.getOutcomeY()[i]));
        }

        //find leftmost point
        Point2D start = tmpArr.get(0);
        for(int i = 1; i < tmpArr.size(); i++){
            if(tmpArr.get(i).getX() < start.getX()){
                start = tmpArr.get(i);
            }
        }

        Point2D cur = start;
        Set<Point2D> res = new HashSet<>();
        res.add(start);

        ArrayList<Point2D> collinearPoints = new ArrayList<>();
        while(true){
            Point2D nextTarget = tmpArr.get(0);
            for(int i = 0; i < tmpArr.size(); i++){
                if(tmpArr.get(i) == cur){
                    continue;
                }

                double crossProductValue = crossProduct(cur, nextTarget, tmpArr.get(i));
                //if value > 0 => Point (tmpArr.get(i)) is on the left from vector (cur; nextTarget).
                //make it nextTarget
                if(crossProductValue > 0){
                    nextTarget = tmpArr.get(i);
                    //reset coll points
                    collinearPoints = new ArrayList<>();
                }
                else if(crossProductValue == 0){
                    if(isCloserToCurrentPoint(cur, nextTarget, tmpArr.get(i)) < 0){
                        //if point nextTarget is closer to cur than tmpArr.get(i)
                        collinearPoints.add(nextTarget);
                        nextTarget = tmpArr.get(i);
                    }
                    else{
                        collinearPoints.add(tmpArr.get(i));
                    }
                }
            }

            for(Point2D pnt : collinearPoints){
                res.add(pnt);
            }

            if(nextTarget == start){
                break;
            }

            res.add(nextTarget);
            cur = nextTarget;
        }
        convexHullX = new double[res.size()];
        convexHullY = new double[res.size()];
        Iterator it = res.iterator();
        int i = 0;

        for(Point2D pnt : res){
            convexHullX[i] = pnt.getX();
            convexHullY[i] = pnt.getY();
            i++;
        }
    }

    public void plotConvex(){

        chart.addSeries("Convex Hull", convexHullX, convexHullY).setMarker(SeriesMarkers.DIAMOND).setMarkerColor(Color.RED).setLineStyle(SeriesLines.SOLID).setLineColor(Color.BLACK);

        debugDummy();
    }

}
