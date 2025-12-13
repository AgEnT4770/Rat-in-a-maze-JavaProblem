import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RatThread implements Runnable{
    private int[][] matrix;
    private Rectangle[][] rects;
    //private boolean[][] visited;
    private int startRow;
    private int startCol;
    private Color color;

    public RatThread(int[][] matrix, Rectangle[][] rects, int row, int col, Color color){//row,col passed here is frontier
        this.matrix = matrix;
        this.rects = rects;
        this.startRow = row;
        this.startCol = col;
        this.color = color;
    }

    public void run(){
        DFS dfs = new DFS(matrix, rects, color/*, visited*/);
        dfs.search(startRow, startCol);
    }
}