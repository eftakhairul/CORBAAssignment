package library;

import java.util.Date;

/**
 * Book Class
 * 
 * Holding all information of a individual book
 * 
 * @author Md Eftakhairul Islam <eftakhairul@gmail.com>
 */
public class Book {
	
	public String 	 bookName;
	public String 	 authorName; 
	public int 	  	 size; 
	public Register [] assocStudents;
	
	public int index;
	
	/**
	 * Book constructor	 
	 */
	public Book(String bookName, 
				String authorName, 
				int size) {
		
		this.bookName       		= bookName;
		this.authorName        		= authorName;
		this.size    				= size;
		this.assocStudents          = new Register[100];
		this.index					= 0;
	}
	
	
	/**
	 * Making association between student and book 
	 * 
	 * @return true|false
	 */
	public boolean addBook(String username) {
		if(this.size == 0) {
			return false;
		} else {
			Date date = new Date();
			this.assocStudents[index++] = new Register(username, this.bookName, date);				
			size--;
			return true;				
		}
	}
	
	/**
	 *  Search register information by book 
	 *  
	 * @return index
	 */
	public int searchRegister(String userName) {
		for(int i = 0; i< this.index; i++)
		{
			if(this.assocStudents[i].userName.equals(userName)) {
				return i; 
			}
		} 
		
		return -1;
	}
}
