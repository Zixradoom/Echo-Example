/*
The MIT License (MIT)

Copyright (c) [2016] [Anthony Joseph Simon]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.simonsoftwaredesign.example.echo.client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import com.simonsoftwaredesign.example.echo.core.EchoClient;

/**
 * This is the panel that handles laying out and implementing the
 * GUI for the echo client
 * 
 * @author Anthony J Simon
 *
 */
public final class ClientPanel extends JPanel
{
  private static final long serialVersionUID = -737732331509580469L;
  
  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 19090;
  private static final String SEND_MESSAGE_ACTION = "send.message";

  private ExecutorService clientWorker;
  private EchoClient echoClient;
  
  private JScrollPane jScrollPane;
  private JTextArea jTextArea;
  private JTextField jTextField;
  private JButton jButton;
  
  /**
   * Create the panel
   * 
   * @throws IOException if an error occurs initializing the client
   */
  public ClientPanel () throws IOException
  {
    super ( new SpringLayout () );

    // init connection and pool
    echoClient = new EchoClient ( new InetSocketAddress ( DEFAULT_HOST, DEFAULT_PORT ) );
    clientWorker = Executors.newSingleThreadExecutor ();

    // build gui
    jTextArea = new JTextArea ( 40, 80 );
    Font f = new Font ( Font.MONOSPACED, Font.PLAIN, 16 );
    jTextArea.setFont ( f );
    jTextArea.setLineWrap ( true );
    jTextArea.setWrapStyleWord ( true );
    jTextArea.setEditable ( false );
    
    jScrollPane = new JScrollPane ( jTextArea );
    jScrollPane.setHorizontalScrollBarPolicy ( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
    jScrollPane.setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
    jScrollPane.setPreferredSize ( new Dimension ( 640, 480 ) );
    jScrollPane.setMinimumSize ( new Dimension ( 200, 200 ) );
    
    jTextField = new JTextField ( 40 )
    {
      private static final long serialVersionUID = -2380881384854851017L;

      @Override
      public Dimension getMaximumSize ()
      {
        return new Dimension ( Integer.MAX_VALUE, this.getPreferredSize ().height );
      }
    };
    jTextField.setFont ( f );
    
    jButton = new JButton ( "Send" );
    jButton.setActionCommand ( SEND_MESSAGE_ACTION );
    jButton.addActionListener ( new SendListener ( echoClient, jTextArea, jTextField, clientWorker ) );
    
    // add the components
    add ( jScrollPane );
    add ( jTextField );
    add ( jButton );
    
    // setup the layout
    SpringLayout sl = ( SpringLayout ) getLayout ();
    
    sl.putConstraint ( SpringLayout.WEST, jScrollPane,
                       5,
                       SpringLayout.WEST, this );
                       
    sl.putConstraint ( SpringLayout.NORTH, jScrollPane,
                       5,
                       SpringLayout.NORTH, this );
                       
    sl.putConstraint ( SpringLayout.EAST, jScrollPane,
                       -5,
                       SpringLayout.EAST, this );
    sl.putConstraint ( SpringLayout.NORTH, jTextField,
                       5,
                       SpringLayout.SOUTH, jScrollPane );
    sl.putConstraint ( SpringLayout.WEST, jTextField,
                       5,
                       SpringLayout.WEST, this );
    sl.putConstraint ( SpringLayout.SOUTH, this,
                       5,
                       SpringLayout.SOUTH, jTextField );
    sl.putConstraint ( SpringLayout.NORTH, jButton,
                       5,
                       SpringLayout.SOUTH, jScrollPane );
    sl.putConstraint ( SpringLayout.WEST, jButton,
                       5,
                       SpringLayout.EAST, jTextField );
    sl.putConstraint ( SpringLayout.EAST, this,
                       5,
                       SpringLayout.EAST, jButton );    
  }
  
  /**
   * 
   * @return a JBotton that sends the entered text to the server
   */
  public JButton getSendButton ()
  {
    return jButton;
  }
  
  /**
   * Shutdown the worker thread and close the socket
   */
  public void shutdownClient ()
  {
    clientWorker.execute ( new CloseClient ( echoClient ) );
    clientWorker.shutdown ();
  }
  
  /**
   * Listen for button clicks, collect the entered text
   * and send it to the server.
   * 
   * @author Anthony J Simon
   *
   */
  private static final class SendListener implements ActionListener
  {
    private EchoClient echoClient;
    private JTextArea jTextArea;
    private JTextField jTextField;
    private ExecutorService clientWorker;

    public SendListener ( EchoClient echoClient, JTextArea jTextArea, JTextField jTextField, ExecutorService clientWorker )
    {
      this.echoClient = echoClient;
      this.jTextField = jTextField;
      this.jTextArea = jTextArea;
      this.clientWorker = clientWorker;
    }
  
    @Override
    public void actionPerformed ( ActionEvent e )
    {
      if ( SEND_MESSAGE_ACTION.equals ( e.getActionCommand () ) )
      {
        String message = jTextField.getText ();
        jTextField.setText ( "" );
        jTextArea.append ( "You: " + message + "\n" );
        clientWorker.execute ( new MessageTask ( message, jTextArea, echoClient ) );
      }
    }
  }
  
  /**
   * Sends the message to the server and waits for a response.
   * 
   * @author Anthony J Simon
   *
   */
  private static final class MessageTask implements Runnable
  {
    private EchoClient echoClient;
    private String message;
    private JTextArea jTextArea;
  
    public MessageTask ( String message, JTextArea jTextArea, EchoClient echoClient )
    {
      this.message = message;
      this.jTextArea = jTextArea;
      this.echoClient = echoClient;
    }
  
    @Override
    public void run ()
    {
      try
      {
        String response = echoClient.send ( message );
        SwingUtilities.invokeLater ( new Updater ( response, jTextArea ) );
      }
      catch ( IOException e )
      {
        e.printStackTrace ();
      }
    }
  }
  
  /**
   * Meant to be invoked on the EventDispatchThread to update
   * the {@link JTextArea} being used as a log
   * @author Anthony J Simon
   *
   */
  private static final class Updater implements Runnable
  {
    private String message;
    private JTextArea jTextArea;
  
    public Updater ( String message, JTextArea jTextArea )
    {
      this.message = message;
      this.jTextArea = jTextArea;
    }
  
    public void run ()
    {
      jTextArea.append ( "Server: " + message + "\n" );
    }
  }
  
  /**
   * Close the echoClient on the Worker Thread
   * 
   * @author Anthony J Simon
   *
   */
  private static final class CloseClient implements Runnable
  {
    private EchoClient echoClient;
    
    public CloseClient ( EchoClient echoClient )
    {
      this.echoClient = echoClient;
    }
    
    @Override
    public void run ()
    {
      echoClient.close ();
    }
  }
}