package Interface;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Listener that redraws the canvas when it is resized.
 */
public class CanvasResizeListener implements ChangeListener<Number> {

    private MapCanvas canvas;

    /**
     * Constructs the listener with the canvas to redraw.
     * @param canvas the map canvas
     */
    public CanvasResizeListener(MapCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
        canvas.redraw();
    }
}