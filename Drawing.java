import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Drawing extends Canvas {

    private ArrayList<Integer> L1_sortedX;
    private ArrayList<Integer> L2_sortedY;

    private ArrayList<Integer> pointsX, pointsY;

    private int[][] M;
    private int N = 0;

    private Vector<Integer> rectPointsX, rectPointsY;
    private Vector<Boolean> isPointInside;

    private boolean isAddingPoints = true;
    private boolean isDrawingRectangle = false;

    private int insidePointsCount = 0;

    private JTextArea statusArea = null;
    private String statusMessage = "";

    public Drawing() {
        setSize(501, 501);
        setBackground(new Color(185, 213, 213));
        isAddingPoints = true;
        isDrawingRectangle = false;

        pointsX = new ArrayList<Integer>();
        pointsY = new ArrayList<Integer>();
        L1_sortedX = new ArrayList<Integer>();
        L2_sortedY = new ArrayList<Integer>();
        rectPointsX = new Vector<Integer>();
        rectPointsY = new Vector<Integer>();
        isPointInside = new Vector<Boolean>();
        M = new int[1][1];

        this.enableEvents(MouseEvent.MOUSE_CLICKED);
        statusMessage = "Left-click to add points...";
    }

    public void setStatusTextArea(JTextArea area) {
        this.statusArea = area;
    }

    public void postInitMessage() {
        logStatus("MODE: Add points to be searched.\nRight-click to switch to MODE drawing.");
    }

    private void logStatus(String message) {
        if (statusArea != null) {
            statusArea.append(message + "\n\n");
            statusArea.setCaretPosition(statusArea.getDocument().getLength());
        } else
            System.out.println(message);
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 500, 500);

        g.setColor(new Color(47, 71, 69));
        g.drawLine(0, 250, 500, 250);
        g.drawLine(250, 0, 250, 500);

        g.setColor(new Color(3, 142, 139));
        for (int i = 0; i < pointsX.size(); i++)
            g.fillOval(pointsX.get(i) - 2, pointsY.get(i) - 2, 6, 6);

        g.setColor(new Color(255, 255, 255));
        for (int i = 0; i < rectPointsX.size(); i++)
            g.fillOval(rectPointsX.elementAt(i) - 3, rectPointsY.elementAt(i) - 3, 7, 7);

        if (rectPointsX.size() == 2) {
            int ax = rectPointsX.elementAt(0);
            int ay = rectPointsY.elementAt(0);
            int cx = rectPointsX.elementAt(1);
            int cy = rectPointsY.elementAt(1);

            int minX = Math.min(ax, cx);
            int maxX = Math.max(ax, cx);
            int minY = Math.min(ay, cy);
            int maxY = Math.max(ay, cy);

            g.drawRect(minX, minY, maxX - minX, maxY - minY);

            g.setColor(new Color(0, 25, 21));
            for (int i = 0; i < isPointInside.size(); i++)
                if (isPointInside.elementAt(i))
                    g.drawOval(pointsX.get(i) - 3, pointsY.get(i) - 3, 8, 8);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String message = "";
        if (rectPointsX.size() == 2) {
            message = "There are " + insidePointsCount + " points inside.";
            if (insidePointsCount == 1)
                message = "There is 1 point inside.";
        }
        g.drawString(message, 10, 490);

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString(statusMessage, 10, 20);
    }

    public void prepareDataStructures() {
        N = pointsX.size();
        if (N == 0) {
            M = new int[1][1];
            return;
        }

        L1_sortedX = new ArrayList<>(pointsX);
        Collections.sort(L1_sortedX);

        L2_sortedY = new ArrayList<>(pointsY);
        Collections.sort(L2_sortedY);

        M = new int[N + 1][N + 1];

        for (int j = 1; j <= N; j++) {
            int px = L1_sortedX.get(j - 1);
            int originalIndex = -1;
            for(int i = 0; i < N; i++)
                if(pointsX.get(i).equals(px) && !pointsY.get(i).equals(-1)){
                    originalIndex = i;
                    break;
                }

            if (originalIndex == -1) continue;
            int py = pointsY.get(originalIndex);

            int k = binarySearchRank(L2_sortedY, py, false);

            for (int i = 1; i <= N; i++)
                if (i <= k)
                    M[i][j] = M[i][j - 1];
                else
                    M[i][j] = M[i][j - 1] + 1;
        }
    }

    private int binarySearchRank(ArrayList<Integer> L, int coord, boolean strictLower) {
        int left = 0;
        int right = L.size() - 1;
        int rank = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int val = L.get(mid);

            if (strictLower) {
                if (val < coord) {
                    rank = mid + 1;
                    left = mid + 1;
                } else
                    right = mid - 1;

            } else {
                if (val <= coord) {
                    rank = mid + 1;
                    left = mid + 1;
                } else
                    right = mid - 1;
            }
        }
        return rank;
    }

    public void calculateInsidePoints() {
        if (rectPointsX.size() < 2 || N == 0) {
            insidePointsCount = 0;
            isPointInside.clear();
            for(int i=0; i<pointsX.size(); i++)
                isPointInside.add(Boolean.FALSE);
            return;
        }

        int ax = rectPointsX.elementAt(0);
        int ay = rectPointsY.elementAt(0);
        int cx = rectPointsX.elementAt(1);
        int cy = rectPointsY.elementAt(1);

        int x1 = Math.min(ax, cx);
        int y1 = Math.min(ay, cy);
        int x2 = Math.max(ax, cx);
        int y2 = Math.max(ay, cy);

        // p3(x2, y2)
        int j3 = binarySearchRank(L1_sortedX, x2, false);
        int i3 = binarySearchRank(L2_sortedY, y2, false);
        int Qp3 = M[i3][j3];

        // p2(x1, y2)
        int j2 = binarySearchRank(L1_sortedX, x1, true);
        int i2 = binarySearchRank(L2_sortedY, y2, false);
        int Qp2 = M[i2][j2];

        // p4(x2, y1)
        int j4 = binarySearchRank(L1_sortedX, x2, false);
        int i4 = binarySearchRank(L2_sortedY, y1, true);
        int Qp4 = M[i4][j4];

        // p1(x1, y1)
        int j1 = binarySearchRank(L1_sortedX, x1, true);
        int i1 = binarySearchRank(L2_sortedY, y1, true);
        int Qp1 = M[i1][j1];

        insidePointsCount = Qp3 - Qp2 - Qp4 + Qp1;

        insidePointsCount = Math.max(0, insidePointsCount);

        isPointInside.clear();
        for (int i = 0; i < pointsX.size(); i++) {
            int px = pointsX.get(i);
            int py = pointsY.get(i);

            if (px > x1 && px < x2 && py > y1 && py < y2)
                isPointInside.add(Boolean.TRUE);
            else
                isPointInside.add(Boolean.FALSE);

        }
    }

    public void processMouseEvent(MouseEvent e) {
        if (e.getID() != MouseEvent.MOUSE_CLICKED)
            return;

        if (isAddingPoints && e.getButton() == MouseEvent.BUTTON1) {
            this.pointsX.add(e.getX());
            this.pointsY.add(e.getY());
        }
        else if (isAddingPoints && e.getButton() == MouseEvent.BUTTON3) {
            isAddingPoints = false;
            prepareDataStructures();

            statusMessage = "Rectangle Mode: Left-click for point A.";
            logStatus("MODE: Draw Rectangles.\nLeft-click for point A.");
        }
        else if (!isAddingPoints && e.getButton() == MouseEvent.BUTTON1) {
            if (!isDrawingRectangle) {
                rectPointsX.clear();
                rectPointsY.clear();
                isPointInside.clear();
                insidePointsCount = 0;

                rectPointsX.add(e.getX());
                rectPointsY.add(e.getY());
                isDrawingRectangle = true;
                statusMessage = "Point A added. Left-click for point C.";
                logStatus("Point A added.\nLeft-click for point C.");
            } else {
                rectPointsX.add(e.getX());
                rectPointsY.add(e.getY());
                isDrawingRectangle = false;

                calculateInsidePoints();

                statusMessage = "Found " + insidePointsCount + " points. Left-click for a new point A.";
                logStatus("Point C added.\nFound " + insidePointsCount + " points.\n\nAwaiting new point A...");
            }
        }
        repaint();
    }
}