#include <Arduino.h>
#include <WiFi.h>
#include <AsyncTCP.h>
#include <SPIFFS.h>
#include <HTTPClient.h>
#include <ESPAsyncWebServer.h>
#include <ArduinoJson.h>
#include <DHT.h>

TaskHandle_t Task1;
#if CONFIG_FREERTOS_UNICORE
#define ARDUINO_RUNNING_CORE 0
#else
#define ARDUINO_RUNNING_CORE 1
#endif


#define DHTPIN 33
#define DHTPIN2 32

#define DHTTYPE DHT11

DHT dht(DHTPIN, DHTTYPE);


AsyncWebServer server(80);

const char* ssid = "Duys";
const char* password = "12345678";


const char* PARAM_Ssid = "ssid";
const char* PARAM_Pass = "pass";
const char* PARAM_Ipserver = "ipserver";

const char* PARAM_nameport1 = "nameport1";
const char* PARAM_codedevice1 = "codedevice1";

const char* PARAM_nameport2 = "nameport2";
const char* PARAM_codedevice2 = "codedevice2";

const char* PARAM_nameport3 = "nameport3";
const char* PARAM_codedevice3 = "codedevice3";

const char* PARAM_nameport4 = "nameport4";
const char* PARAM_codedevice4 = "codedevice4";

const char* PARAM_nameport5 = "nameport5";
const char* PARAM_codedevice5 = "codedevice5";

const char* PARAM_nameport6 = "nameport6";
const char* PARAM_codedevice6 = "codedevice6";

const char* PARAM_temhum1 = "temhum1";
const char* PARAM_codetemhum1 = "codetemhum1";
const char* PARAM_timetemhum1 = "timetemhum1";

const char* PARAM_temhum2 = "temhum2";
const char* PARAM_codetemhum2 = "codetemhum2";
const char* PARAM_timetemhum2 = "timetemhum2";


#define LED_ON HIGH
#define LED_OFF LOW


int idcode = 1;
String ip,codedevice1, codedevice2, codedevice3, codedevice4, codedevice5, codedevice6, codetemhum1, timetemhum1, codetemhum2,timetemhum2;

float temp,hum;


