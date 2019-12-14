#include "fsl_device_registers.h"

static int i = 0;
static int j = 0;
static int p = 0;
static int k = 0;
int upflag = 1;
int uparr[10];
int downarr[10];
int uplace = 0;
int dplace = 0;
int currfloor = 1;
int newfloor = 0; //this is actually to calculate the difference between input floor and current floor
int check = 0;
unsigned char segg[10] = {0x7E, 0x30, 0x6D, 0x79, 0x33, 0x5B, 0x5F, 0x70, 0x7F, 0x7B};




void sound() {
for (j = 0; j < 500; j++){
GPIOC_PDOR = 0x80;
for (p = 0; p < 2000; p++);
GPIOC_PDOR = 0x00;
for (p = 0; p < 2000; p++);
}
return;
}


void upmove() {
if (uplace != 0) {
for(k = 0; k < uplace; ++k) {
newfloor = uparr[k] - currfloor;
//currfloor = uparr[i];
   uparr[k] = 0;
newfloor *= 55;

for (j = 1; j <= newfloor; ++j) {
GPIOC_PDOR = 0x36;  //this is counter clockwise so position stepper correctly
for (i = 0; i < 10000; i++);
GPIOC_PDOR = 0x35;
for (i = 0; i < 10000; i++);
GPIOC_PDOR = 0x39;
for (i = 0; i < 10000; i++);
GPIOC_PDOR = 0x3A;
for (i = 0; i < 10000; i++);

check = j % 55;

if (check == 0) {
currfloor++;
GPIOD_PDOR = segg[currfloor];
}


}
//for (j = 0; j < 10000000; ++j);
//for (i = 0; i < 100000; i++); //delay
sound();
}}
//upflag = 0;

uplace = 0;
}





void downmove() {
if(dplace != 0) {
for(k = 0; k < dplace; ++k) {
newfloor = currfloor - downarr[k];
//currfloor = downarr[k];
downarr[k] = 0;
newfloor *= 55;

for (j = 1; j <= newfloor; ++j) {
GPIOC_PDOR = 0x3A;  //this is counter clockwise so position stepper correctly
for (i = 0; i < 10000; i++);
GPIOC_PDOR = 0x39;
for (i = 0; i < 10000; i++);
GPIOC_PDOR = 0x35;
for (i = 0; i < 10000; i++);
GPIOC_PDOR = 0x36;
for (i = 0; i < 10000; i++);
check = j % 55;

if (check == 0) {
currfloor--;
GPIOD_PDOR = segg[currfloor];
}

}

sound();
//for (j = 0; j < 10000000; ++j);
//for (i = 0; i < 100000; i++); //delay
}}
//upflag = 1;

dplace = 0;
}




int main(void)
{
SIM_SCGC5 |= SIM_SCGC5_PORTD_MASK; /*Enable Port E Clock Gate Control*/
SIM_SCGC5 |= SIM_SCGC5_PORTB_MASK; /*Enable Port C Clock Gate Control*/
//SIM_SCGC5 |= SIM_SCGC5_PORTA_MASK; /*Enable Port A Clock Gate Control*/
SIM_SCGC5 |= SIM_SCGC5_PORTC_MASK;

PORTD_GPCLR = 0x00FF0100; //portd 0-7 io
PORTC_GPCLR = 0x00BF0100; //PORTc 0-5 IO
//PORTA_GPCLR = 0x00070100;
PORTB_GPCLR = 0x060C0100;

GPIOC_PDDR = 0x000000FF; //output
//GPIOA_PDDR = 0x00000000; //input
GPIOD_PDDR = 0x000000FF;
GPIOB_PDDR = 0x00000000;

int input = 0;

GPIOD_PDOR = segg[1];
while(1) {
input = ~GPIOB_PDIR;




while((input & 0x400)||(input & 0x200)||(input & 0x08)||(input & 0x04)) {

if (input & 0x400) { //checks pin 0
input = 1;
}
else if (input & 0x200) { // pin 1
input = 2;
}
else if (input & 0x08) { //pin 2
input = 3;
}
else if (input & 0x04) { //pin 3
input = 4;
}
else {
input = currfloor;
}


if (input == currfloor) {
currfloor += 0;
}
else if (input > currfloor) {
uparr[uplace] = input;
uplace++;
}
else if (input < currfloor) {
downarr[dplace] = input;
dplace++;
}



for (i = 0; i < 10000000; ++i);
input = ~GPIOB_PDIR;

}




int temp = 0;


for ( i = 0; i < uplace; i++)                     //Loop for ascending ordering
{
for ( j = 0; j < uplace; j++)             //Loop for comparing other values
{
if (uparr[j] > uparr[i])                //Comparing other array elements
{
   temp = uparr[i];         //Using temporary variable for storing last value
uparr[i] = uparr[j];            //replacing value
uparr[j] = temp;             //storing last value
}
}
}


for ( i = 0; i < dplace; i++)                     //Loop for descending ordering
{
for ( j = 0; j < dplace; j++)             //Loop for comparing other values
{
if ((downarr[j] < downarr[i]) || (downarr[j] != 0))                //Comparing other array elements
{
temp = downarr[i];         //Using temporary variable for storing last value
downarr[i] = downarr[j];            //replacing value
downarr[j] = temp;             //storing last value
}
}
}

if (dplace == uplace) {
upflag = 1;
}
else if (dplace > uplace) {
upflag = 0;
}
else {
upflag = 1;
}


if (upflag) {
upmove();
}
else {
downmove();
}
}

    return 0;
}
