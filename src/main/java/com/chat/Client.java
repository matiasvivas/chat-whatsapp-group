import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.StringReader;

public class Client {

  private String host;
  private int port;

  public static void main(String[] args) throws UnknownHostException, IOException {
    new Client("127.0.0.1", 12345).run();
  }

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void run() throws UnknownHostException, IOException {
    // conectamos el cliente al servidor
    Socket client = new Socket(host, port);
    System.out.println("Conexion al servidor exitosa!");

    // Hacemos get socket output stream (Donde el cliente podra enviar su mensaje)
    PrintStream output = new PrintStream(client.getOutputStream());

    // Ingresa un nick para identificarlo (nombre de contacto del celular)
    Scanner sc = new Scanner(System.in);
    System.out.print("Ingrese un nick: ");
    String nickname = sc.nextLine();

    // nick al servidor
    output.println(nickname);

    // Creamos un nuevo hilo para el manejo del mensaje en el servidor
    new Thread(new ReceivedMessagesHandler(client.getInputStream())).start();

    // Leemos los mensajes y enviamos al servidor
    System.out.println("Mensaje: \n");

    // Mientras haya nuevos mensajes
    while (sc.hasNextLine()) {
      output.println(sc.nextLine());
    }
    
    output.close();
    sc.close();
    client.close();
  }
}

class ReceivedMessagesHandler implements Runnable {

  private InputStream server;

  public ReceivedMessagesHandler(InputStream server) {
    this.server = server;
  }

  public void run() {
    // Recibir los nuevos mensajes del servidor y enviarlos a la pantalla
    Scanner s = new Scanner(server);
    String tmp = "";
    while (s.hasNextLine()) {
      tmp = s.nextLine();
      if (tmp.charAt(0) == '[') {
        tmp = tmp.substring(1, tmp.length()-1);
        System.out.println(
            "\nLista de usuarios: " +
            new ArrayList<String>(Arrays.asList(tmp.split(","))) + "\n"
            );
      }else{
        try {
          System.out.println("\n" + getTagValue(tmp));
          // System.out.println(tmp);
        } catch(Exception ignore){}
      }
    }
    s.close();
  }

  public static String getTagValue(String xml){
    return  xml.split(">")[2].split("<")[0] + xml.split("<span>")[1].split("</span>")[0];
  }

}