const char index_html[] PROGMEM = R"rawliteral(
<!doctype html>
<html lang="en">
  <head>
    <title>Title</title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <style>
        .wifisettings, .linkserversettings {
            border: 1px #b1b1e9 solid;
            border-radius: 6px;
        }
    </style>
  </head>
  <body>
      <div class="jumbotron jumbotron-fluid">
            <div class="container">
                <h1 class="display-5 text-center">device control settings</h1>
                <div class="row">
                    <div class="wifisettings col-xs-3 col-sm-4 col-md-4 col-lg-4 col-xl-4 m-3">
                        <p class="lead">Wifi settings:</p>
                        <form class="form-group text-center" action="/get" target="hidden-form">
                            <input type="text" name="ssid" id="ssid" class="form-control" placeholder="ssid" value =%ssid%>
                            <br>
                            <input type="text" name="pass" id="pass" class="form-control" placeholder="password" value =%pass%>
                            <input type="submit" value ="submit" class="btn btn-outline-primary mt-2" onclick="submitMessage()">
                        </form>
                    </div>
                    <div class="linkserversettings col-xs-6 col-sm-6 col-md-6 col-lg-7 col-xl-7 m-3">
                        <p class="lead">IP server:</p>
                        <form class="form-group text-center" action="/get" target="hidden-form">
                            <input type="text" name="ipserver" id="ipserver" class="form-control" placeholder="ip server" value =%ipserver%>
                            <input type="submit" value ="submit" class="btn btn-outline-primary mt-2" onclick="submitMessage()">
                        </form>
                    </div>
                </div>
              <hr class="my-2">
              <p class="lead">port device settings:</p>
                <div class="settingdevice">
                    <div class="table-responsive">
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th>Port</th>
                                    <th>name device</th>
                                    <th>code device</th>
                                    <th>action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <form action="/get" target="hidden-form">
                                        <td>port 1</td>
                                        <td><input type="text" name="nameport1" id="nameport1" class="form-control" value =%nameport1%></td>
                                        <td><input type="text" name="codedevice1" id="codedevice1" class="form-control" value =%codedevice1%></td>
                                        <td><input type="submit" value="submit" class="btn btn-outline-primary" onclick="submitMessage()"></td>
                                    </form>
                                </tr>
                                <tr>
                                    <form action="/get" target="hidden-form">
                                        <td>port 2</td>
                                        <td><input type="text" name="nameport2" id="nameport2" class="form-control" value =%nameport2%></td>
                                        <td><input type="text" name="codedevice2" id="codedevice2" class="form-control" value =%codedevice2%></td>
                                        <td><input type="submit" value="submit" class="btn btn-outline-primary" onclick="submitMessage()"></td>
                                    </form>
                                </tr>
                                <tr>
                                    <form action="/get" target="hidden-form">
                                        <td>port 3</td>
                                        <td><input type="text" name="nameport3" id="nameport3" class="form-control" value =%nameport3% ></td>
                                        <td><input type="text" name="codedevice3" id="codedevice3" class="form-control" value =%codedevice3%></td>
                                        <td><input type="submit" value="submit" class="btn btn-outline-primary" onclick="submitMessage()"></td>
                                    </form>
                                </tr>
                                <tr>
                                    <form action="/get" target="hidden-form">
                                        <td>port 4</td>
                                        <td><input type="text" name="nameport4" id="nameport4" class="form-control" value =%nameport4%></td>
                                        <td><input type="text" name="codedevice4" id="codedevice4" class="form-control" value =%codedevice4%></td>
                                        <td><input type="submit" value="submit" class="btn btn-outline-primary" onclick="submitMessage()"></td>
                                    </form>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
          </div>
      </div>
      <iframe style="display:none" name="hidden-form"></iframe>
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
    <script>
            document.getElementById('timetemhum1').value = %timetemhum1%
            document.getElementById('timetemhum2').value = %timetemhum2%
            
            function submitMessage() {
                setTimeout(function(){ document.location.reload(false); }, 500);   
             }
    </script>
  </body>
</html>)rawliteral";

void notFound(AsyncWebServerRequest *request) {
  request->send(404, "text/plain", "Not found");
}


String readFile(fs::FS &fs, const char * path) {
  Serial.printf("Reading file: %s\r\n", path);
  File file = fs.open(path, "r");
  if (!file) {
    Serial.println("- empty file");
    return String();
  }
  if (file.isDirectory()) {
    Serial.println("failed to open file");
    return String();
  }
  Serial.println("- read from file:");
  String fileContent;
  while (file.available()) {
    fileContent += String((char)file.read());
  }
  file.close();
  Serial.println(fileContent);
  return fileContent;
}



void writeFile(fs::FS &fs, const char * path, const char * message) {
  Serial.printf("Writing file: %s\r\n", path);
  File file = fs.open(path, "w");
  if (!file) {
    Serial.println("- failed to open file for writing");
    return;
  }
  if (file.print(message)) {
    Serial.println("- file written");
  } else {
    Serial.println("- write failed");
  }
  file.close();
}

void reads(){
  ip = readFile(SPIFFS, "/ipserver.txt");
  codedevice1 = readFile(SPIFFS, "/codedevice1.txt");
  codedevice2 = readFile(SPIFFS, "/codedevice2.txt");
  codedevice3 =readFile(SPIFFS, "/codedevice3.txt");
  codedevice4 = readFile(SPIFFS, "/codedevice4.txt");
  codedevice5 = readFile(SPIFFS, "/codedevice5.txt");
  codedevice6 = readFile(SPIFFS, "/codedevice6.txt");
  codetemhum1 = readFile(SPIFFS, "/codetemhumone.txt");
  timetemhum1 = readFile(SPIFFS, "/timetemhumone.txt");
  codetemhum2 = readFile(SPIFFS, "/codetemhumtwo.txt");
  timetemhum2 = readFile(SPIFFS, "/timetemhumtwo.txt");
 }

