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

package com.simonsoftwaredesign.example.echo.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import com.simonsoftwaredesign.example.echo.core.EchoServer;
import com.simonsoftwaredesign.example.echo.core.Server;

/**
 * Server application entry point.
 * 
 * @author Anthony J Simon
 *
 */
public final class EchoServerApp implements Runnable, Server
{
  public static final String SERVER_ATTRIBUTE = "com.simonsoftwaredesign.example.echo.core.server";
  
  private Semaphore semaphore = null;
  
  public EchoServerApp ()
  {
    this.semaphore = new Semaphore ( 0 );
  }
  
  @Override
  public void run ()
  {
    // build web server
    org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server ( 19080 );
    
    Path path = Paths.get ( "../webapp" );
    List < Path > warFiles = fileList ( path );
    
    ContextHandlerCollection chc = new ContextHandlerCollection ();
    List < WebAppContext > waCtxs = new ArrayList < WebAppContext > ();
    
    for ( Path p : warFiles )
    {
      String ctxPath = p.getFileName ().toString ().replace ( ".war", "" );
      WebAppContext waCtx = new WebAppContext ();
      waCtx.setContextPath ( "/".concat ( ctxPath ) );
      waCtx.setAttribute ( SERVER_ATTRIBUTE, this );
      waCtx.setWar ( p.toAbsolutePath ().toFile ().toString () );
      waCtxs.add ( waCtx );
    }
    
    WebAppContext[] wacs = new WebAppContext[ waCtxs.size () ];
    chc.setHandlers ( waCtxs.toArray ( wacs ) );
    server.setHandler ( chc );
    
    // run web server
    try
    {
      server.start ();
    }
    catch ( Exception e )
    {
      throw new RuntimeException ( e );
    }
    
    // build echo server
    ExecutorService es = Executors.newSingleThreadExecutor ();
    InetSocketAddress isa = new InetSocketAddress ( "localhost", 19090 );
    EchoServer echoServer = new EchoServer ( isa );
    
    // run echo server
    es.execute ( echoServer );
    
    try
    {
      semaphore.acquire ();
    }
    catch ( InterruptedException e )
    {
      e.printStackTrace ();
    }
    
    try
    {
      server.stop ();
    }
    catch ( Exception e )
    {
      e.printStackTrace ();
    }
    
    echoServer.shutdown ();
    es.shutdown ();
  }
  
  @Override
  public void shutdown ()
  {
    // release the mail thread to run the 
    // shutdown procedure
    semaphore.release ();
  }
  
  private static List < Path > fileList ( Path directory )
  {
    List< Path > fileNames = new ArrayList<>();
    
    try ( DirectoryStream< Path > directoryStream = Files.newDirectoryStream ( directory, "*.war" ) )
    {
      for ( Path path : directoryStream )
      {
        fileNames.add( path );
      }
    } 
    catch ( IOException ex )
    {
      ex.printStackTrace ();
    }
    return Collections.unmodifiableList ( fileNames );
  }
  
  public static void main ( String[] args )
  {
    EchoServerApp esa = new EchoServerApp ();
    esa.run ();
  }
}