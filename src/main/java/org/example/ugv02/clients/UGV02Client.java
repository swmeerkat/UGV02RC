package org.example.ugv02.clients;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

/*
 * References:
 *  - https://www.waveshare.com/wiki/2-Axis_Pan-Tilt_Camera_Module
 */
public class UGV02Client {

    private static final String CMD_PATH = "/ugv02/cmd";
    private static final String UPS_PATH = "/ups/status";
    private static final String ENV_PATH = "/env/status";
    private static final String GIMBAL_CAMERA_PATH = "/gimbal/camera";

    @Getter
    private final double DEFAULT_SPEED = 0.25;
    private final JetsonOrinNanoClient jetson;
    @Getter
    private double speedLevel;
    @Setter
    @Getter
    private int actPan;
    @Setter
    @Getter
    private int actTilt;
    private String gimbal_cam_pid = null;


    public UGV02Client() {
        this.jetson = new JetsonOrinNanoClient();
        this.speedLevel = getDEFAULT_SPEED();
        this.actPan = 0;
        this.actTilt = 0;
    }

    public void setSpeedLevel(double speedLevel) {
        if (speedLevel < 0) {
            speedLevel = 0;
        }
        if (speedLevel > 0.5) {
            speedLevel = 0.5;
        }
        this.speedLevel = speedLevel;
    }

    /*
     * CMD_FEEDBACK
     * * Output:
     *  - L: speed of the left wheels
     *  - R: speed of the right wheels
     *  - r: roll
     *  - p: pitch
     *  - y: yaw
     *  - v: voltage in V
     */
    public JsonNode get_feedback() {
        return jetson.post(CMD_PATH, "{\"T\":130}");
    }

    public JsonNode get_environment() {
        return jetson.get(ENV_PATH);
    }

    /*
     * CMD_SPEED_CTRL
     * Input:
     *  - L, R: speed of the wheel, value range 0 - 1800 rpm
     */
    public void cmd_speed_control(MovingDirection direction) {
        double left = 0;
        double right = 0;
        double reducedSpeed = speedLevel / 4;
        switch (direction) {
            case NORTH -> {
                left = speedLevel;
                right = speedLevel;
            }
            case NORTHEAST -> {
                left = speedLevel;
                right = reducedSpeed;
            }
            case EAST -> {
                left = speedLevel;
                right = -speedLevel;
            }
            case SOUTHEAST -> {
                left = -speedLevel;
                right = -reducedSpeed;
            }
            case SOUTH -> {
                left = -speedLevel;
                right = -speedLevel;
            }
            case SOUTHWEST -> {
                left = -reducedSpeed;
                right = -speedLevel;
            }
            case WEST -> {
                left = -speedLevel;
                right = speedLevel;
            }
            case NORTHWEST -> {
                left = reducedSpeed;
                right = speedLevel;
            }
            case STOP -> {
            }
        }
        String cmd = "{\"T\":1,\"L\":" + left + ",\"R\":" + right + "}";
        jetson.post(CMD_PATH, cmd);
    }

    public void gimbal_middle_pos() {
        jetson.post(CMD_PATH, "{\"T\":134,\"X\":0,\"Y\":0,\"SX\":500,\"SY\":500}");
        actPan = 0;
        actTilt = 0;
    }

    /*
     * delta_pan: -1 -> left, 0 -> no step, 1 -> right
     * delta_tilt: -1 -> up, 0 -> no step, 1 -> up
     */
    public void gimbal_step(int delta_pan, int delta_tilt) {
        int new_pan = actPan + delta_pan;
        // 100 degrees left and right due to modules behind the camera
        if (new_pan < -100) {
            new_pan = -100;
        } else if (new_pan > 100) {
            new_pan = 100;
        }
        int new_tilt = actTilt + delta_tilt;
        if (new_tilt < -30) {
            new_tilt = -30;
        } else if (new_tilt > 90) {
            new_tilt = 90;
        }
        jetson.post(CMD_PATH, "{\"T\":134,\"X\":" + new_pan + ",\"Y\":" + new_tilt + ",\"SX\":500,\"SY\":500}");
        actPan = new_pan;
        actTilt = new_tilt;
    }

    public void switch_gimbal_camera(boolean camera_on) {
        if (camera_on) {
            gimbal_cam_pid = jetson.post(GIMBAL_CAMERA_PATH + "/on", "{}").toString();
        } else {
            jetson.post(GIMBAL_CAMERA_PATH + "/off", gimbal_cam_pid);
        }
    }

    /*
     * UGV02 IO4, IO5 command: {"T": 132, "IO4":255, "IO5":255}"
     * on -> 255, off -> 0
     * IO4: chassis light
     */
    public void switch_chassis_light(boolean chassis_on) {
        String cmd;
        if (chassis_on) {
            cmd = "{\"T\":132,\"IO4\":255,\"IO5\":0}";
        } else {
            cmd = "{\"T\":132,\"IO4\":0,\"IO5\":0}";
        }
        jetson.post(CMD_PATH, cmd);
    }

    public JsonNode get_ups_status() {
        return jetson.get(UPS_PATH);
    }
}
