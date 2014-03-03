PiGarage
========

This project runs on a raspberry pi.  It utilizes GPIO pins to track status of a magnetic sensor mounted to a garage door,
and a relay used to open/close the door.  It will trigger the garage door to close after a defined amount of time being
left open.  Additionally, it runs a small webserver which has REST service urls to trigger closing/opening the door
and checking status.

Wiring devices to the pi<br>
Sensor for status<br>
	<a href="http://www.smarthome.com/7455/Seco-Larm-SM-226L-Garage-Door-Contacts-for-Closed-Circuits/p.aspx">Seco-Larm SM-226L</a><br>
This is wired to Pin 9 and 13 (GPIO 2 by default).<br><br>

The solid state relay is wired to ground on pin 6, 5v on pin 2, and GPIO 7 on pin 7 by default<br><br>

There are multiple configurable properties that can be set at runtime:<br>
log.location: Path to the file to log information about opener status, actions, etc.<br>
status.path: URL to get status from (default is "/Status")<br>
close.path: URL to close garage door (default is "/Close")<br>
open.path: URL to open garage door (default is "/Open")<br>
close.delay: Number of milliseconds before closing the door once it's detected open (default is 10 minutes)<br>
triggerDelay: How long to leave the switch active before turning off (how long you press the physical button) (default is 400ms)<br>
GPIO.status.pin: Pin to use for the magnetic sensor (default is GPIO 2, pin 9)<br>
GPIO.action.pin: Pin to use for the solid state relay (default is GPIO 7, pin 7)
