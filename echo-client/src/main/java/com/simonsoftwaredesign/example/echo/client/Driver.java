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

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * The client application entry point.
 * 
 * @author Anthony J Simon
 *
 */
public final class Driver
{
  public static void main ( String[] args )
  {
    SwingUtilities.invokeLater ( new AppInvoker () );
  }
  
  private static final class AppInvoker implements Runnable
  {
    @Override
    public void run ()
    {
      try
      {
        JFrame jFrame = new JFrame ();
        jFrame.setTitle ( "Echo Client" );
        jFrame.getContentPane ().setLayout ( new BorderLayout () );
        ClientPanel cp = new ClientPanel ();
        jFrame.getContentPane ().add ( cp, BorderLayout.CENTER );
        jFrame.setDefaultCloseOperation ( JFrame.DO_NOTHING_ON_CLOSE );
        jFrame.addWindowListener ( new CloseListener ( jFrame, cp ) );
        jFrame.getRootPane ().setDefaultButton ( cp.getSendButton () );
        jFrame.pack ();
        jFrame.setLocationRelativeTo ( null );
        jFrame.setVisible ( true );
      }
      catch ( IOException e )
      {
        e.printStackTrace ();
      }
    }
  }
  
  /**
   * Listen for the application close request.
   * 
   * @author Anthony J Simon
   *
   */
  private static final class CloseListener extends WindowAdapter
  {
    JFrame jFrame;
    ClientPanel clientPanel;
    
    public CloseListener ( JFrame jFrame, ClientPanel clientPanel )
    {
      this.jFrame = jFrame;
      this.clientPanel = clientPanel;
    }
    
    @Override
    public void windowClosing ( WindowEvent e )
    {
      clientPanel.shutdownClient ();
      jFrame.dispose ();
    }
  }
}