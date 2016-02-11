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

package com.simonsoftwaredesign.example.echo.core;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents a single connection to a remote server.
 * 
 * @author Anthony J Simon
 *
 */
public final class EchoClient implements AutoCloseable
{
  private Socket socket;
  private BufferedReader reader;
  private PrintWriter writer;
  
  /**
   * Create a new EchoClient and connect it to the remote end point
   * 
   * @param address the remote end point to connect to
   * @throws IOException if an exception occurs during connection negotiation
   */
  public EchoClient ( SocketAddress address ) throws IOException
  {
    Objects.requireNonNull ( address, "Address is null" );
    this.socket = new Socket ();
    this.socket.connect ( address );
    this.reader = new BufferedReader ( new InputStreamReader ( socket.getInputStream (), StandardCharsets.UTF_8 ) );
    this.writer = new PrintWriter ( new OutputStreamWriter ( new BufferedOutputStream ( socket.getOutputStream () ), StandardCharsets.UTF_8 ) );
  }
  
  /**
   * Send a message to the server and wait for the response.
   * 
   * @param message the message to send to the server
   * @return the response from the server
   * @throws IOException if an error occurs during the communication operations
   */
  public String send ( String message ) throws IOException
  {
    if ( socket.isClosed () )
      throw new IllegalStateException ( "Closed" );
    
    writer.println ( message );
    writer.flush ();
    String response = reader.readLine ();
    
    return response;
  }
  
  @Override
  public void close ()
  {
    try
    {
      writer.close ();
      @SuppressWarnings ( "unused" )
      String m = null;
      while ( ( m = reader.readLine () ) != null );
      reader.close ();
    }
    catch ( IOException e )
    {
      e.printStackTrace ();
    }
  }
}