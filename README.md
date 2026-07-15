# UGV02RC
## Getting Started
UGV02RC is a JavaFX based application that provides a simple remote control of a Waveshare 
UGV02 from a host computer via WLAN. It uses a web server running on a Jetson Orin Nano Super 
module mounted on the UGV. This web server controls all other functions of the UGV partly via 
the integrated ESP32 controller of the UGV. 

* Host computer and UGV must be in the same WLAN.
* The IP address of the UGV is configured in application.properties.

## References
* https://www.waveshare.com/wiki/UGV02
* https://www.waveshare.com/wiki/General_Driver_for_Robots
* https://www.waveshare.com/wiki/2-Axis_Pan-Tilt_Camera_Module