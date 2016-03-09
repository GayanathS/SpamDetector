package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.io.*;

import java.text.DecimalFormat;
import java.util.*;

import javafx.scene.control.TableView;
import java.util.Map;
import java.util.Scanner;

public class Main extends Application {

    private BorderPane layout;
    private Stage window;
    private TableView table;

    @Override
    public void start(Stage primaryStage) throws Exception {
        DecimalFormat df = new DecimalFormat("0.0000000");


        Map<String, Integer> trainfrequency = new TreeMap<>();
        int hamcount = 0;
        for (File fileList : new File("./data/train/ham").listFiles()) {
            hamcount++;
        }

        ReadFromFile hamtrain = new ReadFromFile
                ("./data/train/ham", trainfrequency);
        trainfrequency = hamtrain.read(new File("./data/train/ham"), trainfrequency);

        int spamcount = 0;
        for (File fileList : new File("./data/train/spam").listFiles()) {
            spamcount++;
        }

        Map<String, Integer> spamFrequency = new TreeMap<>();
        ReadFromFile spamtrain = new ReadFromFile
                (("./data/train/spam"), spamFrequency);
        spamFrequency = spamtrain.read(new File("./data/train/spam"), spamFrequency);

        // chances of the word
        Map hamFolder = hamtrain.getchances(trainfrequency, hamcount);

        Map spamFolder = spamtrain.getchances(spamFrequency, spamcount);

        Chances spam = new Chances(hamFolder, spamFolder);

        Map spamwordcount = spam.spamChance(hamFolder, spamFolder);

        // setting the directory to be chosen and testing
        DirectoryChooser Chooser = new DirectoryChooser();
        Chooser.setInitialDirectory(new File("./data"));
        File main = Chooser.showDialog(primaryStage);


        test file = new test(spamwordcount);
        double chanceSpam;

        table = new TableView<>();
        TableColumn<TestData, String> dataColumn = null;
        dataColumn = new TableColumn<>("File");
        dataColumn.setMinWidth(100);
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("Filename"));

        TableColumn<TestData, String> classColumn = null;
        classColumn = new TableColumn<>("Class name");
        classColumn.setMinWidth(100);
        classColumn.setCellValueFactory(new PropertyValueFactory<>("Classname"));


        TableColumn<TestData, String> chanceColumn = null;
        chanceColumn = new TableColumn<>("Spam probability");
        chanceColumn.setMinWidth(200);

        chanceColumn.setCellValueFactory(new PropertyValueFactory<>("spamProbRounded"));

        table.getColumns().add(dataColumn);
        table.getColumns().add(classColumn);
        table.getColumns().add(chanceColumn);

        ObservableList<TestData> testFiles = FXCollections.observableArrayList();


        int count = 0;
        int r = 0;
        double num = 0;
        double[] values;

        // checks whether the is chosen file is in the directory, and iterates through and checks if its spam or ham
        for (File fileList : main.listFiles()) {
            if (fileList.isDirectory()) {
                for (File subFile : fileList.listFiles()) {
                    chanceSpam = file.checkfile(subFile, spamwordcount);
                    TestData testFile = new TestData(subFile.getName(), chanceSpam,
                            fileList.getName());
                    testFiles.add(testFile);
                    table.getItems().add(testFile);

                    if (chanceSpam < .5 && fileList.getName().equalsIgnoreCase("ham") ||
                            chanceSpam > .5 && fileList.getName().equalsIgnoreCase("spam")) {
                        r++;
                    }
                    num = num + chanceSpam;
                    count++;
                }
            }

        }
        values = new double[count];
        double mean = num / count;
        double difference = 0;
        int index = 0;
        for (File fileList : main.listFiles()) {
            if (fileList.isDirectory()) {
                for (File subFile : fileList.listFiles()) {
                    chanceSpam = file.checkfile(subFile, spamwordcount);
                    values[index] = chanceSpam;
                    difference = difference + Math.abs(mean - values[index]);
                    index++;
                }
            }
        }
        double precision = 1 - (difference / (count));
        primaryStage.setTitle("Spam Detector 3000");

        GridPane stats = new GridPane();
        double accuracy = (double) r / count;
        Label accuracyL = new Label("Accuracy: " +
                df.format(accuracy));
        stats.add(accuracyL, 0, 0);

        Label precisionL = new Label("Precision: " +
                df.format(precision));
        stats.add(precisionL, 0, 1);

        layout = new BorderPane();
        layout.setCenter(table);
        layout.setBottom(stats);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }

    public class TestData {

        private String file;
        private double spamChance;
        private String classname;


        public TestData(String file, double spamchance, String classname) {
            this.file = file;
            this.spamChance = spamchance;
            this.classname = classname;
        }


        public String getFilename() {
            return this.file;
        }

        public double getSpamchance() {
            return this.spamChance;
        }

        public String getSpamProbRounded() {
            DecimalFormat df = new DecimalFormat("0.0000000");
            return df.format(this.spamChance);
        }

        public String getClassname() {
            return this.classname;
        }

        public void setFile(String value) {
            this.file = value;
        }

        public void setSpamchance(double val) {
            this.spamChance = val;
        }

        public void setClassname(String value) {
            this.classname = value;
        }
    }

    public class test {
        private Map<String, Double> spamwordcount;

        public test(Map<String, Double> spamwordcount) {
            this.spamwordcount = spamwordcount;
        }

        public double checkfile(File file, Map<String, Double> spamwordcount) {
            double count = 0;
            try {
                String word;
                Scanner scan = new Scanner(file);
                while (scan.hasNext()) {  // scans every word in file
                    word = scan.next();
                    if (checkWord(word)) {
                        if (spamwordcount.containsKey(word)) { // if the word is in the spam increases count
                            count = count + (Math.log(1 -
                                    spamwordcount.get(word) -
                                    Math.log(spamwordcount.get(word))));
                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            double spamchance = 1 /
                    (1 + Math.pow(Math.E, count));//calculate the chances of the spam in the file
            return spamchance;
        }

        private boolean checkWord(String s) {   // checks whether or not its a word
            String arrangement = "^[a-zA-Z]*$";
            if (s.matches(arrangement)) {
                return true;
            }
            return false;
        }

    }
}
















