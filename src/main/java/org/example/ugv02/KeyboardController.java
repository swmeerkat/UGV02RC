package org.example.ugv02;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.example.ugv02.clients.UGV02Client;
import org.example.ugv02.clients.MovingDirection;

/*
 Apple key codes:
  - command: 157
  - option: 18
  - cursor up: 38
  - cursor down: 40
  - cursor right: 39
  - cursor left: 37
  - numpad '0': 96
 */
@Slf4j
public class KeyboardController {

  private final UGV02Client ugv;
  private boolean optionKeyPressed = false;

  public KeyboardController(UGV02Client ugv) {
    this.ugv = ugv;
  }

  public void keyPressed(KeyEvent e) {
    switch (e.getCode()) {
      case KeyCode.SHIFT -> optionKeyPressed = true;
      case KeyCode.H -> {
        if (optionKeyPressed) {
          ugv.gimbal_step(-100, 0);
        } else {
          ugv.cmd_speed_control(MovingDirection.WEST);
        }
      }
      case KeyCode.J -> {
        if (optionKeyPressed) {
          ugv.gimbal_step(0, -100);
        } else {
          ugv.cmd_speed_control(MovingDirection.NORTH);
        }
      }
      case KeyCode.K -> {
        if (optionKeyPressed) {
          ugv.gimbal_step(0, 100);
        } else {
          ugv.cmd_speed_control(MovingDirection.SOUTH);
        }
      }
      case KeyCode.L -> {
        if (optionKeyPressed) {
          ugv.gimbal_step(100, 0);
        } else {
          ugv.cmd_speed_control(MovingDirection.EAST);
        }
      }
      case KeyCode.SPACE -> ugv.cmd_speed_control(MovingDirection.STOP);
      default -> log.info("unexpected key pressed: char={} code={}, ignored",
          e.getText(), e.getCode());
    }
  }

  public void keyReleased(KeyEvent e) {
    if (e.getCode() == KeyCode.SHIFT) {
      optionKeyPressed = false;
    } else {
      ugv.cmd_speed_control(MovingDirection.STOP);
    }
  }
}
