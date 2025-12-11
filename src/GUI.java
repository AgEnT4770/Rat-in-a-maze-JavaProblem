import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GUI extends Application{

    private static Listener listener;
    // Method for Main to set listener
    public static void setListener(Listener l) {
        listener = l;
    }

    public void start(Stage stage){
        TextField input = new TextField();
        input.setStyle("-fx-font-size: 15px;");
        input.setMaxWidth(150); //prevents stretching

        Button button = new Button("Start");
        button.setStyle("-fx-font-size: 16px;");

        button.setOnAction(e -> {
            try {
                int N = Integer.parseInt(input.getText());
                if (N <= 0) throw new IllegalArgumentException("Number must be positive above 0");
                if (N > 10) throw new IllegalArgumentException("Number must be 10 or less");
                // Call the listener
                if (listener != null) {
                    listener.onInputEntered(N);
                }
                stage.close(); // close input window

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid integer!");
                alert.showAndWait();
            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        Label label = new Label("Enter Matrix Size:");
        label.setStyle("-fx-font-size: 16px;");
        VBox layout = new VBox(10, label, input, button);
        layout.setAlignment(Pos.CENTER);
        //VBox.setMargin(input, new Insets(0, 0, 20, 0)); // bottom margin of 20px

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.setTitle("window");
        stage.show();
    }


    private static Rectangle[][] rects;
    public static Rectangle[][] getRects(){return rects;}
    public static void showGrid(int[][] matrix) {
        Stage stage = new Stage();
        GridPane grid = new GridPane();
        int N = matrix.length;
        rects = new Rectangle[N][N];
        int cellSize = 30;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Rectangle rect = new Rectangle(cellSize, cellSize);
                rect.setStroke(Color.BLACK);
                rect.setFill(matrix[i][j] == 0 ? Color.GREY : Color.WHITE);
                grid.add(rect, j, i);
                rects[i][j] = rect;
            }
        }


        Scene scene = new Scene(grid, N * cellSize + 10, N * cellSize + 10);
        stage.setScene(scene);
        stage.setTitle("Matrix Visualization");
        stage.show();
    }
    // Popup warning
    private static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
