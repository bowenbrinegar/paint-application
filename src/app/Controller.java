package app;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class Controller {
    public ColorPicker colorPicker;
    public Button createCanvas;
    public Pane canvasFrame;
    public Slider brushSize;

    private Canvas canvas;
    private Line dragLine;
    private Circle dragCircle;
    private Rectangle dragRectangle;
    private enum mode { BRUSH, RECTANGLE, GRADIENT, CIRCLE, SELECTION, ERASE }
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

    public void selection() {
        this.currentMode = mode.SELECTION;
    }

    public void erase() {
        this.currentMode = mode.ERASE;
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

        this.canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

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
                dragLine.setStrokeWidth(1);
                dragLine.setStroke(Color.BLACK);

                canvasFrame.getChildren().add(dragLine);
            }

            if (this.currentMode == mode.CIRCLE) {
                canvasFrame.getChildren().remove(dragCircle);

                double _dist = dist(_prev_x, _prev_y, x, y);
                this.dragCircle = new Circle();

                dragCircle.setCenterX(_prev_x);
                dragCircle.setCenterY(_prev_y);
                dragCircle.setRadius(_dist);
                dragCircle.setFill(null);
                dragCircle.setStrokeWidth(1);
                dragCircle.setStroke(Color.BLACK);

                canvasFrame.getChildren().add(dragCircle);
            }

            if (this.currentMode == mode.RECTANGLE) {
                canvasFrame.getChildren().remove(dragRectangle);

                this.dragRectangle = new Rectangle();

                dragRectangle.setX(_prev_x);
                dragRectangle.setY(_prev_y);
                dragRectangle.setWidth(Math.abs(_prev_x - x));
                dragRectangle.setHeight(Math.abs(_prev_y - y));
                dragRectangle.setFill(null);
                dragRectangle.setStrokeWidth(1);
                dragRectangle.setStroke(Color.BLACK);

                canvasFrame.getChildren().add(dragRectangle);
            }

            if (this.currentMode == mode.ERASE) {
                gc.clearRect(_prev_x, _prev_y, brushSize.getValue(), brushSize.getValue());

                _prev_x = x;
                _prev_y = y;
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
            canvasFrame.getChildren().remove(dragLine);

            if (this.currentMode == mode.RECTANGLE) {
                dragRectangle.setFill(colorPicker.getValue());
                dragRectangle.setStrokeWidth(0);
            }

            if (this.currentMode == mode.CIRCLE) {
                dragCircle.setFill(colorPicker.getValue());
                dragCircle.setStrokeWidth(0);
            }

            _prev_x = null;
            _prev_y = null;
        });

        canvasFrame.setOnMouseDragged(event -> {
            double _x_position = event.getX();
            double _y_position = event.getY();

            if (this.currentMode == mode.SELECTION) {
                if (event.getTarget() == dragRectangle) {
                    dragRectangle.setX(_x_position);
                    dragRectangle.setY(_y_position);
                } else if (event.getTarget() == dragCircle) {
                    dragCircle.setCenterX(_x_position);
                    dragCircle.setCenterY(_y_position);
                }
            }
        });

        canvasFrame.getChildren().add(canvas);
    }

    private double dist(double ax, double ay, double bx, double by) {
        double x = Math.pow((ax - bx), 2);
        double y = Math.pow((ay - by), 2);
        double d = Math.sqrt(x + y);
        return d;
    }
}
