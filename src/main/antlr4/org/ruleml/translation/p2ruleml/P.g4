grammar P;

start : (asserts | queries)* EOF ;

asserts : assertion+ ;

queries : query+ ;

assertion : (fact | implies) '.' ;

query : '?-' facts '.' ;

facts : fact | and ;

and : fact (',' | 'and') fact ((',' | 'and') fact)*;

implies : facts ':-' facts ;

fact : atom | equal | not ;

not : 'not' atom ;

equal : arg '=' arg ;

atom : rel ('(' arg (',' arg)* ')')? ;

rel : op ;

op : '.'? ID ('.' ID)* ;

arg : ind | var | expr ;

expr : fun ('(' arg (',' arg)* ')')? ;

fun : op ;

ind : NUM # IndNUM | ID # IndID | SQUOTED # IndSQUOTED ;

var : '?' ID ;

NUM : ('+' | '-')? (('.' [0-9]+) | ([0-9]+ ('.' [0-9]*)?)) ;

SQUOTED : '\'' ([\u0020-\u0026\u0028-\u007E] | '\\\'')* '\'' ;

ID : [a-zA-Z_][0-9a-zA-Z_]* ;

COMMENT : '//' (~[\n])* '\n' -> channel(HIDDEN) ;

WS : [ \t\r\n] -> skip ;

