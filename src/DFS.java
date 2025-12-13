import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class DFS {
    private final int[][] matrix;
    private final Rectangle[][] rects;
    private final Color color;
    private static boolean[][] visited;
    private static final Object lock = new Object();
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();//thread limit
    private static AtomicInteger activeThreads = new AtomicInteger(0);
    private static volatile boolean found = false;
    private static final Semaphore semaphore = new Semaphore(MAX_THREADS, true); //counting semaphore with initial value = max_threads

    // list of colors for new threads
    private static Color[] colors = {
            Color.GOLD, Color.PINK, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.YELLOW,
            Color.CYAN, Color.DARKMAGENTA, Color.BROWN, Color.LIME, Color.TEAL, Color.INDIGO};
    private static AtomicInteger colorIndex = new AtomicInteger(0);

    public DFS(int[][] matrix, Rectangle[][] rects, Color color/*, boolean[][] visited*/){
        this.matrix = matrix;
        this.rects = rects;
        this.color = color;
        //this.visited = visited;
        synchronized(lock){ //instance specific and not a parameter to prevent race conditions
            if(visited == null){
                visited = new boolean[matrix.length][matrix.length];
            }
        }
    }

    public void search(int row, int col){
        if(found || Thread.currentThread().isInterrupted()) return;

        int N = matrix.length;
        if(!isSafe(row, col)) return;

        synchronized(lock){// lock while updating
            visited[row][col] = true;
        }

        Platform.runLater(() -> { //thread-safe for javaFX
            rects[row][col].setFill(color);
        });

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // destination
        if(row==N-1 && col==N-1) {
            found = true;
            //System.out.println("\nThread " + Thread.currentThread().getName() + " reached destination.");
            printVisited();
            return;
        }


        // possible moves: down, right
        int[][] moves = {{row+1, col}, {row, col+1}};
        int validMoves = 0;
        for(int[] m: moves){
            if(isSafe(m[0], m[1])) validMoves++;
        }

        for(int[] m: moves){
            if(found || Thread.currentThread().isInterrupted()) return;
            int r = m[0], c = m[1];
            if(isSafe(r,c)){
                if(validMoves > 1 && semaphore.tryAcquire()){
                    Color nextColor = getNextColor();
                    //RatThread newThread = new RatThread(matrix, rects, r, c, nextColor);
                    //activeThreads.incrementAndGet(); in if using CAS to prevent race condition: if two checked at the same time and incremented later, we can exceed MAX_THREADS
                    new Thread(() -> {
                        try {//each thread create a DFS object
                            DFS dfs = new DFS(matrix, rects, nextColor);
                            dfs.search(r, c);
                        } finally {
                            semaphore.release();
                        }
                    }).start();
                } else {
                    // No permit available, do it in current thread
                    search(r, c);
                }

                  /*  new Thread(() -> { //using RatThread wrapper
                        try {
                            //RatThread newThread = new RatThread(matrix, rects, r, c, nextColor);
                            //newThread.run();
                        } finally {
                            activeThreads.decrementAndGet();
                        }
                    }).start();
                } else {
                    activeThreads.decrementAndGet();
                    search(r,c);
                }*/
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

    private void printVisited(){
        synchronized(lock){
            System.out.println("\nMatrix Visited:");
            for(int i=0; i<visited.length; i++){
                for(int j=0; j<visited.length; j++){
                    System.out.print((visited[i][j] ? "1"+" " : "0"+" "));
                }
                System.out.print("\n");
            }
            System.out.println();
        }
    }

}