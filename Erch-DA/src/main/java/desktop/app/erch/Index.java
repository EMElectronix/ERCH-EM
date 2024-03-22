package desktop.app.erch;

import desktop.app.erch.Connection.Comport;
import desktop.app.erch.Nonc.WriteNonc;
import desktop.app.erch.PPRVS.WritePPR;
import desktop.app.erch.RTC.WriteRTC;
import desktop.app.erch.RealTime.Dashboard;
import desktop.app.erch.SSource.WritesSource;
import desktop.app.erch.Sampling.WriteSampling;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Objects;

import static desktop.app.erch.Helper.Common.erchIcon;
import static desktop.app.erch.Helper.Common.setBackground;
import static desktop.app.erch.Helper.Display.sof;

public class Index extends Application {

     Logger log = LogManager.getLogger(Index.class);
    BorderPane indexLayout = new BorderPane();
    public static final MenuBar mainMenuBar = new MenuBar();

    MenuItem item1, item2, item3, item4, item5, item6, item7, item8, item9, item10;
    MenuItem item11, item12, item13, item14, item15, item16, item17,item18,item19;
    Image[] wallPapers;
    int currentImageIndex = 0;
    Comport envSetup = new Comport();
    Dashboard realtime = new Dashboard();

    WriteRTC rtcEdit = new WriteRTC();

    WriteSampling srEdit = new WriteSampling();

    ReadConf confRead = new ReadConf();

    WritePPR pprEdit = new WritePPR();

    WritesSource ssEdit = new WritesSource();

    WriteNonc noncEdit = new WriteNonc();