// Replaces placeholder with stored values
String processor(const String& var) {
  //Serial.println(var);
  if (var == "ssid") {
    return readFile(SPIFFS, "/ssid.txt");
  }
  else if (var == "pass") {
    return readFile(SPIFFS, "/pass.txt");
  }
  else if (var == "ipserver") {
    ip = readFile(SPIFFS, "/ipserver.txt");
    return ip;
  }
  else if (var == "nameport1") {
    return readFile(SPIFFS, "/nameport1.txt");
  }
  else if (var == "codedevice1") {
    codedevice1 = readFile(SPIFFS, "/codedevice1.txt");
    return codedevice1;
  }
  else if (var == "nameport2") {
    return readFile(SPIFFS, "/npT.txt");
  }
  else if (var == "codedevice2") {
    codedevice2 = readFile(SPIFFS, "/codedevice2.txt");
    return codedevice2;
  }
  else if (var == "nameport3") {
    return readFile(SPIFFS, "/npba.txt");
  }
  else if (var == "codedevice3") {
    codedevice3 =readFile(SPIFFS, "/codedevice3.txt");
    return codedevice3;
  }
  else if (var == "nameport4") {
    return readFile(SPIFFS, "/npbon.txt");
  }
  else if (var == "codedevice4") {
    codedevice4 = readFile(SPIFFS, "/codedevice4.txt");
    return codedevice4;
  }
  else if (var == "nameport5") {
    return readFile(SPIFFS, "/npnam.txt");
  }
  else if (var == "codedevice5") {
    codedevice5 = readFile(SPIFFS, "/codedevice5.txt");
    return codedevice5;
  }
  else if (var == "nameport6") {
    return readFile(SPIFFS, "/npsau.txt");
  }
  else if (var == "codedevice6") {
    codedevice6 = readFile(SPIFFS, "/codedevice6.txt");
    return codedevice6;
  }
  else if (var == "temhum1") {
    return readFile(SPIFFS, "/temhumone.txt");
  }
  else if (var == "codetemhum1") {
    codetemhum1 = readFile(SPIFFS, "/codetemhumone.txt");
    return codetemhum1;
  }
  else if (var == "timetemhum1") {
    timetemhum1 = readFile(SPIFFS, "/timetemhumone.txt");
    return timetemhum1;
  }
  else if (var == "temhum2") {
    return readFile(SPIFFS, "/temhumtwo.txt");
  }
  else if (var == "codetemhum2") {
    codetemhum2 = readFile(SPIFFS, "/codetemhumtwo.txt");
    return codetemhum2;
  }
  else if (var == "timetemhum2") {
    timetemhum2 = readFile(SPIFFS, "/timetemhumtwo.txt");
    return timetemhum2;
  }
  return String();
}



void onoffdevice(String status, int port) {
  Serial.println("Đã đến đây");
  Serial.println(port);
  if (status == "0") {
    digitalWrite(port, LOW);
  } else {
    digitalWrite(port, HIGH);
  }
}


void reaDAtaSensorDHT(){
  
    delay(5000);

        hum = dht.readHumidity();
        temp = dht.readTemperature();
        float f = dht.readTemperature(true);

        if (isnan(hum) || isnan(temp) || isnan(f)) {
          Serial.println(F("Failed to read from DHT sensor!"));
          return;
        }

       Serial.print("Humidity: ");
       Serial.print(hum);
       Serial.print("temp: ");
       Serial.print(temp);
 }


void readTempAndHum1( void * pvParameters ) {
  for (;;) {
    reaDAtaSensorDHT();
    HTTPClient http1;
    Serial.println(codetemhum1);
    String url ="http://"+ ip+"/webcontroldevice/api-admin-status?codedevice=" + codetemhum1 + "&temp=" + String(temp)+ "&hum=" + String(hum);
    Serial.println(url);
    http1.begin(url);

    int httpCode = http1.GET();
    if (httpCode > 0)
    {
      String payload = http1.getString();
      Serial.println(payload);
    }
    http1.end();
    delay(timetemhum1.toInt());
  }
}


