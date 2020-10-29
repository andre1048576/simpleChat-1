// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;
/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println();
    System.out.println("Message received: " + msg + " from " + client.getInfo("login"));
    if (msg.toString().length() > 7 && "#login ".equals(msg.toString().substring(0,7))) {
      String loginId = msg.toString().substring(7);
      client.setInfo("login", loginId);
      System.out.println(loginId + " has logged on.");
      this.sendToAllClients(loginId + " has logged on.");
    } else this.sendToAllClients(client.getInfo("login") + "> " + msg);
  }

  private void handleServerMessage(String msg) {
    System.out.println("SERVER MSG> " + msg);
    this.sendToAllClients("SERVER MSG> " + msg);
  }
    
  public void handleMessageFromServerConsole(String message)
  {
    boolean isCmd = false;
    if (message.startsWith("#")) {
      isCmd = true;
      String[] components = message.substring(1).split(" ",2);
      String command = components[0];
      String param = components.length > 1 ? components[1] : null;
      if ("quit".equals(command)) {
        try {
        this.close();
        } catch (Exception e) {}
        finally {
        System.exit(0);
        }
      } else if ("stop".equals(command)) {
        this.stopListening();
        this.sendToAllClients("WARNING - the server has stopped listening for connections");
      } else if ("close".equals(command)) {
        try {
          this.sendToAllClients("SERVER SHUTTING DOWN! DISCONNECTING!");
          this.close();
        } catch (Exception e) {}
      } else if ("start".equals(command)) {
        try {
          this.listen();
          } catch (Exception e) {}
      } else if ("getport".equals(command)) {
        System.out.println("the server port is: " + this.getPort());
      } else if ("setport".equals(command) && !this.isListening()) {
        System.out.println("port set to: " + this.getPort());
        this.setPort(Integer.parseInt(param));
      } else isCmd = false;
    } 
    if (!isCmd) {
      try
      {
        handleServerMessage(message);
      } catch(Exception e) {
        System.out.println("Could not send message.  Terminating server.");
        System.exit(0);
      }
    }
  }
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  protected void clientConnected(ConnectionToClient client) {
    System.out.println
      ("A new client is attempting to connect to the server.");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
  @Override
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {
      sendToAllClients(client.getInfo("login") + " has disconnected");
      System.out.println(client.getInfo("login") + " has disconnected");
    }
    @Override
    synchronized protected void clientException(
      ConnectionToClient client, Throwable exception) {
        System.out.println(client.getInfo("login") + " has disconnected");
        sendToAllClients(client.getInfo("login") + " has disconnected");}
}
//End of EchoServer class
