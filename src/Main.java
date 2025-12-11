import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;
public class Main {

    public static void main(String[] args){
        GUI.setListener(N ->{
            int[][] matrix = new int[N][N];
            Random rand = new Random();
            for(int i=0; i<N; i++){
                for(int j = 0; j<N; j++){
                    //matrix[i][j] = rand.nextInt(2); // 0 or 1
                    //increasing 1 cells
                    matrix[i][j] = rand.nextInt(10) < 7? 1: 0;
                }
            }
            //source and destination are always 1
            matrix[0][0] = 1;
            matrix[N-1][N-1] = 1;

            for(int i=0; i<N; i++){
                for(int j = 0; j<N; j++){
                    System.out.print(matrix[i][j] + "  ");
                }
                System.out.print("\n");
            }
            GUI.showGrid(matrix);
            //int t = Runtime.getRuntime().availableProcessors();
            Rectangle[][] rects = GUI.getRects(); //get's existing rects from gui
            RatThread rat = new RatThread(matrix, rects, 0, 0, Color.RED);
            new Thread(rat).start();
        });
        GUI.launch(GUI.class);

    }
}
