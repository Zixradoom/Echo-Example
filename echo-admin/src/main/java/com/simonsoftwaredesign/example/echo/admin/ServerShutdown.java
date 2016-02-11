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

package com.simonsoftwaredesign.example.echo.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.simonsoftwaredesign.example.echo.core.Server;

/**
 * Any query that hits this servlet will instruct the server to shutdown
 * 
 * @author Anthony J Simon
 *
 */
public final class ServerShutdown extends HttpServlet 
{
  private static final long serialVersionUID = 6483683499197635636L;
  
  public static final String SERVER_ATTRIBUTE = "com.simonsoftwaredesign.example.echo.core.server";
  
  @Override
  public void doGet ( HttpServletRequest request,
    HttpServletResponse response ) throws ServletException, IOException
  {
      // Set response content type
      response.setContentType ( "text/html" );

      // get the server
      Server server = ( Server ) getServletContext ().getAttribute ( SERVER_ATTRIBUTE );
      
      // Actual logic goes here.
      String header = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><meta name=\"author\" content=\"Anthony J Simon\"></head><body>";
      
      String body = null;
      if ( server == null )
      {
        body = "<h1>No server registered!</h1>";
      }
      else
      {
        server.shutdown ();
        body = "<h1>Server Shutdown!</h1>";
      }
      
      String footer = "</body></html>";
      
      PrintWriter out = response.getWriter ();
      out.print ( header );
      out.print ( body );
      out.println ( footer );
  }
}