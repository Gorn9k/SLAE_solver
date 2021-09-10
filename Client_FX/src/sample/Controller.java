package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.fxml.FXML;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.*;

public class Controller {

    private TextField[][] textFields;

    @FXML
    private TextArea res_1, result_time; @FXML private Label label_razmer; @FXML private Button solve; @FXML private TextField tf;
    @FXML private GridPane gridPane; @FXML private Label label_3; @FXML private Label head; @FXML private Label er1;
    private static boolean err1, s_err1, neshod_err; @FXML private Label server_error1; @FXML private Button clear1;
    @FXML private Slider slider; @FXML Button rand, label_comparison; @FXML private RadioButton rb_LU, rb_G, rb_S;
    @FXML Button next; @FXML Label label_znak, label_e; @FXML TextField e; @FXML ToggleGroup tg; @FXML MenuButton menu;
    @FXML MenuItem m1,m2,m3,m4; @FXML Label label_neshod1, label_neshod2;

    private int razmer; static String zagolovok; final static double width = 255; final static double iks = 214;
    private int colvo_znakov_posle_zap9toy; private String choosedMethod;

    @FXML
    private void open_head(ActionEvent event) {
        head.setText("Пожалуйста, выберите метод, которым вы хотите решить СЛАУ");
        zagolovok = head.getText();
        setVisible_menu_1(true);
        setVisible_menu_2(false);
        setVisible_menu_3(false);
        setVisible_menu_4(false);
    }

    @FXML
    private void open_pullMatrix(ActionEvent event) {
        head.setText("Введите исходные данные");
        zagolovok = head.getText();
        setVisible_menu_1(false);
        setVisible_menu_2(true);
        setVisible_menu_3(false);
        setVisible_menu_4(false);
    }

    @FXML
    private void open_result(ActionEvent event) {
        head.setText("Результат решения СЛАУ выбранным методом");
        zagolovok = head.getText();
        setVisible_menu_1(false);
        setVisible_menu_2(false);
        setVisible_menu_3(true);
        setVisible_menu_4(false);
    }

    @FXML
    private void open_compare(ActionEvent event) {
        head.setText("Сравнение методов");
        zagolovok = head.getText();
        setVisible_menu_1(false);
        setVisible_menu_2(false);
        setVisible_menu_3(false);
        setVisible_menu_4(true);
    }

    @FXML
    private void create_SLAY(ActionEvent event) {
        try {
            razmer = Integer.parseInt(tf.getText());
            if (razmer <= 0){
                er1.setVisible(true);
                err1 = true;
                return;
            }
        } catch (Throwable e){
            er1.setVisible(true);
            err1 = true;
            return;
        }
        if (razmer > 10) {
            return;
        }
        er1.setVisible(false);
        err1 = false;
        if (textFields != null) {
            for (TextField[] t : textFields) {
                for (TextField t1 : t) {
                    t1.setVisible(false);
                }
            }
        }
        res_1.setText("");
        int n = 0;
        textFields = new TextField[razmer][razmer + 1];
        for (int i = 0; i < razmer; i++) {
            for (int j = 0; j < razmer + 1; j++) {
                if (n < razmer + 1) {
                    gridPane.getColumnConstraints().add(new ColumnConstraints(55));
                    if (n < razmer) {
                        gridPane.getRowConstraints().add(new RowConstraints(40));
                    }
                    n++;
                }
                textFields[i][j] = new TextField();
                if (j == razmer) textFields[i][j].setPromptText("=");
                else textFields[i][j].setPromptText("x" + (j+1));
                textFields[i][j].setAlignment(Pos.CENTER);
                gridPane.add(textFields[i][j], j, i);
            }
        }
        if (razmer > 7) gridPane.setLayoutX((308 - 22 * razmer) - 4*razmer);
        else gridPane.setLayoutX((313 - 21 * razmer) - 5*razmer);
    }

    @FXML private void randPull(ActionEvent event) {
        if (textFields != null) {
            double scale = Math.pow(10, 2);
            for (int i = 0; i < razmer; i++) {
                textFields[i][i].setText(String.valueOf(Math.ceil(((Math.random() * 1999) - 999) * scale) / scale));
            }
            for (int i = 0; i < razmer; i++) {
                for (int j = 0; j < razmer; j++) {
                    if(!(i==j))
                        textFields[i][j].setText(String.valueOf(Math.ceil(((Math.random() *
                                Double.parseDouble(textFields[i][i].getText())/2+1) -
                                Double.parseDouble(textFields[i][i].getText())/4) * scale) / scale));
                }
            }
            for (int i = 0; i < razmer; i++) {
                textFields[i][razmer].setText(String.valueOf(Math.ceil(((Math.random() * 1999) - 999) * scale) / scale));
            }
        }
    }

