import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.concurrent.atomic.AtomicInteger;

public class DFS {
    private final int[][] matrix;
    private final Rectangle[][] rects;
    private final Color color;
    private static boolean[][] visited;
    private static final Object lock = new Object();
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();//thread limit
    private static AtomicInteger activeThreads = new AtomicInteger(0);

    // list of colors for new threads
    private static Color[] colors = {Color.PINK, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.YELLOW};
    private static AtomicInteger colorIndex = new AtomicInteger(0);

    public DFS(int[][] matrix, Rectangle[][] rects, Color color){
        this.matrix = matrix;
        this.rects = rects;
        this.color = color;
        synchronized(lock){
            if(visited == null){
                visited = new boolean[matrix.length][matrix.length];
            }
        }
    }

    public void search(int row, int col){
        int N = matrix.length;
        if(!isSafe(row, col)) return;

        synchronized(lock){
            visited[row][col] = true;
        }
        Platform.runLater(() -> rects[row][col].setFill(color));
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // destination
        if(row==N-1 && col==N-1) return;

        // possible moves: down, right
        int[][] moves = {{row+1, col}, {row, col+1}};
        int validMoves = 0;
        for(int[] m: moves){
            if(isSafe(m[0], m[1])) validMoves++;
        }

        for(int[] m: moves){
            int r = m[0], c = m[1];
            if(isSafe(r,c)){
                if(validMoves > 1 && activeThreads.get() < MAX_THREADS){
                    Color nextColor = getNextColor();
                    RatThread newThread = new RatThread(matrix, rects, r, c, nextColor);
                    activeThreads.incrementAndGet();
                    new Thread(() -> {
                        //newThread.run();
                        new Thread (newThread).start();
                        activeThreads.decrementAndGet();
                    }).start();
                } else {
                    search(r,c);
                }
            }
        }
    }

    private boolean isSafe(int r, int c){
        int N = matrix.length;
        synchronized (lock) {
            return r >= 0 && r < N && c >= 0 && c < N && !visited[r][c] && matrix[r][c] == 1;
        }
    }

    private Color getNextColor(){
        int index = colorIndex.getAndIncrement() % colors.length;
        //same as (colorIndex+1) % color.length
        return colors[index];
    }
}