void setup() {

  Serial.begin(115200);

  pinMode(15, OUTPUT);//port 1
  pinMode(2, OUTPUT);//port 2
  pinMode(0, OUTPUT);//port 3
  pinMode(4, OUTPUT);//port 4
  pinMode(16, OUTPUT);//port 5
  pinMode(17, OUTPUT);//port 6


  digitalWrite(15, LED_OFF);
  digitalWrite(2, LED_OFF);
  digitalWrite(0, LED_OFF);
  digitalWrite(4, LED_OFF);
  digitalWrite(16, LED_OFF);
  digitalWrite(17, LED_OFF);

  if (!SPIFFS.begin(true)) {
    Serial.println("An Error has occurred while mounting SPIFFS");
    return;
  }

  if (readFile(SPIFFS, "/ssid.txt") == String() || (readFile(SPIFFS, "/pass.txt")) == String()) {
    writeFile(SPIFFS, "/ssid.txt", ssid);
    writeFile(SPIFFS, "/pass.txt", password);
  }


  WiFi.mode(WIFI_STA);

  String ssids = readFile(SPIFFS, "/ssid.txt");
  Serial.println("doc: " + ssids);

  String passs = readFile(SPIFFS, "/pass.txt");
  Serial.println("doc: " + passs);


  WiFi.begin(ssids.c_str(), passs.c_str());
  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("connectting...");
    delay(1000);
  }
  Serial.println();
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  // Send web page with input fields to client
  server.on("/", HTTP_GET, [](AsyncWebServerRequest * request) {
    request->send_P(200, "text/html", index_html, processor);
  });

  // Send a GET request to <ESP_IP>/get?inputString=<inputMessage>
  server.on("/get", HTTP_GET, [] (AsyncWebServerRequest * request) {
    String inputMessage;
    String inputMessage2;
    String inputMessage3;

    if (request->hasParam(PARAM_Ssid) && request->hasParam(PARAM_Pass)) {
      inputMessage = request->getParam(PARAM_Ssid)->value();
      inputMessage2 = request->getParam(PARAM_Pass)->value();
      writeFile(SPIFFS, "/ssid.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/pass.txt", inputMessage2.c_str());
    }
    else if (request->hasParam(PARAM_Ipserver)) {
      inputMessage = request->getParam(PARAM_Ipserver)->value();
      writeFile(SPIFFS, "/ipserver.txt", inputMessage.c_str());
    }
    else if (request->hasParam(PARAM_nameport1) && request->hasParam(PARAM_codedevice1)) {
      inputMessage = request->getParam(PARAM_nameport1)->value();
      inputMessage2 = request->getParam(PARAM_codedevice1)->value();

      writeFile(SPIFFS, "/nameport1.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/codedevice1.txt", inputMessage2.c_str());
    }
    else if (request->hasParam(PARAM_nameport2) && request->hasParam(PARAM_codedevice2)) {
      inputMessage = request->getParam(PARAM_nameport2)->value();
      inputMessage2 = request->getParam(PARAM_codedevice2)->value();

      writeFile(SPIFFS, "/npT.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/codedevice2.txt", inputMessage2.c_str());
    }
    else if (request->hasParam(PARAM_nameport3) && request->hasParam(PARAM_codedevice3)) {
      inputMessage = request->getParam(PARAM_nameport3)->value();
      inputMessage2 = request->getParam(PARAM_codedevice3)->value();

      writeFile(SPIFFS, "/npba.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/codedevice3.txt", inputMessage2.c_str());
    }
    else if (request->hasParam(PARAM_nameport4) && request->hasParam(PARAM_codedevice4)) {
      inputMessage = request->getParam(PARAM_nameport4)->value();
      inputMessage2 = request->getParam(PARAM_codedevice4)->value();

      writeFile(SPIFFS, "/npbon.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/codedevice4.txt", inputMessage2.c_str());
    }
    else if (request->hasParam(PARAM_nameport5) && request->hasParam(PARAM_codedevice5)) {
      inputMessage = request->getParam(PARAM_nameport5)->value();
      inputMessage2 = request->getParam(PARAM_codedevice5)->value();

      writeFile(SPIFFS, "/npnam.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/codedevice5.txt", inputMessage2.c_str());
    }
    else if (request->hasParam(PARAM_nameport6) && request->hasParam(PARAM_codedevice6)) {
      inputMessage = request->getParam(PARAM_nameport6)->value();
      inputMessage2 = request->getParam(PARAM_codedevice6)->value();

      writeFile(SPIFFS, "/npsau.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/codedevice6.txt", inputMessage2.c_str());
    }
    else if (request->hasParam(PARAM_temhum1) && request->hasParam(PARAM_codetemhum1) && request->hasParam(PARAM_timetemhum1)) {
      inputMessage = request->getParam(PARAM_temhum1)->value();
      inputMessage2 = request->getParam(PARAM_codetemhum1)->value();
      inputMessage3 = request->getParam(PARAM_timetemhum1)->value();

      writeFile(SPIFFS, "/temhumone.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/codetemhumone.txt", inputMessage2.c_str());
      writeFile(SPIFFS, "/timetemhumone.txt", inputMessage3.c_str());
    }
    else if (request->hasParam(PARAM_temhum2) && request->hasParam(PARAM_codetemhum2) && request->hasParam(PARAM_timetemhum2)) {
      inputMessage = request->getParam(PARAM_temhum2)->value();
      inputMessage2 = request->getParam(PARAM_codetemhum2)->value();
      inputMessage3 = request->getParam(PARAM_timetemhum2)->value();

      writeFile(SPIFFS, "/temhumtwo.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/codetemhumtwo.txt", inputMessage2.c_str());
      writeFile(SPIFFS, "/timetemhumtwo.txt", inputMessage3.c_str());
    }
    else {
      inputMessage = "No message sent";
    }
    Serial.println(inputMessage);
    request->send(200, "text/text", inputMessage);
  });
  server.onNotFound(notFound);

  server.begin();

  dht.begin();
  
  reads();

