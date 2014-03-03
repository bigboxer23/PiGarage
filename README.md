PiGarage
========

This project runs on a raspberry pi.  It utilizes GPIO pins to track status of a magnetic sensor mounted to a garage door,
and a relay used to open/close the door.  It will trigger the garage door to close after a defined amount of time being
left open.  Additionally, it runs a small webserver which has REST service urls to trigger closing/opening the door
and checking status.

Wiring devices to the pi<br>
test

There are multiple configurable properties that can be set at runtime:
log.location: Path to the file to log information about opener status, actions, etc.
status.path: URL to get status from (default is "/Status")
close.path: URL to close garage door (default is "/Close")
open.path: URL to open garage door (default is "/Open")
close.delay: Number of milliseconds before closing the door once it's detected open (default is 10 minutes)
triggerDelay: How long to leave the switch active before turning off (how long you press the physical button) (default is 400ms)