    @FXML private void comparison(ActionEvent event) {
        if(s_err1) {
            server_error1.setVisible(false);
            s_err1 = false;
        }
        if(neshod_err) {
            label_neshod1.setVisible(false);
            label_neshod2.setVisible(false);
            neshod_err = false;
        }
        Client.line.put("id", "comparison");
        try {
            new Client().client_go();
            if (Client.line.get("time_result").equals("Error_NanOrInfinity")) {
                neshod_err = true;
                label_neshod1.setVisible(true);
                label_neshod2.setVisible(true);
                result_time.setText("");
            } else {
                String line = "Результат определения скорости каждого метода(за какое кол-во времени каждый метод решил данную СЛАУ):\n\n";
                JSONArray jsonArray = (JSONArray) Client.line.get("time_result");
                line += "Решение данной СЛАУ LU методом заняло ";
                line += jsonArray.get(0) + " мс;\n";
                line += "Решение данной СЛАУ методом Гаусса заняло ";
                line += jsonArray.get(1) + " мс;\n";
                line += "Решение данной СЛАУ методом Зейделя(метод Гаусса-Зейделя) заняло ";
                line += jsonArray.get(2) + " мс.\n\n";
                double min = jsonArray.getDouble(0);
                int index = 0;
                for (int i = 1; i < 3; i++) {
                    if (jsonArray.getDouble(i) < min) {
                        min = jsonArray.getDouble(i);
                        index = i;
                    }
                }
                if (index == 0)
                    line += "Вывод: решение данной СЛАУ LU методом было найдено за наименьшее кол-во времени (" + min +
                            " мс), а значит, этот метод оказался наиболее быстрым и эффективным для данной СЛАУ.";
                else if (index == 1)
                    line += "Вывод: решение данной СЛАУ методом Гаусса было найдено за наименьшее кол-во времени (" + min +
                            " мс), а значит, этот метод оказался наиболее быстрым и эффективным для данной СЛАУ.";
                else
                    line += "Вывод: решение данной СЛАУ методом Зейделя(метод Гаусса-Зейделя) было найдено за наименьшее " +
                            "кол-во времени (" + min + " мс), а значит, этот метод оказался наиболее быстрым и эффективным для данной СЛАУ.";
                result_time.setText(line);
            }
        }
        catch (Throwable e){
            s_err1 = true;
            server_error1.setVisible(true);
        }
    }

    @FXML private void chooseMethod(ActionEvent event){
        if(tg.getSelectedToggle()!=null) {
            RadioButton radioButton = (RadioButton) tg.getSelectedToggle();
            choosedMethod = radioButton.getText();
            m2.setVisible(true);
            m1.setVisible(true);
            open_pullMatrix(null);
        }
    }

    private void setVisible_menu_4(Boolean bool) {
        label_comparison.setVisible(bool);
        result_time.setVisible(bool);
    }

    private void setVisible_menu_3(Boolean bool) {
        res_1.setVisible(bool);
    }

    private void setVisible_menu_2(Boolean bool) {
        if(err1){
            er1.setVisible(bool);
        }
        if(s_err1){
            server_error1.setVisible(bool);
        }
        if(choosedMethod.equals("Метод Зейделя (метод Гаусса-Зейделя)")){
            if(neshod_err) {
                label_neshod1.setVisible(bool);
                label_neshod2.setVisible(bool);
            }
            label_e.setVisible(bool);
            e.setVisible(bool);
        }
        slider.setVisible(bool);
        label_znak.setVisible(bool);
        rand.setVisible(bool);
        solve.setVisible(bool);
        clear1.setVisible(bool);
        label_3.setVisible(bool);
        label_razmer.setVisible(bool);
        tf.setVisible(bool);
        if (textFields != null) {
            for (TextField[] t : textFields) {
                for (TextField t1 : t) {
                    t1.setVisible(bool);
                }
            }
        }
    }

    private void setVisible_menu_1(Boolean bool) {
        rb_LU.setVisible(bool);
        rb_G.setVisible(bool);
        rb_S.setVisible(bool);
        next.setVisible(bool);
    }

