package library;

/**
 * Holder class for : LibraryServer
 * 
 * @author OpenORB Compiler
 */
final public class LibraryServerHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal LibraryServer value
     */
    public library.LibraryServer value;

    /**
     * Default constructor
     */
    public LibraryServerHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public LibraryServerHolder(library.LibraryServer initial)
    {
        value = initial;
    }

    /**
     * Read LibraryServer from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = LibraryServerHelper.read(istream);
    }

    /**
     * Write LibraryServer into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        LibraryServerHelper.write(ostream,value);
    }

    /**
     * Return the LibraryServer TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return LibraryServerHelper.type();
    }

}