//    xTaskCreatePinnedToCore(
//                    readTempAndHum1,   /* Task function. */
//                    "Task1",     /* name of task. */
//                    10000,       /* Stack size of task */
//                    NULL,        /* parameter of the task */
//                    2,           /* priority of the task */
//                    &Task1,      /* Task handle to keep track of created task */
//                    1);          /* pin task to core 1 */
//    delay(500);
    
//    xTaskCreate(
//    readTempAndHum1
//    ,  "readTempAndHum1"
//    ,  10000  // Stack size
//    ,  NULL
//    ,  1  // Priority
//    ,  NULL );

  
}

void loop() {
//    reaDAtaSensorDHT();
  String code = "";
  int port;
  if (idcode == 1) {
    code = codedevice1;
    port = 15;
  }
  else if (idcode == 2) {
    code = codedevice2;
    port = 2;
  }
  else if (idcode == 3) {
    code = codedevice3;
    port = 0;
  }
  else if (idcode == 4) {
    code = codedevice4;
    port = 4;
  }
  else if (idcode == 5) {
    code = codedevice5;
    port = 16;
  }
  else if (idcode == 6) {
    code = codedevice6;
    port = 17;
  }
  HTTPClient http;
  String url ="http://" +ip + "/webcontroldevice/api-admin-device?type=1&code=" + code;
  http.begin(url);
  int httpCode = http.GET();
  if (httpCode > 0)
  {
    const size_t bufferSize = JSON_OBJECT_SIZE(2) + JSON_OBJECT_SIZE(3) + JSON_OBJECT_SIZE(5) + JSON_OBJECT_SIZE(8) + 370;
    DynamicJsonBuffer jsonBuffer(bufferSize);
    JsonObject& root = jsonBuffer.parseObject(http.getString());
    int id = root["id"];
    Serial.print("id: ");
    Serial.println(id);
    const char* name = root["name"];
    Serial.print("name: ");
    Serial.println(name);
    const char* devicecode = root["devicecode"];
    Serial.print("devicecode: ");
    Serial.println(devicecode);
    String status = root["status"];
    Serial.print("status: ");
    Serial.println(status);
    onoffdevice(status, port);
  }
  http.end();
  idcode++;
  if (idcode > 6) {
    idcode = 1;
  }
}
