package desktop.app.erch.RealTime;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.LcdDesign;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class LCD extends Region {
    /*
    Constructor to create LCD Gauge
     */
    private  Gauge Speed;
    public LCD(String title, int decimals,String subTitle, String unit, double MaxValue , LcdDesign lcdDesign, boolean thresholdVisible, double threshold, int Width) {
        initGraphics(title, decimals,subTitle, unit, MaxValue, lcdDesign, thresholdVisible, threshold,Width);
    }

    private void initGraphics(String title, int decimals, String subTitle, String unit, double MaxValue,LcdDesign lcdDesign, boolean thresholdVisible, double threshold,int Width) {
       Speed = GaugeBuilder.create()
                .skinType(Gauge.SkinType.LCD)
               // .animated(true)
                .title(title)
               .decimals(decimals)
               .prefWidth(Width)
               .borderWidth(10)
               .minValue(0)
               .maxValue(MaxValue)
               .thresholdColor(Color.RED)
               //.unit("  ")
                .subTitle(subTitle)
                .maxMeasuredValueVisible(false)
                .minMeasuredValueVisible(false)
               .oldValueVisible(false)
//                .oldValueVisible(false)
                .lcdDesign(lcdDesign)
                .thresholdVisible(thresholdVisible)
                .threshold(threshold)
                .build();



        Pane pane = new Pane(Speed);

        getChildren().setAll(pane);
    }

    public Gauge getLcdGauge()  { return Speed; }
}
