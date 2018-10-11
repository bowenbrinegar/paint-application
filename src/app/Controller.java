package app;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Controller {
    public ColorPicker colorPicker;
    public Button createCanvas;
    public Pane canvasFrame;
    public Slider brushSize;
    public Line dragLine;

    private enum mode { BRUSH, RECTANGLE, GRADIENT, CIRCLE }
    private mode currentMode;
    private Double _prev_x;
    private Double _prev_y;

    public void brush() {
        this.currentMode = mode.BRUSH;
    }

    public void rectangle() {
        this.currentMode = mode.RECTANGLE;
    }

    public void circle() {
        this.currentMode = mode.CIRCLE;
    }
    
    public void createCanvas() {
        createCanvas.setText("Clear Canvas");

        if (canvasFrame.getChildren().size() > 0) {
            canvasFrame.getChildren().clear();
            _prev_x = null;
            _prev_y = null;
        }

        double width = 597.0;
        double height = 333.0;

        final Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(25,25,width - 50,height - 50);

        canvas.getStyleClass().add("canvas-frame");
        canvas.setOnMouseDragged(event -> {
            double x = event.getX();
            double y = event.getY();

            if (this.currentMode == mode.BRUSH) {
                gc.setStroke(colorPicker.getValue());
                gc.setLineWidth(brushSize.getValue());
                gc.setLineCap(StrokeLineCap.ROUND);
                gc.strokeLine(_prev_x, _prev_y, x, y);

                _prev_x = x;
                _prev_y = y;
            } else if (this.currentMode == mode.RECTANGLE || this.currentMode == mode.CIRCLE) {
                canvasFrame.getChildren().remove(dragLine);

                this.dragLine = new Line();

                dragLine.setStartX(_prev_x);
                dragLine.setStartY(_prev_y);
                dragLine.setEndX(x);
                dragLine.setEndY(y);

                canvasFrame.getChildren().add(dragLine);
            }
        });

        canvas.setOnMousePressed(event -> {
            double x = event.getX();
            double y = event.getY();

            if (this.currentMode == mode.BRUSH) {
                double _x_center = x - (brushSize.getValue() / 2);
                double _y_center = y - (brushSize.getValue() / 2);

                gc.setFill(colorPicker.getValue());
                gc.fillOval(_x_center, _y_center, brushSize.getValue(), brushSize.getValue());
            }

            _prev_x = x;
            _prev_y = y;
        });

        canvas.setOnMouseReleased(event -> {
            double x = event.getX();
            double y = event.getY();


            canvasFrame.getChildren().remove(dragLine);
            gc.setFill(colorPicker.getValue());


            if (this.currentMode == mode.RECTANGLE) {
                gc.fillRect(_prev_x, _prev_y, _prev_x - x, _prev_y - y);
            }

            if (this.currentMode == mode.CIRCLE) {
                double _dist = dist(_prev_x, _prev_y, x, y) * 2;
                gc.fillOval(_prev_x - (_dist / 2), _prev_y - (_dist / 2), _dist, _dist);
            }

            _prev_x = null;
            _prev_y = null;
        });

        canvasFrame.getChildren().add(canvas);
    }

    public double dist(double ax, double ay, double bx, double by) {
        double x = Math.pow((ax - bx), 2);
        double y = Math.pow((ay - by), 2);
        double d = Math.sqrt(x + y);
        return d;
    }
}
