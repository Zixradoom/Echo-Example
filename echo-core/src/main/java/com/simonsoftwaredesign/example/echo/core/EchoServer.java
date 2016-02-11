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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implements the brains of the server that will 
 * listen for and service requests
 * 
 * @author Anthony J Simon
 *
 */
public final class EchoServer implements Runnable, Server
{
  private static final int DEFAULT_TIMEOUT = 1000; // 1 second
  
  private SocketAddress address;
  private ExecutorService executorService;
  
  private volatile boolean run = true;
  
  /**
   * Create the server object
   * 
   * @param address the address the server should listen on
   */
  public EchoServer ( SocketAddress address )
  {
    this.address = Objects.requireNonNull ( address, "Address is null" );
    this.executorService = Executors.newCachedThreadPool ();
  }
  
  @Override
  public void run ()
  {
    try ( ServerSocket server = new ServerSocket () )
    {
      server.setSoTimeout ( DEFAULT_TIMEOUT );
      server.bind ( address );
      
      while ( run )
      {
        try
        {
          Socket socket = server.accept ();
          System.out.format ( "[%s]: New connection%n", socket.getRemoteSocketAddress () );
          EchoHandler eh = new EchoHandler ( socket );
          executorService.execute ( eh );
        }
        catch ( SocketTimeoutException ste )
        {
          // ignore, timeouts are expected
        }
      }
      
      server.close ();
    }
    catch ( IOException e )
    {
      run = false;
      executorService.shutdown ();
      e.printStackTrace ();
    }
  }
  
  @Override
  public void shutdown ()
  {
    run = false;
    executorService.shutdown ();
  }
}