package com.uu.app;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        GatlParserHandler handler = new GatlParserHandler();

        handler.ParseAndPrint("reify(pi(sigma( interpol(noise,locations)  , euros)) )");

        System.out.println( "Hello World!" );
    }
}
