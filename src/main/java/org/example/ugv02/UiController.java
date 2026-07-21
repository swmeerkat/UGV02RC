package org.example.ugv02;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.ugv02.clients.MovingDirection;
import org.example.ugv02.clients.UGV02Client;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class UiController {

    @FXML
    public Slider chassis_speed;
    @FXML
    public RadioButton gimbal_cam;
    @FXML
    public TextArea console;

    @Setter
    private Stage stage;

    private UGV02Client ugv02Client;
    private KeyboardController keyboardController;
    private Timer gimbalTimer;
    private Timer chassisTimer;


    @FXML
    public void initialize() {
        this.ugv02Client = new UGV02Client();
        keyboardController = new KeyboardController(ugv02Client);
        chassis_speed.valueProperty().addListener(
                (_, _, newValue) ->
                        ugv02Client.setSpeedLevel(newValue.doubleValue()));
        Platform.runLater(() -> stage.setOnCloseRequest(_ -> exitApplication()));
        ugv02Client.gimbal_middle_pos();
        log.info("UGV02 RC initialized");
    }

    @FXML
    public void getFeedback() {
        JsonNode result = ugv02Client.get_feedback();
        console.appendText(result + "\n");
    }

    // gimbal upper left button
    @FXML
    public void gul_pressed() {
        repeat_gimbal_cmd(-1, 1);
    }

    // gimbal upper middle button
    @FXML
    public void gum_pressed() {
        repeat_gimbal_cmd(0, 1);
    }

    // gimbal upper right button
    @FXML
    public void gur_pressed() {
        repeat_gimbal_cmd(1, 1);
    }

    // gimbal middle left button
    @FXML
    public void gml_pressed() {
        repeat_gimbal_cmd(-1, 0);
    }

    // gimbal middle middle button
    @FXML
    public void gmm_pressed() {
        ugv02Client.gimbal_middle_pos();
    }

    // gimbal middle right button
    @FXML
    public void gmr_pressed() {
        repeat_gimbal_cmd(1, 0);
    }

    // gimbal bottom left button
    @FXML
    public void gbl_pressed() {
        repeat_gimbal_cmd(-1, -1);
    }

    // gimbal bottom middle button
    @FXML
    public void gbm_pressed() {
        repeat_gimbal_cmd(0, -1);
    }

    // gimbal bottom right button
    @FXML
    public void gbr_pressed() {
        repeat_gimbal_cmd(1, -1);
    }

    // chassis upper left button
    @FXML
    public void cul_pressed() {
        repeat_chassis_cmd(MovingDirection.NORTHWEST);
    }

    // chassis upper middle button
    @FXML
    public void cum_pressed() {
        repeat_chassis_cmd(MovingDirection.NORTH);
    }

    // chassis upper right button
    @FXML
    public void cur_pressed() {
        repeat_chassis_cmd(MovingDirection.NORTHEAST);
    }

    // chassis middle left button
    @FXML
    public void cml_pressed() {
        repeat_chassis_cmd(MovingDirection.WEST);
    }

    // chassis middle middle button
    @FXML
    public void cmm_pressed() {
        ugv02Client.cmd_speed_control(MovingDirection.STOP);
    }

    // chassis middle right button
    @FXML
    public void cmr_pressed() {
        repeat_chassis_cmd(MovingDirection.EAST);
    }

    // chassis bottom left button
    @FXML
    public void cbl_pressed() {
        repeat_chassis_cmd(MovingDirection.SOUTHWEST);
    }

    // chassis bottom middle button
    @FXML
    public void cbm_pressed() {
        repeat_chassis_cmd(MovingDirection.SOUTH);
    }

    // chassis bottom right button
    @FXML
    public void cbr_pressed() {
        repeat_chassis_cmd(MovingDirection.SOUTHEAST);
    }

    @FXML
    public void keyPressed(KeyEvent event) {
        keyboardController.keyPressed(event);
    }

    @FXML
    public void keyReleased(KeyEvent event) {
        keyboardController.keyReleased(event);
    }

    @FXML
    public void enterKeyboardControl() {
        // focus on button is sufficient
    }

    @FXML
    public void gimbal_camera_switched() {
        ugv02Client.switch_gimbal_camera(gimbal_cam.isSelected());
    }

    private void repeat_gimbal_cmd(int delta_pan, int delta_tilt) {
        if (gimbalTimer != null) {
            gimbalTimer.cancel();
        }
        gimbalTimer = new Timer();
        gimbalTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ugv02Client.gimbal_step(delta_pan, delta_tilt);
            }
        }, 0, 20);
    }

    @FXML
    public void gimbal_released() {
        if (gimbalTimer != null) {
            gimbalTimer.cancel();
        }
    }

    private void repeat_chassis_cmd(MovingDirection direction) {
        if (chassisTimer != null) {
            chassisTimer.cancel();
        }
        ugv02Client.cmd_speed_control(direction);
        chassisTimer = new Timer();
        chassisTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ugv02Client.cmd_speed_control(direction);
            }
        }, 0, 500);
    }

    @FXML
    public void chassis_released() {
        if (chassisTimer != null) {
            chassisTimer.cancel();
        }
        ugv02Client.cmd_speed_control(MovingDirection.STOP);
    }

    private void exitApplication() {
        chassis_released();
        gimbal_released();
    }
}