    @FXML
    private void solve(ActionEvent event) {
        if(err1) {
        er1.setVisible(false);
        err1 = false;
        }
        if(s_err1) {
            server_error1.setVisible(false);
            s_err1 = false;
        }
        if(neshod_err) {
            label_neshod1.setVisible(false);
            label_neshod2.setVisible(false);
            neshod_err = false;
        }
        Client.line = new JSONObject();
        Client.line.put("id", choosedMethod);
        Client.line.put("razmer", razmer);
        if(choosedMethod.equals("Метод Зейделя (метод Гаусса-Зейделя)")){
            if(new Expression(e.getText()).calculate() > 0 && new Expression(e.getText()).calculate() < 1)
                Client.line.put("eps", e.getText());
        }
        double[][] masA = new double[razmer][razmer];
        double[] masB = new double[razmer];
        for (int i = 0; i < razmer; i++) {
            for (int j = 0; j < razmer; j++) {
                masA[i][j] = new Expression(textFields[i][j].getText()).calculate();
            }
            masB[i] = new Expression(textFields[i][razmer].getText()).calculate();
        }
        Client.line.put("masA", masA);
        Client.line.put("masB", masB);
        try {
            new Client().client_go();
            if (Client.line.get("result").equals("Server_error")) {
                s_err1 = true;
                server_error1.setVisible(true);
            } else if (Client.line.get("result").equals("Error")) {
                err1 = true;
                er1.setVisible(true);
            } else if (choosedMethod.equals("Метод Зейделя (метод Гаусса-Зейделя)") &&
                    Client.line.get("result").equals("Error_NanOrInfinity")) {
                neshod_err = true;
                label_neshod1.setVisible(true);
                label_neshod2.setVisible(true);
            } else if (textFields != null) {
                JSONArray jsonArray = (JSONArray) Client.line.get("result");
                colvo_znakov_posle_zap9toy = (int) slider.getValue();
                setName_result();
                push_on_result(jsonArray, res_1);
                m3.setVisible(true);
                m4.setVisible(true);
                open_result(null);
            } else {
                err1 = true;
                er1.setVisible(true);
            }
        }
        catch (Throwable e){
            s_err1 = true;
            server_error1.setVisible(true);
        }
    }

    private void setName_result(){
        if(choosedMethod.equals("LU метод")){
            res_1.setText("Результат решения СЛАУ LU методом:\n");
        } else if(choosedMethod.equals("Метод Гаусса")){
            res_1.setText("Результат решения СЛАУ методом Гаусса:\n");
        } else {
            res_1.setText("Результат решения СЛАУ методом Зейделя(метод Гаусса-Зейделя):\n");
        }
    }

    private void push_on_result(JSONArray jsonArray, TextArea textArea){
        String line = res_1.getText();
        for (int i = 0; i < razmer; i++) {
            String result = String.format("%." + colvo_znakov_posle_zap9toy + "f", Double.parseDouble(jsonArray.get(i).toString()));
            int point_index = result.indexOf(",")+1;
            int number_of_zeros = 0;
            for (int j = point_index; j < result.length(); j++) {
                if(Character.getNumericValue(result.charAt(j)) == 0){
                    number_of_zeros++;
                } else number_of_zeros = 0;
            }
            if (i == razmer - 1) {
                line += String.format("x" + (i + 1) + " = " + "%." + (colvo_znakov_posle_zap9toy - number_of_zeros) + "f.\n", Double.parseDouble(jsonArray.get(i).toString()));
            }
            else {
                line += String.format("x" + (i + 1) + " = " + "%." + (colvo_znakov_posle_zap9toy - number_of_zeros) + "f;\n", Double.parseDouble(jsonArray.get(i).toString()));
            }
        }
        textArea.setText(line);
    }

    @FXML
    private void clearing1(){
        if (textFields != null) {
            for (TextField[] t : textFields) {
                for (TextField t1 : t) {
                    t1.setText("");
                }
            }
        }
        if (err1){
            er1.setVisible(false);
            err1 = false;
        }
        if (s_err1){
            server_error1.setVisible(false);
            s_err1 = false;
        }
        if(choosedMethod.equals("Метод Зейделя (метод Гаусса-Зейделя)")) {
            if (neshod_err) {
                label_neshod1.setVisible(false);
                label_neshod2.setVisible(false);
                neshod_err = false;
            }
            e.setText("");
        }
        res_1.setText("");
        result_time.setText("");
        m3.setVisible(false);
        m4.setVisible(false);
    }
}
