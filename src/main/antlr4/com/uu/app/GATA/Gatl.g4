
grammar Gatl;

gatl: Ident  '(' gatl (',' gatl)* ')' #function
    | Ident                           #data
    ;



Ident: [a-z_A-Z0-9]+;
WS      : [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out

