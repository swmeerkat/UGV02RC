module org.example.ugv02 {
  requires javafx.controls;
  requires javafx.fxml;
  requires lombok;
  requires org.apache.httpcomponents.core5.httpcore5;
  requires org.apache.httpcomponents.client5.httpclient5;
  requires org.slf4j;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;

  opens org.example.ugv02 to javafx.fxml;
  exports org.example.ugv02;
  exports org.example.ugv02.clients;
}