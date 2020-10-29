import java.io.*;
import java.util.Scanner;

import client.*;
import common.*;

public class ServerConsole implements ChatIF  {
    
  /**
   * The default port to connect on.
   */
  public static final int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  //ServerClient client;
  public static final String LOGIN = "SERVER MSG";
  EchoServer sv;
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ServerConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ServerConsole(int port) 
  {
    
    sv = new EchoServer(port);
    try 
    {
      sv.listen();
      //client = new ServerClient(host, port,this,sv);
    } 
    catch(IOException exception) 
    {
      System.out.println(exception);
      System.out.println("Error: Can't setup connection!"
                + " Terminating server.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */

  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        sv.handleMessageFromServerConsole(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!" + ex.toString());
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println(message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    int port = DEFAULT_PORT;
    try
    {
      port = Integer.parseInt(args[0]);
    }
    catch(ArrayIndexOutOfBoundsException | NumberFormatException e)
    {
      
    }
    ServerConsole chat= new ServerConsole(port);
    chat.accept();  //Wait for console data
  }
}
