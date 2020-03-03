/** Grammars always start with a grammar header. This grammar is called
 *  ArrayInit and must match the filename: SLTL.g4
 */
grammar SLTL;

sltl : '(' sltl ')'         #parentSltl
     | unaryOp sltl         #unarySltl
     | sltl binaryOp sltl   #binarySltl
     | NAME                 #idSltl
     | TRUE                 #trueSltl
     ;

unaryOp : GLOBAL            #globalOp
      | FUTURE              #futureOp
      | NEG                 #negOp
      | '<' NAME '>'        #nextOp
      ;

binaryOp : UNTIL            #untilOp
    | AND                   #andOp
    | OR                    #opOp
    ;

TRUE    : 'true';
AND     : 'and';
OR      : 'or';
// parser rules start with lowercase letters, lexer rules with uppercase
GLOBAL  : 'G' ;
UNTIL   : 'U' ;
FUTURE  : 'F' ;
NEG     : '~';
NAME    : [a-z_A-Z]+;
INT     : [0-9]+ ;             // Define token INT as one or more digits
WS      : [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out
