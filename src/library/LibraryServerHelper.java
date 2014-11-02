package library;

/** 
 * Helper class for : LibraryServer
 *  
 * @author OpenORB Compiler
 */ 
public class LibraryServerHelper
{
    /**
     * Insert LibraryServer into an any
     * @param a an any
     * @param t LibraryServer value
     */
    public static void insert(org.omg.CORBA.Any a, library.LibraryServer t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract LibraryServer from an any
     *
     * @param a an any
     * @return the extracted LibraryServer value
     */
    public static library.LibraryServer extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return library.LibraryServerHelper.narrow( a.extract_Object() );
        }
        catch ( final org.omg.CORBA.BAD_PARAM e )
        {
            throw new org.omg.CORBA.MARSHAL(e.getMessage());
        }
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the LibraryServer TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "LibraryServer" );
        }
        return _tc;
    }

    /**
     * Return the LibraryServer IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:library/LibraryServer:1.0";

    /**
     * Read LibraryServer from a marshalled stream
     * @param istream the input stream
     * @return the readed LibraryServer value
     */
    public static library.LibraryServer read(org.omg.CORBA.portable.InputStream istream)
    {
        return(library.LibraryServer)istream.read_Object(library._LibraryServerStub.class);
    }

    /**
     * Write LibraryServer into a marshalled stream
     * @param ostream the output stream
     * @param value LibraryServer value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, library.LibraryServer value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to LibraryServer
     * @param obj the CORBA Object
     * @return LibraryServer Object
     */
    public static LibraryServer narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof LibraryServer)
            return (LibraryServer)obj;

        if (obj._is_a(id()))
        {
            _LibraryServerStub stub = new _LibraryServerStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to LibraryServer
     * @param obj the CORBA Object
     * @return LibraryServer Object
     */
    public static LibraryServer unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof LibraryServer)
            return (LibraryServer)obj;

        _LibraryServerStub stub = new _LibraryServerStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
