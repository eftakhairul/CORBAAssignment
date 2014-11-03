package library;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;



public class LibraryImpl extends LibraryServerPOA implements Runnable{
	
	public String instituteName;
	

	/* Global Libraries definition */
	public static Map<String, Book> VanLibrary = Collections.synchronizedMap(new HashMap<String, Book>(1000));
	public static Map<String, Book> ConLibrary = Collections.synchronizedMap(new HashMap<String, Book>(1000));
	public static Map<String, Book> DowLibrary = Collections.synchronizedMap(new HashMap<String, Book>(1000));

	/* Global Student definition */
	public static Map<String, Student> StudentRecord = Collections.synchronizedMap(new HashMap<String, Student>(1000));

	/* All Ports */
	int VanLibraryPort = 3000;
	int ConLibraryPort = 4000;
	int DowLibraryPort = 5000;

	/*
	 * Creating the student record
	 * 
	 * @param firstName	 
	 * @param lastName
	 * @param emailAddress
	 * @param phoneNumber
	 * @param username
	 * @param password
	 * @param educationalInstitude
	 * @return boolean
	 * @throws RemoteException
	 */
	public boolean createAccount(String firstName, 
								 String lastName,
								 String emailAddress, 
								 String phoneNumber, 
								 String username,
								 String password, 
								 String educationalInstitude) {

		Boolean createRecordFlag = false;
		Student student = new Student(firstName, 
									  lastName, 
									  emailAddress,
									  phoneNumber, 
									  username, 
									  password, 
									  educationalInstitude);
		synchronized (StudentRecord) {
			if (StudentRecord.get(username) == null) {
				StudentRecord.put(username, student);
				// Log the operation
				logFile("multi", "Student name: " + student.firstName + " record is created");
				return true;
			} else {
				logFile("multi", "Student name: " + student.firstName
												  + " record is failed");
				return false;
			}
		}
	}

	/*
	 * Reserve a book for a student
	 * 
	 * @param username
	 * @param password
	 * @param bookName
	 * @param authorName
	 * @return boolean
	 * 
	 * @throws RemoteException
	 */
	public boolean reserveBook(String username, 
							   String password,
							   String bookName, 
							   String authorName) {
		
		Boolean reserveBookeRecordFlag = false;
		Student student 			   = StudentRecord.get(username);
		
		if (student == null) {
			return reserveBookeRecordFlag;
		}
		
		logFile("debug", "server: " + student.educationalInstitude);
		switch (student.educationalInstitude) {
		
		// Vanier college
		case "van": {
			synchronized (VanLibrary) {
				Book book = VanLibrary.get(bookName);
				if (book != null) {
					if (book.addBook(student.username)) {
						reserveBookeRecordFlag = true;
						// Log file create
						logFile("van", "One book :" + bookName
								+ " is reserved for student: "
								+ student.username);
					} else {
						reserveBookeRecordFlag = false;
						logFile("van", "One book :" + bookName
								+ " is not reserved for student: "
								+ student.username);
					}
				}
			}
		}
		break;
		
		// Concordia University
		case "con": {
			synchronized (ConLibrary) {
				Book book = ConLibrary.get(bookName);
				if (book != null) {
					if (book.addBook(student.username)) {
						reserveBookeRecordFlag = true;
						// Log file create
						logFile("con", "One book :" + bookName
								+ " is reserved for student: "
								+ student.username);
					} else {
						reserveBookeRecordFlag = false;
						logFile("con", "One book :" + bookName
								+ " is not reserved for student: "
								+ student.username);
					}
				}
			}
		}
		break;
		
		// Dawson College
		case "dow": {
			synchronized (DowLibrary) {
				Book book = DowLibrary.get(bookName);
				if (book != null) {
					if (book.addBook(student.username)) {
						reserveBookeRecordFlag = true;
						// Log file create
						logFile("dow", "One book :" + bookName
								+ " is reserved for student: "
								+ student.username);
					} else {
						reserveBookeRecordFlag = false;
						logFile("dow", "One book :" + bookName
								+ " is not reserved for student: "
								+ student.username);
					}
				}
			}
		}
		break;
		default: {
		}
			break;
		}
		
		//return success and failure message
		return reserveBookeRecordFlag;
	}

