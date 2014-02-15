PiGarage
========

This project runs on a raspberry pi.  It utilizes GPIO pins to track status of a magnetic sensor mounted to a garage door,
and a relay used to open/close the door.  It will trigger the garage door to close after a defined amount of time being
left open.  Additionally, it runs a small webserver which has REST service urls to trigger closing/opening the door
and checking status.