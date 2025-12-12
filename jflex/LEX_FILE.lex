/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/

import java_cup.runtime.*;

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/
      
%%
   
/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/
   
/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column
    
/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup
   
/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{
    /*********************************************************************************/
    /* Create a new java_cup.runtime.Symbol with information about the current token */
    /*********************************************************************************/
  private Symbol symbol(int type)               {return new Symbol(type, yyline+1, yycolumn+1);}
  private Symbol symbol(int type, Object value) {return new Symbol(type, yyline+1, yycolumn+1, value);}
    /*******************************************/
    public int getLine() { return yyline + 1; } 

    /**********************************************/
    /* Enable token position extraction from main */
    /**********************************************/
    public int getTokenStartPosition() { return yycolumn + 1; } 
%}

/***********************/
/* MACRO DECLARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
WhiteSpace		= {LineTerminator} | [ \t\f]
INTEGER			= 0 | [1-9][0-9]*
LEADING_ZERO       = 0{Digit}+
Letter 			= [a-zA-Z] 			
Digit      		= [0-9]				
ID				= {Letter}({Letter}|{Digit})*    		
WhiteSpace_no_new_line=  [ \t\f]
Quote 		= \"					
StringLetter = {Letter}   			
StringBody = {StringLetter}*   
String = {Quote}{StringBody}{Quote}   				

// state for the block comments :

%state BLOCKCOMMENT

AllowedPunctInTypeOneComment = [\(\)\[\]\{\}\?\!\+\-\*\/\.\;]
AllowedPunctInTypeTwoComment = [\(\)\[\]\{\}\?\!\+\-\.\;]	
TypeOneLineCommentCharAllowed = {Letter}|{Digit}|{WhiteSpace_no_new_line}|{AllowedPunctInTypeOneComment}
TypeTwoBlockCommentCharAllowed = {Letter}|{Digit}|{WhiteSpace}|{AllowedPunctInTypeTwoComment}
LineCommentStart  = "//"
BlockCommentStart = "/*"
BlockCommentEnd   = "*/"

        // a line comment we will define as a macro, but a block comment we will define as a state :
LineComment = {LineCommentStart}{TypeOneLineCommentCharAllowed}*{LineTerminator}


/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/
   
/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> 
{

":="                 { return symbol(TokenNames.ASSIGN); }
"("                  { return symbol(TokenNames.LPAREN); }
")"                  { return symbol(TokenNames.RPAREN); }
"["                  { return symbol(TokenNames.LBRACK); }
"]"                  { return symbol(TokenNames.RBRACK); }
"{"                  { return symbol(TokenNames.LBRACE); }
"}"                  { return symbol(TokenNames.RBRACE); }
"+"					 { return symbol(TokenNames.PLUS);}
"-"					 { return symbol(TokenNames.MINUS);}
"*"					 { return symbol(TokenNames.TIMES);}

/* before trying to consume divide operation we need to check if it is the start of a comment*/

{LineComment}       {/* just skip  */}
{LineCommentStart}[^\r\n]*{LineTerminator} { return symbol(TokenNames.ERROR); }
{BlockCommentStart}       { yybegin(BLOCKCOMMENT); }

"/"					{ return symbol(TokenNames.DIVIDE);}
","                  { return symbol(TokenNames.COMMA); }
"."                  { return symbol(TokenNames.DOT); }
";"                  { return symbol(TokenNames.SEMICOLON); }
"="                  { return symbol(TokenNames.EQ); }
"<"                  { return symbol(TokenNames.LT); }
">"                  { return symbol(TokenNames.GT); }
"array"              { return symbol(TokenNames.ARRAY); }
"class"              { return symbol(TokenNames.CLASS); }
"return"             { return symbol(TokenNames.RETURN); }
"while"              { return symbol(TokenNames.WHILE); }
"if"                 { return symbol(TokenNames.IF); }
"else"               { return symbol(TokenNames.ELSE); }
"new"                { return symbol(TokenNames.NEW); }
"extends"            { return symbol(TokenNames.EXTENDS); }
"nil"                { return symbol(TokenNames.NIL); }
"int"                { return symbol(TokenNames.TYPE_INT); }
"string"             { return symbol(TokenNames.TYPE_STRING); }
"void"               { return symbol(TokenNames.TYPE_VOID); }

{LEADING_ZERO}     { return symbol(TokenNames.ERROR); }


{INTEGER}            
{
    try
    {
        int value = Integer.parseInt(yytext());
        if (value < 0 || value > 32767) 
        {
         return symbol(TokenNames.ERROR); 
        }
    return symbol(TokenNames.INT, value);
    }
    catch(NumberFormatException e)
    {
        return symbol(TokenNames.ERROR); 
    }
}

{String}   		{ return symbol(TokenNames.STRING, yytext()); }
{ID}                 { return symbol(TokenNames.ID, yytext()); }
{WhiteSpace}		{ /* just skip what was found, do nothing */ }
.  					{ return symbol(TokenNames.ERROR); }
<<EOF>>				{ return symbol(TokenNames.EOF);}

}

<BLOCKCOMMENT> 
{
  
  {BlockCommentEnd}                 { yybegin(YYINITIAL); }
  {TypeTwoBlockCommentCharAllowed}+ { /* skip */ }

  "/"                               { /* allow a lone slash in comments */ }
  "*"                               { /* allow a lone star in comments  */ }
  
  .                                 { return symbol(TokenNames.ERROR);}
  <<EOF>>                           { return symbol(TokenNames.ERROR);}
}