    @Override
    public void start(Stage indexStage) {

        // Set logger level to INFO
        Configurator.setRootLevel(Level.INFO);

        /**••• Menubar Starts •••**/
        // Create MenuBar
        Menu config  = new Menu("Configuration");
        Menu view    = new Menu("View");
        Menu options = new Menu("Options");
        Menu help    = new Menu("Help");

        mainMenuBar.getMenus().addAll(config,view,options,help);
        mainMenuBar.setUseSystemMenuBar(true);

        VBox menuVbox = new VBox(mainMenuBar);
        indexLayout.setTop(menuVbox);

        //add Menuitems under Config menu
        item1 = createMenuItem("Set Date and Time of RTC","ctrl+D",config);
        item2 = createMenuItem("Parameter limits of Warning","ctrl+L",config);
        item3 = createMenuItem("NO/NC Configuration (for fault inputs)","ctrl+N",config);
        item4 = createMenuItem("Change ERCH Sampling Rate","ctrl+R",config);
        item5 = createMenuItem("Pulses per Revolution for Various speeds","ctrl+P",config);
        item6 = createMenuItem("Signal source for Eng.RPM & Vehicle Speed","ctrl+S",config);
        item7 = createMenuItem("Change Vehicle Model No. & Serial No.","ctrl+V",config);
        item8 = createMenuItem("Change ERCH Password",config);
        item9 = createMenuItem("ERCH Setup", config);
        item10 = createMenuItem("Exit","ctrl+X",config);

        //add Menuitems under view menu
        item11 = createMenuItem("Access ERCH Data","Alt+D",view);
        item12 = createMenuItem("ERCH Configuration Info","Alt+I",view);
        item13 = createMenuItem("Realtime Data Dashboard","Alt+R",view);
        item14 = createMenuItem("Reports","Analog Parameters","Control Outputs",
                "Digital Parameters",view);
        item15 = createMenuItem("Graph Analysis","Analog Trend Analysis (Single)",
                "Analog Trend Analysis (Multiline)","Digital Trend Analysis",view);

        //add Menuitems under options menu
        item16 = createMenuItem("Erase Flash Memory",options);
        item17 = createMenuItem("Environmental Setup","Alt+C",options);

        //add Menuitems under help menu
        item18 = createMenuItem("Contents",help);
        item19 = createMenuItem("About...",help);


        SeparatorMenuItem[] sepLine = new SeparatorMenuItem[5];

        for(int i=0;i<5;i++){
            sepLine[i] = new SeparatorMenuItem();
        }

        config.getItems().add(8,sepLine[0]);
        config.getItems().add(10,sepLine[1]);

        view.getItems().add(3,sepLine[2]);
        /**--- Menubar Ends ---**/


        wallPapers = new Image[]{
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/desktop/app/erch/Images/Tatra.png"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/desktop/app/erch/Images/TatraSnow.png"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/desktop/app/erch/Images/tatra.jpg"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/desktop/app/erch/Images/TatraDesert.png")))

        };
        // Set initial background
        setBackground(wallPapers[currentImageIndex],indexLayout);



        /**••• Menuitem functionality Starts •••**/

        //When clicked on set Date and Time of RTC
        item1.setOnAction(e -> {
            if(Comport.isEcuConnected){
                rtcEdit.displayWriteRTC(Comport.getConnectedPort(),indexStage);
            }
            else {
               disconnected();
            }

        });

        //When clicked on Nonc Configuration
        item3.setOnAction(e -> {
            if(Comport.isEcuConnected){
                noncEdit.displayWriteNonc(Comport.getConnectedPort(),indexStage);
            }
            else {
                disconnected();
            }

        });


        //When clicked on Change Sampling Rate
        item4.setOnAction(e -> {
            if(Comport.isEcuConnected){
                srEdit.displayWriteSampling(Comport.getConnectedPort(),indexStage);
            }
            else {
                disconnected();
            }

        });

        //When clicked on Pulse Per Revolution
        item5.setOnAction(e -> {
            if(Comport.isEcuConnected){
                pprEdit.displayWritePPR(Comport.getConnectedPort(),indexStage);
            }
            else {
                disconnected();
            }

        });

        //When clicked on Signal source for Eng.RPM & Vehicle Speed
        item6.setOnAction(e -> {
            if(Comport.isEcuConnected){
                ssEdit.displayWritesSource(Comport.getConnectedPort(),indexStage);
            }
            else {
                disconnected();
            }

        });


        //When clicked on ERCH Read Configuration
        item12.setOnAction(e -> {
            if(Comport.isEcuConnected){
                confRead.displayConfig(Comport.getConnectedPort());
            }
            else {
                disconnected();
            }

        });


        //When clicked on Realtime Dashboard
        item13.setOnAction(e -> {
            if(Comport.isEcuConnected){
                realtime.displayDashboard(Comport.getConnectedPort());
            }
            else {
                disconnected();
            }

        });

        //When clicked on Environmental Setup
        item17.setOnAction(e -> envSetup.displayComport(indexStage));


        /**--- Menuitem functionality Ends ---**/

        /**••• Create and start a thread for image transition •••**/

        Thread imageTransitionThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000); // Sleep for 10 seconds
                } catch (InterruptedException e) {
                    // Handle InterruptedException
                    log.warn("Thread sleep interrupted! {}", e.getMessage());
                    // Restore interrupted status
                    Thread.currentThread().interrupt();
                }

                // Switch to the next image with a fade transition
                switchImageWithFade();
            }
        });
        imageTransitionThread.setDaemon(true); // Set the thread as a daemon
        imageTransitionThread.start();


        // add icon and title to Index Dialog
        indexStage.getIcons().add(erchIcon);
        indexStage.setTitle("ERCH ECU PC Software");

        Scene indexScene = new Scene(indexLayout, 800, 600);
        indexScene.getStylesheets().add(getClass().getResource("/desktop/app/erch/css/Index.css").toExternalForm());
        indexStage.setScene(indexScene);
        indexStage.show();

    }


    private MenuItem createMenuItem(String title, String shortcut, Menu menuBar) {
        /*
        createMenuItem creates menuitem with shortcut keys
         args    : title    → Title of menuItem
                   shortcut → shortcut key of menuItem
                   menuBar  → menuBar contains menuItems
         returns : menuitem with title
                   menuitem with shortcut key
                   menuitem added to menuBar
         */

        MenuItem menuItem = new MenuItem(title);
        menuItem.setAccelerator(KeyCombination.keyCombination(shortcut));
        menuBar.getItems().add(menuItem);
        return menuItem;
    }

    private MenuItem createMenuItem(String title,Menu menuBar) {
        /*
        createMenuItem creates menuitem without shortcut keys
         args    : title    → Title of menuItem
                   menuBar  → menuBar contains menuItems
         returns : menuitem with title
                   menuitem added to menuBar
         */
        MenuItem menuItem = new MenuItem(title);
        menuBar.getItems().add(menuItem);
        return menuItem;
    }

    private Menu createMenuItem(String title, String r1Name, String r2Name, String r3Name, Menu menuBar) {

        /*
        createMenuItem creates menuitem with submenu-items
         args    : title    → Title of menuItem
                   r1,r2,r3 → submenu-items under menuItem
                   menuBar  → menuBar contains menuItems
         returns : menuitem with title
                   menuitem added to menuBar
         */

        RadioMenuItem r1 = new RadioMenuItem(r1Name);
        RadioMenuItem r2 = new RadioMenuItem(r2Name);
        RadioMenuItem r3 = new RadioMenuItem(r3Name);

        Menu submenu = new Menu(title);
        submenu.getItems().addAll(r1, r2, r3);

        menuBar.getItems().add(submenu);
        return submenu;
    }


     void switchImageWithFade() {
        /*
        switchImageWithFade switches Wallpaper Images
         */


        // Calculate the index of the next image
        currentImageIndex = (currentImageIndex + 1) % wallPapers.length;

        // Create a new fade transition
        FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(0.25), indexLayout);
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.3);

        // Set the action to be performed when the fade-out transition finishes
        fadeOutTransition.setOnFinished(event -> {
            // Set the new background image
            setBackground(wallPapers[currentImageIndex],indexLayout);

            // Create a new fade-in transition
            FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(0.25), indexLayout);
            fadeInTransition.setFromValue(0.3);
            fadeInTransition.setToValue(1.0);

            // Start the fade-in transition
            fadeInTransition.play();
        });

        // Start the fade-out transition
        fadeOutTransition.play();
    }

    void disconnected(){
        log.warn("Connection not Established");
        sof("Disconnected", "Connection not Established", false);
    }




    public static void main(String[] args) {
     launch(args);
    }

}




/*****


,------.                        ,--.                        ,--.
|  .-.  \  ,---.,--.  ,--.,---. |  | ,---.  ,---.  ,---.  ,-|  |
|  |  \  :| .-. :\  `'  /| .-. :|  || .-. || .-. || .-. :' .-. |
|  '--'  /\   --. \    / \   --.|  |' '-' '| '-' '\   --.\ `-' |
`-------'  `----'  `--'   `----'`--' `---' |  |-'  `----' `---'
                                           `--'
,--.
|  |-.,--. ,--.
| .-. '\  '  /
| `-' | \   '
 `---'.-'  /
      `---'

,------.          ,--.          ,--.  ,--.
|  .--. ',--,--.  `--' ,--,--.,-'  '-.|  ,---.
|  '--'.' ,-.  |  ,--.' ,-.  |'-.  .-'|  .-.  |
|  |\  \\ '-'  |  |  |\ '-'  |  |  |  |  | |  |
`--' '--'`--`--'.-'  / `--`--'  `--'  `--' `--'
                '---'
 *****/