package eu.mihosoft.visoline;

public class Path_int {

    private final int isoValue;

    private final java.util.List<java.util.List<Vector2d>> contours;
    private java.util.List<Vector2d> vertices;

    private final BSpline optimizer = new BSpline();

    public Path_int(int isoValue) {
        contours = new java.util.ArrayList<>();
        vertices = new java.util.ArrayList<>();
        contours.add(vertices);
        this.isoValue = isoValue;
    }

    public int getNumberOfContours() {
        return contours.size();
    }

    public java.util.List<Vector2d> getContour(int i) {
        return contours.get(i);
    }

    public void moveTo(double x, double y) {
        moveTo(new Vector2d(x, y));
    }

    public void moveTo(Vector2d p) {
        breakContour();
        vertices.add(p);

    }

    public void lineTo(Vector2d p) {
        vertices.add(p);
    }

    public void lineTo(double x, double y) {
        lineTo(new Vector2d(x, y));
    }



    public void breakContour() {
        if (!vertices.isEmpty()) {
            vertices = new java.util.ArrayList<>();
            contours.add(vertices);
        }
    }

        public int getIsoValue() {
        return isoValue;
    }

    public boolean isEmpty() {
        return contours.isEmpty() || contours.stream().allMatch(verts -> verts.isEmpty());
    }
}
