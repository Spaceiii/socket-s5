import java.io.*;

public class Position {

    private double x,y,z;

    public Position() {
        x = 0; y = 0; z = 0;
    }

    public Position(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }


    public String toString() {
        return x + "," + y + "," + z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Position clone() {
        return new Position(x,y,z);
    }

    public boolean equals(Position p, double eps) {
        return (Math.abs(p.x - x) <= eps) && (Math.abs(p.y - y) <= eps) && (Math.abs(p.z - z) <= eps);
    }

    public double distanceTo(Position p) {
        return Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2) + Math.pow((p.z - z), 2));
    }
}