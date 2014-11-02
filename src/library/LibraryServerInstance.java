package library;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;


/**
 * Server Instance
 * 
 * @author Eftakhairul Islam <eftakhairul@gmail.com>  
 */
public class LibraryServerInstance {

	/**	  
	 * @param args
	 * @throws InvalidName 
	 * @throws WrongPolicy 
	 * @throws ServantAlreadyActive 
	 * @throws ObjectNotActive 
	 * @throws FileNotFoundException 
	 * @throws AdapterInactive 
	 */
	public static void main(String[] args) throws InvalidName, 
												  ServantAlreadyActive, 
												  WrongPolicy, 
												  ObjectNotActive, 
												  FileNotFoundException, 
												  AdapterInactive {
		
		ORB orb 	= ORB.init(args,null);
		POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		
		LibraryImpl aLibraryIntance = new LibraryImpl();
		byte[] id 					= rootPoa.activate_object(aLibraryIntance); 
		org.omg.CORBA.Object ref 	= rootPoa.id_to_reference(id);
		
		String ior 					= orb.object_to_string(ref);
		System.out.println(ior);
		
		PrintWriter file = new PrintWriter("lib.txt");
		file.println(ior);
		file.close();
		
		rootPoa.the_POAManager().activate();
		orb.run();
	}
}
