import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Hashtable;


public class mainClass{
	
	static final int NUMBER_OF_USERS = 4;
	static final int NUMBER_OF_DISKS = 2;
	static final int NUMBER_OF_PRINTERS = 3;
	
	static UserThread users[] = new UserThread[NUMBER_OF_USERS];
	static Printer printers[] = new Printer[NUMBER_OF_PRINTERS];
	static Disk disks[] = new Disk[NUMBER_OF_DISKS];
	
	static ResourceManager diskResource = new ResourceManager(NUMBER_OF_DISKS);
	static ResourceManager printerResource = new ResourceManager(NUMBER_OF_PRINTERS);
	
	static DiskManager diskManager[] = new DiskManager[NUMBER_OF_DISKS];
	
	static DirectoryManager directory = new DirectoryManager();
	
	static OsGui os = new OsGui(NUMBER_OF_USERS, NUMBER_OF_DISKS, NUMBER_OF_PRINTERS);
	
	public static void main(String[] args) {		
	
		os.display_gui();
		
		for(int i=0; i<NUMBER_OF_DISKS; i++) {
			disks[i] = new Disk();
			diskManager[i] = new DiskManager();
		}
		
		for(int i=0; i<NUMBER_OF_PRINTERS; i++) {
			printers[i] = new Printer("PRINTER"+i);
		}
		
		for(int i=0; i<NUMBER_OF_USERS; i++) {
			//adds file name with index
			users[i] = new UserThread("USER"+(i+1), i);
			users[i].start();
		}
		
		
	}
	
}


class UserThread extends Thread{
	StringBuffer current_line; 
	//name of file
	String Useri;
	//stores the user index for gui 
	int index;
	
	//takes in the file name to read
	UserThread(String file_name, int i){
		Useri = file_name;
		index = i;
	}
	
	public void run() {
		//if running on eclipse change to "inputs/all/"+Useri
		File file = new File("../inputs/all/"+Useri);
		
		try {
			Scanner sc = new Scanner(file);
			
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				
				//splits by space in order to find command
				String[] command = line.split(" ");
				
				
				//if save command found
				if(command[0].equals(".save")) {
					int file_length = 0;
					
					mainClass.os.update_user_label(this.index,"requesting disk resource");
					
					//requests a disk resource and stores the disk number returned
					int diskNum = mainClass.diskResource.request();
					
					//updates user status on gui
					mainClass.os.update_user_label(this.index,"saving file: "+command[1]+" to disk: " + (diskNum+1));
					
					while(sc.hasNextLine() ) {
						String nextLine = sc.nextLine();
						
						//if .end then command is done, releases disk resource
						if(nextLine.equals(".end")) {
							
							mainClass.os.update_disk_label(diskNum, "IDLE");
							
							//gets the starting sector of the file
							int start = mainClass.diskManager[diskNum].get_free_sector() - file_length;
							//makes a new FileInfo class storing the disknumber, starting sector
							//and file length
							FileInfo info = new FileInfo(diskNum,start,file_length);
							//adds info to the hash map, with file name as the key
							mainClass.directory.enter(command[1], info );
							
							//System.out.println("Entering key: "+ command[1]+" value: {disk: "+diskNum
									//+",start: "+ start+",length: "+file_length+"}");
							
							mainClass.diskResource.release(diskNum);
							
							
							break;
						}
						
						//else the line is file content 
						else {
							file_length ++;
							//stores current line
							current_line = new StringBuffer(nextLine);
							
							//gets what sector is free given the disk number from the resource request
							int sectorNum = mainClass.diskManager[diskNum].get_free_sector();
							//for debugging purposes
							//System.out.println("Writing to disk: "+diskNum+" sector: "+sectorNum+" content: "+current_line);
							
							//updates disk status on gui
							mainClass.os.update_disk_label(diskNum, "writing "+ current_line + " to sector: "+sectorNum);
							
							Thread.sleep(200*mainClass.os.get_offset());
							//writes file content in requested disk number 
							mainClass.disks[diskNum].write(sectorNum, current_line);
							
							mainClass.os.add_directory_label(diskNum, "SECTOR "+sectorNum+": "+current_line);
							//moves the free sector index by 1
							mainClass.diskManager[diskNum].update_free_sector();

						}
					}
					
				}
				
				else if(command[0].equals(".print")) {
					
					//makes a new printjobthread 
					//passes name of file to constructor
					new PrintJobThread(command[1], this.index).run();
					
				}
				
			}
			
			mainClass.os.update_user_label(this.index, "Idle");
			sc.close();
		} catch (FileNotFoundException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} 
	
}



