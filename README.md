# OSGui
How to run program:
	- Navigate to src file
	- In terminal type: make run 


Process: 

- UserThread parses the file it is reading from 
- if command is save:
	- updates user label on GUI 
	- requests a disk resource and continues reading from file until it finds .end command
	- keeps track of the file length 
	- it stores current line in StringBuffer, gets a free 
	sector from the available disk and then puts system to sleep for (200*speed offset from GUI).
	- writes the current line in requested disk number 
- if .end command is found 
	- gets the starting sector of the file from the disk it wrote to by
	subtracting the returning free sector by the file_length
	- makes a new FileInfo object and stores the disk number, starting sector and file length
	- it makes a new entry in the directory with the name of the file as the key and its FileInfo as the value
	- releases disk resource 

- if command is print:
	- creates and runs a new PrintJobThread with the file name and user index for GUI in Constructor 
	- PrintJobThread looks up the file name in the directory manager 
	- it stores the FileInfo from that file
	- gets a free printer 
	- afterwards, goes in a loop until file length reached
	- in loop, make the system sleep for (200*speed offset from GUI)
	- reads sector data into data StringBuffer
	- system sleeps for (2750*speed offset from GUI)
	- sends the data to printer print function 


NOTE: GUI Updates more frequently than I wrote above. It updates every time it requests/releases a resource and it also updates when printing, reading, saving etc. In my code there are comments that document the updates going on in the GUI.






