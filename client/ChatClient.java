// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  String login;
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String login,String host, int port, ChatIF clientUI) 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.login = login;
    try {
      openConnection();
      sendToServer("#login " + login);
    } catch(IOException e) {
      System.out.println("Cannot open connection. Awaiting command.");
    }
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    boolean isLocalCmd = false;
    if (message.startsWith("#")) {
      isLocalCmd = true;
      String[] components = message.substring(1).split(" ",2);
      switch(components[0]) {
        case "quit":
          quit();
        break;
        case "login":
          if (isConnected()) {
            clientUI.display("Already connected to server");
          } else {
            try {
              openConnection();
              login = components[1];
              sendToServer("#login " + login);
            } catch (Exception e) {
              clientUI.display("could not open connection");
            }
          }
        break;
        case "logoff":
        try {
          closeConnection();
        } catch (Exception e) {
          clientUI.display("could not close connection");
        }
        break;
        case "gethost":
          clientUI.display("The host is " + super.getHost());
        break;
        case "getport":
          clientUI.display("The port is " + super.getPort());
        break;
        case "setport":
          setPort(Integer.parseInt(components[1]));
          clientUI.display("Port set to: " + super.getPort());
        break;
        case "sethost":
          setHost(components[1]);
          clientUI.display("Host set to: " + super.getHost());
        break;
        default:
        isLocalCmd = false;
        break;
      }
    } 
    if (!isLocalCmd) {
      try
      {
        sendToServer(message);
      } catch(IOException e) {
        clientUI.display("Could not send message to server.  Terminating client.");
        quit();
      }
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try {
      closeConnection();
    } catch(Exception e) {}
    System.exit(0);
  }

  @Override
  protected void connectionClosed() {
    System.out.println("Connection Closed.");
  }
  @Override
  protected void connectionException(Exception exception) {
    System.out.println("Abnormal termination of connection.");
	}
}
//End of ChatClient class
