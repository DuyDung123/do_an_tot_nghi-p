#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266HTTPClient.h>
#include <ESPAsyncTCP.h>
#include <Hash.h>
#include <FS.h>
#include <ESPAsyncWebServer.h>
#include <ArduinoJson.h>
#include <DHT.h>

AsyncWebServer server(80);

char* ssid = "Duys";
char* password = "12345678";

#define DHTPIN 5

#define DHTTYPE  DHT11

DHT dht(DHTPIN, DHTTYPE);

const char* PARAM_Ssid = "ssid";
const char* PARAM_Pass = "pass";
const char* PARAM_Ipserver = "ipserver";

const char* PARAM_temhum1 = "temhum1";
const char* PARAM_codetemhum1 = "codetemhum1";
const char* PARAM_timetemhum1 = "timetemhum1";



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
            /* padding: 7px; */
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
              <p class="lead">port device Temp&Hum settings:</p>
                <div class="settingdevice">
                    <div class="table-responsive">
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th>Port</th>
                                    <th>name device</th>
                                    <th>code device</th>
                                    <th>sample scan time</th>
                                    <th>action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                   <td>port 1</td>
                                    <form action="/get" target="hidden-form">
                                        <td><input type="text" name="temhum1" id="temhum1" class="form-control" value =%temhum1%></td>
                                        <td><input type="text" name="codetemhum1" id="codetemhum1" class="form-control" value =%codetemhum1%></td>
                                        <td>
                                            <select class="form-control" id="timetemhum1" name="timetemhum1">
                                                <option value="300000">5 minutes</option>
                                                <option value="600000">10 minutes</option>
                                                <option value="900000">15 minutes</option>
                                            </select>
                                        </td>
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
      function submitMessage() {
        setTimeout(function(){ document.location.reload(false); }, 500);   
      }
      document.getElementById('timetemhum1').value = %timetemhum1%
    </script>
  </body>
</html>)rawliteral";

void notFound(AsyncWebServerRequest *request) {
  request->send(404, "text/plain", "Not found");
}

String readFile(fs::FS &fs, const char * path){
  Serial.printf("Reading file: %s\r\n", path);
  File file = fs.open(path, "r");
  if(!file){
    Serial.println("- empty file");
    return String();
  }
  if(file.isDirectory()){
    Serial.println("failed to open file");
    return String();
  }
  Serial.println("- read from file:");
  String fileContent;
  while(file.available()){
    fileContent+=String((char)file.read());
  }
  file.close();
  Serial.println(fileContent);
  return fileContent;
}

void writeFile(fs::FS &fs, const char * path, const char * message){
  Serial.printf("Writing file: %s\r\n", path);
  File file = fs.open(path, "w");
  if(!file){
    Serial.println("- failed to open file for writing");
    return;
  }
  if(file.print(message)){
    Serial.println("- file written");
  } else {
    Serial.println("- write failed");
  }
  file.close();
}

// Replaces placeholder with stored values
String processor(const String& var){
  //Serial.println(var);
  if(var == "ssid"){
    return readFile(SPIFFS, "/ssid.txt");
  }
  else if(var == "pass"){
    return readFile(SPIFFS, "/pass.txt");
  }
  else if(var == "ipserver"){
    return readFile(SPIFFS, "/ipserver.txt");
  }
  else if(var == "temhum1"){
    return readFile(SPIFFS, "/temhumone.txt");
  }
  else if(var == "codetemhum1"){
    return readFile(SPIFFS, "/codetemhumone.txt");
  }
  else if(var == "timetemhum1"){
    return readFile(SPIFFS, "/timetemhumone.txt");
  }
  return String();
}
void setup() {
  Serial.begin(115200);

  
  // Initialize SPIFFS
    if(!SPIFFS.begin()){
      Serial.println("An Error has occurred while mounting SPIFFS");
      return;
    }

  if(readFile(SPIFFS, "/ssid.txt") == String()||(readFile(SPIFFS, "/pass.txt"))==String()){
      writeFile(SPIFFS, "/ssid.txt", ssid);
      writeFile(SPIFFS, "/pass.txt", password);
  }

  WiFi.mode(WIFI_STA);

  String ssids = readFile(SPIFFS, "/ssid.txt");
  Serial.println("doc: "+ssids);

  String passs = readFile(SPIFFS, "/pass.txt");
  Serial.println("doc: "+passs);

    
  WiFi.begin(ssids.c_str(), passs.c_str());
  while (WiFi.status() != WL_CONNECTED) {
      Serial.println("connectting...");
      delay(1000);
  }
  Serial.println();
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  // Send web page with input fields to client
  server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send_P(200, "text/html", index_html, processor);
  });

  // Send a GET request to <ESP_IP>/get?inputString=<inputMessage>
  server.on("/get", HTTP_GET, [] (AsyncWebServerRequest *request) {
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
    else if (request->hasParam(PARAM_temhum1) && request->hasParam(PARAM_codetemhum1) && request->hasParam(PARAM_timetemhum1)) {
      inputMessage = request->getParam(PARAM_temhum1)->value();
      inputMessage2 = request->getParam(PARAM_codetemhum1)->value();
      inputMessage3 = request->getParam(PARAM_timetemhum1)->value();

      writeFile(SPIFFS, "/temhumone.txt", inputMessage.c_str());
      writeFile(SPIFFS, "/codetemhumone.txt", inputMessage2.c_str());
      writeFile(SPIFFS, "/timetemhumone.txt", inputMessage3.c_str());
    }
    
    else {
      inputMessage = "No message sent";
    }
    Serial.println(inputMessage);
    request->send(200, "text/text", inputMessage);
  });
  server.onNotFound(notFound);
  
  dht.begin();
  server.begin();
  
}

void loop() {
        WiFiClient clients;
        HTTPClient http;
       String codetemhum1 = readFile(SPIFFS, "/codetemhumone.txt");
       String timetemhum1 = readFile(SPIFFS, "/timetemhumone.txt");
       String ip = readFile(SPIFFS, "/ipserver.txt");
        delay(5000);

        float hum = dht.readHumidity();
        float temp = dht.readTemperature();
        float f = dht.readTemperature(true);

        if (isnan(hum) || isnan(temp) || isnan(f)) {
          Serial.println(F("Failed to read from DHT sensor!"));
          return;
        }
        
       Serial.print("Humidity: ");
       Serial.print(hum);
       Serial.print("temp: ");
       Serial.print(temp);

        Serial.println(codetemhum1);
        String url = "http://"+ip+"/webcontroldevice/api-admin-status?codedevice=" + codetemhum1 + "&temp=" + String(temp)+ "&hum=" + String(hum);
        Serial.println(url);
        http.begin(clients,url);

        int httpCode = http.GET();
        if (httpCode > 0)
        {
          String payload = http.getString();
          Serial.println(payload);
        }
        http.end();
        delay(timetemhum1.toInt());

}
