int Trig1 = 8;
int Echo1 = 9;
int Trig2 = 10;
int Echo2 = 11;
int Duration1;
int Duration2;

void setup() {
  Serial.begin(9600);
  pinMode(Trig1,OUTPUT);
  pinMode(Echo1,INPUT);
  pinMode(Trig2,OUTPUT);
  pinMode(Echo2,INPUT);
}

void loop() {
  digitalWrite(Trig1,HIGH);
  delayMicroseconds(1);
  digitalWrite(Trig1,LOW);
  Duration1 = pulseIn(Echo1,HIGH,1000000);
  digitalWrite(Trig2,HIGH);
  delayMicroseconds(1);
  digitalWrite(Trig2,LOW);
  Duration2 = pulseIn(Echo2,HIGH,1000000);
  Serial.print((int)Duration1);
  Serial.print("/");
  Serial.println((int)Duration2);
  delay(100);
}
