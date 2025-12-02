import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;

public class Main
{
	/*this code works on the pc*/ 
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AstProgram  ast;
		FileReader fileReader=null;
		PrintWriter fileWriter=null;
		String inputFileName = argv[0];
		String outputFileName = argv[1];
		
		try
		{
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			fileReader = new FileReader(inputFileName);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			fileWriter = new PrintWriter(outputFileName);
			
			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(fileReader);
			
			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			p = new Parser(l);

			/***********************************/
			/* [5] 3 ... 2 ... 1 ... Parse !!! */
			/***********************************/
			ast = (AstProgram) p.parse().value;
			
			/*************************/
			/* [6] Print the AST ... */
			/*************************/
			if (ast != null) 
			{
                ast.printMe();
				ast.semantMe();
                AstGraphviz.getInstance().finalizeFile();
            }


			fileWriter.print("OK");
            fileWriter.flush();

			
			/*************************/
			/* [7] Close output file */
			/*************************/
			fileWriter.close();
			
			/*************************************/
			/* [8] Finalize AST GRAPHIZ DOT file */
			/*************************************/
			AstGraphviz.getInstance().finalizeFile();
    	}
			     
		catch (SyntaxErrorException e)
        {
            // Syntax error: must print ERROR(line)
            try {
                if (fileWriter == null) {
                    fileWriter = new PrintWriter(outputFileName);
                }
                fileWriter.print("ERROR(" + e.getLine() + ")");
                fileWriter.flush();
            } catch (Exception ignored) {}
        }
        catch (Error e)
        {
            // Lexical error: must print ERROR (no brackets)
            try 
			{
                if (fileWriter == null) {
                    fileWriter = new PrintWriter(outputFileName);
                }
                fileWriter.print("ERROR");
                fileWriter.flush();
            } 
			catch (Exception ignored) 
			{}
        }
        catch (Exception e)
        {
            // Anything else -> plain ERROR
            try 
			{
                if (fileWriter == null) {
                    fileWriter = new PrintWriter(outputFileName);
                }
                fileWriter.print("ERROR");
                fileWriter.flush();
            } 
			catch (Exception ignored) 
			{}
			System.err.println("==== General exception caught in Main ====");
    		e.printStackTrace(System.err);


        }
        finally
        {
            try {
                if (fileReader != null) fileReader.close();
            } catch (IOException ignored) {}
            if (fileWriter != null) fileWriter.close();
        }
    }
}