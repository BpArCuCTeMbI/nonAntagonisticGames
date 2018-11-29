import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.*;

public class GameModel {

    private XYChart chart;

    private double[][] winValue;

    private DoubleProperty incr;

    private OutcomesData outData;

    private double[] convexHullX;
    private double[] convexHullY;

    /**
     *
     * @param a point a of vector AB
     * @param b point b of vector AB
     * @param c point c of vector AC
     * @return 0 if point C is on line AB, >0 if C is on the right, <0 if C is on the left.
     */
    private double crossProduct(Point2D a, Point2D b, Point2D c){
        Point2D vec1 = new Point2D(b.getX() - a.getX(), b.getY() - a.getY());
        Point2D vec2 = new Point2D(c.getX() - a.getX(), c.getY() - a.getY());

        return vec1.getX() * vec2.getY() - vec1.getY() * vec2.getX();
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

    /**
     *
     * @param a point A
     * @param b point B
     * @param c point C
     * @return 0 if B and C are on the same distance from  A. < 0 if B is closer to A. >0 if C is closer to A.
     */
    private double compareDistanceToCurrentPoint(Point2D a, Point2D b, Point2D c){
        double distAB = Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2);
        double distAC = Math.pow(c.getX() - a.getX(), 2) + Math.pow(c.getY() - a.getY(), 2);
        return Double.compare(distAB, distAC);
    }

    public void findConvexHull(){
        ArrayList<Point2D> auxArr = new ArrayList<>();

        //unpack outcomes to ArrayListx
        for(int i = 0; i < outData.getOutcomeX().length; i++){
            auxArr.add(new Point2D(outData.getOutcomeX()[i], outData.getOutcomeY()[i]));
        }
        auxArr.trimToSize();


        Point2D start = new Point2D(auxArr.get(0).getX(), auxArr.get(0).getY());
        //find the leftmost start point
        for(int i = 1; i < auxArr.size(); i++){
            if(auxArr.get(i).getX() < start.getX()){
                start = new Point2D(auxArr.get(i).getX(), auxArr.get(i).getY());
            }
        }

        //create HashSet of points. Add point start to it, because it's always in the hull.
        Point2D cur = start;
        Set<Point2D> convHull= new LinkedHashSet<>();
        convHull.add(start);
        ArrayList<Point2D> collPoints = new ArrayList<>();

        while(true){
            Point2D nextTarget = auxArr.get(0);

            //iterate through all points in auxArr
            for(int i = 1; i < auxArr.size(); i++){
                if(auxArr.get(i).equals(cur)){
                    continue;
                }

                double val = crossProduct(cur, nextTarget, auxArr.get(i));

                //if i-th point is on the left
                if(val > 0){
                    nextTarget = auxArr.get(i);
                    collPoints = new ArrayList<>();
                }else if(val == 0){
                    if(compareDistanceToCurrentPoint(cur, nextTarget, auxArr.get(i)) < 0){
                        collPoints.add(nextTarget);
                        nextTarget = auxArr.get(i);
                    }else{
                        collPoints.add(auxArr.get(i));
                    }
                }
            }

            collPoints.trimToSize();
            for(Point2D pnt : collPoints){
                convHull.add(pnt);
            }

            if(nextTarget.equals(start)){
                break;
            }
            convHull.add(nextTarget);
            cur = nextTarget;
        }

        //TODO: write to arrays in GameModel
        convexHullX = new double[convHull.size() + 1];
        convexHullY = new double[convHull.size() + 1];
        Iterator<Point2D> it = convHull.iterator();
        for(int i = 0; i < convHull.size() && it.hasNext(); i++){
            Point2D pnt = it.next();
            convexHullX[i] = pnt.getX();
            convexHullY[i] = pnt.getY();
        }
        convexHullX[convexHullX.length - 1] = convexHullX[0];
        convexHullY[convexHullY.length - 1] = convexHullY[0];
    }

    public void plotConvex(){

        chart.addSeries("Convex Hull", convexHullX, convexHullY).setMarker(SeriesMarkers.DIAMOND).setMarkerColor(Color.RED).setLineStyle(SeriesLines.SOLID).setLineColor(Color.BLACK);

        debugDummy();
    }

}