	/*
	 * All records return as string
	 * 
	 * @param userName
	 * @param password
	 * @param educationalInstitude
	 * @return message
	 * 
	 * @throws RemoteException
	 */
	@SuppressWarnings("deprecation")
	public String getNonReturn(String userName, 
							   String password,
							   String educationalInstitude, 
							   short days) {
		String message = null;
		if ((!userName.equals("admin")) || (!password.equals("admin"))) {
			message = "Sorry!!! You are not admin";
			return message;
		}
		String messsage = null;
		DatagramSocket DPISSocket = null;
		DatagramSocket DPISSocket1 = null;
		DatagramSocket DPISSocket2 = null;
		byte[] buffer = new byte[1000000];
		switch (educationalInstitude) {
		// Vanier College
		case "van": {
			try {
				message = "Vanier College :";
				if (VanLibrary.size() > 0) {
					for (Book book : VanLibrary.values()) {
						if (book.index != 0) {
							for (int i = 0; i < book.index; i++) {
								Student student = StudentRecord
										.get(book.assocStudents[i].userName);
								if (book.assocStudents[i].getDueDays() >= days) {
									message += student.firstName + " "
											+ student.lastName + " "
											+ student.phoneNumber + "\n";
								}
							}
						}
					}
				}
				// UDP server call to Concordia Server
				DPISSocket = new DatagramSocket();
				InetAddress aHost = InetAddress.getByName("localhost");
				byte[] m = "send me information".getBytes();
				DatagramPacket request = new DatagramPacket(m,
						"send me information".length(), aHost, ConLibraryPort);
				DPISSocket.send(request);
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				DPISSocket.receive(reply);
				message += new String(reply.getData());
				
				// UDP server call to Dowson Collgee
				DatagramPacket request1 = new DatagramPacket(m,
						"send me information".length(), aHost, DowLibraryPort);
				DPISSocket.send(request1);
				DatagramPacket reply1 = new DatagramPacket(buffer,
						buffer.length);
				DPISSocket.receive(reply1);
				message += new String(reply1.getData());
			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			} finally {
				if (DPISSocket != null)
					DPISSocket.close();
			}
		}
		break;
		
		// For Concordia University
		case "con": {
			message = "\nConcordia University :";
			if (ConLibrary.size() > 0) {
				for (Book book : ConLibrary.values()) {
					if (book.index != 0) {
						for (int i = 0; i < book.index; i++) {
							Student student = StudentRecord
									.get(book.assocStudents[i].userName);
							if (book.assocStudents[i].getDueDays() >= days) {
								message += student.firstName + " "
										+ student.lastName + " "
										+ student.phoneNumber + "\n";
							}
						}
					}
				}
			}
			message += "\nVanier College :";
			if (VanLibrary.size() > 0) {
				for (Book book : VanLibrary.values()) {
					if (book.index != 0) {
						for (int i = 0; i < book.index; i++) {
							Student student = StudentRecord
									.get(book.assocStudents[i].userName);
							if (book.assocStudents[i].getDueDays() >= days) {
								message += student.firstName + " "
										+ student.lastName + " "
										+ student.phoneNumber + "\n";
							}
						}
					}
				}
			}
			message += "\nDowson College :";
			if (DowLibrary.size() > 0) {
				for (Book book : DowLibrary.values()) {
					if (book.index != 0) {
						for (int i = 0; i < book.index; i++) {
							Student student = StudentRecord
									.get(book.assocStudents[i].userName);
							if (book.assocStudents[i].getDueDays() >= days) {
								message += student.firstName + " "
										+ student.lastName + " "
										+ student.phoneNumber + "\n";
							}
						}
					}
				}
			}
			try {
				// UDP server call to Varnier college
				DPISSocket1 = new DatagramSocket();
				InetAddress aHost = InetAddress.getByName("localhost");
				byte[] m = "send".getBytes();
				DatagramPacket request = new DatagramPacket(m, "send".length(),
						aHost, VanLibraryPort);
				DPISSocket1.send(request);
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				DPISSocket1.receive(reply);
				messsage += new String(reply.getData());
				
				// UDP server call to Dawson college
				DatagramPacket request1 = new DatagramPacket(m,
						"send".length(), aHost, DowLibraryPort);
				DPISSocket1.send(request1);
				DatagramPacket reply1 = new DatagramPacket(buffer,
						buffer.length);
				DPISSocket1.receive(reply1);
				messsage += new String(reply1.getData());
			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (DPISSocket1 != null)
					DPISSocket1.close();
			}
		}
		break;
		
		// Dowson College
		case "dow": {
			message = "Dowson College :";
			if (DowLibrary.size() > 0) {
				for (Book book : DowLibrary.values()) {
					if (book.index != 0) {
						for (int i = 0; i < book.index; i++) {
							Student student = StudentRecord
									.get(book.assocStudents[i].userName);
							message += student.firstName + " "
									+ student.lastName + " "
									+ student.phoneNumber + "\n";
						}
					}
				}
			}
			try {
				// UDP server call to Varnier college
				DPISSocket2 = new DatagramSocket();
				InetAddress aHost = InetAddress.getByName("localhost");
				byte[] m = "send me information".getBytes();
				DatagramPacket request = new DatagramPacket(m,
						"send me information".length(), aHost, VanLibraryPort);
				DPISSocket2.send(request);
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				DPISSocket2.receive(reply);
				message += new String(reply.getData());
				// UDP server call to Concordia University
				DatagramPacket request1 = new DatagramPacket(m,
						"send me information".length(), aHost, ConLibraryPort);
				DPISSocket2.send(request1);
				DatagramPacket reply1 = new DatagramPacket(buffer,
						buffer.length);
				DPISSocket2.receive(reply1);
				message += new String(reply1.getData());
			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (DPISSocket2 != null)
					DPISSocket2.close();
			}
		}
			break;
		default: {
		}
			break;
		}
		return message;
	}

