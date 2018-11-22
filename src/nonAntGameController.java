import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.colors.SeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class nonAntGameController {

    private GameModel mdl;

    private static final double INIT_SLD_VAL = 0.02;

    @FXML
    private TextField amountOfPointsTextField;

    @FXML
    private Slider pointsSlider;

    @FXML
    private Label gamemtrLabel;

    @FXML
    private TextField zzTextField;

    @FXML
    private TextField zoTextField;

    @FXML
    private TextField ozTextField;

    @FXML
    private TextField ooTextField;

    @FXML
    private TextField ztwTextField;

    @FXML
    private TextField zthTextField;

    @FXML
    private TextField otwTextField;

    @FXML
    private TextField othTextField;

    @FXML
    private Button resetButton;

    @FXML
    private Button calculateButton;

    @FXML
    private Button convexHullButton;

    @FXML
    void calculateButtonPressed(ActionEvent event) {
        try {
            mdl.getWinValue()[0][0] = Double.parseDouble(zzTextField.getText());
            mdl.getWinValue()[0][1] = Double.parseDouble(zoTextField.getText());
            mdl.getWinValue()[1][0] = Double.parseDouble(ztwTextField.getText());
            mdl.getWinValue()[1][1] = Double.parseDouble(zthTextField.getText());
            mdl.getWinValue()[2][0] = Double.parseDouble(ozTextField.getText());
            mdl.getWinValue()[2][1] = Double.parseDouble(ooTextField.getText());
            mdl.getWinValue()[3][0] = Double.parseDouble(otwTextField.getText());
            mdl.getWinValue()[3][1] = Double.parseDouble(othTextField.getText());

            OutcomesData data = mdl.calcWinning();
            mdl.setOutData(data);

            XYChart chart = new XYChartBuilder().width(400).height(400).xAxisTitle("Winnings of 1st player").yAxisTitle("Winnings of 2nd player").title("Set of Winnings").build();
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
            chart.addSeries("Set of Wins", data.getOutcomeX(), data.getOutcomeY()).setMarker(SeriesMarkers.CIRCLE).setLineStyle(SeriesLines.NONE);
            new SwingWrapper(chart).displayChart().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            mdl.setChart(chart);

        }
        catch (NumberFormatException ex){
            Alert alertWin = new Alert(Alert.AlertType.WARNING);
            alertWin.setTitle("Wrong input!");
            alertWin.setHeaderText("Wrong input");
            alertWin.setContentText("Game matrix elements are in wrong format or not a numbers at all.");

            alertWin.showAndWait();
        }
    }

    @FXML
    void resetButtonPressed(ActionEvent event) {
        mdl.debugDummy();
        mdl = new GameModel();
        pointsSlider.setValue(INIT_SLD_VAL);
        mdl.getIncr().bind(pointsSlider.valueProperty());
        amountOfPointsTextField.setText(new Double(INIT_SLD_VAL).toString());
        amountOfPointsTextField.textProperty().bindBidirectional(pointsSlider.valueProperty(), NumberFormat.getNumberInstance());
    }

    @FXML
    private void initialize(){
        mdl = new GameModel();
        pointsSlider.setValue(INIT_SLD_VAL);
        mdl.getIncr().bind(pointsSlider.valueProperty());
        amountOfPointsTextField.setText(new Double(INIT_SLD_VAL).toString());
        amountOfPointsTextField.textProperty().bindBidirectional(pointsSlider.valueProperty(), NumberFormat.getNumberInstance());
    }

    @FXML
    void convexHullButtonPressed(ActionEvent event) {
        mdl.calcConvexHullJarvis();
        mdl.plotConvex();
    }

}
