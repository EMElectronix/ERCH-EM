package desktop.app.erch.RealTime;

import eu.hansolo.medusa.*;
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.Gauge.NeedleSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class Roundgauge extends Region {
    private static final double PREFERRED_WIDTH  = 320;
    private static final double PREFERRED_HEIGHT = 320;
    private static final double MINIMUM_WIDTH    = 5;
    private static final double MINIMUM_HEIGHT   = 5;
    private static final double MAXIMUM_WIDTH    = 1024;
    private static final double MAXIMUM_HEIGHT   = 1024;
    private        Gauge   rGauge;
    private        FGauge   rimrGauge;

    private        Pane    pane;


    // ******************** Constructors **************************************
    public Roundgauge(int decimal, double Startangle, double Anglerange, Gauge.ScaleDirection Direction,double min,
                      double max,boolean medium,boolean minor, String Title,String Unit,String CustomLabel,int Subtitle,
                      String Substr,int width,int height) {
        init();
        initGraphics(decimal,Startangle,Anglerange,Direction,min,max,medium,minor,Title,Unit,CustomLabel,Subtitle,
                Substr,width,height);
    }
    public Roundgauge(int decimal,  Gauge.ScaleDirection Direction,double min, double max,boolean medium,boolean minor,
                      String Title,String Unit,String CustomLabel,int Subtitle,String Substr,int width,int height) {
        init();
        initGraphics(decimal,Direction,min,max,medium,minor,Title,Unit,CustomLabel,Subtitle,Substr,width,height);
    }

    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
                Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics(int decimal, double Startangle, double Anglerange, Gauge.ScaleDirection Direction,
                              double min, double max,boolean medium, boolean minor,String Title,String Unit,
                              String CustomLabel,int Subtitle,String Substr,int width,int height) {
        CustomLabel = CustomLabel.replace("\"", "");
        String[] CustomLabelsArr = CustomLabel.split(",");
        GaugeBuilder builder = GaugeBuilder.create()
                .borderPaint(Color.WHITE)
                .borderWidth(3)
                .skinType(Gauge.SkinType.GAUGE)
                .decimals(decimal)
                .subTitleColor(Color.WHITE)
                .startAngle(Startangle)
                .angleRange(Anglerange)
                .scaleDirection(Direction)
                .minValue(min)
                .maxValue(max)
                .animated(true)
                .autoScale(true)
                .shadowsEnabled(true)
                .ledColor(Color.rgb(250, 50, 0))
                .majorTickMarkType(TickMarkType.TRAPEZOID)
                .majorTickMarksVisible(true)
                .mediumTickMarkType(TickMarkType.TRAPEZOID)
                .mediumTickMarksVisible(medium)
                .mediumTickMarkColor(Color.YELLOW)
                .minorTickMarkType(TickMarkType.LINE)
                .minorTickMarksVisible(minor)
                .tickLabelsVisible(true)
                .tickLabelColor(Color.WHITE)
                .tickLabelOrientation(TickLabelOrientation.HORIZONTAL)
                .tickLabelLocation(TickLabelLocation.INSIDE)
                .needleType(Gauge.NeedleType.STANDARD)
                .needleShape(NeedleShape.FLAT)
                .needleSize(NeedleSize.THICK)
                .needleColor(Color.rgb(234, 67, 38))
                .knobColor(Gauge.DARK_COLOR)
                .ledVisible(true)
                .ledColor(Color.RED)
                .ledType(Gauge.LedType.STANDARD)
                .title(Title)
                .titleColor(Color.YELLOW)
                .unit(Unit)
                .customTickLabelsEnabled(true)
                .customTickLabelFontSizeEnabled(true)
                .customTickLabelFontSize(30)
                .customTickLabels(CustomLabelsArr)
                .unitColor(Color.WHITE)
                .foregroundBaseColor(Gauge.BRIGHT_COLOR);

        // Add subtitle only if necessary
        if (Subtitle == 1) {
            builder.subTitle(Substr);
            builder.subTitleColor(Color.WHITE);
        }

        rGauge = builder.build();


        rimrGauge  = FGaugeBuilder

                .create()

                .prefSize(width, height)

                .gauge(rGauge)

                .gaugeDesign(GaugeDesign.SHINY_METAL)

                .gaugeBackground(GaugeDesign.GaugeBackground.BLACK)

                .foregroundVisible(true)

                .build();

        pane = new Pane(rimrGauge) ;


        getChildren().setAll(pane);
    }

    //---!!!--- This initGraphics is specially created for vsGauge i.e Vehicle Speed Gauge as the Gauge
    //           was not created properly when start angle and end angle was passed.

    private void initGraphics(int decimal,  Gauge.ScaleDirection Direction,double min, double max,boolean medium,
                              boolean minor,String Title,String Unit,String CustomLabel,int Subtitle,String Substr,
                              int width,int height) {
        CustomLabel = CustomLabel.replace("\"", "");
        String[] CustomLabelsArr = CustomLabel.split(",");
        GaugeBuilder builder = GaugeBuilder.create()
                .borderPaint(Color.WHITE)
                .borderWidth(3)
                .skinType(Gauge.SkinType.GAUGE)
                .decimals(decimal)
                .subTitleColor(Color.WHITE)
                .scaleDirection(Direction)
                .minValue(min)
                .maxValue(max)
                .animated(true)
                .autoScale(true)
                .shadowsEnabled(true)
                .ledColor(Color.rgb(250, 50, 0))
                .majorTickMarkType(TickMarkType.TRAPEZOID)
                .majorTickMarksVisible(true)
                .mediumTickMarkType(TickMarkType.TRAPEZOID)
                .mediumTickMarksVisible(medium)
                .mediumTickMarkColor(Color.YELLOW)
                .minorTickMarkType(TickMarkType.LINE)
                .minorTickMarksVisible(minor)
                .tickLabelsVisible(true)
                .tickLabelColor(Color.WHITE)
                .tickLabelOrientation(TickLabelOrientation.HORIZONTAL)
                .tickLabelLocation(TickLabelLocation.INSIDE)
                .needleType(Gauge.NeedleType.STANDARD)
                .needleShape(NeedleShape.FLAT)
                .needleSize(NeedleSize.THICK)
                .needleColor(Color.rgb(234, 67, 38))
                .knobColor(Gauge.DARK_COLOR)
                .ledVisible(true)
                .ledColor(Color.RED)
                .ledType(Gauge.LedType.STANDARD)
                .title(Title)
                .titleColor(Color.YELLOW)
                .unit(Unit)
                .customTickLabelsEnabled(true)
                .customTickLabelFontSizeEnabled(true)
                .customTickLabelFontSize(30)
                .customTickLabels(CustomLabelsArr)
                .unitColor(Color.WHITE)
                .foregroundBaseColor(Gauge.BRIGHT_COLOR);

        // Add subtitle only if necessary
        if (Subtitle == 1) {
            builder.subTitle(Substr);
            builder.subTitleColor(Color.WHITE);
        }

        rGauge = builder.build();


        rimrGauge  = FGaugeBuilder

                .create()

                .prefSize(width, height)

                .gauge(rGauge)

                .gaugeDesign(GaugeDesign.SHINY_METAL)

                .gaugeBackground(GaugeDesign.GaugeBackground.BLACK)

                .foregroundVisible(true)

                .build();

        pane = new Pane(rimrGauge) ;


        getChildren().setAll(pane);
    }


    // ******************** Methods *******************************************
    public Gauge getRGauge()  { return rGauge; }



}