	/*
	 * Debug Tool to set custom numOfDays
	 * 
	 * @param username
	 * @param bookName
	 * @param numDays
	 */
	public static void setDuration(String username, 
								   String bookName, 
								   int numDays) {
		
		if (DowLibrary.size() > 0) {
			Book book = DowLibrary.get(bookName);
			if (book != null) {
				int i = book.searchRegister(username);
				if (i != -1) {
					if (i != -1) {
						book.assocStudents[i].dueDay = numDays;
					}
				}
			}
		}
		if (ConLibrary.size() > 0) {
			Book book = ConLibrary.get(bookName);
			if (book != null) {
				int i = book.searchRegister(username);
				if (i != -1) {
					if (i != -1) {
						book.assocStudents[i].dueDay = numDays;
					}
				}
			}
		}
		if (VanLibrary.size() > 0) {
			Book book = VanLibrary.get(bookName);
			if (book != null) {
				int i = book.searchRegister(username);
				if (i != -1) {
					book.assocStudents[i].dueDay = numDays;
				}
			}
		}
	}

	/*
	 * Vanier College UDP Server
	 */
	public class VanUdpServer implements Runnable {
		@Override
		public void run() {
			DatagramSocket aSocket = null;
			try {
				aSocket = new DatagramSocket(VanLibraryPort);
				byte[] buffer = new byte[100000];
				while (true) {
					DatagramPacket request = new DatagramPacket(buffer,
							buffer.length);
					aSocket.receive(request);
					String message = "Vanier College :";
					if (VanLibrary.size() > 0) {
						for (Book book : VanLibrary.values()) {
							if (book.index != 0) {
								for (int i = 0; i < book.index; i++) {
									Student student = StudentRecord
											.get(book.assocStudents[i].userName);
									message += student.firstName + " "
											+ student.lastName + " "
											+ student.phoneNumber + "\n";
								}
							}
						}
					}
					buffer = message.getBytes();
					DatagramPacket reply = new DatagramPacket(buffer,
							buffer.length, request.getAddress(),
							request.getPort());
					aSocket.send(reply);
				}
			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			} finally {
				if (aSocket != null)
					aSocket.close();
			}
		}
	}

	/*
	 * Concordia UDP Server
	 */
	public class ConUdpServer implements Runnable {
		@Override
		public void run() {
			DatagramSocket aSocket = null;
			try {
				aSocket = new DatagramSocket(ConLibraryPort);
				byte[] buffer = new byte[100000];
				while (true) {
					DatagramPacket request = new DatagramPacket(buffer,
							buffer.length);
					aSocket.receive(request);
					String message = "Concordia University :";
					if (ConLibrary.size() > 0) {
						for (Book book : ConLibrary.values()) {
							if (book.index != 0) {
								for (int i = 0; i < book.index; i++) {
									Student student = StudentRecord
											.get(book.assocStudents[i].userName);
									message += student.firstName + " "
											+ student.lastName + " "
											+ student.phoneNumber + "\n";
								}
							}
						}
					}
					buffer = message.getBytes();
					DatagramPacket reply = new DatagramPacket(buffer,
							buffer.length, request.getAddress(),
							request.getPort());
					aSocket.send(reply);
				}
			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (aSocket != null)
					aSocket.close();
			}
		}
	}

