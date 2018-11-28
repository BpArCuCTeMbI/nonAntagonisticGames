public class Point2D {
    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    Point2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }else if(obj == null || getClass() != obj.getClass()){
            return  false;
        }

        Point2D pnt = (Point2D) obj;
        return x == pnt.getX() && y == pnt.getY();
    }
}
