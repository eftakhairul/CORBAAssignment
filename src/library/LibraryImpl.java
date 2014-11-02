package library;

public class LibraryImpl extends LibraryServerPOA {

	@Override
	public boolean createAccount(String firstName, String lastName,
			String emailAddress, String phoneNumber, String username,
			String password, String educationalInstitude) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reserveBook(String username, String password,
			String bookName, String authorName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNonReturn(String userName, String password,
			String educationalInstitude, short days) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean reserveInterLibrary(String username, String password,
			String bookName, String authorName) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
