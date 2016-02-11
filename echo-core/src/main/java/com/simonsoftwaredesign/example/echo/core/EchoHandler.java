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
import java.nio.charset.StandardCharsets;

/**
 * Invoked to handle a single connection.
 * 
 * @author Anthony J Simon
 *
 */
public final class EchoHandler implements Runnable
{
  private Socket socket;
  
  /**
   * Create a handler passing in the connection it is to service.
   * 
   * @param socket the connection end point used to communicate with the remote peer.
   */
  public EchoHandler ( Socket socket )
  {
    this.socket = socket;
  }
  
  @Override
  public void run ()
  {
    try ( Socket s = socket )
    {
      BufferedReader reader = new BufferedReader ( new InputStreamReader ( s.getInputStream (), StandardCharsets.UTF_8 ) );
      PrintWriter writer = new PrintWriter ( new OutputStreamWriter ( new BufferedOutputStream ( s.getOutputStream () ) , StandardCharsets.UTF_8 ) );
      
      String message = null;
      while ( ( message = reader.readLine () ) != null )
      {
        System.out.format ( "[%s]: %s%n", s.getRemoteSocketAddress (), message );
        writer.println ( message );
        writer.flush ();
      }
      System.out.format ( "[%s]: Connection closed%n", s.getRemoteSocketAddress () );
      
      reader.close ();
      writer.close ();
    }
    catch ( IOException e )
    {
      e.printStackTrace ();
    }
  }
}