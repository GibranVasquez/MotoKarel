package analizadores;

import java_cup.runtime.*;

%%

%class Lexer
%public
%cup
%unicode
%line
%column

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace = [ \t\f]
Numero = [0-9]+
Identificador = [a-zA-Z][a-zA-Z0-9_]*

%%

/* Reglas léxicas simplificadas */
"mover"             { return symbol(sym.MOVER); }
"girar"             { return symbol(sym.GIRAR); }
"izquierda"         { return symbol(sym.IZQUIERDA); }
"derecha"           { return symbol(sym.DERECHA); }
"repetir"           { return symbol(sym.REPETIR); }
"si"                { return symbol(sym.SI); }
"frente_libre"      { return symbol(sym.FRENTE_LIBRE); }
"frente_obstaculo"  { return symbol(sym.FRENTE_OBSTACULO); }
"{"                 { return symbol(sym.LLAVE_IZQ); }
"}"                 { return symbol(sym.LLAVE_DER); }
";"                 { return symbol(sym.PUNTO_COMA); }

/* Patrones básicos (sin manejo de valores) */
{Numero}            { return symbol(sym.NUMERO); }  // Sin Integer.parseInt
{Identificador}     { return symbol(sym.IDENTIFICADOR); }
{WhiteSpace}        { /* Ignorar */ }
{LineTerminator}    { /* Ignorar */ }

/* Manejo de errores */
.                   { 
                      throw new RuntimeException(
                        "Carácter no válido '" + yytext() + 
                        "' en línea " + (yyline+1) + 
                        ", columna " + (yycolumn+1)
                      ); 
                    }