	/*
	 * Dowson College UDP Server
	 */
	public class DowUdpServer implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			DatagramSocket aSocket = null;
			try {
				aSocket = new DatagramSocket(DowLibraryPort);
				byte[] buffer = new byte[100000];
				while (true) {
					DatagramPacket request = new DatagramPacket(buffer,
							buffer.length);
					aSocket.receive(request);
					String message = "Dowson College :";
					if (DowLibrary.size() > 0) {
						for (Book book : DowLibrary.values()) {
							if (book.index != 0) {
								for (int i = 0; i < book.index; i++) {
									Student student = StudentRecord
											.get(book.assocStudents[i].userName);
									message += student.firstName + " "
											+ student.lastName + " "
											+ student.phoneNumber + "\n";
								}
							}
						}
					}
					buffer = message.getBytes();
					DatagramPacket reply = new DatagramPacket(buffer,
							buffer.length, request.getAddress(),
							request.getPort());
					aSocket.send(reply);
				}
			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			} finally {
				if (aSocket != null)
					aSocket.close();
			}
		}
	}

	/*
	 * Debug Tool
	 * 
	 * @return: void
	 */
	public static void runDebugTool() {
		Scanner keyboard = new Scanner(System.in);
		while (true) {
			try {
				System.out.println("User Name: ");
				String userName = keyboard.nextLine();
				System.out.println("Book: ");
				String bookName = keyboard.nextLine();
				System.out.println("No Of Days: ");
				int numOfDays = keyboard.nextInt();
				keyboard.nextLine();
				setDuration(userName, bookName, numOfDays);
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
	}

	/*
	 * logs the activities of servers
	 * 
	 * @return: void
	 * 
	 * @throws: SecurityException
	 */
	public static void logFile(String fileName, String Operation) throws SecurityException {
		fileName = fileName + "_server_log.txt";
		File log = new File(fileName);
		try {
			if (!log.exists()) {
			}
			log.setWritable(true);
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(Operation);
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println("COULD NOT LOG!!");
		}
	}

	/*
	 * Raw book creating for all libraries
	 * 
	 * @return: void
	 */
	public static void rawBookEntry() {
		Book bookA = new Book("english", "aa", 3);
		Book bookB = new Book("french", "bb", 3);
		
		// Vanlien Library Book insertion
		VanLibrary.put("english", bookA);
		VanLibrary.put("french", bookB);
		System.out.println("Varnier's books are: english (3 copies), french(3 copies)");
		// Concordia Library Book insertion
		bookA = new Book("cuda", "nicholas", 2);
		bookB = new Book("opencl", "munshi", 3);
		ConLibrary.put("cuda", bookA);
		ConLibrary.put("opencl", bookB);
		System.out.println("Concordia's books are: cuda (2 copies), opencl (3 copies)");
		
		// Dowson ULibrary Book insertion
		bookA = new Book("3dmath", "plecher", 1);
		bookB = new Book("4dmath", "Sr. plecher", 3);
		DowLibrary.put("3dmath", bookA);
		DowLibrary.put("4dmath", bookB);
		System.out.println("Dowson's books are: 3dmath (1 copies), 4dmath (3 copies)");
	}

	/*
	 * Running all UDP servers
	 * 
	 * @return: void
	 * 
	 * @throws: RemoteException
	 */
	public void exportUdpServer() {
		Thread t1 = new Thread(new VanUdpServer());
		Thread t2 = new Thread(new ConUdpServer());
		Thread t3 = new Thread(new DowUdpServer());
		t1.start();
		t2.start();
		t3.start();
		System.out.println("All udp servers are ready.");
	}
	
	/**
	* Run thread + UDP server
	*/
	public void run()
	{				
		//ORB Part
		try{		
			String[] args = null;
			ORB orb = ORB.init(args,null);
			POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			byte[] id = rootPoa.activate_object(this);
			org.omg.CORBA.Object ref = rootPoa.id_to_reference(id);
			String ior = orb.object_to_string(ref);
			System.out.println(ior);
			PrintWriter file = new PrintWriter(this.instituteName+".txt");
			file.println(ior);
			file.close();
			rootPoa.the_POAManager().activate();
			orb.run();	
		}catch(Exception err) {
			err.printStackTrace();
		}			
	}

	/*
	 * Main Method
	 */
	public static void main(String[] args) {
		// Raw data entry
		rawBookEntry();
		try {
			// Invoke message for running all UDP Server
			LibraryImpl ls = new LibraryImpl();
			ls.exportUdpServer();
			
			//CORBA block
			
			
			ls = new LibraryImpl();
			ls.instituteName = "van";			
			Thread server1 = new Thread(ls);
			server1.start();
			
			ls = new LibraryImpl();
			ls.instituteName = "con";	
			Thread server2 = new Thread(ls);			
			server2.start();
			
			ls = new LibraryImpl();
			ls.instituteName = "dow";	
			Thread server3 = new Thread(ls);
			server3.start();
			
			// Debug tool
			runDebugTool();
		} catch (Exception e) {
			System.out.println("Exception in servers Startup:" + e);
		}
	}


	@Override
	public boolean reserveInterLibrary(String username, String password,
			String bookName, String authorName) {
		// TODO Auto-generated method stub
		return false;
	}
}
