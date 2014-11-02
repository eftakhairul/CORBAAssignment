package library;

/**
 * Interface definition: LibraryServer.
 * 
 * @author OpenORB Compiler
 */
public interface LibraryServerOperations
{
    /**
     * Operation createAccount
     */
    public boolean createAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String username, String password, String educationalInstitude);

    /**
     * Operation reserveBook
     */
    public boolean reserveBook(String username, String password, String bookName, String authorName);

    /**
     * Operation getNonReturn
     */
    public String getNonReturn(String userName, String password, String educationalInstitude, short days);

    /**
     * Operation reserveInterLibrary
     */
    public boolean reserveInterLibrary(String username, String password, String bookName, String authorName);

}