class Disk{
	
	static final int NUM_SECTORS = 1024;
	StringBuffer sectors[] = new StringBuffer[NUM_SECTORS];
	
	Disk() {
		for(int i=0; i<NUM_SECTORS; i++) {
			sectors[i] = new StringBuffer("");
		}
	}
	
	//writes data in given sector
	void write(int sector, StringBuffer data) {
		sectors[sector] = data;
		//System.out.println("WRITING TO "+sectors[sector]);
	} 
	
	void read(int sector, StringBuffer data) {
		data.append(sectors[sector]);
	}
	
	StringBuffer[] get_sectors() {
		return sectors;
	}
	
}

class Printer {
	String Printeri;
	
	Printer(String name){
		//if running on eclipse change to "outputs/"+name
		Printeri = "../outputs/"+name;
	}
	
	void print(StringBuffer data) {
		try {
			FileWriter writer = new FileWriter(Printeri,true);
			PrintWriter printwriter = new PrintWriter(writer);
			
			printwriter.println(data.toString());
			printwriter.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

class DiskManager{
	/** keeps track of the next free sector on each disk and contains 
	 * the DirectoryManager for finding file sectors on disk. Hashtable but private to 
	 * class DirectoryManager **/
	
	int free_sector = 0;
	//returns the index of a free sector
	int get_free_sector() {
		return free_sector;
	}
	//moves the index of free sector by 1
	void update_free_sector() {
		free_sector++;
	}
	
}

class DirectoryManager{
	//table that knows where files are stored on what disk
	Hashtable<String, FileInfo> T = new Hashtable<String, FileInfo>();
	
	void enter(String key, FileInfo file) {
		T.put(key, file);
	}
	
	FileInfo lookup(String key) {
		return T.get(key);
	}
}

class FileInfo{
	int diskNumber;
	int startingSector;
	int fileLength;
	
	FileInfo(int diskNum, int startSect, int length){
		diskNumber = diskNum;
		startingSector = startSect;
		fileLength = length;
	}
	
	int getDiskNumber() {
		return diskNumber;
	}
	
	int getStartingSector() {
		return startingSector;
	}
	
	int getFileLength() {
		return fileLength;
	}
	
	void printInfo() {
		System.out.println("{diskNumber: "+ diskNumber+", startingSector: "+startingSector+
				", fileLength: "+fileLength+"}");
	}
}


class ResourceManager
{
        boolean isFree[];
        
        ResourceManager(int numberOfItems)
        {
                isFree = new boolean[numberOfItems];
                for (int i=0; i<isFree.length; ++i)
                        isFree[i] = true;
        }
        
        synchronized int request()
        {
                while (true)
                {
                        for (int i = 0; i < isFree.length; ++i)
                                if ( isFree[i] )
                                {
                                        isFree[i] = false;
                                        return i;
                                }
                        
                        try {
							this.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} // block until someone releases a Resource
                }
        }
        
        synchronized void release( int index )
        {
                isFree[index] = true;
                this.notify(); // let a waiting thread run
        }
}


class PrintJobThread extends Thread{
	String file_name;
	int index;
	
	PrintJobThread(String name, int i){
		file_name = name;
		index = i;
	}
	
	public void run() {
		
		//looks up the file name in the directory manager
		FileInfo info = mainClass.directory.lookup(file_name);
		int sector = info.getStartingSector();
		int file_length = info.getFileLength();
		int disk_num = info.getDiskNumber();
		
		//updates user status on gui
		mainClass.os.update_user_label(this.index,"requesting printer resource");
		
		//gets a free printer 
		int printer_num = mainClass.printerResource.request();
		
		mainClass.os.update_user_label(this.index,"printing "+ file_name+" from Disk: "+disk_num+" using printer: "+ (printer_num+1));
		
		//System.out.println("PRINTING: "+ file_name+" TO: FILE"+ printer_num);
		
		
		for(int i=0; i<file_length; i++) {
			StringBuffer data = new StringBuffer();
			
			try {
				Thread.sleep(200*mainClass.os.get_offset());
				//reads sectors from the disk 
				mainClass.disks[info.getDiskNumber()].read(sector, data);
				
				mainClass.os.update_printer_label(printer_num, "printing line: "+ data);
				//sends to the printer one at a time
				Thread.sleep(2750*mainClass.os.get_offset());
				//System.out.println("DATA PRINTING TO FILE: "+data);
				mainClass.printers[printer_num].print(data);
				sector ++;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		//update printers label on gui
		mainClass.os.update_printer_label(printer_num, "IDLE");
		mainClass.printerResource.release(printer_num);
	}
}

