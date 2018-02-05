package eu.mihosoft.visoline;

public class MarchingSquares_byte {

    private final Data_byte forceField;
    private final Vector2d[] vertices;
    boolean[] marked;
    private final int width;
    private final int height;
    private final boolean boundryLow;

    private static MarchingSquares_double dInst;
    private static MarchingSquares_float fInst;
    private static MarchingSquares_int iInst;
    private static MarchingSquares_byte bInst;

    public MarchingSquares_byte(Data_byte forceField) {
        this(forceField, true);
    }

    public MarchingSquares_byte(Data_byte forceField, boolean boundryLow) {

        this.forceField = forceField;
        this.boundryLow = boundryLow;

        height = forceField.getHeight() * 2;
        width = forceField.getWidth() * 2;
        vertices = new Vector2d[width * height];
        marked = new boolean[vertices.length];
    }

    public Path_byte computePaths(byte isoVal) {
        Path_byte result = new Path_byte(isoVal);
        int[] segments = new int[vertices.length];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = -1;
        }

        int w = forceField.getWidth() - 1;
        int h = forceField.getHeight() - 1;


        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final byte val0 = forceField.get(x, y);
                final byte val1 = forceField.get(x + 1, y);
                final byte val2 = forceField.get(x + 1, y + 1);
                final byte val3 = forceField.get(x, y + 1);


                int caseIndex = 0;
                if (val0 < isoVal) {
                    caseIndex |= 1;
                }
                if (val1 < isoVal) {
                    caseIndex |= 2;
                }
                if (val2 < isoVal) {
                    caseIndex |= 4;
                }
                if (val3 < isoVal) {
                    caseIndex |= 8;
                }

                                                if (x < 1) {
                    if (boundryLow) {
                        caseIndex |= 1;
                        caseIndex |= 8;
                    } else {
                        caseIndex &= 15 - 1;
                        caseIndex &= 15 - 8;
                    }
                }

                if (y < 1) {
                    if (boundryLow) {
                        caseIndex |= 1;
                        caseIndex |= 2;
                    } else {
                        caseIndex &= 15 - 1;
                        caseIndex &= 15 - 2;
                    }
                }

                if (x == w - 1) {
                    if (boundryLow) {
                        caseIndex |= 2;
                        caseIndex |= 4;
                    } else {
                        caseIndex &= 15 - 2;
                        caseIndex &= 15 - 4;
                    }
                }

                if (y == h - 1) {
                    if (boundryLow) {
                        caseIndex |= 4;
                        caseIndex |= 8;
                    } else {
                        caseIndex &= 15 - 4;
                        caseIndex &= 15 - 8;
                    }
                }

                                                                if (caseIndex == 5) {
                                        double avg = (((double) val0) + val1 + val2 + val3) / 4.0;
                    if (avg >= isoVal) {
                        caseIndex = 16;
                    }
                } else if (caseIndex == 10) {
                                        double avg = (((double) val0) + val1 + val2 + val3) / 4.0;
                    if (avg >= isoVal) {
                        caseIndex = 17;
                    }
                }

                if (caseIndex != 0 && caseIndex != 15) {
                    int n = 0;
                    while (lineSegments[caseIndex][n] != -1) {

                        int edgeIndex1 = lineSegments[caseIndex][n];

                        final int x1 = getVertexX(x, edgeIndex1);
                        final int y1 = getVertexY(y, edgeIndex1);
                        final int indexFrom = y1 * width + x1;

                        final Vector2d p1 = new Vector2d();
                        interpolatePoint(isoVal, x, y, p1, edgeIndex1);
                        vertices[indexFrom] = p1;
                        n++;

                        int edgeIndex2 = lineSegments[caseIndex][n];

                        int x2 = getVertexX(x, edgeIndex2);
                        int y2 = getVertexY(y, edgeIndex2);
                        final int indexTo = y2 * width + x2;

                        final Vector2d p2 = new Vector2d();
                        interpolatePoint(isoVal, x, y, p2, edgeIndex2);
                        vertices[indexTo] = p2;
                        n++;

                        segments[indexFrom] = indexTo;

                    }                 }
            }         }         for (int i = 0; i < marked.length; i++) {
            marked[i] = segments[i] == -1;
        }

        int currentLineFrom = -1;
        int currentLineTo = -1;
        int lastUnmarkedIndex = 0;
        while ((lastUnmarkedIndex = nextIndex(marked, lastUnmarkedIndex)) != -1) {
            if (currentLineTo != -1 && !marked[currentLineFrom]) {
                result.lineTo(vertices[currentLineFrom]);
                marked[currentLineFrom] = true;
                currentLineFrom = currentLineTo;
                currentLineTo = segments[currentLineFrom];
            } else {
                currentLineFrom = lastUnmarkedIndex;
                currentLineTo = segments[currentLineFrom];
                result.breakContour();
            }
        }

        return result;
    }

    private int nextIndex(boolean[] marked, int lastUnmarkedIndex) {
        for (int i = lastUnmarkedIndex; i < marked.length; i++) {
            if (!marked[i]) {
                return i;
            }
        }
        return -1;
    }

    private int getVertexX(int x, int edgeIndex) {

        return (int) (2 * (x + centerPointsPerEdge[edgeIndex][0]));
    }

    private int getVertexY(int y, int edgeIndex) {

        return (int) (2 * (y + centerPointsPerEdge[edgeIndex][1]));
    }

    private void interpolatePoint(byte isoVal,
            int x, int y,
            Vector2d result,
            int edgeIndex) {

        final int p1X = x + squareEdges[edgeIndex][0][0];
        final int p1Y = y + squareEdges[edgeIndex][0][1];
        final int p2X = x + squareEdges[edgeIndex][1][0];
        final int p2Y = y + squareEdges[edgeIndex][1][1];

        final byte valueA = forceField.get(p1X,p1Y);
        final byte valueB = forceField.get(p2X,p2Y);
        final double interpolVal;
        if (valueA < isoVal == valueB < isoVal) {
                                    interpolVal = 0.5;
        } else {
                        interpolVal = (((double) isoVal) - valueB) / (((double) valueA) - valueB);
        }
        result.y = p2Y
                - interpolVal * (p2Y - p1Y);
        result.x = p2X - interpolVal * (p2X - p1X);
    }

    private static final int[][][] squareEdges = {
        {{0, 0}, {1, 0}},
        {{1, 0}, {1, 1}},
        {{1, 1}, {0, 1}},
        {{0, 1}, {0, 0}}
    };

    private static final float[][] centerPointsPerEdge = {
        {0.5f, 0f},
        {1.0f, 0.5f},
        {0.5f, 1.0f},
        {0.f, 0.5f}
    };

    private static final int[][] lineSegments = {
        {-1, -1, -1, -1, -1},
         {3, 0, -1, -1, -1},
         {0, 1, -1, -1, -1},
         {3, 1, -1, -1, -1},
         {1, 2, -1, -1, -1},
         {3, 2, 1, 0, -1},
         {0, 2, -1, -1, -1},
         {3, 2, -1, -1, -1},
         {2, 3, -1, -1, -1},
         {2, 0, -1, -1, -1},
         {0, 3, 2, 1, -1},
         {2, 1, -1, -1, -1},
         {1, 3, -1, -1, -1},
         {1, 0, -1, -1, -1},
         {0, 3, -1, -1, -1},
         {-1, -1, -1, -1, -1},
         {3, 0, 1, 2, -1},           {2, 3, 0, 1, -1},      };

}
