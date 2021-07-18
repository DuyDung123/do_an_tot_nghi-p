#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <DHT.h>

#define DHTPIN 21
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);


TaskHandle_t Task1;
TaskHandle_t Task2;


const char* ssid = "Duys";
const char* password = "12345678";



void readTempAndHum( void * pvParameters ){
  Serial.println(xPortGetCoreID());
  String d ="5000";
  
  int deylay = d.toInt();
  Serial.println(deylay);

  for(;;){
      if (WiFi.status() == WL_CONNECTED)
      {
        HTTPClient http;
        Serial.println("---------");
        delay(deylay);

        float h = dht.readHumidity();
        float t = dht.readTemperature();
        float f = dht.readTemperature(true);

        if (isnan(h) || isnan(t) || isnan(f)) {
          Serial.println(F("Failed to read from DHT sensor!"));
          return;
        }

      float hif = dht.computeHeatIndex(f, h);
      float hic = dht.computeHeatIndex(t, h, false);

      String temp = String(t);
      String hum = String(h);


      String url = "http://34.134.46.182/webcontroldevice/api-admin-status?codedevice=nhiet-%C4%91o-dht11&temp="+temp+"&hum="+hum+"";
      http.begin(url);

      int httpCode = http.GET();
      if (httpCode > 0)
      {
        const size_t bufferSize = JSON_OBJECT_SIZE(2) + JSON_OBJECT_SIZE(3) + JSON_OBJECT_SIZE(5) + JSON_OBJECT_SIZE(8) + 370;
        DynamicJsonBuffer jsonBuffer(bufferSize);
        String payload = http.getString();
        Serial.println(payload);
        Serial.println("x");
      }
      http.end();
    }
  } 
}

void setup() {
  //Serial.begin(9600);
  Serial.begin(115200);
  Serial.println(F("DHTxx test!"));
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi..");
  }
   Serial.println(WiFi.localIP());

  dht.begin();
  xTaskCreatePinnedToCore(
                    readTempAndHum,   /* Task function. */
                    "Task1",     /* name of task. */
                    10000,       /* Stack size of task */
                    NULL,        /* parameter of the task */
                    2,           /* priority of the task */
                    &Task1,      /* Task handle to keep track of created task */
                    1);          /* pin task to core 1 */
    delay(500);
}

void loop() {
//  if (WiFi.status() == WL_CONNECTED)
//      {
//        HTTPClient http;
//        Serial.println("---------");
//        delay(5000);
//
//        float h = dht.readHumidity();
//        float t = dht.readTemperature();
//        float f = dht.readTemperature(true);
//
//        if (isnan(h) || isnan(t) || isnan(f)) {
//          Serial.println(F("Failed to read from DHT sensor!"));
//          return;
//        }
//
//      float hif = dht.computeHeatIndex(f, h);
//      float hic = dht.computeHeatIndex(t, h, false);
//
//      String temp = String(t);
//      String hum = String(h);
//
//
//      String url = "http://34.134.46.182/webcontroldevice/api-admin-status?codedevice=nhiet-%C4%91o-dht11&temp="+temp+"&hum="+hum+"";
//      http.begin(url);
//
//      int httpCode = http.GET();
//      if (httpCode > 0)
//      {
//        const size_t bufferSize = JSON_OBJECT_SIZE(2) + JSON_OBJECT_SIZE(3) + JSON_OBJECT_SIZE(5) + JSON_OBJECT_SIZE(8) + 370;
//        DynamicJsonBuffer jsonBuffer(bufferSize);
//        String payload = http.getString();
//        Serial.println(payload);
//        Serial.println("x");
//      }
//      http.end();
//    }
}
