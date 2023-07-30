package hearserv.app_elements;

import hearserv.io.networking.ClientConnection;

import java.util.ArrayList;

// TODO This class must support concurrent access of all public methods

public class UserManager {
	private ArrayList<User> users;
	public UserManager(){
		users = new ArrayList<User>();
	}
	
	public User lookupUser(long id) throws UserNotFoundException {
		for (User u : users){
			if (u.getUserID() == id){
				return u;
			}
		}
		throw new UserNotFoundException();
	}
	
	/**
	 * adds a user
	 * @param nm name of the client
	 * @param cn ClientConnection
	 * @return the user that was added
	 */
	public User addUser(String nm, ClientConnection cn){
		User newUser;
		if( users.size() == 0){
			newUser = new User(0, nm, cn);
		}else{
			newUser = new User(users.size(), nm, cn);
		}
		users.add(newUser);
		return newUser;
	}
	
	public void removeUser(long id) throws UserNotFoundException {
		for (User u : users){
			if(u.getUserID() == id){
				users.remove(u);
			}			
		}
		throw new UserNotFoundException();
	}
}
