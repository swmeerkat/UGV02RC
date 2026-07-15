package org.example.ugv02.clients;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

/*
 * References:
 *  - https://www.waveshare.com/wiki/2-Axis_Pan-Tilt_Camera_Module
 */
public class UGV02Client {

  private static final String FEEDBACK_PATH = "/ugv02/feedback";
  private static final String CMD_PATH = "/ugv02/cmd";
  private static final String GIMBAL_STEP_PATH = "/gimbal/step";
  private static final String GIMBAL_MIDDLE_POS_PATH = "/gimbal/middle_position";
  private static final String GIMBAL_CAMERA_PATH = "/gimbal/camera";

  @Getter
  private final int DEFAULT_SPEED = 600;
  private final JetsonOrinNanoClient jetson;
  @Getter
  private int speedLevel;
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

  public void setSpeedLevel(int speedLevel) {
    if (speedLevel < 0) {
      speedLevel = 0;
    }
    this.speedLevel = speedLevel;
  }

  /*
   * CMD_FEEDBACK
   * * Output:
   *  - M1: speed of the left front wheel
   *  - M2: speed of the right front wheel
   *  - M3: speed of the right rear wheel
   *  - M4: speed of the left rear wheel
   *  - odl: mileage of the left wheel in cm after last start of the chassis
   *  - odr: mileage of the right wheel in cm after the last start of the chassis
   *  - v: voltage in mV
   */
  public JsonNode get_feedback() {
    return jetson.get(FEEDBACK_PATH);
  }

  /*
   * CMD_SPEED_CTRL
   * Input:
   *  - L, R: speed of the wheel, value range 0 - 1800 rpm
   */
  public void cmd_speed_control(MovingDirection direction) {
    int frontLeft = 0;     // M1
    int frontRight = 0;    // M2
    int rearRight = 0;     // M3
    int rearLeft = 0;      // M4
    int reducedSpeed = speedLevel / 2;
    switch (direction) {
      case NORTH -> {
        frontLeft = speedLevel;
        frontRight = speedLevel;
        rearRight = speedLevel;
        rearLeft = speedLevel;
      }
      case NORTHEAST -> {
        frontLeft = speedLevel;
        frontRight = reducedSpeed;
        rearRight = reducedSpeed;
        rearLeft = speedLevel;
      }
      case EAST -> {
        frontLeft = speedLevel;
        frontRight = -speedLevel;
        rearRight = -speedLevel;
        rearLeft = speedLevel;
      }
      case SOUTHEAST -> {
        frontLeft = -speedLevel;
        frontRight = -reducedSpeed;
        rearRight = -reducedSpeed;
        rearLeft = -speedLevel;
      }
      case SOUTH -> {
        frontLeft = -speedLevel;
        frontRight = -speedLevel;
        rearRight = -speedLevel;
        rearLeft = -speedLevel;
      }
      case SOUTHWEST -> {
        frontLeft = -reducedSpeed;
        frontRight = -speedLevel;
        rearRight = -speedLevel;
        rearLeft = -reducedSpeed;
      }
      case WEST -> {
        frontLeft = -speedLevel;
        frontRight = speedLevel;
        rearRight = speedLevel;
        rearLeft = -speedLevel;
      }
      case NORTHWEST -> {
        frontLeft = reducedSpeed;
        frontRight = speedLevel;
        rearRight = speedLevel;
        rearLeft = reducedSpeed;
      }
      case STOP -> {
      }
    }
    String cmd = "{\"T\":11,\"M1\":" + frontLeft + ",\"M2\":" + frontRight + ",\"M3\":" + rearRight + ",\"M4\":" + rearLeft + "}";
    jetson.post(CMD_PATH, cmd);
  }

  public void gimbal_middle_pos() {
    jetson.post(GIMBAL_MIDDLE_POS_PATH, "{}");
  }

  /*
   * delta_pan: -100 -> left, 0 -> no step, 100 -> right
   * delta_tilt: -100 -> up, 0 -> no step, 100 -> up
   */
  public void gimbal_step(int delta_pan, int delta_tilt) {
    String cmd = "{\"pan\":" + delta_pan + ",\"tilt\":" + delta_tilt + "}";
    jetson.post(GIMBAL_STEP_PATH, cmd);
  }

  public void switch_gimbal_camera(boolean camera_on) {
    if (camera_on) {
      gimbal_cam_pid = jetson.post(GIMBAL_CAMERA_PATH + "/on", "{}").toString();
    } else {
      jetson.post(GIMBAL_CAMERA_PATH + "/off", gimbal_cam_pid);
    }
  }
}
